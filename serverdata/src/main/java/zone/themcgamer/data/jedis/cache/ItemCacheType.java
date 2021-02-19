package zone.themcgamer.data.jedis.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import zone.themcgamer.data.jedis.cache.impl.PlayerCache;
import zone.themcgamer.data.jedis.cache.impl.PlayerStatusCache;

import java.util.concurrent.TimeUnit;

/**
 * @author Braydon
 */
@AllArgsConstructor @Getter
public enum ItemCacheType {
    PLAYER("player", TimeUnit.HOURS.toMillis(24L), PlayerCache.class),
    PLAYER_STATUS("playerStatus", -1L, PlayerStatusCache.class);

    private final String identifier;
    private final long duration;
    private final Class<? extends ICacheItem<?>> clazz;
}