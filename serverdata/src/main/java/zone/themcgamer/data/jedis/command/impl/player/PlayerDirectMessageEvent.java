package zone.themcgamer.data.jedis.command.impl.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import zone.themcgamer.data.jedis.command.JedisCommand;

import java.util.UUID;

/**
 * Fired when a player sends a direct message to another player on the network.
 */
@RequiredArgsConstructor @Getter
public class PlayerDirectMessageEvent extends JedisCommand {
    private final String senderDisplayName, message;
    @Nullable private final UUID receiver;
    private final boolean reply;
}
