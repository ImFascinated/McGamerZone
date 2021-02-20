package zone.themcgamer.proxy.hub;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import zone.themcgamer.common.Tuple;
import zone.themcgamer.data.jedis.command.JedisCommandHandler;
import zone.themcgamer.data.jedis.command.impl.ServerRestartCommand;
import zone.themcgamer.data.jedis.command.impl.ServerStateChangeCommand;
import zone.themcgamer.data.jedis.data.ServerGroup;
import zone.themcgamer.data.jedis.data.server.MinecraftServer;
import zone.themcgamer.data.jedis.data.server.ServerState;
import zone.themcgamer.data.jedis.repository.RedisRepository;
import zone.themcgamer.data.jedis.repository.impl.MinecraftServerRepository;
import zone.themcgamer.data.jedis.repository.impl.ServerGroupRepository;
import zone.themcgamer.proxy.Proxy;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Braydon
 */
public class HubBalancer implements Runnable, Listener {
    private static final String NO_AVAILABLE_HUB = "&2&lMc&6&lGamer&c&lZone &8» &7There are no available lobbies found!";
    private static final String HUB_SEND_FAILED = "&2&lMc&6&lGamer&c&lZone &8» &cAn error occured while sending you to a hub!";

    private final Proxy proxy;
    private ServerGroupRepository serverGroupRepository;
    private MinecraftServerRepository minecraftServerRepository;
    private final Map<String, ServerInfo> hubs = new HashMap<>();

    private ServerGroup group;

