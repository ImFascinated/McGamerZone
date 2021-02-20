package zone.themcgamer.discordbot.utilities;

import zone.themcgamer.discordbot.BotConstants;
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
}