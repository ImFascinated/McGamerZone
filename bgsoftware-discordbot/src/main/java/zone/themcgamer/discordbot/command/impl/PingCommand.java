package zone.themcgamer.discordbot.command.impl;

import com.jagrosh.jdautilities.command.CommandEvent;
import zone.themcgamer.discordbot.command.BaseCommand;
import zone.themcgamer.discordbot.guild.Guild;
import zone.themcgamer.discordbot.utilities.EmbedUtils;

import java.awt.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class PingCommand  extends BaseCommand {

    private static long inputTime;

    public static void setInputTime(long inputTimeLong) {
        inputTime = inputTimeLong;
    }

    private Color getColorByPing(long ping) {
        if (ping < 100)
            return Color.cyan;
        if (ping < 400)
            return Color.green;
        if (ping < 700)
            return Color.yellow;
        if (ping < 1000)
            return Color.orange;
        return Color.red;
    }

    public PingCommand() {
        name = "ping";
        aliases = new String[]{"latency"};
        help = "Get the latency of the bot to the guild.";
        guildOnly = true;
        guilds = Arrays.asList(Guild.MAIN, Guild.TEST);
    }

    @Override
    protected void execute(CommandEvent event, List<String> args) {
        long processing = new Date().getTime() - inputTime;
        long ping = event.getJDA().getGatewayPing();
        event.getTextChannel().sendMessage(EmbedUtils.defaultEmbed().setColor(getColorByPing(ping)).setDescription(
                String.format(":ping_pong:   **Pong!**\n\nThe bot took `%s` milliseconds to response.\nIt took `%s` milliseconds to parse the command and the ping is `%s` milliseconds.",
                        processing + ping, processing, ping)
        ).build()).queue();
    }
}
