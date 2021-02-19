package zone.themcgamer.core.account.command.rank.arguments;

import org.bukkit.command.CommandSender;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.data.Rank;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Braydon
 */
public class ListArgument {
    @Command(name = "rank.list", description = "View a list of ranks", ranks = { Rank.MODERATOR })
    public void onCommand(CommandProvider command) {
        CommandSender sender = command.getSender();
        List<Rank> ranks = Arrays.asList(Rank.values());
        if (ranks.isEmpty()) {
            sender.sendMessage(Style.error("Rank", "There are no ranks to list."));
            return;
        }
        sender.sendMessage(Style.main("Rank", "Showing &f" + ranks.size() + " &7ranks: &f" +
                ranks.stream().map(Rank::getDisplayName).collect(Collectors.joining("&7, &f"))));
    }
}