package zone.themcgamer.core.server.command;

import lombok.RequiredArgsConstructor;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.plugin.MGZPlugin;
import zone.themcgamer.core.server.ServerManager;
import zone.themcgamer.data.Rank;

@RequiredArgsConstructor
public class StopCommand {
    private final ServerManager serverManager;

    @Command(name = "stop", aliases = { "stopserver" }, description = "Stop this server", ranks = { Rank.ADMIN })
    public void onCommand(CommandProvider command) {
        command.getSender().sendMessage(Style.main("Server", "&aSafely stopping the Minecraft server..."));
        serverManager.restart(MGZPlugin.getMinecraftServer());
    }
}
