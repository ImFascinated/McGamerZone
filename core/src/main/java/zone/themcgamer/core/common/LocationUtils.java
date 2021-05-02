package zone.themcgamer.core.common;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * @author Braydon
 */
public class LocationUtils {
    /**
     * Serialize the given {@link Location}
     *
     * @param location the location to serialize
     * @return the serialized location
     */
    public static String toString(Location location) {
        if (location == null)
            return "null";
        return location.getWorld().getName() + "|" +
                location.getX() + "|" +
                location.getY() + "|" +
                location.getZ() + "|" +
                location.getYaw() + "|" +
                location.getPitch();
    }

    /**
     * Deserialize the given {@link String}
     *
     * @param s the string to deserialize
     * @return the deserialized {@link Location}
     */
    public static Location fromString(String s) {
        if (s == null || (s.equals("null") || s.trim().isEmpty()))
            return null;
        String[] data = s.split("\\|");
        World world = Bukkit.getWorld(data[0]);
        double x = Double.parseDouble(data[1]);
        double y = Double.parseDouble(data[2]);
        double z = Double.parseDouble(data[3]);
        float yaw = Float.parseFloat(data[4]);
        float pitch = Float.parseFloat(data[5]);
        return new Location(world, x, y, z, yaw, pitch);
    }
}