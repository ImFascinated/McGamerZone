package zone.themcgamer.core.kingdom;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.game.MGZGame;
import zone.themcgamer.core.kingdom.command.KingdomCommand;
import zone.themcgamer.core.module.Module;
import zone.themcgamer.core.module.ModuleInfo;
import zone.themcgamer.core.plugin.MGZPlugin;
import zone.themcgamer.core.traveller.ServerTraveller;
import zone.themcgamer.data.Rank;
import zone.themcgamer.data.jedis.data.ServerGroup;
import zone.themcgamer.data.jedis.repository.RedisRepository;
import zone.themcgamer.data.jedis.repository.impl.ServerGroupRepository;

import java.util.Optional;

/**
 * @author Braydon
 */
@ModuleInfo(name = "Kingdoms")
public class KingdomManager extends Module {
    private static final int MAX_KINGDOMS = 3;
    private static final MGZGame DEFAULT_GAME = MGZGame.THE_BRIDGE;

    private final ServerGroupRepository serverGroupRepository;

    public KingdomManager(JavaPlugin plugin, ServerTraveller traveller) {
        super(plugin);
        serverGroupRepository = RedisRepository.getRepository(ServerGroupRepository.class).orElse(null);
//        JedisCommandHandler.getInstance().addListener(jedisCommand -> {
//            if (jedisCommand instanceof ServerStateChangeCommand) {
//                ServerStateChangeCommand stateChangeCommand = (ServerStateChangeCommand) jedisCommand;
//                if (stateChangeCommand.getNewState() != ServerState.RUNNING)
//                    return;
//                MinecraftServer server = stateChangeCommand.getServer();
//                for (Player player : Bukkit.getOnlinePlayers()) {
//                    if (!server.getName().equals(player.getName() + "-1"))
//                        continue;
//                    traveller.sendPlayer(player, server);
//                }
//            }
//        });
        registerCommand(new KingdomCommand(this));
    }

    public boolean host(Player player) {
        if (MAX_KINGDOMS <= 0) {
            player.sendMessage(Style.error("Kingdom", "§cKingdoms are currently disabled!"));
            return false;
        }
        if (serverGroupRepository.getCached().stream().filter(ServerGroup::isKingdom).count() >= MAX_KINGDOMS) {
            player.sendMessage(Style.error("Kingdom", "§cYou cannot create a Kingdom at this time!"));
            return false;
        }
        if (serverGroupRepository.lookup(serverGroup -> serverGroup.getName().equalsIgnoreCase(player.getName())).isPresent()) {
            player.sendMessage(Style.error("Kingdom", "§cYou already have a Kingdom created!"));
            return false;
        }
        Optional<Account> optionalAccount = AccountManager.fromCache(player.getUniqueId());
        if (!optionalAccount.isPresent()) {
            player.sendMessage(Style.error("Kingdom", "§cThere was an error whilst creating your Kingdom!"));
            return false;
        }
        int ram = 1024;
        if (optionalAccount.get().hasRank(Rank.DEVELOPER))
            ram = 2048;
        ServerGroup currentGroup = MGZPlugin.getMinecraftServer().getGroup();
        ServerGroup serverGroup = new ServerGroup(
                player.getName(),
                ram,
                currentGroup.getServerJar(),
                "Arcade.zip",
                "McGamerCore-arcade-v1.0-SNAPSHOT.jar",
                "HUB/Hub_Normal_2021.zip",
                currentGroup.getStartupScript(),
                currentGroup.getPrivateAddress(),
                player.getUniqueId(),
                DEFAULT_GAME.name(),
                Integer.MAX_VALUE,
                50,
                1,
                1,
                true,
                false
        );
        serverGroupRepository.post(serverGroup);
        return true;
    }
}