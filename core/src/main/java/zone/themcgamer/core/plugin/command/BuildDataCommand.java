package zone.themcgamer.core.plugin.command;

import org.bukkit.command.CommandSender;
import zone.themcgamer.common.BuildData;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.data.Rank;

/**
 * @author Braydon
 */
public class BuildDataCommand {
    @Command(name = "builddata", ranks = { Rank.JR_DEVELOPER }, description = "Show the build information")
    public void onCommand(CommandProvider command) {
        CommandSender sender = command.getSender();
        BuildData build = BuildData.getBuild();
        sender.sendMessage(Style.main("Build Data", "Build Information:"));
        sender.sendMessage(" §8- §7Branch §f" + build.getBranch() + "/" + build.getModule());
        sender.sendMessage(" §8- §7Username §f" + build.getUsername() + "@" + build.getHost());
        sender.sendMessage(" §8- §7Version §f" + build.getVersion());
        sender.sendMessage(" §8- §7Time §f" + build.getTime());
    }
}