package zone.themcgamer.data.jedis.repository.impl;

import zone.themcgamer.common.MiscUtils;
import zone.themcgamer.data.jedis.JedisController;
import zone.themcgamer.data.jedis.data.ServerGroup;
import zone.themcgamer.data.jedis.repository.RedisRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Braydon
 * @implNote This class serves the purpose of fetching all {@link ServerGroup}'s
 *           from Redis
 */
public class ServerGroupRepository extends RedisRepository<String, ServerGroup> {
    public ServerGroupRepository(JedisController controller) {
        super(controller, "serverGroup:*");
    }

    @Override
    public Optional<ServerGroup> lookup(String name) {
        return getCached().stream().filter(group -> group.getName().equalsIgnoreCase(name)).findFirst();
    }

    @Override
    public String getKey(ServerGroup serverGroup) {
        return "serverGroup:" + serverGroup.getName();
    }

    @Override
    public Optional<ServerGroup> fromMap(Map<String, String> map) {
        return Optional.of(new ServerGroup(
                map.get("name"),
                Long.parseLong(map.get("memoryPerServer")),
                map.get("serverJar"),
                map.get("templatePath"),
                map.get("pluginJarName"),
                map.get("worldPath"),
                map.get("startupScript"),
                map.get("privateAddress"),
                MiscUtils.getUuid(map.get("host")),
                map.get("game"),
                Integer.parseInt(map.get("minPlayers")),
                Integer.parseInt(map.get("maxPlayers")),
                Integer.parseInt(map.get("minServers")),
                Integer.parseInt(map.get("maxServers")),
                Boolean.parseBoolean(map.get("kingdom")),
                Boolean.parseBoolean(map.get("static"))
        ));
    }

    @Override
    public long getExpiration(ServerGroup serverGroup) {
        return -1;
    }

    @Override
    public Map<String, Object> toMap(ServerGroup serverGroup) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", serverGroup.getName());
        data.put("memoryPerServer", serverGroup.getMemoryPerServer());
        data.put("serverJar", serverGroup.getServerJar());
        data.put("templatePath", serverGroup.getTemplatePath());
        data.put("pluginJarName", serverGroup.getPluginJarName());
        data.put("worldPath", serverGroup.getWorldPath());
        data.put("startupScript", serverGroup.getStartupScript());
        data.put("privateAddress", serverGroup.getPrivateAddress());
        data.put("host", serverGroup.getHost() == null ? "" : serverGroup.getHost().toString());
        data.put("game", serverGroup.getGame());
        data.put("minPlayers", serverGroup.getMinPlayers());
        data.put("maxPlayers", serverGroup.getMaxPlayers());
        data.put("minServers", serverGroup.getMinServers());
        data.put("maxServers", serverGroup.getMaxServers());
        data.put("kingdom", serverGroup.isKingdom());
        data.put("static", serverGroup.isStaticGroup());
        return data;
    }
}