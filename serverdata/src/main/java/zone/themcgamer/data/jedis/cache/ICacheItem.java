package zone.themcgamer.data.jedis.cache;

import java.util.Map;

/**
 * @author Braydon
 */
public interface ICacheItem<T> {
    ItemCacheType getType();

    T getIdentifier();

    void fromData(String key, Map<String, String> data);

    Map<String, Object> toData();

    String toString();
}