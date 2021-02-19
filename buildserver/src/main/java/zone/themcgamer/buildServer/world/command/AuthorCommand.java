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
public class AuthorCommand {
    private final WorldManager worldManager;

    @Command(name = "author", description = "Set the author of a map", ranks = { Rank.BUILDER }, playersOnly = true)
    public void onCommand(CommandProvider command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();
        if (args.length < 1) {
            player.sendMessage(Style.main("Map", "Usage: /author <author>"));
            return;
        }
        MGZWorld world = worldManager.getWorld(player.getWorld());
        if (world == null) {
            player.sendMessage(Style.error("Map", "§cYou cannot update the author of this map."));
            return;
        }
        if (!world.getOriginalCreator().equals(player.getName()) && !player.isOp()) {
            player.sendMessage(Style.error("Map", "§cYou cannot update the author of this map."));
            return;
        }
        if (worldManager.beingParsed(world)) {
            player.sendMessage(Style.error("Map", "§cThis map is currently being parsed, changing the author has been disabled"));
            return;
        }
        world.setAuthor(String.join(" ", args));
        world.save();
        player.sendMessage(Style.main("Map", "Map author set to §b" + world.getAuthor()));
    }
}