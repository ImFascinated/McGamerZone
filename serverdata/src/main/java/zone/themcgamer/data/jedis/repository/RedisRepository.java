package zone.themcgamer.data.jedis.repository;

import lombok.Getter;
import redis.clients.jedis.Jedis;
import zone.themcgamer.data.jedis.JedisConstants;
import zone.themcgamer.data.jedis.JedisController;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Braydon
 */
@Getter
public abstract class RedisRepository<I, T> {
    @Getter private static final Set<RedisRepository<?, ?>> repositories = new HashSet<>();

    private final JedisController controller;
    private final String pattern;
    protected List<T> cached = new ArrayList<>();
    private final Collection<Consumer<List<T>>> updateListeners = new ArrayList<>();

    public RedisRepository(JedisController controller) {
        this(controller, null);
    }

    public RedisRepository(JedisController controller, String pattern) {
        this.controller = controller;
        this.pattern = pattern;
        repositories.add(this);
    }

    /**
     * Lookup a {@link T} object by {@link I}
     *
     * @param i The object to use to do the lookup
     * @return optional {@link T}
     */
    public abstract Optional<T> lookup(I i);

    /**
     * Get the Redis key for the given {@link T} object
     *
     * @param t The object to get the key for
     * @return the key
     */
    public abstract String getKey(T t);

    /**
     * Create a new {@link T} instance from the given map
     *
     * @param map The map
     * @return the new instance
     */
    public abstract Optional<T> fromMap(Map<String, String> map);

    /**
     * Get how long a key should be stored
     *
     * @return the expiration time, -1 if none
     */
    public abstract long getExpiration(T t);

    /**
     * Create a new map for the given {@link T} object
     *
     * @param t The object`
     * @return the map
     */
    public abstract Map<String, Object> toMap(T t);

    /**
     * Get the cached value for the repository
     *
     * @return the cached values
     */
    public List<T> getCached() {
        return new ArrayList<>(cached);
    }

    /**
     * Lookup a {@link T} object that tests against the {@link Predicate}
     *
     * @param predicate The predicate to test
     * @return optional {@link T}
     */
    public Optional<T> lookup(Predicate<T> predicate) {
        return cached.stream().filter(predicate).findFirst();
    }

    /**
     * Add an update listener
     *
     * @param consumer the consumer to add
     */
    public void addUpdateListener(Consumer<List<T>> consumer) {
        updateListeners.add(consumer);
    }

    /**
     * Adds the given {@link T} object to the local cache and to Redis
     *
     * @param t The object to add
     */
    public void post(T t) {
        cached.add(t);
        String key = getKey(t);
        if (key == null || (key.isEmpty()))
            throw new IllegalArgumentException("Cannot post, the key is null or empty: \"" + (key == null ? "null" : key) + "\"");
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, Object> entry : toMap(t).entrySet())
            map.put(entry.getKey(), entry.getValue().toString());
        try (Jedis jedis = controller.getPool().getResource()) {
            jedis.auth(JedisConstants.AUTH);
            jedis.select(JedisConstants.SELECTED_DB);
            jedis.hmset(key, map);

            long expiration = getExpiration(t);
            if (expiration > 0)
                jedis.pexpire(key, expiration);
        }
    }

    /**
     * Remove the given {@link T} object from the local cache and from Redis
     *
     * @param t The object to remove
     */
    public void remove(T t) {
        cached.remove(t);
        String key = getKey(t);
        if (key == null || (key.isEmpty()))
            throw new IllegalArgumentException("Cannot remove, the key is null or empty: \"" + (key == null ? "null" : key) + "\"");
        try (Jedis jedis = controller.getPool().getResource()) {
            jedis.auth(JedisConstants.AUTH);
            jedis.select(JedisConstants.SELECTED_DB);
            jedis.del(key);
        }
    }

    /**
     * Clear the local repository cache and fetch {@link T} from Redis and
     * populate the local cache with it
     */
    protected void updateCache() {
        if (pattern == null)
            return;
        List<T> cached = new ArrayList<>();
        try (Jedis jedis = controller.getPool().getResource()) {
            jedis.auth(JedisConstants.AUTH);
            jedis.select(JedisConstants.SELECTED_DB);
            Set<String> keys = jedis.keys(pattern);
            for (String key : keys) {
                Map<String, String> data = jedis.hgetAll(key);
                if (data.isEmpty())
                    continue;
                try {
                    fromMap(jedis.hgetAll(key)).ifPresent(cached::add);
                // We ignore this exception so if a repository object is being created
                // in Redis and is not completed, it will skip it
                } catch (Exception ignored) {}
            }
        }
        this.cached = cached;
        for (Consumer<List<T>> updateListener : updateListeners)
            updateListener.accept(cached);
    }

    /**
     * Get a {@link RedisRepository} from the given {@link Class}
     *
     * @param clazz The class to get the repository from
     * @return the repository
     */
    public static <T extends RedisRepository<?, ?>> Optional<T> getRepository(Class<T> clazz) {
        return repositories.stream()
                .filter(repository -> repository.getClass().equals(clazz))
                .findFirst().map(clazz::cast);
    }
}