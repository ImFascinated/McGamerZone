package zone.themcgamer.core.common.menu;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Braydon
 */
@UtilityClass
public class MenuPattern {
    /**
     * Get the slots matching the given pattern.
     * Example: 'XXOOOOOXX' would return slots
     * from 1 to 6 for that pattern
     *
     * @param patterns - The patterns
     * @return the slots
     */
    public static List<Integer> getSlots(String... patterns) {
        List<Integer> slots = new ArrayList<>();
        for (int row = 0; row < patterns.length; row++) {
            String s = patterns[row];
            if (s.length() != 9)
                throw new IllegalArgumentException("String '" + s + "' cannot have a length of " + s.length() + ", it must be 9!");
            char[] chars = s.toCharArray();
            for (int slot = 0; slot < 9; slot++) {
                char character = chars[slot];
                if (Character.toLowerCase(character) == 'o') {
                    slots.add((row * 9) + slot);
                }
            }
        }
        return slots;
    }
}