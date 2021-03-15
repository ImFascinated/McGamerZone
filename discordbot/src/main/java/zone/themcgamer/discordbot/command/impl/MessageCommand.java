package zone.themcgamer.discordbot.command.impl;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import zone.themcgamer.discordbot.command.BaseCommand;
import zone.themcgamer.discordbot.guild.Guild;
import zone.themcgamer.discordbot.utilities.EmbedUtils;
import zone.themcgamer.discordbot.utilities.MessageUtils;

import java.util.Arrays;
import java.util.List;

public class MessageCommand extends BaseCommand {

    public MessageCommand() {
        name = "message";
        aliases = new String[] { "say" };
        help = "Announce something in an embed format.";
        arguments = "<title> <description>";
        userPermissions = new Permission[] { Permission.ADMINISTRATOR };
        guildOnly = true;
        guilds = Arrays.asList(Guild.MAIN, Guild.TEAM, Guild.TEST);
    }

    @Override
    protected void execute(CommandEvent event, List<String> args) {
        if (args.size() < 1) {
            MessageUtils.sendUsageMessage(event.getTextChannel(),this);
            return;
        }

        //TODO a way to add images, and such to the embeds.
        EmbedBuilder embedBuilder = EmbedUtils.defaultEmbed();
        embedBuilder.setTitle(args.get(1).replace("_", " "));
        embedBuilder.setDescription(event.getMessage().getContentRaw().replace(args.get(1), "").replace("." + args.get(0), ""));
        event.getChannel().sendMessage(embedBuilder.build()).queue();
    }
}
