package zone.themcgamer.buildServer.world.command;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.data.Rank;

/**
 * @author Braydon
 */
public class ReloadWorldEditCommand {
    @Command(name = "reloadworldedit", aliases = { "reloadwe", "rwe" }, description = "Reload WorldEdit", ranks = { Rank.BUILDER })
    public void onCommand(CommandProvider command) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldEdit");
        plugin.onDisable();
        plugin.onEnable();
        Bukkit.broadcastMessage(Style.main("Map", "WorldEdit was reloaded"));
    }
}