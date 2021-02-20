package zone.themcgamer.discordbot.utilities;

import net.dv8tion.jda.api.EmbedBuilder;
import zone.themcgamer.discordbot.BotConstants;
import zone.themcgamer.discordbot.MGZBot;

import java.awt.*;
import java.time.LocalDateTime;

public class EmbedUtils {
    public static EmbedBuilder successEmbed() {
        return defaultEmbed().setColor(Color.decode("#41bc7f"));
    }

    public static EmbedBuilder errorEmbed() {
        return defaultEmbed().setTitle("Oops!")
                .setColor(Color.decode("#d74742"));
    }

    public static EmbedBuilder warnEmbed() {
        return defaultEmbed().setColor(Color.decode("#f85b2e"));
    }

    public static EmbedBuilder defaultEmbed() {
        return new EmbedBuilder()
                .setColor(Color.decode("#1DD0D5"))
                .setTimestamp(LocalDateTime.now())
                .setFooter(BotConstants.COPYRIGHT, MGZBot.getInstance().getJda().getSelfUser().getEffectiveAvatarUrl());
    }
}