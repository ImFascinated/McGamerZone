package zone.themcgamer.data.jedis.command.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import zone.themcgamer.data.jedis.command.JedisCommand;
import zone.themcgamer.data.jedis.data.server.MinecraftServer;
import zone.themcgamer.data.jedis.data.server.ServerState;

/**
 * @author Braydon
 */
@AllArgsConstructor @Getter
public class ServerStateChangeCommand extends JedisCommand {
    private final MinecraftServer server;
    private final ServerState oldState, newState;
}