package zone.themcgamer.proxy.motd;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import zone.themcgamer.proxy.Proxy;
import zone.themcgamer.proxy.ProxyData;

/**
 * @author Braydon
 */
public class MOTDHandler implements Listener {
    private static final String DEFAULT_HEADER = "§2§lMc§6§lGamer§c§lZone";

    private final Proxy proxy;

    public MOTDHandler(Proxy proxy) {
        this.proxy = proxy;
        proxy.getProxy().getPluginManager().registerListener(proxy, this);
    }

    @EventHandler
    public void onPing(ProxyPingEvent event) {
        ProxyData data = proxy.getProxyData();
        ServerPing response = event.getResponse();

        // MOTD
        if (data == null)
            response.setDescriptionComponent(new TextComponent(DEFAULT_HEADER));
        else response.setDescriptionComponent(new TextComponent(ChatColor.translateAlternateColorCodes('&', data.getMotd().getHeader() + "\n" + data.getMotd().getText())));

        // Maintenance Display
        if (data != null && (data.isMaintenance()))
            response.setVersion(new ServerPing.Protocol("§4Maintenance", -1));

        event.setResponse(response);
    }
}