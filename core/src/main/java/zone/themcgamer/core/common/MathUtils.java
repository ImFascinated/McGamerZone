package zone.themcgamer.core.common;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Collection;

/**
 * @author Braydon
 */
public class MathUtils {
    public static float getFacingYaw(Location location, Collection<Location> targetLocations) {
        if (targetLocations.isEmpty())
            return 0f;
        return getYaw(getTrajectory(location, findClosest(location, targetLocations)));
    }

    public static Location findClosest(Location center, Collection<Location> locations) {
        Location bestLocation = null;
        double lastDistance = 0;
        for (Location location : locations) {
            double distance = center.toVector().subtract(location.toVector()).length();
            if (bestLocation == null || distance < lastDistance) {
                bestLocation = location;
                lastDistance = distance;
            }
        }
        return bestLocation;
    }

    public static Vector getTrajectory(Location from, Location to) {
        return getTrajectory(from.toVector(), to.toVector());
    }

    public static Vector getTrajectory(Vector from, Vector to) {
        return to.subtract(from).normalize();
    }

    public static float getYaw(Vector vector) {
        double x = vector.getX();
        double z = vector.getZ();
        double yaw = Math.toDegrees(Math.atan((-x) / z));
        if (z < 0)
            yaw+= 180;
        return (float) yaw;
    }
}