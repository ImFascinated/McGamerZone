package zone.themcgamer.discordbot.events;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import zone.themcgamer.discordbot.BotConstants;
import zone.themcgamer.discordbot.utilities.EmbedUtils;

import javax.annotation.Nonnull;

public class ReactionListener extends ListenerAdapter {

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

    protected void toggleRole(Guild guild, Member member, Role role) {
        if (member.getRoles().contains(role))
            guild.removeRoleFromMember(member.getIdLong(), role).queue();
        else guild.addRoleToMember(member.getIdLong(), role).queue();

        member.getUser().openPrivateChannel().queue(privateChannel -> {
            privateChannel.sendMessage(EmbedUtils.successEmbed().setDescription("Succesfully toggled " + role.getName() + " " + (!member.getRoles().contains(role) ? "On" : "Off")).build()).queue();
        }, error -> {

        });
    }
}