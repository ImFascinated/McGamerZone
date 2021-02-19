package zone.themcgamer.proxy;

import lombok.Getter;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import zone.themcgamer.data.jedis.JedisController;
import zone.themcgamer.proxy.hub.HubBalancer;
import zone.themcgamer.proxy.motd.MOTDHandler;
import zone.themcgamer.proxy.player.PlayerHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Braydon
 */
@Getter
public class Proxy extends Plugin {
    private ServerInfo defaultServer;
    private ProxyData proxyData;

    @Override
    public void onEnable() {
        // Setting the default server
        Map<String, ServerInfo> serversMap = getProxy().getServersCopy();
        if (!serversMap.isEmpty()) {
            defaultServer = new ArrayList<>(serversMap.values()).get(0);
            System.out.println("defaultServer = " + defaultServer.getName());
        } else {
            System.err.println("Cannot find default server");
            getProxy().stop();
            return;
        }
        // Initializing Redis
        JedisController controller = new JedisController();
        ProxyDataRepository repository = new ProxyDataRepository(controller);
        controller.start();
        getProxy().getScheduler().schedule(this, () -> {
            List<ProxyData> cached = repository.getCached();
            if (cached.isEmpty())
                proxyData = null;
            else proxyData = cached.get(0);
        }, 2L, 30L, TimeUnit.SECONDS);

        // Initializing Modules
        new MOTDHandler(this);
        new PlayerHandler(this);
        new HubBalancer(this);
    }
}