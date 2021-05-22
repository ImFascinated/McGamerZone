package zone.themcgamer.discordbot.command.impl;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import zone.themcgamer.core.command.CommandManager;
import zone.themcgamer.discordbot.BotConstants;
import zone.themcgamer.discordbot.MGZBot;
import zone.themcgamer.discordbot.command.BaseCommand;
import zone.themcgamer.discordbot.guild.Guild;
import zone.themcgamer.discordbot.utilities.EmbedUtils;
import zone.themcgamer.discordbot.utilities.MessageUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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