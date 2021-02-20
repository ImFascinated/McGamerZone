package zone.themcgamer.core.server;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.module.Module;
import zone.themcgamer.core.module.ModuleInfo;
import zone.themcgamer.core.plugin.MGZPlugin;
import zone.themcgamer.core.server.command.*;
import zone.themcgamer.core.traveler.ServerTraveler;
import zone.themcgamer.data.jedis.command.JedisCommandHandler;
import zone.themcgamer.data.jedis.command.impl.ServerRestartCommand;
import zone.themcgamer.data.jedis.data.server.MinecraftServer;
import zone.themcgamer.data.jedis.data.server.ServerState;
import zone.themcgamer.data.jedis.repository.RedisRepository;
import zone.themcgamer.data.jedis.repository.impl.MinecraftServerRepository;
import zone.themcgamer.data.jedis.repository.impl.ServerGroupRepository;

/**
 * @author Braydon
 */
@ModuleInfo(name = "Server Manager")
public class ServerManager extends Module {
    public ServerManager(JavaPlugin plugin, ServerTraveler traveler) {
        super(plugin);
        ServerGroupRepository serverGroupRepository = RedisRepository.getRepository(ServerGroupRepository.class).orElse(null);
        MinecraftServerRepository minecraftServerRepository = RedisRepository.getRepository(MinecraftServerRepository.class).orElse(null);
        registerCommand(new MonitorCommand());
        registerCommand(new ServerCommand(traveler, minecraftServerRepository));
        registerCommand(new HubCommand(traveler));
        registerCommand(new RestartCommand(this, serverGroupRepository, minecraftServerRepository));
        registerCommand(new StopCommand(this));

        // Handle server restarting
        JedisCommandHandler.getInstance().addListener(jedisCommand -> {
            if (jedisCommand instanceof ServerRestartCommand) {
                MinecraftServer minecraftServer = MGZPlugin.getMinecraftServer();
                if (!((ServerRestartCommand) jedisCommand).getServerId().equals(minecraftServer.getId()))
                    return;
                try {
                    traveler.sendAll("Hub", "&6" + minecraftServer.getName() + " &7is restarting");
                } catch (IllegalArgumentException ignored) {}
                Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () ->
                    minecraftServer.setState(ServerState.RESTARTING), 10L);
                Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), Bukkit::shutdown, 40L);
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onJoin(AsyncPlayerPreLoginEvent event) {
        if (!MGZPlugin.getMinecraftServer().isRunning())
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Style.color("&cThis server is currently restarting..."));
    }

    /**
     * Restart the given {@link MinecraftServer}
     * @param minecraftServer the server to restart
     */
    public void restart(MinecraftServer minecraftServer) {
        JedisCommandHandler.getInstance().send(new ServerRestartCommand(minecraftServer.getId()));
    }
}