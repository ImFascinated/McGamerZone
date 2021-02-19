package zone.themcgamer.core.command.help;

import org.apache.commons.lang.WordUtils;
import org.bukkit.command.CommandSender;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.common.PageBuilder;
import zone.themcgamer.data.Rank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class serves the purpose of sending the help menu
 * to the executor. The {@code sendHelp} method can be overridden
 * inside of a command class so that command can have it's own help
 * menu instead of the default one
 * @author Braydon
 */
public class HelpCommand {
    /**
     * Get the color scheme for the help menu
     * @return the color scheme
     */
    public HelpColorScheme getColorScheme() {
        return new HelpColorScheme(null, "§6", "§e");
    }

    /**
     * Send the help menu to the provided sender
     * @param sender - The sender to send the help menu to
     * @param label - The command label
     * @param parent - The parent command
     * @param children - The child commands
     * @param page - The page to display
     * @return the response
     */
    public HelpResponse sendHelp(CommandSender sender, String label, Command parent, List<Command> children, int page) {
        HelpColorScheme colorScheme = getColorScheme();
        String header = colorScheme.getHeader();
        if (header == null)
            header = WordUtils.capitalize(parent.name().toLowerCase());
        List<Command> commands = new ArrayList<>(Collections.singletonList(parent));
        commands.addAll(children);
        PageBuilder<?> pageBuilder = new PageBuilder<>(commands, (entry, command) -> {
            String usage = parent.usage().isEmpty() ? label + " [page]" : parent.usage();
            boolean isParent = parent.equals(command);
            if (!isParent) {
                String[] split = command.name().split("\\.");
                usage = split[split.length - 1] + (command.usage().isEmpty() ? "" : " " + command.usage());
            }
            String descriptionExtension = "";
            if (!command.description().isEmpty() || isParent)
                descriptionExtension = " §8- §7" + (!command.description().isEmpty() ? command.description() : "Show's this menu");
            String rankExtension = "";
            Rank rank = command.ranks()[0];
            if (rank != Rank.DEFAULT)
                rankExtension = " " + rank.getColor() + rank.getDisplayName();
            sender.sendMessage(" §7- §" + (isParent ? "f" : "7") + "/" + (isParent ? "" : label + " §f") + usage + descriptionExtension + rankExtension);
        }).resultsPerPage(5);
        int maxPage = pageBuilder.getMaxPages();
        if (page <= 0 || page > maxPage)
            return HelpResponse.INVALID_PAGE;
        sender.sendMessage("");
        sender.sendMessage(colorScheme.getPrimaryColor() + "§l" + header + " " + colorScheme.getSecondaryColor() + "Help §7(Page " + page + " / " + maxPage + ")");
        sender.sendMessage(colorScheme.getSecondaryColor() + "<>§7 = required, " + colorScheme.getSecondaryColor() + "[]§7 = optional");
        sender.sendMessage("");
        pageBuilder.send(sender, page);
        sender.sendMessage("");
        return HelpResponse.SUCCESS;
    }

    public enum HelpResponse {
        INVALID_PAGE, SUCCESS
    }
}