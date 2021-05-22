package zone.themcgamer.discordbot.guild;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Nicholas
 */
@AllArgsConstructor @Getter
public enum Guild {
    MAIN("554276823010246687"),
    TEST("845696905496363078");

    private final String guildId;
}