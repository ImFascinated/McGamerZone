package zone.themcgamer.buildServer.world.command;

import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;
import zone.themcgamer.buildServer.world.WorldManager;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.world.MGZWorld;
import zone.themcgamer.core.world.WorldCategory;
import zone.themcgamer.data.Rank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Braydon
 */
@AllArgsConstructor
public class MapsCommand {
    private final WorldManager worldManager;

    @Command(name = "maps", description = "List all maps", ranks = { Rank.BUILDER })
    public void onCommand(CommandProvider command) {
        CommandSender sender = command.getSender();
        Map<WorldCategory, List<MGZWorld>> worldsMap = new HashMap<>();
        for (MGZWorld world : worldManager.getWorlds()) {
            List<MGZWorld> worlds = worldsMap.getOrDefault(world.getCategory(), new ArrayList<>());
            worlds.add(world);
            worldsMap.put(world.getCategory(), worlds);
        }
        if (worldsMap.isEmpty()) {
            sender.sendMessage(Style.main("Map", "There are no maps to view."));
            return;
        }
        int maps = 0;
        for (List<MGZWorld> list : worldsMap.values())
            maps+= list.size();
        sender.sendMessage(Style.main("Maps", "Showing §b" + maps + " §7maps"));
        for (Map.Entry<WorldCategory, List<MGZWorld>> entry : worldsMap.entrySet()) {
            sender.sendMessage(Style.color("&6" + entry.getKey().getName() + " &8» &f" +
                    entry.getValue().stream().map(MGZWorld::getName).collect(Collectors.joining("§7, §f"))));
        }
    }
}