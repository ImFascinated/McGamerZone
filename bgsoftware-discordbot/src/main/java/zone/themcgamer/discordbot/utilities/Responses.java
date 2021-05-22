package zone.themcgamer.discordbot.utilities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import zone.themcgamer.discordbot.BotConstants;

@AllArgsConstructor
@Getter
public enum Responses {
    PURCHASE("Purchase",
            new String[] {"purchase", "buy"},
            "Premium plugins at no cost!",
            "Since 1st January, 2021, " +
                    "{plugin} is free, with all the features included to make sure you get the best quality for no cost!\u200B\n"+
                    "You can download the plugin [here](https://bg-software.com/{plugin})",
            "Are all plugins",
            false,
            null),
    ERROR("error",
            new String[] {"java.lang", "org.bukkit.event", "error occurred while enabling", "error"},
            "No errors!",
            "Please do not paste stack-traces and or errors here!\n" +
                    "please report them at our [github](https://github.com/BG-Software-LLC)!",
            null,
            true,
            null),
    COMMANDS("commands",
            new String[] {"commands", "cmds"},
            "{plugin} - Commands",
            "You can find the list of commands [here](https://wiki.bg-software.com/#/{plugin}/?id=commands)",
            "not found",
            true,
            null),
    DISABLE("Disable Commands",
            new String[] {"disable command", "permissions"},
            "How to disable a command?",
            "All commands are based on permissions. If you want to disable or enable a command just give them the permission or not. You can find all permissions at the wiki of the plugin!\n" +
                    "You can find the permissions [here](https://wiki.bg-software.com/#/{plugin}/?id=permissions)",
            "not found",
            true,
            null),
    WIKI("Wiki",
            new String[] {"wiki", "about"},
            "{plugin} - Wiki",
            "[Click here](https://bg-software.com/{plugin}/) for the wiki of the plugin {plugin}",
            "not found",
            true,
            null),
    PLACEHOLDERS("Placeholders",
            new String[] {"placeholders"},
            "{plugin} - Placeholders",
            "You can find all placeholders of this plugin [here](https://wiki.bg-software.com/#/{plugin}/?id=placeholders)",
            "not found",
            true,
            null),
    WILDSTACKER_LOOT_TABLES("Loot Tables",
            new String[] {"loot tables", "loot"},
            "Loot Tables",
            "Loot tables are used to store all the loot data of entities.\n" +
                    "They cannot be disabled, and they are used to calculate loot faster.\n" +
                    "They can be changed however you want, as long as you follow the formatting rules.\n" +
                    "\n" +
                    "Every file is represented as a \"loot table\". Loot tables contain global settings and pairs.\n" +
                    "Pairs contain the items, and can be manipulated differently to get different results.\n" +
                    "\n" +
                    "[Click Here](https://wiki.bg-software.com/#/wildstacker/loot-tables/) for more information!",
            "not found",
            true,
            BotConstants.WILDSTACKER),
    NOT_SAVING("No data saving",
            new String[] {"not saving"},
            "Is your settings not saving?",
            "If you're using `/{plugin} settings` and you changed settings, make sure to go back to the main menu" +
                    "and click the `Save Settings` button.",
            "pluginName",
            false,
            null);

    private final String name;
    private final String[] triggerWords;
    private final String title;
    private final String description;
    private final String defaultReplace;
    private final boolean requireHelpCategory;
    private final String requiredChannel;

}
