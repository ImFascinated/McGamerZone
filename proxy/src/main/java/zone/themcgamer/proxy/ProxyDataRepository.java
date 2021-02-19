package zone.themcgamer.proxy;

import zone.themcgamer.data.jedis.JedisController;
import zone.themcgamer.data.jedis.repository.RedisRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Braydon
 */
public class ProxyDataRepository extends RedisRepository<String, ProxyData> {
    public ProxyDataRepository(JedisController controller) {
        super(controller, "proxy");
    }

    @Override
    public Optional<ProxyData> lookup(String name) {
        return Optional.empty();
    }

    @Override
    public String getKey(ProxyData proxyData) {
        return "proxy";
    }

    @Override
    public Optional<ProxyData> fromMap(Map<String, String> map) {
        return Optional.of(new ProxyData(
                new ProxyData.MOTD(map.get("motd.header"), map.get("motd.text")),
                Boolean.parseBoolean(map.get("maintenance")),
                new ProxyData.TABLIST(map.get("tablist.header"), map.get("tablist.footer"))
        ));
    }

    @Override
    public long getExpiration(ProxyData proxyData) {
        return -1;
    }

    @Override
    public Map<String, Object> toMap(ProxyData proxyData) {
        Map<String, Object> data = new HashMap<>();
        data.put("motd.header", proxyData.getMotd().getHeader());
        data.put("motd.text", proxyData.getMotd().getText());
        data.put("maintenance", proxyData.isMaintenance());
        data.put("tablist.header", proxyData.getTablist().getHeader());
        data.put("tablist.footer", proxyData.getTablist().getFooter());
        return data;
    }
}