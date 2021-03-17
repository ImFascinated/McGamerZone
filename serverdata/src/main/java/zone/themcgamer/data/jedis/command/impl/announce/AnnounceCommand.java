package zone.themcgamer.data.jedis.command.impl.announce;

import lombok.AllArgsConstructor;
import lombok.Getter;
import zone.themcgamer.data.jedis.command.JedisCommand;

/**
 * @author Nicholas
 */
@AllArgsConstructor @Getter
public class AnnounceCommand extends JedisCommand {
    private final AnnounceType type;
    private final String message;
}