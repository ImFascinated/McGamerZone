package zone.themcgamer.core.account;

import com.cryptomorin.xseries.XSound;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import zone.themcgamer.common.EnumUtils;
import zone.themcgamer.core.account.command.GemsCommand;
import zone.themcgamer.core.account.command.GoldCommand;
import zone.themcgamer.core.account.command.PlayerInfoCommand;
import zone.themcgamer.core.account.command.rank.RankCommand;
import zone.themcgamer.core.account.command.rank.arguments.ClearArgument;
import zone.themcgamer.core.account.command.rank.arguments.InfoArgument;
import zone.themcgamer.core.account.command.rank.arguments.ListArgument;
import zone.themcgamer.core.account.command.rank.arguments.SetArgument;
import zone.themcgamer.core.account.event.AccountLoadEvent;
import zone.themcgamer.core.account.event.AccountUnloadEvent;
import zone.themcgamer.core.common.MojangUtils;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.module.Module;
import zone.themcgamer.core.module.ModuleInfo;
import zone.themcgamer.core.nametag.NametagManager;
import zone.themcgamer.data.Rank;
import zone.themcgamer.data.jedis.cache.CacheRepository;
import zone.themcgamer.data.jedis.cache.impl.PlayerCache;
import zone.themcgamer.data.jedis.command.JedisCommandHandler;
import zone.themcgamer.data.jedis.command.impl.RankMessageCommand;
import zone.themcgamer.data.jedis.command.impl.StaffChatCommand;
import zone.themcgamer.data.jedis.command.impl.account.AccountRankClearCommand;
import zone.themcgamer.data.jedis.command.impl.account.AccountRankSetCommand;
import zone.themcgamer.data.jedis.command.impl.player.PlayerDirectMessageEvent;
import zone.themcgamer.data.jedis.repository.RedisRepository;
import zone.themcgamer.data.mysql.MySQLController;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * @author Braydon
 */
@ModuleInfo(name = "Account Manager")
public class AccountManager extends Module {
    public static final List<MiniAccount<?>> MINI_ACCOUNTS = new ArrayList<>();
    public static final Map<UUID, Account> CACHE = new HashMap<>(); // Account cache for online players
    public static final Cache<UUID, Account> LOOKUP_CACHE = CacheBuilder.newBuilder() // Account cache for players that were looked up via the lookup method
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .removalListener(removalNotification -> {
                Object key = removalNotification.getKey();
                if (key instanceof UUID) {
                    if (CACHE.containsKey(key))
                        return;
                    for (MiniAccount<?> miniAccount : MINI_ACCOUNTS)
                        miniAccount.getAccounts().remove(key);
                }
            }).build();

    private static final String KICK_MESSAGE = "Failed to fetch your account data, please try again in a few moments";

    private final AccountRepository repository;
    private final CacheRepository cacheRepository;
    private final NametagManager nametagManager;

    private final AtomicInteger playersConnecting = new AtomicInteger(); // The amount of players connecting to the server
    private final AtomicInteger accountsLoading = new AtomicInteger(); // The amount of players connecting to the server
    private final List<UUID> loggingIn = Collections.synchronizedList(new ArrayList<>()); // The list of uuids logging in

