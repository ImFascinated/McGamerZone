package zone.themcgamer.core.common;

import org.bukkit.ChatColor;
import zone.themcgamer.data.Rank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Braydon
 */
public class Style {
    /**
     * Return the rank required message for the given player name with the given prefix
     * @return the rank required message.
     * @param rank the rank that is required
     */
    public static String rankRequired(Rank rank) {
        if (rank.getCategory() == Rank.RankCategory.DONATOR)
            return Style.color("\n" +
                    "&a&l  Account &8∙ &cYou do not have " + rank.getDisplayName() + " rank!\n" +
                    "&7  You need " + rank.getColor() + "&l" + rank.getDisplayName() + "&7 or a &e&lhigher &7donator rank to unlock this!\n" +
                    "&b  &nstore.mcgamerzone.net&f\n  &7");
        return Style.main("Account", "You need " + rank.getColor() + rank.getDisplayName() + " &7or &chigher &7to use this command!");
    }

    /**
     * Return the invalid account error for the given player name with the given prefix
     * @param prefix the prefix
     * @param playerName the player name
     * @return the error
     */
    public static String invalidAccount(String prefix, String playerName) {
        return Style.error(prefix, "§7Could not find a Minecraft account with the name §b" + playerName);
    }

    /**
     * Return the default chat format with the given prefix and message
     * @param prefix the prefix of the message
     * @param message the message
     * @return the formatted message
     */
    public static String main(String prefix, String message) {
        return color("&a&l" + prefix + " &8» &7" + message);
    }

    /**
     * Return the default error chat format with the given prefix and message
     * @param prefix the prefix of the message
     * @param message the message
     * @return the formatted message
     */
    public static String error(String prefix, String message) {
        return color("&c&l" + prefix + " &8» &7" + message);
    }

    /**
     * Color the provided {@link Collection<String>} using {@link ChatColor}
     * @param lines the lines to color
     * @return the colored lines
     */
    public static List<String> colorLines(Collection<String> lines) {
        List<String> newLines = new ArrayList<>();
        for (String line : lines)
            newLines.add(color(line));
        return newLines;
    }

    /**
     * Color the provided message using {@link ChatColor}
     * @param message the message to color
     * @return the colored message
     */
    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message.replaceAll("§", "&"));
    }
}