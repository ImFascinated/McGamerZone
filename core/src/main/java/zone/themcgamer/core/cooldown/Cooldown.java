package zone.themcgamer.core.cooldown;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Braydon
 */
@AllArgsConstructor @Getter
public class Cooldown {
    private final String name;
    private final long time, started;
    private final boolean inform;

    public long getRemaining() {
        return (started + time) - System.currentTimeMillis();
    }
}