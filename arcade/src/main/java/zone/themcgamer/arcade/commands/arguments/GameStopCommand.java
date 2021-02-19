package zone.themcgamer.arcade.commands.arguments;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import zone.themcgamer.arcade.game.Game;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.data.Rank;

@RequiredArgsConstructor
public class GameStopCommand {
    final Game game;
    @Command(name = "game.stop", description = "Force stop this game.", ranks = { Rank.ADMIN })
    public void onCommand(CommandProvider command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();
    }
}
