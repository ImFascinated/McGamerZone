package zone.themcgamer.discordbot.utilities;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import zone.themcgamer.discordbot.guild.Guild;

import java.util.Arrays;
import java.util.List;

/**
 * @author JohannesHQ
 */
public class GuildUtils {
    public static Guild matchGuild(String guildId) {
        return Arrays.stream(Guild.values()).filter(guild -> guild.getGuildId().equals(guildId)).findFirst().orElse(null);
    }

    public static Role hasRole(Member member, String id) {
        List<Role> roles = member.getRoles();
        return roles.stream()
                .filter(role -> role.getId().equals(id)) // filter by role name
                .findFirst() // take first result
                .orElse(null); // else return null
    }
}