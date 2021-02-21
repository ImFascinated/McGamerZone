package zone.themcgamer.discordbot.command.impl;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import zone.themcgamer.discordbot.MGZBot;
import zone.themcgamer.discordbot.command.BaseCommand;
import zone.themcgamer.discordbot.guild.Guild;
import zone.themcgamer.discordbot.utilities.EmbedUtils;
import zone.themcgamer.discordbot.utilities.MessageUtils;

import java.util.Arrays;
import java.util.List;

public class AddReactionToMessageCommand extends BaseCommand {

    public AddReactionToMessageCommand() {
        name = "addreaction";
        aliases = new String[]{"react"};
        help = "Edit a message from the bot.";
        arguments = "<channelID> <messageID> <reaction>";
        userPermissions = new Permission[] { Permission.ADMINISTRATOR };
        guildOnly = true;
        guilds = Arrays.asList(Guild.MAIN, Guild.TEAM, Guild.TEST);
    }

    @Override
    protected void execute(CommandEvent event, List<String> args) {
        if (args.size() < 3) {
            MessageUtils.sendUsageMessage(event.getTextChannel(),this);
            return;
        }

        TextChannel textChannelById = MGZBot.getInstance().getJda().getTextChannelById(args.get(1));
        if (textChannelById == null) {
            event.reply("Channel does not exist!");
            return;
        }

        textChannelById.addReactionById(args.get(2), args.get(3)).queue(message -> {

        }, error -> {
            EmbedBuilder embedBuilder = EmbedUtils.errorEmbed();
            embedBuilder.setDescription(error.getLocalizedMessage());
            event.reply(embedBuilder.build());
        });
    }
}
