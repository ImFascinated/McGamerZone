package zone.themcgamer.discordbot;

import java.util.Calendar;

/**
 * @author Nicholas
 */
public class BotConstants {
    public static final String TOKEN = "NzY1NTI5MTM2NzgxMDY2MjQ2.X4WIkQ.L7pszAdOq1cbqwyhxtr_-qBULpA";
    public static final String PREFIX = ".";

    public static final String OWNER_ID = "504069946528104471"; // Joel
    public static final String[] BOT_ADMINS = new String[] {
            "758733013579595836", // Nicholas
            "504147739131641857" // Braydon
    };

    // Guilds
    public static final String MAIN_GUILD_ID = "764609803459756093";
    public static final String TEAM_GUILD_ID = "796582717956423760";
    public static final String TEST_GUILD_ID = "811044415211700234";

    // Default Lines
    public static final String COPYRIGHT = "© McGamerZone - " + Calendar.getInstance().get(Calendar.YEAR);

    // Channels
    public static final String HAROLD_LOG = "813151182758608936";
    public static final String SUGGESTIONS = "802304706701426730"; // TODO: 2/15/2021 Change this to the main guild's suggestions channel when the bot is on the main guild.
}