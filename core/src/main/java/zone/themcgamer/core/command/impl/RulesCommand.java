package zone.themcgamer.core.command.impl;

import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;

public class RulesCommand {
    @Command(name = "rules", description = "View the rules", playersOnly = true)
    public void onCommand(CommandProvider command) {
        command.getPlayer().sendMessage(Style.main("Rules", "In order to play you will have to follow our rules.\n" +
                "You can find our rules at: &bhttps://mcgamerzone.net/rules"));
    }
}
