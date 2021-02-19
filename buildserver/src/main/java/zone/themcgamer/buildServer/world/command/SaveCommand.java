package zone.themcgamer.buildServer.world.command;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import zone.themcgamer.buildServer.world.WorldManager;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.world.MGZWorld;
import zone.themcgamer.data.Rank;

import java.io.File;

@RequiredArgsConstructor
public class SaveCommand {
    private final WorldManager worldManager;

    @Command(name = "save", aliases = { "saveworld", "sw" }, description = "Save your world", ranks = { Rank.BUILDER }, playersOnly = true)
    public void onCommand(CommandProvider command) {
        Player player = command.getPlayer();
        player.getWorld().save();

        MGZWorld mgzWorld = worldManager.getWorld(player.getWorld());
        if (mgzWorld == null)
            return;

        if (!mgzWorld.hasPrivileges(player)) {
            player.sendMessage(Style.error("Map", "§cYou cannot save this map."));
            return;
        }

        Bukkit.broadcastMessage(Style.main("Map", "Saved the map &b" + mgzWorld.getName() + "&7!"));
        worldManager.copyWorld(player.getWorld().getWorldFolder(), new File("maps" + File.separator + mgzWorld.getCategory().name() + File.separator + mgzWorld.getName()));
    }
}
