package zone.themcgamer.discordbot.command.impl;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import zone.themcgamer.discordbot.BGSoftwareBot;
import zone.themcgamer.discordbot.command.BaseCommand;
import zone.themcgamer.discordbot.guild.Guild;
import zone.themcgamer.discordbot.utilities.EmbedUtils;
import zone.themcgamer.discordbot.utilities.MessageUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Nicholas
 */
public class SetActivityCommand extends BaseCommand {
    public SetActivityCommand() {
        name = "setactivity";
        help = "Set the bot activity.";
        arguments = "<message>";
        userPermissions = new Permission[] { Permission.ADMINISTRATOR };
        guildOnly = true;
        guilds = Arrays.asList(Guild.MAIN, Guild.TEST);
    }

    @Override
    protected void execute(CommandEvent event, List<String> args) {
        if (args.size() < 1) {
            MessageUtils.sendUsageMessage(event.getTextChannel(),this);
            return;
        }
        String activity = args.stream().skip(1).collect(Collectors.joining(" "));
        BGSoftwareBot.getInstance().getJda().getPresence().setActivity(Activity.playing(activity));
        event.getChannel().sendMessage(EmbedUtils.successEmbed()
                .setThumbnail(event.getAuthor().getAvatarUrl())
                .setTitle("Activity updated!")
                .appendDescription(event.getAuthor().getAsTag() + " updated the bot activity to \"" + activity + "\".")
                .build()
        ).queue();
    }
}