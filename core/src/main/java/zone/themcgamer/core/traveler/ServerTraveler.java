package zone.themcgamer.core.traveler;

import com.cryptomorin.xseries.XSound;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import zone.themcgamer.common.MiscUtils;
import zone.themcgamer.common.RandomUtils;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.module.Module;
import zone.themcgamer.core.module.ModuleInfo;
import zone.themcgamer.core.plugin.MGZPlugin;
import zone.themcgamer.data.jedis.command.JedisCommandHandler;
import zone.themcgamer.data.jedis.command.impl.ServerSendCommand;
import zone.themcgamer.data.jedis.data.ServerGroup;
import zone.themcgamer.data.jedis.data.server.MinecraftServer;
import zone.themcgamer.data.jedis.repository.RedisRepository;
import zone.themcgamer.data.jedis.repository.impl.MinecraftServerRepository;
import zone.themcgamer.data.jedis.repository.impl.ServerGroupRepository;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Braydon
 */
@ModuleInfo(name = "Server Traveler")
public class ServerTraveler extends Module {
    private final ServerGroupRepository serverGroupRepository;
    private final MinecraftServerRepository minecraftServerRepository;

    public ServerTraveler(JavaPlugin plugin) {
        super(plugin);
        serverGroupRepository = RedisRepository.getRepository(ServerGroupRepository.class).orElse(null);
        minecraftServerRepository = RedisRepository.getRepository(MinecraftServerRepository.class).orElse(null);
    }

    /**
     * Send all players to the provided server
     * @param server the server to send the players to
     */
    public void sendAll(String server) {
        sendAll(server, null, true);
    }

    /**
     * Send all players to the provided server
     * @param server the server to send the players to
     * @param reason the reason for sending the players
     */
    public void sendAll(String server, String reason) {
        sendAll(server, reason, true);
    }

    /**
     * Send all players to the provided server
     * @param server the server to send the players to
     * @param reason the reason for sending the players
     * @param inform whether or not to inform the player that they are
     *               being sent
     */
    public void sendAll(String server, String reason, boolean inform) {
        if (reason != null) {
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(Style.color(" &c➢ &7" + reason));
            Bukkit.broadcastMessage("");
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                player.playSound(player.getEyeLocation(), XSound.ENTITY_VILLAGER_AMBIENT.parseSound(), 0.9f, 1f);
            } catch (NoClassDefFoundError ignored) {}
            sendPlayer(player, server, inform);
        }
    }

    /**
     * Send the provided player to the provided server
     * @param player the player to send
     * @param server the name of the server to send the player to
     */
    public void sendPlayer(Player player, String server) {
        sendPlayer(player, server, true);
    }

    /**
     * Send the provided player to the provided server
     * @param player the player to send
     * @param server the name of the server to send the player to
     * @param inform whether or not to inform the player that they are
     *               being sent
     */
    public void sendPlayer(Player player, String server, boolean inform) {
        MinecraftServer minecraftServer = null;
        Optional<ServerGroup> serverGroup = serverGroupRepository.lookup(server);
        if (serverGroup.isPresent() && (!serverGroup.get().getServers().isEmpty())) {
            ArrayList<MinecraftServer> servers = serverGroup.get().getServers().stream()
                    .filter(mcServer -> !mcServer.equals(MGZPlugin.getMinecraftServer()) && mcServer.isRunning())
                    .collect(Collectors.toCollection(ArrayList::new));
            if (servers.size() >= 2)
                minecraftServer = RandomUtils.random(servers);
            else if (!servers.isEmpty()) minecraftServer = servers.get(0);
        } else minecraftServer = minecraftServerRepository.lookup(mcServer -> mcServer.getName().equalsIgnoreCase(server)).orElse(null);
        if (minecraftServer == null)
            throw new IllegalArgumentException("Minecraft server doesn't exist");
        sendPlayer(player, minecraftServer, inform);
    }

    /**
     * Send the provided player to the provided {@link MinecraftServer}
     * @param player the player to send
     * @param server the server to send the player to
     */
    public void sendPlayer(Player player, MinecraftServer server) {
        sendPlayer(player, server, true);
    }

    /**
     * Send the provided player to the provided {@link MinecraftServer}
     * @param player the player to send
     * @param server the server to send the player to
     * @param inform whether or not to inform the player that they are
     *               being sent
     */
    public void sendPlayer(Player player, MinecraftServer server, boolean inform) {
        if (!server.isRunning())
            throw new IllegalStateException("Server is unavailable");
        if (MGZPlugin.getMinecraftServer().equals(server))
            throw new IllegalArgumentException("Player is already connected");
        if (inform) {
            player.sendMessage(new ComponentBuilder(Style.main("Traveler", "Connecting to &6" + server.getName()))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MiscUtils.arrayToString(
                            Style.color("&7Server Id: &6" + server.getId()),
                            Style.color("&7Type: &6" + server.getGroup().getName())
                    )).create())).create());
        }
        JedisCommandHandler.getInstance().send(new ServerSendCommand(player.getName(), server.getId()));
    }
}