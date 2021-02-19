package zone.themcgamer.data.jedis.command.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import zone.themcgamer.data.Rank;
import zone.themcgamer.data.jedis.command.JedisCommand;

/**
 * @author Braydon
 */
@AllArgsConstructor @Getter
public class RankMessageCommand extends JedisCommand {
    private final Rank rank;
    private final String message;
}