package zone.themcgamer.data.jedis.cache;

import redis.clients.jedis.Jedis;
import zone.themcgamer.data.jedis.JedisConstants;
import zone.themcgamer.data.jedis.JedisController;
import zone.themcgamer.data.jedis.repository.RedisRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Braydon
 * @implNote This class serves the purpose of fetching all {@link ICacheItem}'s
 *           from Redis
 */
public class CacheRepository extends RedisRepository<String, ICacheItem<?>> {
    public CacheRepository(JedisController controller) {
        super(controller);
    }

    @Override
    public Optional<ICacheItem<?>> lookup(String name) {
        return getCached().stream().filter(cacheItem -> cacheItem.getIdentifier().toString().equalsIgnoreCase(name)).findFirst();
    }

    @Override
    public String getKey(ICacheItem<?> cacheItem) {
        return cacheItem.getType().getIdentifier() + ":" + cacheItem.getIdentifier().toString();
    }

    @Override
    public Optional<ICacheItem<?>> fromMap(Map<String, String> map) {
        return Optional.empty();
    }

    @Override
    public long getExpiration(ICacheItem<?> cacheItem) {
        return cacheItem.getType().getDuration();
    }

    @Override
    public Map<String, Object> toMap(ICacheItem<?> cacheItem) {
        return cacheItem.toData();
    }

    @Override
    protected void updateCache() {
        List<ICacheItem<?>> cached = new ArrayList<>();
        try (Jedis jedis = getController().getPool().getResource()) {
            jedis.auth(JedisConstants.AUTH);
            for (ItemCacheType cacheType : ItemCacheType.values()) {
                for (String key : jedis.keys(cacheType.getIdentifier() + ":*")) {
                    Map<String, String> map = jedis.hgetAll(key);
                    if (map.isEmpty()) {
                        jedis.del(key);
                        continue;
                    }
                    try {
                        ICacheItem<?> item = cacheType.getClazz().newInstance();
                        item.fromData(key.split(":")[1], map);
                        cached.add(item);
                    } catch (Exception ex) {
                        System.err.println("Failed to update item with key '" + key + "':");
                        ex.printStackTrace();
                    }
                }
            }
        }
        this.cached = cached;
        for (Consumer<List<ICacheItem<?>>> updateListener : getUpdateListeners())
            updateListener.accept(cached);
    }

    public List<ICacheItem<?>> filter(Predicate<ICacheItem<?>> predicate) {
        return getCached().stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * Find a {@link ICacheItem} by the given {@link Class} and identifier
     * @param clazz the class of the cache item to lookup
     * @param identifier the identifier of the cache item
     * @return the optional cache item
     */
    public <T extends ICacheItem<?>> Optional<T> lookup(Class<T> clazz, Object identifier) {
        return lookup(clazz, cacheItem -> cacheItem.getIdentifier().equals(identifier));
    }

    /**
     * Find a {@link ICacheItem} by the given {@link Class} and test against the {@link Predicate}
     * @param clazz the class of the cache item to lookup
     * @param predicate the predicate to test against
     * @return the optional cache item
     */
    public <T extends ICacheItem<?>> Optional<T> lookup(Class<T> clazz, Predicate<T> predicate) {
        return getCached().stream()
                .filter(cacheItem -> cacheItem.getClass().equals(clazz) && (predicate.test((T) cacheItem)))
                .findFirst().map(clazz::cast);
    }

    /**
     * Find a {@link ICacheItem} by the given {@link Class} and identifier and remove it from Redis
     * @param clazz the class of the cache item to remove
     * @param identifier the identifier of the cache item
     */
    public void remove(Class<? extends ICacheItem<?>> clazz, Object identifier) {
        lookup(clazz, identifier).ifPresent(this::remove);
    }
}