    public HubBalancer(Proxy proxy) {
        this.proxy = proxy;
        RedisRepository.getRepository(ServerGroupRepository.class).ifPresent(repository -> serverGroupRepository = repository);
        RedisRepository.getRepository(MinecraftServerRepository.class).ifPresent(repository -> minecraftServerRepository = repository);
        proxy.getProxy().getScheduler().schedule(proxy, this, 1000L, 350L, TimeUnit.MILLISECONDS);
        proxy.getProxy().getPluginManager().registerListener(proxy, this);

        JedisCommandHandler.getInstance().addListener(jedisCommand -> {
            if (jedisCommand instanceof ServerRestartCommand) {
                ServerRestartCommand serverRestartCommand = (ServerRestartCommand) jedisCommand;
                removeServer(serverRestartCommand.getServerId());
                ServerInfo serverInfo = proxy.getProxy().getServerInfo(serverRestartCommand.getServerId());
                if (serverInfo == null) {
                    System.out.println("ServerInfo is null");
                    //We do this check due sometimes players stay in a server which is deleted. But were send to it before it was deleted.
                    for (ProxiedPlayer player : proxy.getProxy().getPlayers()) {
                        if (player.getServer() == null && (hubs.size() == 0))
                            kickPlayer(player, NO_AVAILABLE_HUB);
                    }
                    return;
                } else {
                    for (ProxiedPlayer player : proxy.getProxy().getPlayers()) {
                        if (player.getServer().getInfo().equals(serverInfo)) {
                            if (hubs.isEmpty())
                                kickPlayer(player, NO_AVAILABLE_HUB);
                        }
                    }
                }
            }
            if (jedisCommand instanceof ServerStateChangeCommand) {
                ServerStateChangeCommand serverStateChangeCommand = (ServerStateChangeCommand) jedisCommand;
                System.out.println("Received update status from server " + serverStateChangeCommand.getServer().getId() + " status: " + serverStateChangeCommand.getNewState());
                if (serverStateChangeCommand.getNewState().equals(ServerState.RUNNING))
                    registerServer(serverStateChangeCommand.getServer());
                if (!serverStateChangeCommand.getNewState().isShuttingDownState())
                    return;
                MinecraftServer server = serverStateChangeCommand.getServer();
                removeServer(server.getId());
            }
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(ServerConnectEvent event) {
        if (event.getReason() != ServerConnectEvent.Reason.JOIN_PROXY)
            return;
        if (group == null) // If the server group has yet to be pulled from the repository, return
            return;
        ProxiedPlayer player = event.getPlayer();
        ServerConnectEvent.Reason reason = event.getReason();

        // Debugging
        System.out.println("reason = " + reason.name() + ", hubs = " + hubs.size());

        // If the player is joining the proxy, and the target server is the name of the server group, we wanna
        // find a hub for them to connect to
        if (reason == ServerConnectEvent.Reason.JOIN_PROXY && event.getTarget().getName().equals(group.getName())) {
            ServerInfo serverInfo = sendToHub(player);
            if (serverInfo == null) {
                kickPlayer(player, HUB_SEND_FAILED);
                event.setCancelled(true);
            } else event.setTarget(serverInfo);
        } else if (reason == ServerConnectEvent.Reason.LOBBY_FALLBACK || (event.getTarget().isRestricted())) {
            kickPlayer(player, NO_AVAILABLE_HUB);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onKick(ServerKickEvent event) {
        System.out.println(event.getCause());
        System.out.println(event.getKickReason());
        System.out.println(event.getKickedFrom().getName());
        ProxiedPlayer player = event.getPlayer();
        if (player.getServer() == null)
            return;
        ServerInfo serverInfo = sendToHub(player);
        if (serverInfo == null) {
            kickPlayer(player, HUB_SEND_FAILED + "\n&f" + TextComponent.toLegacyText(event.getKickReasonComponent()));
            event.setCancelled(true);
            return;
        }
        event.setCancelServer(serverInfo);
        event.setCancelled(true);
        sendKickMessage(player, event.getKickedFrom(), TextComponent.toLegacyText(event.getKickReasonComponent()));
    }

    @Override
    public void run() {
        Map<String, ServerInfo> serversMap = proxy.getProxy().getServersCopy();
        // If the group is null, we wanna get the group name from the default Bungeecord configuration file.
        // The default server for BungeeCord is the name of the server group to use for fallback servers
        if (group == null)
            group = serverGroupRepository.lookup(proxy.getDefaultServer().getName()).orElse(null);
        if (group == null)
            return;
        // Adding new Minecraft servers to the proxy
        for (MinecraftServer server : minecraftServerRepository.getCached()) {
            if (server.isRunning() && serversMap.get(server.getId()) == null) {
                registerServer(server);
            }
        }
        // Removing dead Minecraft servers from the proxy
        proxy.getProxy().getServers().entrySet().removeIf(entry -> {
            String key = entry.getKey();
            if (key.equals(group.getName()))
                return false;
            if (entry.getValue().getMotd().equals("STATIC"))
                return false;
            Optional<MinecraftServer> optionalMinecraftServer = minecraftServerRepository.getCached().stream()
                    .filter(server -> server.getId().equals(key))
                    .findFirst();
            if (optionalMinecraftServer.isEmpty()) {
                hubs.remove(key);
                return true;
            }
            MinecraftServer minecraftServer = optionalMinecraftServer.get();
            if (!minecraftServer.isRunning()) {
                hubs.remove(minecraftServer.getId());
                return true;
            }
            return false;
        });
    }

    private ServerInfo sendToHub(ProxiedPlayer player) {
        if (hubs.isEmpty()) { // If there are no hubs available, deny the connection and show an error to the connecting player
            kickPlayer(player, NO_AVAILABLE_HUB);
            return null;
        }
        ServerInfo target = null;
        if (hubs.size() == 1) // If there is only 1 hub server, there's no need to balance
            target = new ArrayList<>(hubs.values()).get(0);
        else {
            // Finding the best possible hub to connect the player to. This is done by getting the server with
            // the least amount of players online
            List<Tuple<MinecraftServer, Integer>> playerCounts = new ArrayList<>();
            hubs.values().stream().map(serverInfo -> {
                Optional<MinecraftServer> optionalMinecraftServer = group.getServers().stream()
                        .filter(server -> server.getId().equals(serverInfo.getName()))
                        .findFirst();
                return optionalMinecraftServer.orElse(null);
            }).filter(Objects::nonNull)
                    .sorted(Comparator.comparingInt(MinecraftServer::getOnline))
                    .forEach(minecraftServer -> playerCounts.add(new Tuple<>(minecraftServer, minecraftServer.getOnline())));
            if (!playerCounts.isEmpty())
                target = hubs.get(playerCounts.get(0).getLeft().getId());
        }
        if (target != null) {
            if (player.getServer() != null && (player.getServer().getInfo().equals(target))) {
                kickPlayer(player, NO_AVAILABLE_HUB);
                return null;
            }
            System.out.println("Sending " + player.getName() + " to server \"" + target.getName() + "\"");
            return target;
        }
        System.err.println("Cannot find a server to send " + player.getName() + " to!");
        kickPlayer(player, NO_AVAILABLE_HUB);
        return null;
    }

    private void registerServer(MinecraftServer minecraftServer) {
        ServerInfo serverInfo = proxy.getProxy().constructServerInfo(minecraftServer.getId(),
                new InetSocketAddress(group.getPrivateAddress(), (int) minecraftServer.getPort()), "A MGZ Server", false);
        proxy.getProxy().getServers().put(minecraftServer.getId(), serverInfo);
        if (group.getServers().contains(minecraftServer))
            hubs.put(minecraftServer.getId(), serverInfo);
    }

    private void kickPlayer(ProxiedPlayer player, String string) {
        player.disconnect(TextComponent.fromLegacyText(
                ChatColor.translateAlternateColorCodes('&', string)));
    }

    //This just only sends the kick player when player is moved back to lobby.
    private void sendKickMessage(ProxiedPlayer player, ServerInfo server, String reason) {
        player.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',
                "&7\n" +
                        "&c\u27A2 Unexpected? Contact an administrator!\n" +
                        "&7You have been disconnected from " + (server == null ? "" : "from §6" + server.getName() + "§7 ") + " &7server for\n" +
                        "&b" + reason + "\n" +
                        "&7")));
    }

    private void removeServer(String serverId) {
        proxy.getProxy().getServers().entrySet().removeIf(entry -> {
            String key = entry.getKey();
            if (!key.equals(serverId))
                return false;
            if (key.equals(group.getName()))
                return false;
            if (entry.getValue().getMotd().equals("STATIC"))
                return false;
            hubs.remove(serverId);
            return true;
        });
    }
}