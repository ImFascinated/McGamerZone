package zone.themcgamer.core.common;

import java.text.DecimalFormat;

/**
 * @author Braydon
 */
public class MiscUtils {
    /**
     * Format the given tps value
     *
     * @param tps the tps
     * @return the formatted tps
     */
    public static String formatTps(double tps) {
        tps = Double.parseDouble(new DecimalFormat("#.##").format(tps));
        return ((tps > 18.0) ? "§a" : (tps > 16.0) ? "§e" : "§c")
                + ((tps > 20.0) ? "*" : "" ) + Math.min(Math.round(tps * 100.0) / 100.0, 20.0);
    }
}