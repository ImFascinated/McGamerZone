package zone.themcgamer.discordbot.command.impl;

import com.jagrosh.jdautilities.command.CommandEvent;
import zone.themcgamer.discordbot.command.BaseCommand;
import zone.themcgamer.discordbot.guild.Guild;
import zone.themcgamer.discordbot.utilities.EmbedUtils;

import java.awt.*;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

public class PingCommand  extends BaseCommand {

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
        guilds = Arrays.asList(Guild.MAIN, Guild.TEAM, Guild.TEST);
    }

    @Override
    protected void execute(CommandEvent event, List<String> args) {
        event.reply(":ping_pong: Pong! ...", m -> {
            long ping = event.getMessage().getTimeCreated().until(m.getTimeCreated(), ChronoUnit.MILLIS);
            m.editMessage(EmbedUtils.defaultEmbed().setColor(getColorByPing(ping))
                    .setDescription("Ping: " + ping  + "ms | Websocket: " + event.getJDA().getGatewayPing() + "ms").build())
                    .queue();
        });
    }
}
