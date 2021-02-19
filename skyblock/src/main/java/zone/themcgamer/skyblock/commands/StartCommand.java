package zone.themcgamer.skyblock.commands;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;

public class StartCommand {
    @Command(name = "start", description = "Start your island", playersOnly = true)
    public void onCommand(CommandProvider command) {
        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(command.getPlayer());
        if (superiorPlayer == null)
            return;
        if (superiorPlayer.getIsland() == null)
            SuperiorSkyblockAPI.getSuperiorSkyblock().getMenus().openIslandCreationMenu(superiorPlayer, superiorPlayer.getName());
        else command.getPlayer().sendMessage(Style.main("Skyblock", "You already have an island!"));
    }
}
