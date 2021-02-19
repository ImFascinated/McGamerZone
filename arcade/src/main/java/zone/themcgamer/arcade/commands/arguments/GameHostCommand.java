package zone.themcgamer.arcade.commands.arguments;

import org.bukkit.command.CommandSender;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.data.Rank;

public class GameHostCommand {
    @Command(name = "game.host", description = "Manage the game", ranks = { Rank.ADMIN })
    public void onCommand(CommandProvider command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        //TODO open gui to manage the game
    }
}
