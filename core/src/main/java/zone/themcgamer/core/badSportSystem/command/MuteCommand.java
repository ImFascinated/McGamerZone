package zone.themcgamer.core.badSportSystem.command;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.data.Rank;

@AllArgsConstructor
public class MuteCommand {
    private final AccountManager accountManager;

    @Command(name = "mute", aliases = { "bssmute" }, description = "Mute a player", ranks = { Rank.HELPER }, playersOnly = true)
    public void onCommand(CommandProvider command) {
        Player player = command.getPlayer();
    }
}
