package zone.themcgamer.api.model.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import zone.themcgamer.api.model.IModel;
import zone.themcgamer.data.jedis.data.server.MinecraftServer;
import zone.themcgamer.data.jedis.data.server.ServerState;

import java.util.HashMap;
import java.util.UUID;

/**
 * @author Braydon
 */
@AllArgsConstructor @Setter @Getter @ToString
public class MinecraftServerModel implements IModel {
    private final String id;
    private final int numericId;
    private final String name;
    private final NodeModel node;
    private final ServerGroupModel group;
    private final String address;
    private final long port;

    private final int usedRam, maxRam;
    private final ServerState state;
    private final long lastStateChange;

    private final int online, maxPlayers;
    private final double tps;
    private final UUID host;
    private final String game;

    private final String metaData;
    private final long created, lastHeartbeat;

    @Override
    public HashMap<String, Object> toMap() {
        return new HashMap<>() {{
            put("id", id);
            put("numericId", numericId);
            put("name", name);
            put("node", node == null ? null : node.toMap());
            put("group", group);
            put("address", address);
            put("port", port);
            put("usedRam", usedRam);
            put("maxRam", maxRam);
            put("state", state.name());
            put("lastStateChange", lastStateChange);
            put("online", online);
            put("maxPlayers", maxPlayers);
            put("tps", tps);
            put("host", host);
            put("game", game);
            put("metaData", metaData);
            put("lastHeartbeat", lastHeartbeat);
        }};
    }

    public static MinecraftServerModel fromMinecraftServer(MinecraftServer minecraftServer) {
        if (minecraftServer == null)
            return null;
        return new MinecraftServerModel(
                minecraftServer.getId(),
                minecraftServer.getNumericId(),
                minecraftServer.getName(),
                NodeModel.fromNode(minecraftServer.getNode()),
                ServerGroupModel.fromServerGroup(minecraftServer.getGroup()),
                minecraftServer.getAddress(),
                minecraftServer.getPort(),

                minecraftServer.getUsedRam(),
                minecraftServer.getMaxRam(),
                minecraftServer.getState(),
                minecraftServer.getLastStateChange(),

                minecraftServer.getOnline(),
                minecraftServer.getMaxPlayers(),
                minecraftServer.getTps(),
                minecraftServer.getHost(),
                minecraftServer.getGame(),
                minecraftServer.getMetaData(),
                minecraftServer.getCreated(),
                minecraftServer.getLastHeartbeat()
        );
    }
}