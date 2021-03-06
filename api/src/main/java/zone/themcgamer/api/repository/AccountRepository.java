package zone.themcgamer.api.repository;

import com.zaxxer.hikari.HikariDataSource;
import zone.themcgamer.api.model.impl.AccountModel;
import zone.themcgamer.data.Rank;
import zone.themcgamer.data.mysql.data.column.Column;
import zone.themcgamer.data.mysql.data.column.impl.StringColumn;
import zone.themcgamer.data.mysql.repository.MySQLRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

/**
 * This repository handles fetching of {@link AccountModel}'s from MySQL
 *
 * @author Braydon
 */
public class AccountRepository extends MySQLRepository {
    private static final String SELECT_ACCOUNT = "SELECT * FROM `accounts` WHERE `uuid` = ? LIMIT 1";

    public AccountRepository(HikariDataSource dataSource) {
        super(dataSource);
    }

    /**
     * Fetch the {@link AccountModel} with the provided {@link UUID}
     *
     * @param uuid the uuid of the account
     * @return the account, null if it doesn't exist
     */
    public AccountModel getAccount(UUID uuid) {
        AccountModel[] model = new AccountModel[] { null };
        executeQuery(SELECT_ACCOUNT, new Column[] {
                new StringColumn("uuid", uuid.toString())
        }, resultSet -> {
            try {
                if (resultSet.next())
                    model[0] = constructAccount(uuid, resultSet);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        return model[0];
    }

    /**
     * Construct a {@link AccountModel} from the given parameters
     *
     * @param uuid the uuid
     * @param resultSet the result set
     * @return the account
     */
    private AccountModel constructAccount(UUID uuid, ResultSet resultSet) {
        try {
            Rank[] secondaryRanks = Arrays.stream(resultSet.getString("secondaryRanks")
                    .split(",")).map(rankName -> Rank.lookup(rankName).orElse(null))
                    .filter(Objects::nonNull).toArray(Rank[]::new);
            return new AccountModel(
                    resultSet.getInt("id"),
                    uuid,
                    resultSet.getString("name"),
                    Rank.lookup(resultSet.getString("primaryRank")).orElse(Rank.DEFAULT),
                    secondaryRanks,
                    resultSet.getInt("gold"),
                    resultSet.getInt("gems"),
                    resultSet.getString("ipAddress"),
                    resultSet.getLong("firstLogin"),
                    resultSet.getLong("lastLogin"),
                    -1L
            );
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}