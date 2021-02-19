package zone.themcgamer.data.jedis.command.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import zone.themcgamer.data.jedis.command.JedisCommand;

import java.util.UUID;

/**
 * @author Braydon
 * @implNote This command is called when a player joins the network
 */
@AllArgsConstructor @Getter @ToString
public class NetworkConnectCommand extends JedisCommand {
    private final UUID uuid;
    private final String name;
    private final long timestamp;
}