package zone.themcgamer.data.jedis.command.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import zone.themcgamer.data.jedis.command.JedisCommand;

import java.util.UUID;

/**
 * @author Braydon
 */
@AllArgsConstructor @Getter
public class PunishmentsUpdateCommand extends JedisCommand {
    private final UUID uuid;
    private final String json;
}