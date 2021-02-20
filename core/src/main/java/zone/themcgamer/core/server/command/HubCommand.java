package zone.themcgamer.core.server.command;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.traveler.ServerTraveler;

/**
 * @author Braydon
 */
@AllArgsConstructor
public class HubCommand {
    private final ServerTraveler traveler;

    @Command(name = "hub", aliases = { "lobby" }, description = "Join a random hub", playersOnly = true)
    public void onCommand(CommandProvider command) {
        Player player = command.getPlayer();
        try {
            traveler.sendPlayer(player, "Hub");
        } catch (Exception ex) {
            player.sendMessage(Style.error("Server", "&7Could not find an available hub to send you to!"));
        }
    }
}