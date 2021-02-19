package zone.themcgamer.buildServer.world.command;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import zone.themcgamer.buildServer.world.WorldManager;
import zone.themcgamer.common.EnumUtils;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.command.TabComplete;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.world.MGZWorld;
import zone.themcgamer.core.world.WorldCategory;
import zone.themcgamer.data.Rank;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Braydon
 */
@AllArgsConstructor
public class MapCommand {
    private final WorldManager worldManager;

    @Command(name = "map", description = "Teleport to a map", ranks = { Rank.BUILDER }, playersOnly = true)
    public void onCommand(CommandProvider command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();
        if (args.length < 1) {
            player.sendMessage(Style.main("Map", "Usage: /map <name> [category]"));
            return;
        }
        List<MGZWorld> worlds = worldManager.getWorld(args[0]);
        if (worlds.isEmpty()) {
            player.sendMessage(Style.error("Map", "§cThere is no map with that name"));
            return;
        }
        MGZWorld world;
        if (worlds.size() == 1)
            world = worlds.get(0);
        else {
            WorldCategory category = null;
            if (args.length >= 2)
                category = EnumUtils.fromString(WorldCategory.class, args[1].toUpperCase());
            if (category == null) {
                player.sendMessage(Style.error("Map", "§cYou either didn't specify a category, or the given category is incorrect!"));
                return;
            }
            world = worldManager.getWorld(args[0], category);
        }
        if (world == null) {
            player.sendMessage(Style.error("Map", "§cFailed to locate world, please wait a few moments and try again"));
            return;
        }
        if (worldManager.beingParsed(world)) {
            player.sendMessage(Style.error("Map", "§cThis map is currently being parsed, teleportation has been disabled"));
            return;
        }
        if (world.getWorld() == null) {
            worldManager.loadWorld(world);
            world.setWorld(worldManager.getWorldCreator(world.getCategory().name() + "-" + world.getName(), world.getPreset()).createWorld());
            worldManager.setupWorld(world.getWorld());
            Bukkit.broadcastMessage(Style.main("Map", "Loaded world §b" + world.getName()));
        }
        player.teleport(world.getWorld().getSpawnLocation());
        player.sendMessage(Style.main("Map", "Teleported to §b" + world.getName()));
    }

    @TabComplete(name = "map")
    public List<String> onTab(CommandProvider command) {
        return worldManager.getWorlds().stream().map(MGZWorld::getName).collect(Collectors.toList());
    }
}