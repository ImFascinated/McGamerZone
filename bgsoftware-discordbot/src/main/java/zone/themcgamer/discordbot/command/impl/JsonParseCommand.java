package zone.themcgamer.discordbot.command.impl;

import com.jagrosh.jdautilities.command.CommandEvent;
import zone.themcgamer.discordbot.command.BaseCommand;
import zone.themcgamer.discordbot.guild.Guild;
import zone.themcgamer.discordbot.utilities.MessageUtils;

import java.util.Arrays;
import java.util.List;

public class JsonParseCommand  extends BaseCommand {
    public JsonParseCommand() {
        name = "jsonparse";
        help = "Parse your json!";
        arguments = "<json>";
        guildOnly = true;
        guilds = Arrays.asList(Guild.MAIN, Guild.TEST);
    }

    @Override
    protected void execute(CommandEvent event, List<String> args) {
        if (args.size() < 1) {
            MessageUtils.sendUsageMessage(event.getTextChannel(), this);
        }
    }
}