    public AccountManager(JavaPlugin plugin, MySQLController mySQLController, NametagManager nametagManager) {
        super(plugin);
        repository = new AccountRepository(mySQLController.getDataSource());
        cacheRepository = RedisRepository.getRepository(CacheRepository.class).orElse(null);
        this.nametagManager = nametagManager;

        // In-case somebody decides to do a no no and reloads the server, we wanna kick all
        // online players so they can rejoin to load their account
        for (Player player : Bukkit.getOnlinePlayers())
            Bukkit.getScheduler().runTask(plugin, () -> player.kickPlayer("Please re-join"));

        registerCommand(new RankCommand());
        registerCommand(new InfoArgument(this));
        registerCommand(new SetArgument(this));
        registerCommand(new ClearArgument(this));
        registerCommand(new ListArgument());

        registerCommand(new GoldCommand(this));
        registerCommand(new GemsCommand(this));
        registerCommand(new PlayerInfoCommand(this, cacheRepository));
        registerCommand(new zone.themcgamer.core.command.impl.staff.StaffChatCommand());

        JedisCommandHandler.getInstance().addListener(jedisCommand -> {
            if (jedisCommand instanceof AccountRankSetCommand) {
                AccountRankSetCommand accountRankSetCommand = (AccountRankSetCommand) jedisCommand;
                Player player = Bukkit.getPlayer(accountRankSetCommand.getUuid());
                if (player != null) {
                    Rank rank = EnumUtils.fromString(Rank.class, accountRankSetCommand.getConstantName());
                    if (rank == null)
                        rank = Rank.DEFAULT;
                    Rank finalRank = rank;
                    fromCache(player.getUniqueId()).ifPresent(account -> account.setPrimaryRank(finalRank));
                    nametagManager.setNametag(player, rank.getNametag(), null, rank.ordinal() + 1);
                    player.sendMessage(Style.main("Rank", "Your rank was updated to §f" + accountRankSetCommand.getRankDisplayName()));
                }
            } else if (jedisCommand instanceof AccountRankClearCommand) {
                AccountRankClearCommand accountRankClearCommand = (AccountRankClearCommand) jedisCommand;
                Player player = Bukkit.getPlayer(accountRankClearCommand.getUuid());
                if (player != null) {
                    fromCache(player.getUniqueId()).ifPresent(account -> {
                        account.setPrimaryRank(Rank.DEFAULT);
                        account.setSecondaryRanks(new Rank[0]);

                        Rank rank = account.getPrimaryRank();
                        nametagManager.setNametag(player, rank.getNametag(), null, rank.ordinal() + 1);
                    });
                    player.sendMessage(Style.main("Rank", "Your ranks were cleared"));
                }
            } else if (jedisCommand instanceof RankMessageCommand) {
                RankMessageCommand rankMessageCommand = (RankMessageCommand) jedisCommand;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Optional<Account> optionalAccount = fromCache(player.getUniqueId());
                    if (optionalAccount.isEmpty())
                        continue;
                    if (optionalAccount.get().hasRank(rankMessageCommand.getRank()))
                        player.sendMessage(rankMessageCommand.getMessage());
                }
            } else if (jedisCommand instanceof StaffChatCommand) {
                StaffChatCommand staffChatCommand = (StaffChatCommand) jedisCommand;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Optional<Account> account = fromCache(player.getUniqueId());
                    if (account.isEmpty() || (!account.get().hasRank(Rank.HELPER)))
                        continue;
                    String format = staffChatCommand.getPrefix() +
                            " &7" + staffChatCommand.getUsername() + " &8» &f" + staffChatCommand.getMessage();
                    player.sendMessage(Style.main(staffChatCommand.getServer(), format));
                }
            } else if (jedisCommand instanceof PlayerDirectMessageEvent) {
                PlayerDirectMessageEvent playerDirectMessageEvent = (PlayerDirectMessageEvent) jedisCommand;
                UUID uuid = playerDirectMessageEvent.getReceiver();
                Player player = Bukkit.getPlayer(uuid);
                if (player == null || !player.isOnline())
                    return;

                player.sendMessage(Style.color("&b\u2709 &7(from " + playerDirectMessageEvent.getSenderDisplayName() + "&7) &8\u00BB&f " + playerDirectMessageEvent.getMessage()));
                player.playSound(player.getLocation(), XSound.ENTITY_CHICKEN_EGG.parseSound(), 10, 1);
            }
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        try {
            UUID uuid = event.getUniqueId();

            // Incrementing the players connecting
            playersConnecting.incrementAndGet();

            // If the amount of accounts loading is 3 or more, we wanna sleep the thread (delay the login) for
            // 25 milliseconds. This will cause less strain on the MySQL server with multiple queries at once.
            while (accountsLoading.get() >= 3) {
                try {
                    Thread.sleep(25L);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

            try {
                // We store the time the player started connecting and add them to the logging in list
                long started = System.currentTimeMillis();
                accountsLoading.incrementAndGet();
                loggingIn.add(uuid);
                Account[] accountArray = new Account[] { null };
                AtomicBoolean repositoryException = new AtomicBoolean();

                // Loading the players account from MySQL and removing them from the logging in list
                Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
                    try {
                        accountArray[0] = repository.login(uuid, event.getName(), event.getAddress().getHostAddress());
                    } catch (SQLException ex) {
                        repositoryException.set(true);
                        ex.printStackTrace();
                    }
                    loggingIn.remove(uuid);
                });
                // We sleep the thread for however long it takes the player's account to be fetched from
                // the database (with a maximum time of 20 seconds) to give the MySQL server time to fetch
                // the account
                long timeSlept = 0L;
                while (loggingIn.contains(uuid) && System.currentTimeMillis() - started < TimeUnit.SECONDS.toMillis(20L)) {
                    if (repositoryException.get()) {
                        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, KICK_MESSAGE + " (repository)");
                        break;
                    }
                    timeSlept+= 1L;
                    Thread.sleep(1L);
                }
                log(event.getName() + " has taken " + (System.currentTimeMillis() - started) + "ms to login (" + timeSlept + "ms was spent sleeping)");

                Account account = accountArray[0];
                // If the player is still in the logging in list, or the player's account is null, we wanna disallow login
                // and show the player an error
                if (loggingIn.remove(uuid) || account == null || (account.getId() <= 0))
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, KICK_MESSAGE + " (" + (account == null ? "account" : "timeout") + ")");
                else {
                    // If the login was successful, we call the AccountLoadEvent and locally cache the player's account
                    // so it can be used in the future
                    Bukkit.getScheduler().runTask(getPlugin(), () -> Bukkit.getPluginManager().callEvent(new AccountLoadEvent(account)));
                    CACHE.put(uuid, account);
                }
            } catch (Exception ex) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, KICK_MESSAGE + " (exception)");
                ex.printStackTrace();
            } finally {
                accountsLoading.decrementAndGet();
            }
        } finally {
            playersConnecting.decrementAndGet();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        Account account;
        // If the player does not have an account (this should NEVER happen), disallow login
        if ((account = CACHE.get(player.getUniqueId())) == null)
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, KICK_MESSAGE + " (cache)");
        else {
            // Automatic opping for player's with the rank Jr.Dev or above
            Rank opRank = Rank.JR_DEVELOPER;
            if (account.hasRank(opRank) && !player.isOp())
                player.setOp(true);
            else if (!account.hasRank(opRank) && player.isOp())
                player.setOp(false);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Account account = CACHE.get(player.getUniqueId());
        if (account == null)
            return;
        Rank rank = account.getPrimaryRank();
        nametagManager.setNametag(player, rank.getNametag(), null, rank.ordinal() + 1);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        // Loop through all of the mini accounts and remove the player's account
        for (MiniAccount<?> miniAccount : MINI_ACCOUNTS)
            miniAccount.getAccounts().remove(uuid);
        // Call the unload event and remove the main account
        Bukkit.getPluginManager().callEvent(new AccountUnloadEvent(CACHE.remove(uuid)));
    }

    /**
     * Lookup a {@link Account} with the given username
     * @param name the name of the account to lookup
     * @param consumer the account consumer
     */
    public void lookup(String name, Consumer<Account> consumer) {
        CompletableFuture.runAsync(() -> {
            Player player = Bukkit.getPlayerExact(name);
            UUID uuid = player == null ? null : player.getUniqueId();
            if (uuid == null) {
                Optional<PlayerCache> optionalPlayerCache = cacheRepository
                        .lookup(PlayerCache.class, playerCache -> playerCache.getName().equalsIgnoreCase(name));
                if (optionalPlayerCache.isPresent())
                    uuid = optionalPlayerCache.get().getUuid();
            }
            if (uuid == null)
                MojangUtils.getUUIDAsync(name, fetchedUUID -> lookup(fetchedUUID, name, consumer));
            else lookup(uuid, name, consumer);
        });
    }

    /**
     * Lookup a {@link Account} with the given {@link UUID}
     * @param uuid the uuid of the account to lookup
     * @param name the name of the account to lookup
     * @param consumer the account consumer
     */
    public void lookup(UUID uuid, String name, Consumer<Account> consumer) {
        if (uuid == null) {
            consumer.accept(null);
            return;
        }
        AtomicReference<Account> reference = new AtomicReference<>(CACHE.get(uuid));
        if (reference.get() == null)
            reference.set(LOOKUP_CACHE.getIfPresent(uuid));
        if (reference.get() != null)
            consumer.accept(reference.get());
        else {
            CompletableFuture.runAsync(() -> {
                try {
                    reference.set(repository.login(uuid, name, ""));
                    if (reference.get() != null)
                        LOOKUP_CACHE.put(uuid, reference.get());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                consumer.accept(reference.get());
            });
        }
    }

    public void setRank(Account account, Rank rank) {
        account.setPrimaryRank(rank);
        repository.setRank(account.getId(), rank);
        JedisCommandHandler.getInstance().send(new AccountRankSetCommand(account.getUuid(), rank.name(), rank.getColor() + rank.getDisplayName()));
    }

    public void clearRanks(Account account) {
        account.setPrimaryRank(Rank.DEFAULT);
        account.setSecondaryRanks(new Rank[0]);
        repository.clearRanks(account.getId());
        JedisCommandHandler.getInstance().send(new AccountRankClearCommand(account.getUuid()));
    }

    /**
     * Add the given {@link MiniAccount}
     * @param miniAccount the mini account to add
     */
    public static void addMiniAccount(MiniAccount<?> miniAccount) {
        MINI_ACCOUNTS.add(miniAccount);
    }

    /**
     * Get the {@link Account} from the provided uuid
     * @param uuid the uuid to get the account for
     * @return the optional account
     */
    public static Optional<Account> fromCache(UUID uuid) {
        return Optional.ofNullable(CACHE.get(uuid));
    }
}