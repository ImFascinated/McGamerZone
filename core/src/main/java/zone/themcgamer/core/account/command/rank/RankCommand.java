package zone.themcgamer.core.account.command.rank;

import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.command.help.HelpCommand;
import zone.themcgamer.data.Rank;

/**
 * @author Braydon
 */
public class RankCommand extends HelpCommand {
    @Command(name = "rank", description = "Rank management", ranks = { Rank.ADMIN })
    public void onCommand(CommandProvider command) {}
}