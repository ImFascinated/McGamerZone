package zone.themcgamer.discordbot.command.impl;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import zone.themcgamer.discordbot.command.BaseCommand;
import zone.themcgamer.discordbot.utilities.EmbedUtils;

import java.util.List;

/**
 * @author Nicholas
 */
public class CommandsCommand extends BaseCommand {
    public CommandsCommand() {
        name = "commands";
        aliases = new String[] { "help" };
        help = "List of commands!";
        guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event, List<String> args) {
        EmbedBuilder embedBuilder = EmbedUtils.defaultEmbed();
    }
}