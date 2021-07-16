package zone.themcgamer.buildServer.world.command;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import zone.themcgamer.buildServer.world.WorldManager;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.world.MGZWorld;
import zone.themcgamer.core.world.WorldCategory;
import zone.themcgamer.core.world.WorldGenerator;
import zone.themcgamer.data.Rank;

/**
 * @author Braydon
 */
@AllArgsConstructor
public class CreateCommand {
    private final WorldManager worldManager;

    @Command(name = "create", description = "Create a new map", ranks = { Rank.BUILDER }, playersOnly = true)
    public void onCommand(CommandProvider command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();
        if (args.length < 1) {
            player.sendMessage(Style.main("Map", "Usage: /create <name> [generator|-v]"));
            player.sendMessage(Style.main("Map", "§6generator §7= A custom generator, see here: https://www.minecraft101.net/superflat/"));
            player.sendMessage(Style.main("Map", "§6-v §7= void"));
            return;
        }
        MGZWorld mgzWorld = worldManager.getWorld(args[0], WorldCategory.OTHER);
        if (mgzWorld != null) {
            player.sendMessage(Style.error("Map", "§cThere is already a map with that name"));
            return;
        }
        String name = args[0];
        if (worldManager.isIllegalName(name)) {
            player.sendMessage(Style.error("Map", "§cIllegal map name!"));
            return;
        }
        WorldGenerator generator = WorldGenerator.FLAT;
        String preset = null;
        if (args.length >= 2) {
            if (args[1].equalsIgnoreCase("-v"))
                generator = WorldGenerator.VOID;
            else {
                generator = WorldGenerator.CUSTOM;
                preset = args[1].toLowerCase();
            }
        }
        World world = worldManager.create(name, player.getName(), WorldCategory.OTHER, generator, preset);
        player.teleport(world.getSpawnLocation());
        player.sendMessage(Style.main("Map", "Created a new map named §b" + name + "§7!"));
        player.sendMessage(Style.color("Please pre-generate the world! Use /pregen"));
    }
}