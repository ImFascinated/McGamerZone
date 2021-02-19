package zone.themcgamer.common;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author Braydon
 */
public class MiscUtils {
    /**
     * Get a {@link String} based on the provided string array
     * @param array - The string array
     * @return the string
     */
    public static String arrayToString(String... array) {
        return arrayToString(Arrays.asList(array));
    }

    /**
     * Get a {@link String} based on the provided {@link List<String>}
     * @param list - The string list
     * @return the string
     */
    public static String arrayToString(List<String> list) {
        StringBuilder builder = new StringBuilder();
        for (String message : list)
            builder.append(message).append("\n");
        return builder.substring(0, builder.toString().length() - 1);
    }

    public static UUID getUuid(String s) {
        if (s == null || (s.trim().isEmpty()))
            return null;
        try {
            return UUID.fromString(s);
        } catch (IllegalArgumentException ignored) {}
        return null;
    }

    public static String percent(double value, double max) {
        double percent = (value * 100d) / max;
        return (int) percent + "%";
    }
}