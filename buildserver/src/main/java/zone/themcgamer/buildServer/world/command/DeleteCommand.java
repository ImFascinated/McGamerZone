package zone.themcgamer.buildServer.world.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import zone.themcgamer.buildServer.Build;
import zone.themcgamer.buildServer.world.WorldManager;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.world.MGZWorld;
import zone.themcgamer.data.Rank;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Braydon
 */
@AllArgsConstructor
public class DeleteCommand {
    @Getter private static final List<Player> confirmDelete = new ArrayList<>();

    private final WorldManager worldManager;

    @Command(name = "delete", description = "Delete a map", ranks = { Rank.BUILDER }, playersOnly = true)
    public void onCommand(CommandProvider command) {
        Player player = command.getPlayer();
        MGZWorld mgzWorld = worldManager.getWorld(player.getWorld());
        World world;
        if (mgzWorld == null || ((world = mgzWorld.getWorld()) == null)) {
            player.sendMessage(Style.error("Map", "§cYou cannot delete this map."));
            return;
        }
        if (!mgzWorld.getOriginalCreator().equals(player.getName()) && !player.isOp()) {
            player.sendMessage(Style.error("Map", "§cYou cannot delete this map."));
            return;
        }
        if (worldManager.beingParsed(mgzWorld)) {
            player.sendMessage(Style.error("Map", "§cThis map is currently being parsed, deleting has been disabled"));
            return;
        }
        if (confirmDelete.remove(player)) {
            for (Player worldPlayer : world.getPlayers()) {
                worldPlayer.teleport(Build.INSTANCE.getMainWorld().getSpawnLocation());
                worldPlayer.sendMessage(Style.main("Map", "Map §b" + mgzWorld.getName() + " §7was deleted"));
            }
            Bukkit.unloadWorld(world, true);
            FileUtils.deleteQuietly(world.getWorldFolder());
            mgzWorld.setWorld(null);
            worldManager.getWorlds().remove(mgzWorld);
            FileUtils.deleteQuietly(new File("maps" + File.separator + mgzWorld.getCategory().name() + File.separator + mgzWorld.getName()));
        } else {
            confirmDelete.add(player);
            player.sendMessage(Style.main("Map", "Execute this command again to confirm deletion of map §b" + mgzWorld.getName()));
            player.sendMessage(Style.main("Map", "§c§lNOTE §f- §7This action CANNOT be undone"));
        }
    }
}