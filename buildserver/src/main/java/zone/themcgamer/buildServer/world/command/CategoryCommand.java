package zone.themcgamer.buildServer.world.command;

import lombok.AllArgsConstructor;
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
import zone.themcgamer.core.world.WorldCategory;
import zone.themcgamer.data.Rank;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Braydon
 */
@AllArgsConstructor
public class CategoryCommand {
    private final WorldManager worldManager;

    @Command(name = "category", description = "Set the category of a map", ranks = { Rank.BUILDER }, playersOnly = true)
    public void onCommand(CommandProvider command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();
        if (args.length < 1) {
            player.sendMessage(Style.main("Map", "Usage: /category <category>"));
            return;
        }
        WorldCategory category = WorldCategory.lookup(String.join(" ", args));
        if (category == null) {
            player.sendMessage(Style.error("Map", "§cInvalid category: §f" + Arrays.stream(WorldCategory.values())
                    .map(WorldCategory::getName).collect(Collectors.joining("§7, §f"))));
            return;
        }
        MGZWorld mgzWorld = worldManager.getWorld(player.getWorld());
        World world;
        if (mgzWorld == null || ((world = mgzWorld.getWorld()) == null)) {
            player.sendMessage(Style.error("Map", "§cYou cannot update the category of this map."));
            return;
        }
        if (!mgzWorld.getOriginalCreator().equals(player.getName()) && !player.isOp()) {
            player.sendMessage(Style.error("Map", "§cYou cannot update the category of this map."));
            return;
        }
        if (worldManager.beingParsed(mgzWorld)) {
            player.sendMessage(Style.error("Map", "§cThis map is currently being parsed, changing the category has been disabled"));
            return;
        }
        if (worldManager.getWorld(mgzWorld.getName(), category) != null) {
            player.sendMessage(Style.error("Map", "§cThere is already a map with that category"));
            return;
        }
        for (Player worldPlayer : world.getPlayers()) {
            worldPlayer.teleport(Build.INSTANCE.getMainWorld().getSpawnLocation());
            worldPlayer.sendMessage(Style.main("Map", "Map category set to §b" + category.getName()));
        }
        try {
            Bukkit.unloadWorld(world, true);
            mgzWorld.setWorld(null);

            WorldCategory oldCategory = mgzWorld.getCategory();
            mgzWorld.setCategory(category);
            mgzWorld.save();

            File newDirectory = new File("maps" + File.separator + category.name() + File.separator + mgzWorld.getName());
            FileUtils.moveDirectory(new File(oldCategory.name() + "-" + mgzWorld.getName()), newDirectory);
            mgzWorld.setDataFile(new File(newDirectory, MGZWorld.FILE_NAME));
            FileUtils.deleteQuietly(new File("maps" + File.separator + oldCategory.name() + File.separator + mgzWorld.getName()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}