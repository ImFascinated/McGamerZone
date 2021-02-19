package zone.themcgamer.core.kingdom.command;

import com.cryptomorin.xseries.XSound;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.kingdom.KingdomManager;
import zone.themcgamer.data.Rank;

/**
 * @author Braydon
 */
@AllArgsConstructor
public class KingdomCommand {
    private final KingdomManager kingdomManager;

    @Command(name = "kingdom", description = "Host a kingdom", ranks = { Rank.JR_DEVELOPER }, playersOnly = true)
    public void onCommand(CommandProvider command) {
        Player player = command.getPlayer();
        if (kingdomManager.host(player)) {
            player.playSound(player.getEyeLocation(), XSound.ENTITY_PLAYER_LEVELUP.parseSound(), 0.9f, 1f);
            player.sendMessage(Style.main("Kingdom", "§aSuccess! §7Your §6Kingdom §7is being setup, you will be sent to it shortly!"));
        }
    }
}