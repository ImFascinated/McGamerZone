package zone.themcgamer.arcade.commands.arguments;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import zone.themcgamer.arcade.game.GameState;
import zone.themcgamer.arcade.manager.ArcadeManager;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.data.Rank;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class GameStartCommand {
    private final ArcadeManager arcadeManager;

    @Command(name = "game.start", description = "Force start the game", ranks = { Rank.ADMIN })
    public void onCommand(CommandProvider command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();
        if (args.length < 1) {
            sender.sendMessage(Style.error("Game", "Please use &6/" + command.getLabel() + " <seconds>"));
            return;
        }
        if (arcadeManager.getState() != GameState.LOBBY)
            sender.sendMessage(Style.main("Game", "The game is not in lobby state!"));
        else {
            try {
                long time = Long.parseLong(args[0]);
                arcadeManager.getMapVotingManager().startVoting(TimeUnit.SECONDS.toMillis(time));
            } catch (NumberFormatException ex) {
                sender.sendMessage(Style.error("Game", "Invalid amount! Please use /" + command.getLabel() + " <seconds>"));
                return;
            }
            arcadeManager.getMapVotingManager().startVoting();
        }
    }
}
