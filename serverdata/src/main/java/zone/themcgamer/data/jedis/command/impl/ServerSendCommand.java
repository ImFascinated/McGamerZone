package zone.themcgamer.data.jedis.command.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import zone.themcgamer.data.jedis.command.JedisCommand;

/**
 * @author Braydon
 */
@AllArgsConstructor @Getter
public class ServerSendCommand extends JedisCommand {
    private final String playerName, serverId;
}