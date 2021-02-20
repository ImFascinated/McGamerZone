package zone.themcgamer.discordbot.utilities;

import net.dv8tion.jda.api.entities.TextChannel;
import zone.themcgamer.discordbot.BotConstants;
import zone.themcgamer.discordbot.command.BaseCommand;

public class MessageUtils {

    public static void sendUsageMessage(TextChannel textChannel, BaseCommand command) {
        textChannel.sendMessage(EmbedUtils.errorEmbed()
                .appendDescription("Usage: " + BotConstants.PREFIX + command.getName() + " " + command.getArguments())
                .build()
        ).queue();
    }
}
