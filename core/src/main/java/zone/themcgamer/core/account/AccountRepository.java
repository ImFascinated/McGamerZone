package zone.themcgamer.core.account;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import zone.themcgamer.common.HashUtils;
import zone.themcgamer.core.account.event.AccountPreLoadEvent;
import zone.themcgamer.data.Rank;
import zone.themcgamer.data.jedis.cache.CacheRepository;
import zone.themcgamer.data.jedis.cache.impl.PlayerCache;
import zone.themcgamer.data.jedis.repository.RedisRepository;
import zone.themcgamer.data.mysql.data.column.Column;
import zone.themcgamer.data.mysql.data.column.impl.IntegerColumn;
import zone.themcgamer.data.mysql.data.column.impl.LongColumn;
import zone.themcgamer.data.mysql.data.column.impl.StringColumn;
import zone.themcgamer.data.mysql.repository.MySQLRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author Braydon
 */
@Slf4j(topic = "Account Repository")
public class AccountRepository extends MySQLRepository {
    private static final String SELECT_ACCOUNT = "SELECT * FROM `accounts` WHERE `uuid` = ? LIMIT 1";
    private static final String INSERT_ACCOUNT = "INSERT INTO `accounts` " +
            "(`id`, `uuid`, `name`, `primaryRank`, `secondaryRanks`, `gold`, `gems`, `ipAddress`, `firstLogin`, `lastLogin`) VALUES " +
            "(NULL, ?, ?, '" + Rank.DEFAULT.name() + "', '', '0', '0', ?, ?, ?);";
    private static final String UPDATE_RANK = "UPDATE `accounts` SET `primaryRank` = ? WHERE `id` = ?;";

    public AccountRepository(HikariDataSource dataSource) {
        super(dataSource);
    }

