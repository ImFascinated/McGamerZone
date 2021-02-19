package zone.themcgamer.data.jedis.command.impl.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import zone.themcgamer.data.jedis.command.JedisCommand;

import java.util.UUID;

/**
 * @author Braydon
 */
@AllArgsConstructor @Getter
public class AccountRankSetCommand extends JedisCommand {
    private final UUID uuid;
    private final String constantName, rankDisplayName;
}