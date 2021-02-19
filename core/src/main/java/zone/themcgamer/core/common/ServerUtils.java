package zone.themcgamer.core.common;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Braydon
 */
public class ServerUtils {
    private static String VERSION = Bukkit.getServer().getClass().getPackage().getName();
    private static Object SERVER_OBJECT;
    private static Field RECENT_TPS_FIELD;

    static {
        VERSION = VERSION.substring(VERSION.lastIndexOf('.') + 1);

        try {
            Class<?> clazz = Class.forName("net.minecraft.server." + VERSION + ".MinecraftServer");
            SERVER_OBJECT = clazz.getMethod("getServer").invoke(null);
            RECENT_TPS_FIELD = SERVER_OBJECT.getClass().getField("recentTps");
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Get the current tps of the server
     * @return the tps
     */
    public static double getTps() {
        try {
            return ((double[]) RECENT_TPS_FIELD.get(SERVER_OBJECT))[0];
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return 20D;
    }

    public static List<Player> getLoadedPlayers() {
        List<Player> list = new ArrayList<>();
        for (World world : Bukkit.getWorlds())
            list.addAll(world.getPlayers());
        return Collections.unmodifiableList(list);
    }
}