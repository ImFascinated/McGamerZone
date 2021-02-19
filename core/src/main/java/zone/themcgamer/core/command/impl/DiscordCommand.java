package zone.themcgamer.core.command.impl;

import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;

public class DiscordCommand {
    @Command(name = "discord", description = "A discord full with players to chat with", playersOnly = true)
    public void onCommand(CommandProvider command) {
        command.getPlayer().sendMessage(Style.main("Discord", "&e&lClick&7 this link to join our discord: &9discord.mcgamerzone.net"));
        //TODO this will also include the linking system. To generate the token.
    }
}
