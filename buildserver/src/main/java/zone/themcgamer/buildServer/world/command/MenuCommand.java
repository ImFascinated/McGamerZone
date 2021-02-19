package zone.themcgamer.buildServer.world.command;

import zone.themcgamer.buildServer.world.menu.BuildManagerMenu;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.data.Rank;

public class MenuCommand {
    @Command(name = "menu", aliases = { "gui", "buildmanager", "bm" } , description = "Open the build management menu.",
            ranks = { Rank.BUILDER }, playersOnly = true)
    public void onCommand(CommandProvider command) {
        new BuildManagerMenu(command.getPlayer()).open();
    }
}
