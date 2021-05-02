package zone.themcgamer.common;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Braydon
 */
@UtilityClass
public class RandomUtils {
    /**
     * Return whether or not the {@param chance} has been met
     *
     * @param chance - The chance
     * @param max - The maximum number
     * @return whether or not the {@param chance} has been met
     */
    public static boolean chance(int chance, int max) {
        return randomInt(max) + 1 <= chance;
    }

    /**
     * Return whether or not the {@param chance} has been met
     *
     * @param chance - The chance
     * @param max - The maximum number
     * @return whether or not the {@param chance} has been met
     */
    public static boolean chance(double chance, double max) {
        return randomDouble(max) + 1 <= chance;
    }

    /**
     * Get a random int between 0 and the maximum value
     *
     * @param max - The maximum value
     * @return the random number
     */
    public static int randomInt(int max) {
        return randomInt(0, max);
    }

    /**
     * Get a random int between the minimum and maximum values
     *
     * @param min - The minimum value
     * @param max - The maximum value
     * @return the random number
     */
    public static int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    /**
     * Get a random long between 0 and the maximum value
     *
     * @param max - The maximum value
     * @return the random number
     */
    public static long randomLong(long max) {
        return randomLong(0L, max);
    }

    /**
     * Get a random long between the minimum and maximum values
     *
     * @param min - The minimum value
     * @param max - The maximum value
     * @return the random number
     */
    public static long randomLong(long min, long max) {
        return ThreadLocalRandom.current().nextLong(min, max);
    }

    /**
     * Get a random double between 0 and the maximum value
     *
     * @param max - The maximum value
     * @return the random number
     */
    public static double randomDouble(double max) {
        return randomDouble(0D, max);
    }

    /**
     * Get a random double between the minimum and maximum values
     *
     * @param min - The minimum value
     * @param max - The maximum value
     * @return the random number
     */
    public static double randomDouble(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    /**
     * Select a random {@link Enum<T>} value from the given
     *
     * {@link Enum<T>} class
     * @param enumClass - The enum class
     * @return the random enum value
     */
    @Nullable
    public static <T extends Enum<T>> T random(Class<T> enumClass) {
        if (!enumClass.isEnum())
            throw new IllegalArgumentException("Class '" + enumClass.getSimpleName() + "' must be an enum");
        return random(Arrays.asList(enumClass.getEnumConstants()));
    }

    /**
     * Select a random {@link Object} from the provided {@link java.util.ArrayList}
     *
     * @param list - The list to get the object from
     * @return the random object
     */
    @Nullable
    public static <T> T random(List<T> list) {
        if (list.isEmpty())
            return null;
        if (list.size() == 1)
            return list.get(0);
        return list.get(randomInt(0, list.size() - 1));
    }
}