package zone.themcgamer.discordbot.events;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import zone.themcgamer.discordbot.BotConstants;
import zone.themcgamer.discordbot.MGZBot;
import zone.themcgamer.discordbot.utilities.EmbedUtils;
import zone.themcgamer.discordbot.utilities.GuildUtils;

import javax.annotation.Nonnull;

import static zone.themcgamer.discordbot.utilities.GuildUtils.toggleRole;

@RequiredArgsConstructor
public class MainGuildListener  extends ListenerAdapter {
    private final MGZBot mgzBot;

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        Member member = event.getMember();
        User user = event.getUser();
        Guild guild = event.getGuild();
        if (user.isBot())
            return;

        if (!guild.getId().equals(BotConstants.MAIN_GUILD_ID))
            return;

        Role memberRole = guild.getRoleById(793672609395900446L);
        GuildUtils.toggleRole(guild, member, memberRole);
        Role newsRole = guild.getRoleById(812440883898875914L);
        GuildUtils.toggleRole(guild, member, newsRole);

        user.openPrivateChannel().queue(privateChannel -> {
            EmbedBuilder embedBuilder = EmbedUtils.defaultEmbed();
            embedBuilder.setThumbnail(mgzBot.getJda().getSelfUser().getAvatarUrl());
            embedBuilder.setDescription("Welcome to **McGamerZone** you have received News & Member role.");
            privateChannel.sendMessage(embedBuilder.build()).queue();
        }, error -> {
            TextChannel textChannelById = guild.getTextChannelById(767396615299923998L);
            if (textChannelById != null)
                textChannelById.sendMessage(user.getAsMention() + ", I could not sent a message to you due you have private messages disabled!").queue();
        });

        TextChannel textChannelById = guild.getTextChannelById(812453030405996564L);
        if (textChannelById == null)
            return;

        EmbedBuilder embedBuilder = EmbedUtils.defaultEmbed();
        embedBuilder.setTitle("Welcome to McGamerZone, " + user.getAsTag());
        embedBuilder.setThumbnail(user.getAvatarUrl());
        embedBuilder.setDescription("This is the official Discord server for McGamerZone Minecraft server." +
                " We are a fun Server that is focused on creativity, community-building, and keeping to the" +
                " core of the game itself. Our goal here; is to maintain a friendly, fun, " +
                "and equal community for anyone and everyone that joins in and " +
                "give the og players of MGZ the nostalgia feeling back!");
        embedBuilder.setTimestamp(event.getMember().getTimeJoined());
        embedBuilder.setFooter("Joined at » ");
        textChannelById.sendMessage(user.getAsMention()).queue(message -> message.delete().queue());
        textChannelById.sendMessage(embedBuilder.build()).queue(message -> {
            message.addReaction(":MGZ_Icon_M:791020631042162729").queue();
        });
    }

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        Guild guild = event.getGuild();
        Member member = event.getMember();

        if (!guild.getId().equals(BotConstants.MAIN_GUILD_ID))
            return;

        if (event.getChannel().getId().equals("813139125195898880") && event.getMessageId().equals("813143359249842186")) {
            MessageReaction.ReactionEmote reactionEmote = event.getReactionEmote();
            if (reactionEmote.getName().equals("🗞️")) {
                Role role = guild.getRoleById(812440883898875914L); // News role
                if (role == null)
                    return;
                toggleRole(guild, member, role);
            } else if (reactionEmote.getName().equals("📊")) {
                Role role = guild.getRoleById(813139198428839976L); // Polls role
                if (role == null)
                    return;
                toggleRole(guild, member, role);
            } else if (reactionEmote.getName().equals("🥁")) {
                Role role = guild.getRoleById(813140631705878549L); // Events role
                if (role == null)
                    return;
                toggleRole(guild, member, role);
            }
            event.getReaction().removeReaction(member.getUser()).queue();
        }
    }
}
