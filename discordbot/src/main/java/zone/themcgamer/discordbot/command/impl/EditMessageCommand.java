package zone.themcgamer.discordbot.command.impl;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import zone.themcgamer.discordbot.MGZBot;
import zone.themcgamer.discordbot.command.BaseCommand;
import zone.themcgamer.discordbot.guild.Guild;
import zone.themcgamer.discordbot.utilities.EmbedUtils;
import zone.themcgamer.discordbot.utilities.MessageUtils;

import java.util.Arrays;
import java.util.List;

public class EditMessageCommand extends BaseCommand {
    public EditMessageCommand() {
        name = "edit";
        aliases = new String[]{"editmessage"};
        help = "Edit a message from the bot.";
        arguments = "<channelID> <messageID> <title> <description>";
        userPermissions = new Permission[] { Permission.ADMINISTRATOR };
        guildOnly = true;
        guilds = Arrays.asList(Guild.MAIN, Guild.TEAM, Guild.TEST);
    }

    @Override
    protected void execute(CommandEvent event, List<String> args) {
        if (args.size() < 3) {
            MessageUtils.sendUsageMessage(event.getTextChannel(), this);
            return;
        }

        TextChannel textChannelById = MGZBot.getInstance().getJda().getTextChannelById(args.get(1));
        if (textChannelById == null) {
            event.reply("Channel does not exist!");
            return;
        }

        EmbedBuilder embedBuilder = EmbedUtils.defaultEmbed();
        embedBuilder.setTitle(args.get(3).replace("_", " "));
        embedBuilder.setDescription(event.getMessage().getContentRaw()
                .replace("." + args.get(0), "")
                .replace(args.get(1), "")
                .replace(args.get(2), "")
                .replace(args.get(3), ""));
        textChannelById.editMessageById(args.get(2), embedBuilder.build()).queue(message -> {
            event.replySuccess("Message has been edited!");
        }, error -> {
            event.reply("Message with this ID does not exist, are you sure this is the right id?");
        });
    }
}