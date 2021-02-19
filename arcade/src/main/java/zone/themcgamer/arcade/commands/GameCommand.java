package zone.themcgamer.arcade.commands;

import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.command.help.HelpCommand;
import zone.themcgamer.data.Rank;

public class GameCommand extends HelpCommand {
    @Command(name = "game", aliases = {"arcade"}, description = "Game commands", ranks = { Rank.ADMIN })
    public void onCommand(CommandProvider command) {}
}
