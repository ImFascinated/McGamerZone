package zone.themcgamer.data.jedis.data.server;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Braydon
 */
@AllArgsConstructor @Getter
public enum ServerState {
    STARTING(false),
    RUNNING(false),
    UPDATING(true),
    RESTARTING(true),
    STOPPING(true);

    private final boolean shuttingDownState;
}