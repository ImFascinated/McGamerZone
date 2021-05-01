package zone.themcgamer.discordbot.utilities;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import zone.themcgamer.discordbot.MGZBot;
import zone.themcgamer.discordbot.guild.Guild;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author Nicholas
 */
public class GuildUtils {
    public static Guild matchGuild(String guildId) {
        return Arrays.stream(Guild.values()).filter(guild -> guild.getGuildId().equals(guildId)).findFirst().orElse(null);
    }

    public static void toggleRole(net.dv8tion.jda.api.entities.Guild guild, Member member, Role role) {
        if (role == null)
            return;
        if (member.getRoles().contains(role))
            guild.removeRoleFromMember(member.getIdLong(), role).queue();
        else guild.addRoleToMember(member.getIdLong(), role).queue();

        if (member.getUser() == MGZBot.getInstance().getJda().getSelfUser())
            return;

        member.getUser().openPrivateChannel().queue(privateChannel -> {
            privateChannel.sendMessage(EmbedUtils.successEmbed().setDescription("Successfully toggled " + role.getName() +
                    " " + (!member.getRoles().contains(role) ? "On" : "Off")).build()).queue();
        }, error -> {
            EmbedBuilder embedBuilder = EmbedUtils.successEmbed();
            embedBuilder.setTitle("Role Manager");
            embedBuilder.setDescription("Successfully toggled " + role.getName() + " " +
                    (!member.getRoles().contains(role) ? "On" : "Off"));
            TextChannel textChannelById = guild.getTextChannelById(813139125195898880L);
            if (textChannelById == null)
                return;
            textChannelById.sendMessage(embedBuilder.build()).queue(message -> message.delete().queueAfter(2, TimeUnit.SECONDS));
        });

        EmbedBuilder embedBuilder = EmbedUtils.defaultEmbed();
        embedBuilder.setTitle("Role Log");
        embedBuilder.addField("Member", member.getUser().getAsTag() + " (" + member.getId() + ")", true);
        embedBuilder.addField("Role", role.getName() + " " + (!member.getRoles().contains(role) ? "(Added)" : "(Removed)"), true);
        embedBuilder.addField("Status", "Success", false);
        MessageUtils.sendLogMessage(embedBuilder);
    }
}