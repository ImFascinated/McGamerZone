package zone.themcgamer.data.jedis;

/**
 * @author Braydon
 */

// Maybe we should use env vars instead
public class JedisConstants {
    public static final String HOST = "172.18.0.1";
    public static final String AUTH = System.getenv("REDIS_PASSWORD");
    public static final int SELECTED_DB = 0;
}