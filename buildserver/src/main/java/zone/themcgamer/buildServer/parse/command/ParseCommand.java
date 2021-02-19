package zone.themcgamer.buildServer.parse.command;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import zone.themcgamer.buildServer.world.WorldManager;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.world.MGZWorld;
import zone.themcgamer.data.Rank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Braydon
 */
@AllArgsConstructor
public class ParseCommand {
    private final WorldManager worldManager;

    @Command(name = "parse", description = "Parse a map", ranks = { Rank.BUILDER }, playersOnly = true)
    public void onCommand(CommandProvider command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();
        if (args.length < 1) {
            player.sendMessage(Style.main("Map", "Usage: /parse <radius>"));
            return;
        }
        int radius = 0;
        try {
            radius = Integer.parseInt(args[0]);
        } catch (NumberFormatException ignored) {}
        if (radius <= 0) {
            player.sendMessage(Style.error("Map", "§cCannot parse map with a radius of §b" + radius));
            return;
        }
        MGZWorld mgzWorld = worldManager.getWorld(player.getWorld());
        if (mgzWorld == null) {
            player.sendMessage(Style.error("Map", "§cYou cannot parse this map."));
            return;
        }
        if (!player.isOp()) {
            player.sendMessage(Style.error("Map", "§cOnly server operators can parse maps."));
            return;
        }
        List<Integer> ids = new ArrayList<>();
        for (String idString : Arrays.stream(args).skip(1).collect(Collectors.toList())) {
            int id;
            try {
                id = Integer.parseInt(idString);
                ids.add(id);
            } catch (NumberFormatException ignored) {}
        }
        worldManager.parse(mgzWorld, player.getLocation(), radius, ids);
        Bukkit.broadcastMessage(Style.main("Map", "Map §b" + mgzWorld.getName() + " §7is now being parsed!" +
                (ids.isEmpty() ? "" : " §7(" + ids.size() + " ids)")));
    }
}