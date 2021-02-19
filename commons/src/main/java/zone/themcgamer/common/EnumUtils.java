package zone.themcgamer.common;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

/**
 * @author Braydon
 */
@UtilityClass
public class EnumUtils {
    /**
     * Get the enum value from the given class with the given name
     * @param clazz - The enum class
     * @param name - The name
     * @return the enum value
     */
    @Nullable
    public static <T extends Enum<T>> T fromString(Class<T> clazz, String name) {
        for (T value : clazz.getEnumConstants()) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }
}