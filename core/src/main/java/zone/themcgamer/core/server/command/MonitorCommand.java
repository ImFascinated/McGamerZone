package zone.themcgamer.core.server.command;

import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.server.menu.ServerMonitorMenu;
import zone.themcgamer.data.Rank;

/**
 * @author Braydon
 */
public class MonitorCommand {
    @Command(name = "monitor", description = "Monitor the server", ranks = { Rank.HELPER }, playersOnly = true)
    public void onCommand(CommandProvider command) {
        new ServerMonitorMenu(command.getPlayer()).open();
    }
}