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
import zone.themcgamer.data.Rank;

import java.io.File;
import java.io.IOException;

/**
 * @author Braydon
 */
@AllArgsConstructor
public class RenameCommand {
    private final WorldManager worldManager;

    @Command(name = "name", description = "Rename a map", ranks = { Rank.BUILDER }, playersOnly = true)
    public void onCommand(CommandProvider command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();
        if (args.length < 1) {
            player.sendMessage(Style.main("Map", "Usage: /name <name>"));
            return;
        }
        MGZWorld mgzWorld = worldManager.getWorld(player.getWorld());
        World world;
        if (mgzWorld == null || ((world = mgzWorld.getWorld()) == null)) {
            player.sendMessage(Style.error("Map", "§cYou cannot rename this map."));
            return;
        }
        if (!mgzWorld.getOriginalCreator().equals(player.getName()) && !player.isOp()) {
            player.sendMessage(Style.error("Map", "§cYou cannot rename this map."));
            return;
        }
        String mapName = args[0];
        if (worldManager.isIllegalName(mapName)) {
            player.sendMessage(Style.error("Map", "§cIllegal map name!"));
            return;
        }
        if (worldManager.beingParsed(mgzWorld)) {
            player.sendMessage(Style.error("Map", "§cThis map is currently being parsed, changing the name has been disabled"));
            return;
        }
        String name = String.join(" ", args).replaceAll(" ", "_");
        if (worldManager.isIllegalName(name)) {
            player.sendMessage(Style.error("Map", "§cIllegal map name!"));
            return;
        }
        if (worldManager.getWorld(name, mgzWorld.getCategory()) != null) {
            player.sendMessage(Style.error("Map", "§cThere is already a map with that name"));
            return;
        }
        for (Player worldPlayer : world.getPlayers()) {
            worldPlayer.teleport(Build.INSTANCE.getMainWorld().getSpawnLocation());
            worldPlayer.sendMessage(Style.main("Map", "Map name set to §b" + name));
        }
        try {
            Bukkit.unloadWorld(world, true);
            mgzWorld.setWorld(null);

            String oldName = mgzWorld.getName();
            mgzWorld.setName(name);
            mgzWorld.save();

            File newDirectory = new File("maps" + File.separator + mgzWorld.getCategory().name() + File.separator + name);
            FileUtils.moveDirectory(new File(mgzWorld.getCategory().name() + "-" + oldName), newDirectory);
            FileUtils.deleteQuietly(new File("maps" + File.separator + mgzWorld.getCategory().name() + File.separator + oldName));
            mgzWorld.setDataFile(new File(newDirectory, MGZWorld.FILE_NAME));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}