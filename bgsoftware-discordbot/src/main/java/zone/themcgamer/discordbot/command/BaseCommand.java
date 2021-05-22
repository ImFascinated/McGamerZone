package zone.themcgamer.discordbot.command;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import zone.themcgamer.discordbot.BotConstants;
import zone.themcgamer.discordbot.guild.Guild;
import zone.themcgamer.discordbot.utilities.GuildUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Nicholas
 */
public abstract class BaseCommand extends Command {
    protected List<Guild> guilds; // The guilds the command can be executed in

    @Override
    protected void execute(CommandEvent event) {
        if (!guilds.contains(GuildUtils.matchGuild(event.getGuild().getId())))
            return;
        List<String> args = new ArrayList<>();
        if (event.getArgs() != null && event.getArgs().length() > 0) {
            String[] split = event.getMessage().getContentRaw()
                    .replaceFirst("(?i)" + Pattern.quote(BotConstants.PREFIX), "")
                    .split("\\s+");
            args = Arrays.asList(split);
        }
        execute(event, args);
    }

    protected abstract void execute(CommandEvent event, List<String> args);
}