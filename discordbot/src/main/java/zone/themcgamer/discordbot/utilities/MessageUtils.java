package zone.themcgamer.discordbot.utilities;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import zone.themcgamer.discordbot.BotConstants;
import zone.themcgamer.discordbot.MGZBot;
import zone.themcgamer.discordbot.command.BaseCommand;

import java.util.Objects;

public class MessageUtils {

    public static void sendUsageMessage(TextChannel textChannel, BaseCommand command) {
        textChannel.sendMessage(EmbedUtils.errorEmbed()
                .appendDescription("Usage: " + BotConstants.PREFIX + command.getName() + " " + command.getArguments())
                .build()
        ).queue();
    }

    public static void sendLogMessage(Message message) {
        Objects.requireNonNull(getLogChannel()).sendMessage(message).queue();
    }

    public static void sendLogMessage(EmbedBuilder embedBuilder) {
        Objects.requireNonNull(getLogChannel()).sendMessage(embedBuilder.build()).queue();
    }

    private static TextChannel getLogChannel() {
        TextChannel logChannel = MGZBot.getInstance().getJda().getTextChannelById(BotConstants.HAROLD_LOG);
        if (logChannel == null) {
            Objects.requireNonNull(MGZBot.getInstance().getJda().getUserById("504069946528104471"))
                    .openPrivateChannel().queue(privateChannel ->
                    privateChannel.sendMessage("There was an error while sending a log message, the channel id is invalid or does not exist.").queue());
            return null;
        }
        return logChannel;
    }
}
