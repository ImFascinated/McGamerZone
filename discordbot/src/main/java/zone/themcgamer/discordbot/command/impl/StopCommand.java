package zone.themcgamer.discordbot.command.impl;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import zone.themcgamer.discordbot.MGZBot;
import zone.themcgamer.discordbot.command.BaseCommand;
import zone.themcgamer.discordbot.guild.Guild;

import java.util.Arrays;
import java.util.List;

public class StopCommand extends BaseCommand {

    public StopCommand() {
        name = "stop";
        aliases = new String[]{"shutdown"};
        help = "Stop the bot";
        userPermissions = new Permission[] { Permission.ADMINISTRATOR };
        guildOnly = true;
        guilds = Arrays.asList(Guild.MAIN, Guild.TEAM, Guild.TEST);
    }

    @Override
    protected void execute(CommandEvent event, List<String> args) {
        event.reply("Shutting down...");
        MGZBot.getInstance().getJda().shutdown();
    }
}
