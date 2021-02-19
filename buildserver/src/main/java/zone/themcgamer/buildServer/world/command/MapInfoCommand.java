package zone.themcgamer.buildServer.world.command;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import zone.themcgamer.buildServer.world.WorldManager;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.world.MGZWorld;
import zone.themcgamer.data.Rank;

/**
 * @author Braydon
 */
@AllArgsConstructor
public class MapInfoCommand {
    private final WorldManager worldManager;

    @Command(name = "mapinfo", description = "View information about a map", ranks = { Rank.BUILDER }, playersOnly = true)
    public void onCommand(CommandProvider command) {
        Player player = command.getPlayer();
        MGZWorld world = worldManager.getWorld(player.getWorld());
        if (world == null) {
            player.sendMessage(Style.error("Map", "§cYou cannot view information for this map."));
            return;
        }
        player.sendMessage(Style.main("Map", "Information for §b" + world.getName() + "§7:"));
        player.sendMessage(" §8- §7Author §f" + world.getAuthor() + (world.getAuthor().equals(world.getOriginalCreator()) ? "" : " §7(original: " + world.getOriginalCreator() + ")"));
        player.sendMessage(" §8- §7Preset §f" + world.getPreset());
        player.sendMessage(" §8- §7Category §f" + world.getCategory().getName());
        player.sendMessage(" §8- §7Admins §f" + (world.getAdmins().isEmpty() ? "None" : String.join("§7, §f", world.getAdmins())));
    }
}