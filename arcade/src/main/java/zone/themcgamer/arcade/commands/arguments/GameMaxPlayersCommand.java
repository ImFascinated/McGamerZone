package zone.themcgamer.arcade.commands.arguments;

import org.bukkit.command.CommandSender;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.data.Rank;

public class GameMaxPlayersCommand {
    @Command(name = "game.maxplayers", usage = "<amount>", description = "Set the max players.", ranks = { Rank.ADMIN })
    public void onCommand(CommandProvider command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();
    }
}
