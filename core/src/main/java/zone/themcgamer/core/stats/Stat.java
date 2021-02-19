package zone.themcgamer.core.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Braydon
 */
@AllArgsConstructor @Setter @Getter
public class Stat {
    private final String id;
    private long value;
}