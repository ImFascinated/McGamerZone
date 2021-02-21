package zone.themcgamer.discordbot.events;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
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
public class GuildMemberJoinQuitListener extends ListenerAdapter {
    private final MGZBot mgzBot;
    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        User user = event.getUser();
        Guild guild = event.getGuild();
        if (user.isBot())
            return;

        if (guild.getId().equals(BotConstants.MAIN_GUILD_ID)) {
            Role memberRole = guild.getRoleById(793672609395900446L);
            if (memberRole != null)
                guild.addRoleToMember(user.getId(), memberRole).queue();
            Role newsRole = guild.getRoleById(812440883898875914L);
            if (newsRole != null)
                guild.addRoleToMember(user.getId(), newsRole).queue();

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
                    "We are a fun Server that is focused on creativity, community-building, and keeping to the" +
                    "core of the game itself. Our goal here; is to maintain a friendly, fun, " +
                    "and equal community for anyone and everyone that joins in and " +
                    "give the og players of MGZ the nostalgia feeling back!");
            embedBuilder.setTimestamp(event.getMember().getTimeJoined());
            embedBuilder.setFooter("Joined at » ");
            textChannelById.sendMessage(user.getAsMention()).queue(message -> message.delete().queue());
            textChannelById.sendMessage(embedBuilder.build()).queue(message -> {
                message.addReaction(":MGZ_Icon_M:791020631042162729").queue();
            });

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