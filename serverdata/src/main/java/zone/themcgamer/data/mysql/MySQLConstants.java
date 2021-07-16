package zone.themcgamer.data.mysql;

/**
 * @author Braydon
 */

// Environment vars should be used here / in the future
public class MySQLConstants {
    public static final String HOST = "localhost";

    public static final String USERNAME = System.getenv("MYSQL_USERNAME");
    public static final String AUTH = System.getenv("MYSQL_PASSWORD");
}