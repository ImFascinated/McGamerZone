package zone.themcgamer.discordbot.command.impl;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.TextChannel;
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
public class SuggestCommand extends BaseCommand {
    public SuggestCommand() {
        name = "suggest";
        aliases = new String[]{"suggestion"};
        help = "Share a suggestion!";
        arguments = "<suggestion>";
        guildOnly = true;
        guilds = Collections.singletonList(Guild.MAIN);
    }

    @Override
    protected void execute(CommandEvent event, List<String> args) {
        if (args.size() < 1) {
            MessageUtils.sendUsageMessage(event.getTextChannel(),this);
            return;
        }
        TextChannel channel = MGZBot.getInstance().getJda().getTextChannelById(BotConstants.SUGGESTIONS);
        if (channel == null)
            return;
        String suggestion = args.stream().skip(1).collect(Collectors.joining(" "));
        if (suggestion.length() < 120) {
            event.getChannel().sendMessage(EmbedUtils.errorEmbed()
                    .appendDescription("Your suggestion is too short. Suggestions must be at least 120 characters.")
                    .build()
            ).queue();
            return;
        }
        channel.sendMessage(EmbedUtils.defaultEmbed()
                .setThumbnail(event.getAuthor().getAvatarUrl())
                .setTitle(event.getAuthor().getAsTag() + " has a suggestion!")
                .appendDescription(suggestion)
                .build()
        ).queue(message -> {
            message.addReaction("✅").queue();
            message.addReaction("❌").queue();
        });
    }
}