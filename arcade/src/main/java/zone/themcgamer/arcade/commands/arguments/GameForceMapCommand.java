package zone.themcgamer.arcade.commands.arguments;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import zone.themcgamer.arcade.game.Game;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.data.Rank;

@RequiredArgsConstructor
public class GameForceMapCommand {
    final Game game;
    @Command(name = "game.forcemap", usage = "<mapName>", description = "Force a map.", ranks = { Rank.ADMIN })
    public void onCommand(CommandProvider command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();
    }
}
