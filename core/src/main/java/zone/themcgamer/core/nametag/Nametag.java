package zone.themcgamer.core.nametag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Braydon (credits: https://github.com/sgtcaze/NametagEdit)
 * @implNote This object reprents a nametag for a player
 */
@AllArgsConstructor @Getter @ToString
public class Nametag {
    private final String prefix, suffix;
    private final int priority;
}