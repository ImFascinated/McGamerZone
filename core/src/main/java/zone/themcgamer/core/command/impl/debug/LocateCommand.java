package zone.themcgamer.core.command.impl.debug;

import org.bukkit.entity.Player;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.data.Rank;

public class LocateCommand {
    @Command(name = "location", aliases = "loc", ranks = Rank.BUILDER, description = "Get location you're at", playersOnly = true)
    public void onCommand(CommandProvider command) {
        Player player = command.getPlayer();
        if (player == null) return;
        player.sendMessage(Style.main("Debug", "You're location & facing is: &b" + player.getLocation() + " &7facing: &6" + player.getEyeLocation()));
    }
}
