package zone.themcgamer.proxy.player;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import zone.themcgamer.common.MiscUtils;
import zone.themcgamer.data.jedis.cache.CacheRepository;
import zone.themcgamer.data.jedis.cache.impl.PlayerStatusCache;
import zone.themcgamer.data.jedis.command.JedisCommandHandler;
import zone.themcgamer.data.jedis.command.impl.*;
import zone.themcgamer.data.jedis.data.server.MinecraftServer;
import zone.themcgamer.data.jedis.repository.RedisRepository;
import zone.themcgamer.data.jedis.repository.impl.MinecraftServerRepository;
import zone.themcgamer.proxy.Proxy;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Braydon
 */
public class PlayerHandler implements Listener {
    private final Proxy proxy;
    private CacheRepository cacheRepository;

    public PlayerHandler(Proxy proxy) {
        this.proxy = proxy;
        proxy.getProxy().getPluginManager().registerListener(proxy, this);

        Optional<CacheRepository> optionalCacheRepository = RedisRepository.getRepository(CacheRepository.class);
        optionalCacheRepository.ifPresent(repository -> cacheRepository = repository);

        Optional<MinecraftServerRepository> optionalServerRepository = RedisRepository.getRepository(MinecraftServerRepository.class);
        if (optionalServerRepository.isEmpty())
            return;
        MinecraftServerRepository serverRepository = optionalServerRepository.get();
        JedisCommandHandler.getInstance().addListener(jedisCommand -> {
            if (jedisCommand instanceof ServerSendCommand) {
                ServerSendCommand serverSendCommand = (ServerSendCommand) jedisCommand;
                ProxiedPlayer player = proxy.getProxy().getPlayer(serverSendCommand.getPlayerName());
                if (player == null)
                    return;
                Optional<MinecraftServer> optionalMinecraftServer = serverRepository.lookup(serverSendCommand.getServerId());
                if (optionalMinecraftServer.isEmpty())
                    return;
                MinecraftServer minecraftServer = optionalMinecraftServer.get();
                ServerInfo serverInfo = proxy.getProxy().getServersCopy().get(minecraftServer.getId());
                if (serverInfo == null)
                    return;
                player.connect(serverInfo);
            } else if (jedisCommand instanceof PlayerKickCommand) {
                PlayerKickCommand kickCommand = (PlayerKickCommand) jedisCommand;
                ProxiedPlayer player = proxy.getProxy().getPlayer(kickCommand.getUuid());
                if (player != null)
                    player.disconnect(TextComponent.fromLegacyText(kickCommand.getReason()));
            } else if (jedisCommand instanceof PlayerMessageCommand) {
                PlayerMessageCommand playerMessageCommand = (PlayerMessageCommand) jedisCommand;
                ProxiedPlayer player = proxy.getProxy().getPlayer(playerMessageCommand.getUuid());
                if (player != null)
                    player.sendMessage(TextComponent.fromLegacyText(playerMessageCommand.getMessage()));
            }
        });

        proxy.getProxy().getScheduler().schedule(proxy, () -> {
            int online = Math.toIntExact(cacheRepository.getCached().stream().filter(cacheItem -> cacheItem instanceof PlayerStatusCache).count());
            for (ProxiedPlayer player : proxy.getProxy().getPlayers()) {
                Optional<PlayerStatusCache> optionalPlayerStatusCache = cacheRepository.lookup(PlayerStatusCache.class, player.getUniqueId());
                if (optionalPlayerStatusCache.isEmpty()) {
                    System.err.println("Cannot remove player status for " + player.getName() + ", it does not exist");
                    continue;
                }
                MinecraftServer server = serverRepository.lookup(optionalPlayerStatusCache.get().getServer()).orElse(null);
                if (server == null)
                    continue;
                String header = MiscUtils.arrayToString(proxy.getProxyData().getTablist().getHeader()
                        .replace("{n}", "\n")
                        .replace("{server}", server.getName())
                        .replace("{ping}", String.valueOf(player.getPing()))
                        .replace("{online}", String.valueOf(server.getOnline()))
                        .replace("{gonline}", String.valueOf(online))
                        .replace("{gonlinesub}", (online == 1 ? "" : "s"))
                        .replace("{playername}", player.getName())
                        .replace("{ip}", String.valueOf(player.getPendingConnection().getVirtualHost().getHostName())));
                String footer = MiscUtils.arrayToString(proxy.getProxyData().getTablist().getFooter()
                        .replace("{n}", "\n")
                        .replace("{server}", server.getName())
                        .replace("{ping}", String.valueOf(player.getPing()))
                        .replace("{online}", String.valueOf(server.getOnline()))
                        .replace("{gonline}", String.valueOf(online))
                        .replace("{gonlinesub}", (online == 1 ? "" : "s"))
                        .replace("{playername}", player.getName())
                        .replace("{ip}", String.valueOf(player.getPendingConnection().getVirtualHost().getHostName())));
                player.setTabHeader(TextComponent.fromLegacyText(header),
                        TextComponent.fromLegacyText(footer));
            }
        }, 2, 2, TimeUnit.SECONDS);
    }

