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
import java.util.stream.Collectors;

public class SayCommand  extends BaseCommand {

    public SayCommand() {
        name = "say";
        aliases = new String[]{"announce"};
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
        String description = args.stream().skip(2).collect(Collectors.joining(" "));
        EmbedBuilder embedBuilder = EmbedUtils.defaultEmbed();
        embedBuilder.setTitle(args.get(1));
        embedBuilder.setDescription(description);
        event.getChannel().sendMessage(embedBuilder.build()).queue();
    }
}
