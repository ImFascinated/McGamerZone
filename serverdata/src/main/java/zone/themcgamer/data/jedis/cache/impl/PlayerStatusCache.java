package zone.themcgamer.data.jedis.cache.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zone.themcgamer.data.jedis.cache.ICacheItem;
import zone.themcgamer.data.jedis.cache.ItemCacheType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Braydon
 * @implNote This cache object is stored for each player in Redis when a connection is made to the network.
 *           Unlike {@link PlayerCache}, this object is removed when a player disconnects from the network.
 *           The purpose of this object is to easily get a player's name, the server they're on, and the
 *           time they connected to the network
 */
@AllArgsConstructor @Setter @NoArgsConstructor @Getter
public class PlayerStatusCache implements ICacheItem<UUID> {
    private UUID uuid;
    private String playerName, server, lastReply;
    private long timeJoined;

    @Override
    public ItemCacheType getType() {
        return ItemCacheType.PLAYER_STATUS;
    }

    @Override
    public UUID getIdentifier() {
        return uuid;
    }

    @Override
    public void fromData(String key, Map<String, String> data) {
        uuid = UUID.fromString(key);
        playerName = data.get("playerName");
        server = data.get("server");
        lastReply = data.get("lastReply");
        timeJoined = Long.parseLong(data.get("timeJoined"));
    }

    @Override
    public Map<String, Object> toData() {
        Map<String, Object> data = new HashMap<>();
        data.put("playerName", playerName);
        data.put("server", server);
        data.put("lastReply", lastReply);
        data.put("timeJoined", timeJoined);
        return data;
    }

    @Override
    public String toString() {
        return "PlayerStatusCache{" +
                "uuid=" + uuid +
                ", playerName='" + playerName + '\'' +
                ", server='" + server + '\'' +
                ", lastReply='" + lastReply + '\'' +
                ", timeJoined=" + timeJoined +
                '}';
    }
}