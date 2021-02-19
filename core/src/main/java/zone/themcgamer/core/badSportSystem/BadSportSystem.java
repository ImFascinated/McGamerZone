package zone.themcgamer.core.badSportSystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import zone.themcgamer.common.EnumUtils;
import zone.themcgamer.common.MiscUtils;
import zone.themcgamer.common.TimeUtils;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.account.MiniAccount;
import zone.themcgamer.core.account.event.AccountPreLoadEvent;
import zone.themcgamer.core.badSportSystem.command.BadSportCommand;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.module.Module;
import zone.themcgamer.core.module.ModuleInfo;
import zone.themcgamer.data.Rank;
import zone.themcgamer.data.jedis.command.JedisCommandHandler;
import zone.themcgamer.data.jedis.command.impl.PlayerKickCommand;
import zone.themcgamer.data.jedis.command.impl.PlayerMessageCommand;
import zone.themcgamer.data.jedis.command.impl.PunishmentsUpdateCommand;
import zone.themcgamer.data.jedis.command.impl.RankMessageCommand;
import zone.themcgamer.data.mysql.MySQLController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Braydon
 */
@ModuleInfo(name = "Bad Sport System")
public class BadSportSystem extends Module {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Punishment.class, new PunishmentSerializer())
            .excludeFieldsWithoutExposeAnnotation().create();

    private final Map<UUID, BadSportClient> accounts = new HashMap<>();
    private final BadSportRepository repository;

    public BadSportSystem(JavaPlugin plugin, MySQLController mySQLController, AccountManager accountManager) {
        super(plugin);
        AccountManager.addMiniAccount(new IPPunishmentLoader(plugin));
        AccountManager.addMiniAccount(new AccountsPunishmentLoader(plugin));
        repository = new BadSportRepository(mySQLController.getDataSource());
        JedisCommandHandler.getInstance().addListener(jedisCommand -> {
            if (jedisCommand instanceof PunishmentsUpdateCommand) {
                PunishmentsUpdateCommand punishmentsUpdateCommand = (PunishmentsUpdateCommand) jedisCommand;
                Optional<BadSportClient> optionalBadSportClient = lookup(punishmentsUpdateCommand.getUuid());
                if (!optionalBadSportClient.isPresent())
                    return;
                try {
                    Set<Punishment> punishments = GSON.fromJson(punishmentsUpdateCommand.getJson(), new TypeToken<Set<Punishment>>() {}.getType());
                    optionalBadSportClient.get().setPunishments(punishments);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        registerCommand(new BadSportCommand(accountManager));
    }

    @EventHandler
    private void onAccountPreLoad(AccountPreLoadEvent event) {
        accounts.put(event.getUuid(), new BadSportClient(event.getIpAddress()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onLogin(PlayerLoginEvent event) {
        Optional<BadSportClient> optionalClient = lookup(event.getPlayer().getUniqueId());
        if (!optionalClient.isPresent())
            return;
        Optional<Punishment> optionalPunishment = optionalClient.get().getBan();
        if (!optionalPunishment.isPresent())
            return;
        Punishment punishment = optionalPunishment.get();
        event.disallow(PlayerLoginEvent.Result.KICK_BANNED, PunishmentCategory.format(punishment));
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        accounts.remove(event.getPlayer().getUniqueId());
    }

    public void punish(String encryptedIpAddress, UUID uuid, String name, PunishmentCategory category, PunishmentOffense offense,
                       int severity, @Nullable UUID staffUuid, String staffName, long duration, String reason, boolean silent) {
        Punishment punishment = new Punishment(encryptedIpAddress, uuid, category, offense, severity, staffUuid, staffName,
                System.currentTimeMillis(), duration, reason);
        BadSportClient client = accounts.get(uuid);
        if (client != null) {
            for (Punishment previousPunishment : client.getPunishments().stream()
                    .filter(p -> p.getCategory() == category && p.isActive())
                    .collect(Collectors.toList())) {
                remove(previousPunishment, name, null, null, null, true, true);
            }
            client.getPunishments().add(punishment);
        }
        repository.punish(punishment, id -> {
            punishment.setId(id);
            if (client != null)
                JedisCommandHandler.getInstance().send(new PunishmentsUpdateCommand(uuid, GSON.toJson(client.getPunishments())));
        });

        if (category.isKick())
            JedisCommandHandler.getInstance().send(new PlayerKickCommand(uuid, PunishmentCategory.format(punishment)));

        if (staffUuid != null) {
            Optional<Account> optionalAccount = AccountManager.fromCache(staffUuid);
            if (optionalAccount.isPresent())
                staffName = optionalAccount.get().getDisplayName();
        }
        JedisCommandHandler.getInstance().send(new RankMessageCommand(silent ? Rank.HELPER : Rank.DEFAULT, Style.main(
                "Bad Sport" + (silent ? " §7(Silent)" : ""),
                "§f" + name + " §7was " + category.getIssuedMessage() + " by §f" + staffName +
                        (category.isHasDuration() ? " §7for §f" + TimeUtils.convertString(duration) : "")
        )));
        if (category == PunishmentCategory.WARN || category == PunishmentCategory.MUTE) {
            JedisCommandHandler.getInstance().send(new PlayerMessageCommand(uuid, Style.main("Bad Sport",
                    "You were " + category.getIssuedMessage() +
                            (category.isHasDuration() ? " §7for §f" + TimeUtils.convertString(duration) : "") + " §7because of §f" + reason
            )));
        }
    }

    public void remove(Punishment punishment, String name, UUID staffUuid, String staffName, String reason, boolean silent, boolean removingPrevious) {
        punishment.remove(staffUuid, staffName, reason);
        repository.remove(punishment);
        BadSportClient client = accounts.get(punishment.getTargetUuid());
        if (client != null && !removingPrevious)
            JedisCommandHandler.getInstance().send(new PunishmentsUpdateCommand(punishment.getTargetUuid(), GSON.toJson(client.getPunishments())));
        if (removingPrevious)
            return;
        PunishmentCategory category = punishment.getCategory();
        if (category.getRemovedMessage() == null)
            return;
        if (staffUuid != null) {
            Optional<Account> optionalAccount = AccountManager.fromCache(staffUuid);
            if (optionalAccount.isPresent())
                staffName = optionalAccount.get().getDisplayName();
        }
        JedisCommandHandler.getInstance().send(new RankMessageCommand(silent ? Rank.HELPER : Rank.DEFAULT, Style.main(
                "Bad Sport" + (silent ? " §7(Silent)" : ""),
                "§f" + name + " §7was " + category.getRemovedMessage() + " by §f" + staffName)
        ));
    }

    /**
     * Get the {@link BadSportClient} account for the given {@link UUID}
     * @param uuid the uuid of the account
     * @return the optional account
     */
    public Optional<BadSportClient> lookup(UUID uuid) {
        return Optional.ofNullable(accounts.get(uuid));
    }

    private List<Punishment> getPunishments(String encryptedIp, ResultSet resultSet) {
        List<Punishment> punishments = new ArrayList<>();
        try {
            while (resultSet.next()) {
                PunishmentCategory category = EnumUtils.fromString(PunishmentCategory.class, resultSet.getString("category"));
                if (category == null)
                    continue;
                PunishmentOffense offense = EnumUtils.fromString(PunishmentOffense.class, resultSet.getString("offense"));
                if (offense == null)
                    continue;
                punishments.add(new Punishment(
                        resultSet.getInt("id"),
                        encryptedIp,
                        UUID.fromString(resultSet.getString("targetUuid")),
                        category,
                        offense,
                        resultSet.getInt("severity"),
                        MiscUtils.getUuid(resultSet.getString("staffUuid")),
                        resultSet.getString("staffName"),
                        resultSet.getLong("timeIssued"),
                        resultSet.getLong("duration"),
                        resultSet.getString("reason"),
                        MiscUtils.getUuid(resultSet.getString("removeStaffUuid")),
                        resultSet.getString("removeStaffName"),
                        resultSet.getString("removeReason"),
                        resultSet.getLong("timeRemoved")
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return punishments;
    }

    @ModuleInfo(name = "BSS IP Punishments Loader")
    private class IPPunishmentLoader extends MiniAccount<BadSportClient> {
        public IPPunishmentLoader(JavaPlugin plugin) {
            super(plugin);
        }

        @Override
        public BadSportClient getAccount(int accountId, UUID uuid, String name, String ip, String encryptedIp) {
            return null;
        }

        @Override
        public String getQuery(int accountId, UUID uuid, String name, String ip, String encryptedIp) {
            return "SELECT * FROM `punishments` WHERE `targetIp` = '" + encryptedIp + "';";
        }

        @Override
        public void loadAccount(int accountId, UUID uuid, String name, String ip, String encryptedIp, ResultSet resultSet) {
            if (ip.trim().isEmpty())
                return;
            System.out.println("Fetching punishments for ip \"" + encryptedIp + "\" (\"" + ip + "\")");
            accounts.values().stream().filter(badSportClient -> badSportClient.getIp().equals(ip)).findFirst()
                    .ifPresent(client -> client.getPunishments().addAll(getPunishments(encryptedIp, resultSet)));
        }
    }

    @ModuleInfo(name = "BSS Account Punishments Loader")
    private class AccountsPunishmentLoader extends MiniAccount<BadSportClient> {
        public AccountsPunishmentLoader(JavaPlugin plugin) {
            super(plugin);
        }

        @Override
        public BadSportClient getAccount(int accountId, UUID uuid, String name, String ip, String encryptedIp) {
            return null;
        }

        @Override
        public String getQuery(int accountId, UUID uuid, String name, String ip, String encryptedIp) {
            return "SELECT * FROM `punishments` WHERE `targetUuid` = '" + uuid.toString() + "';";
        }

        @Override
        public void loadAccount(int accountId, UUID uuid, String name, String ip, String encryptedIp, ResultSet resultSet) {
            System.out.println("Fetching punishments for account \"" + uuid.toString() + "\" (\"" + name + "\")");
            BadSportClient client = accounts.get(uuid);
            if (client != null)
                client.getPunishments().addAll(getPunishments(encryptedIp, resultSet));
        }
    }
}