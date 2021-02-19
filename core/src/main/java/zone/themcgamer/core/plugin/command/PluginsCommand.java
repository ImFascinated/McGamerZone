package zone.themcgamer.core.plugin.command;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.data.Rank;

import java.util.ArrayList;
import java.util.List;

public class PluginsCommand {
    @Command(name = "plugins", aliases = { "pl" }, ranks = { Rank.JR_DEVELOPER }, description = "Get a list of plugins")
    public void onCommand(CommandProvider command) {
        List<String> pluginNames = new ArrayList<>();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins())
            pluginNames.add((plugin.isEnabled() ? "§a" + plugin.getName() : "§c" + plugin.getName()));
        command.getSender().sendMessage(Style.main("§9§lPlugins &7(" + pluginNames.size() + ")",
                String.join("§7, §f", pluginNames)));
    }
}
