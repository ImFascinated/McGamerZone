package zone.themcgamer.core.animation.impl;

import org.bukkit.ChatColor;
import zone.themcgamer.core.animation.TextAnimation;

/**
 * @author Braydon
 * @implNote Simple to use wave animation.
 */
public class WaveAnimation extends TextAnimation {
    private String primary, secondary, tertiary;
    private boolean bold;

    public WaveAnimation(String input) {
        super(input);
        primary = secondary = tertiary = "";
    }

    /**
     * Add a primary color to the animation
     * @param color the color to add
     */
    public WaveAnimation withPrimary(String color) {
        primary = color;
        return this;
    }

    /**
     * Add a secondary color to the animation
     * @param color the color to add
     */
    public WaveAnimation withSecondary(String color) {
        secondary = color;
        return this;
    }

    /**
     * Add a third (highlight) color to the animation
     * @param color the color to add
     */
    public WaveAnimation withTertiary(String color) {
        tertiary = color;
        return this;
    }

    /**
     * Make the animation text bold
     */
    public WaveAnimation withBold() {
        bold = true;
        return this;
    }

    /**
     * Animate the animation and return the new text
     * @return the text
     */
    @Override
    public String next() {
        String[] chars = new String[getInput().length() * 2];
        String[] primaryRun = getFrames(primary, secondary);
        String[] secondaryRun = getFrames(secondary, primary);

        System.arraycopy(primaryRun, 0, chars, 0, getInput().length());
        System.arraycopy(secondaryRun, 0, chars, getInput().length(), getInput().length());

        String primary = chars[index];
        if (++index >= chars.length)
            index = 0;
        return primary;
    }

    /**
     * Get the frames for the text with the given primary and secondary colors
     * @param primary the primary color
     * @param secondary the secondary color
     * @return the frames
     */
    private String[] getFrames(String primary, String secondary) {
        String[] output = new String[getInput().length()];
        for (int i = 0; i < getInput().length(); i++) {
            StringBuilder builder = new StringBuilder(getInput().length() * 3)
                    .append(primary).append(bold ? ChatColor.BOLD.toString() : "");
            for (int j = 0; j < getInput().length(); j++) {
                char c = getInput().charAt(j);
                if (j == i) {
                    builder.append(tertiary).append(bold ? ChatColor.BOLD.toString() : "");
                } else if (j == i + 1) {
                    builder.append(secondary).append(bold ? ChatColor.BOLD.toString() : "");
                }
                builder.append(c);
            }
            output[i] = builder.toString();
        }
        return output;
    }
}