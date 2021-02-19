package zone.themcgamer.data.jedis.repository.impl;

import zone.themcgamer.common.MiscUtils;
import zone.themcgamer.data.jedis.JedisController;
import zone.themcgamer.data.jedis.data.ServerGroup;
import zone.themcgamer.data.jedis.data.server.MinecraftServer;
import zone.themcgamer.data.jedis.data.server.ServerState;
import zone.themcgamer.data.jedis.repository.RedisRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Braydon
 * @implNote This class serves the purpose of fetching all {@link MinecraftServer}'s
 *           from Redis
 */
public class MinecraftServerRepository extends RedisRepository<String, MinecraftServer> {
    public MinecraftServerRepository(JedisController controller) {
        super(controller, "minecraftServer:*");
    }

    @Override
    public Optional<MinecraftServer> lookup(String id) {
        return getCached().stream().filter(minecraftServer -> minecraftServer.getId().equalsIgnoreCase(id)).findFirst();
    }

    @Override
    public String getKey(MinecraftServer minecraftServer) {
        return "minecraftServer:" + minecraftServer.getId();
    }

    @Override
    public Optional<MinecraftServer> fromMap(Map<String, String> map) {
        Optional<NodeRepository> optionalNodeRepository = RedisRepository.getRepository(NodeRepository.class);
        if (optionalNodeRepository.isEmpty())
            return Optional.empty();
        Optional<ServerGroupRepository> serverGroupRepository = RedisRepository.getRepository(ServerGroupRepository.class);
        if (serverGroupRepository.isEmpty())
            return Optional.empty();
        Optional<ServerGroup> optionalServerGroup = serverGroupRepository.get().lookup(map.get("group"));
        return optionalServerGroup.map(serverGroup -> new MinecraftServer(
                map.get("id"),
                Integer.parseInt(map.get("numericId")),
                map.get("name"),
                optionalNodeRepository.get().lookup(map.get("node")).orElse(null),
                serverGroup,
                map.get("address"),
                Long.parseLong(map.get("port")),
                Integer.parseInt(map.get("usedRam")),
                Integer.parseInt(map.get("maxRam")),
                ServerState.valueOf(map.get("state")),
                Long.parseLong(map.get("lastStateChange")),
                Integer.parseInt(map.get("online")),
                Integer.parseInt(map.get("maxPlayers")),
                Double.parseDouble(map.get("tps")),
                MiscUtils.getUuid(map.get("host")),
                map.get("game"),
                map.get("metadata"),
                Long.parseLong(map.get("created")),
                Long.parseLong(map.get("lastHeartbeat"))
        ));
    }

    @Override
    public long getExpiration(MinecraftServer minecraftServer) {
        return -1;
    }

    @Override
    public Map<String, Object> toMap(MinecraftServer minecraftServer) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", minecraftServer.getId());
        data.put("numericId", minecraftServer.getNumericId());
        data.put("name", minecraftServer.getName());
        data.put("node", minecraftServer.getNode() == null ? "" : minecraftServer.getNode().getName());
        data.put("group", minecraftServer.getGroup().getName());
        data.put("address", minecraftServer.getAddress());
        data.put("port", minecraftServer.getPort());
        data.put("usedRam", minecraftServer.getUsedRam());
        data.put("maxRam", minecraftServer.getMaxRam());
        data.put("state", minecraftServer.getState().name());
        data.put("lastStateChange", minecraftServer.getLastStateChange());
        data.put("online", minecraftServer.getOnline());
        data.put("maxPlayers", minecraftServer.getMaxPlayers());
        data.put("tps", minecraftServer.getTps());
        data.put("host", minecraftServer.getHost() == null ? "" : minecraftServer.getHost().toString());
        data.put("game", minecraftServer.getGame());
        data.put("metadata", minecraftServer.getMetaData());
        data.put("created", minecraftServer.getCreated());
        data.put("lastHeartbeat", minecraftServer.getLastHeartbeat());
        return data;
    }
}