    @EventHandler
    public void onHandshake(PreLoginEvent event) {
        ProxiedPlayer player = proxy.getProxy().getPlayer(event.getConnection().getName());
        // If the player is already connected to the proxy, disallow login.
        // This prevents a problem with account loading on actual servers as
        // the server would load the new account for the new connection, and
        // then unload the old account, therefore resulting in the player having
        // an unloaded account
        if (player != null) {
            event.setCancelReason(TextComponent.fromLegacyText("§cYou're already connected to this server!"));
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerConnect(ServerConnectEvent event) {
        ServerConnectEvent.Reason reason = event.getReason();
        if (reason == ServerConnectEvent.Reason.UNKNOWN)
            return;
        ProxiedPlayer player = event.getPlayer();
        // If the player is joining the proxy, we wanna call the network connect Redis command
        if (reason == ServerConnectEvent.Reason.JOIN_PROXY)
            JedisCommandHandler.getInstance().send(new NetworkConnectCommand(player.getUniqueId(), player.getName(), System.currentTimeMillis()));
        ServerInfo target = event.getTarget();
        if (target == null)
            return;
        // If the player is connecting to a server with the same name as the default server group, return
        if (target.getName().equalsIgnoreCase(proxy.getDefaultServer().getName()))
            return;
        if (cacheRepository == null)
            return;
        PlayerStatusCache statusCache = cacheRepository.lookup(PlayerStatusCache.class, player.getUniqueId()).orElse(null);
        if (statusCache == null) // If the player's status isn't cached, then create the cache object
            statusCache = new PlayerStatusCache(player.getUniqueId(), player.getName(), target.getName(), "", System.currentTimeMillis());
        else { // If the player's status is cached, update the player's name and location
            statusCache.setPlayerName(player.getName());
            statusCache.setServer(target.getName());
        }
        cacheRepository.post(statusCache); // Publish the status cache object to Redis
        System.out.println("Posted new player status for " + player.getName() + " (" + statusCache.toString() + ")");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        JedisCommandHandler.getInstance().send(new NetworkDisconnectCommand(player.getUniqueId(), player.getName(), System.currentTimeMillis()));
        if (cacheRepository == null)
            return;
        Optional<PlayerStatusCache> optionalPlayerStatusCache = cacheRepository.lookup(PlayerStatusCache.class, player.getUniqueId());
        if (optionalPlayerStatusCache.isEmpty()) {
            System.err.println("Cannot remove player status for " + player.getName() + ", it does not exist");
            return;
        }
        PlayerStatusCache statusCache = optionalPlayerStatusCache.get();
        System.out.println("Removing player status for " + player.getName() + " (" + statusCache.toString() + ")");
        cacheRepository.remove(statusCache);
    }
}