package zone.themcgamer.data.jedis.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * @author Braydon
 */
@AllArgsConstructor @Getter
public class DiscordLink {
    private final UUID uuid;
    private final String token;
    private final Timestamp timestamp;
}