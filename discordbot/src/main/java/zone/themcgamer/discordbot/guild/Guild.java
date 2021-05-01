package zone.themcgamer.discordbot.guild;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Nicholas
 */
@AllArgsConstructor @Getter
public enum Guild {
    MAIN("764609803459756093"),
    TEAM("796582717956423760"),
    TEST("811044415211700234");

    private final String guildId;
}