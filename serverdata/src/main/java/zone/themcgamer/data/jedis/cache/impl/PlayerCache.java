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
 *           This object stores things such as the player's name, and MySQL account id for quick lookups
 *           and queries
 */
@AllArgsConstructor @Setter @NoArgsConstructor @Getter
public class PlayerCache implements ICacheItem<UUID> {
    private UUID uuid;
    private String name;
    private int accountId;

    @Override
    public ItemCacheType getType() {
        return ItemCacheType.PLAYER;
    }

    @Override
    public UUID getIdentifier() {
        return uuid;
    }

    @Override
    public void fromData(String key, Map<String, String> data) {
        uuid = UUID.fromString(key);
        name = data.get("name");
        accountId = Integer.parseInt(data.get("accountId"));
    }

    @Override
    public Map<String, Object> toData() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("accountId", accountId);
        return data;
    }

    @Override
    public String toString() {
        return "PlayerCache{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", accountId=" + accountId +
                '}';
    }
}