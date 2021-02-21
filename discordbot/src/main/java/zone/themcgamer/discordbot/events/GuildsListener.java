package zone.themcgamer.discordbot.events;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import zone.themcgamer.discordbot.BotConstants;
import zone.themcgamer.discordbot.MGZBot;
import zone.themcgamer.discordbot.utilities.EmbedUtils;

import javax.annotation.Nonnull;

@RequiredArgsConstructor
public class GuildsListener extends ListenerAdapter {
    private final MGZBot mgzBot;
    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        User user = event.getUser();
        Guild guild = event.getGuild();
        if (user.isBot())
            return;

        TextChannel logChannel = mgzBot.getJda().getTextChannelById(BotConstants.HAROLD_LOG);
        if (logChannel == null)
            return;
        EmbedBuilder MemberJoinedEmbed = EmbedUtils.successEmbed();
        MemberJoinedEmbed.setTitle("Member Joined");
        MemberJoinedEmbed.addField("Member", user.getAsTag() + " (" + user.getId() + ")", true);
        MemberJoinedEmbed.addField("Guild", guild.getName(), true);
        MemberJoinedEmbed.addField("Status", "Success", false);
        logChannel.sendMessage(MemberJoinedEmbed.build()).queue();
    }

    @Override
    public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent event) {
        User user = event.getUser();
        Guild guild = event.getGuild();
        if (user.isBot())
            return;
        TextChannel logChannel = mgzBot.getJda().getTextChannelById(BotConstants.HAROLD_LOG);
        if (logChannel == null)
            return;

        EmbedBuilder embedBuilder = EmbedUtils.errorEmbed();
        embedBuilder.setTitle("Member Left");
        embedBuilder.addField("Member", user.getAsTag() + " (" + user.getId() + ")", true);
        embedBuilder.addField("Guild", guild.getName(), true);
        embedBuilder.addField("Status", "Success", false);
        logChannel.sendMessage(embedBuilder.build()).queue();
    }
}