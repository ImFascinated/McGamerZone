package zone.themcgamer.discordbot.utilities;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import zone.themcgamer.discordbot.BotConstants;
import zone.themcgamer.discordbot.MGZBot;
import zone.themcgamer.discordbot.guild.Guild;

/**
 * @author Nicholas
 */
public class GuildUtils {
    public static Guild getGuildFromId(String id) {
        switch (id) {
            case BotConstants.MAIN_GUILD_ID:
                return Guild.MAIN;
            case BotConstants.TEAM_GUILD_ID:
                return Guild.TEAM;
            case BotConstants.TEST_GUILD_ID:
                return Guild.TEST;
        }
        return null;
    }

    public static void toggleRole(net.dv8tion.jda.api.entities.Guild guild, Member member, Role role) {
        if (member.getRoles().contains(role))
            guild.removeRoleFromMember(member.getIdLong(), role).queue();
        else guild.addRoleToMember(member.getIdLong(), role).queue();

        member.getUser().openPrivateChannel().queue(privateChannel -> {
            privateChannel.sendMessage(EmbedUtils.successEmbed().setDescription("Succesfully toggled " + role.getName() + " " + (!member.getRoles().contains(role) ? "On" : "Off")).build()).queue();
        }, error -> {
        });

        TextChannel textChannelById = MGZBot.getInstance().getJda().getTextChannelById(BotConstants.HAROLD_LOG);
        if (textChannelById == null)
            return;

        EmbedBuilder embedBuilder = EmbedUtils.defaultEmbed();
        embedBuilder.setTitle("Role Log");
        embedBuilder.addField("Member", member.getUser().getAsTag() + " (" + member.getId() + ")", true);
        embedBuilder.addField("Role", role.getName() + " " + (!member.getRoles().contains(role) ? "(Added)" : "(Removed)"), true);
        embedBuilder.addField("Status", "Success", false);
        textChannelById.sendMessage(embedBuilder.build()).queue();

    }
}