    /**
     * Attempt a login with the given uuid, name, and ip address
     * @param uuid the uuid
     * @param name the username
     * @param ipAddress the ip address
     * @return the fetched account
     */
    public Account login(UUID uuid, String name, String ipAddress) throws SQLException {
        if (uuid == null || (name == null || name.trim().isEmpty()))
            return null;
        boolean offlineLookup = ipAddress.trim().isEmpty();
        log.info("Logging in client " + name + " (" + uuid.toString() + ")" + (offlineLookup ? " - OFFLINE LOOKUP" : ""));
        String encryptedIpAddress = offlineLookup ? "" : HashUtils.encryptSha256(ipAddress);
        int accountId = -1;
        boolean loadedFromCache = false;
        CacheRepository cacheRepository = RedisRepository.getRepository(CacheRepository.class).orElse(null);
        if (cacheRepository != null) {
            PlayerCache cache = cacheRepository.lookup(PlayerCache.class, uuid).orElse(null);
            if (cache != null) {
                if (!cache.getName().equals(name)) {
                    cache.setName(name);
                    cacheRepository.post(cache);
                }
                accountId = cache.getAccountId();
                loadedFromCache = true;
                log.info("Account id for " + name + " loaded from cache: " + accountId);
            }
        }
        Account account = null;
        long now = System.currentTimeMillis();
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(SELECT_ACCOUNT);
        ) {
            // Attempt to select the existing account from the database
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();

            String query = "";

            if (resultSet.next()) { // If the account exists in the database, we wanna load its values
                // If the account id has not been loaded from the cache, we wanna fetch it from the database
                if (accountId <= 0) {
                    accountId = resultSet.getInt(1);
                    log.info("Account id for " + name + " loaded from MySQL: " + accountId);
                }
                account = constructAccount(accountId, uuid, name, resultSet, ipAddress, encryptedIpAddress, offlineLookup ? -1L : now);

                // If the account exists in the database and we're not doing an offline account lookup, we wanna update
                // some key values in the database for the user, like their name, ip address, and last login time
                if (!offlineLookup)
                    query = "UPDATE `accounts` SET `name`='" + name + "', `ipAddress`='" + encryptedIpAddress + "', `lastLogin`='" + System.currentTimeMillis() + "' WHERE `id` = '" + accountId + "';";
            } else {
                // Inserting the new account into the database
                int[] idArray = new int[1];
                executeInsert(connection, INSERT_ACCOUNT, new Column[] {
                        new StringColumn("uuid", uuid.toString()),
                        new StringColumn("name", name),
                        new StringColumn("ipAddress", encryptedIpAddress),
                        new LongColumn("firstLogin", now),
                        new LongColumn("lastLogin", now),
                }, insertResultSet -> {
                    try {
                        while (insertResultSet.next()) {
                            // After we insert the account, we wanna get the account id that was generated and
                            // store it in the account object
                            idArray[0] = insertResultSet.getInt(1);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });
                accountId = idArray[0];
                log.info("Successfully inserted a new account for " + name + ", their account id is " + accountId);
            }
            Bukkit.getPluginManager().callEvent(new AccountPreLoadEvent(uuid, name, ipAddress));
            int finalAccountId = accountId;

            query+= AccountManager.MINI_ACCOUNTS.parallelStream().map(miniAccount -> miniAccount.getQuery(finalAccountId, uuid, name, ipAddress, encryptedIpAddress)).collect(Collectors.joining());
            if (!query.trim().isEmpty()) {
                log.info("Executing mini account tasks (" + AccountManager.MINI_ACCOUNTS.size() + ") for " + name);

                statement.execute(query);
                statement.getUpdateCount();
                statement.getMoreResults();
                for (MiniAccount<?> miniAccount : AccountManager.MINI_ACCOUNTS) {
                    Object miniAccountObject = miniAccount.getAccount(accountId, uuid, name, ipAddress, encryptedIpAddress);
                    if (miniAccountObject != null)
                        miniAccount.addAccount(uuid, miniAccountObject);
                    try {
                        ResultSet miniAccountResultSet = statement.getResultSet();
                        if (miniAccountResultSet == null)
                            continue;
                        miniAccount.loadAccount(accountId, uuid, name, ipAddress, encryptedIpAddress, miniAccountResultSet);
                    } finally {
                        statement.getMoreResults();
                    }
                }
            }
        }
        if (account == null) {
            account = new Account(
                    accountId,
                    uuid,
                    name,
                    Rank.DEFAULT,
                    new Rank[0],
                    0D,
                    0D,
                    encryptedIpAddress,
                    ipAddress,
                    encryptedIpAddress,
                    now,
                    now
            );
        }
        if (!loadedFromCache && cacheRepository != null) {
            cacheRepository.post(new PlayerCache(uuid, name, accountId));
            log.info("Stored new cache object for " + name + " in Redis");
        }
        return account;
    }

    public void setRank(int accountId, Rank rank) {
        CompletableFuture.runAsync(() -> {
            executeInsert(UPDATE_RANK, new Column[] {
                    new StringColumn("primaryRank", rank.name()),
                    new IntegerColumn("id", accountId)
            });
        });
    }

    public void clearRanks(int accountId) {
        String query = UPDATE_RANK
                .replaceFirst("\\?", "'" + Rank.DEFAULT.name() + "'")
                .replaceFirst("\\?", "'" + accountId + "'");
        query+= "UPDATE `accounts` SET `secondaryRanks` = '' WHERE `id` = " + accountId + ";";
        String finalQuery = query;
        CompletableFuture.runAsync(() -> {
            executeInsert(finalQuery, new Column[0]);
        });
    }

    /**
     * Construct a {@link Account} from the given parameters
     * @param accountId the account id
     * @param uuid the uuid
     * @param name the name
     * @param resultSet the result set
     * @param ipAddress the ip address
     * @param encryptedIpAddress the encrypted ip address
     * @param lastLogin the last login
     * @return the account
     */
    private Account constructAccount(int accountId, UUID uuid, String name, ResultSet resultSet, String ipAddress, String encryptedIpAddress, long lastLogin) {
        try {
            Rank[] secondaryRanks = Arrays.stream(resultSet.getString("secondaryRanks")
                    .split(",")).map(rankName -> Rank.lookup(rankName).orElse(null))
                    .filter(Objects::nonNull).toArray(Rank[]::new);
            return new Account(
                    accountId,
                    uuid,
                    resultSet.getString("name"),
                    Rank.lookup(resultSet.getString("primaryRank")).orElse(Rank.DEFAULT),
                    secondaryRanks,
                    resultSet.getInt("gold"),
                    resultSet.getInt("gems"),
                    resultSet.getString("ipAddress"),
                    ipAddress,
                    encryptedIpAddress,
                    resultSet.getLong("firstLogin"),
                    lastLogin == -1L ? resultSet.getLong("lastLogin") : lastLogin
            );
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}