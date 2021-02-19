package zone.themcgamer.core.world;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Braydon
 */
@AllArgsConstructor @Getter
public enum WorldGenerator {
    FLAT("3;"),
    VOID("3;minecraft:air;2"),
    CUSTOM(null);

    private final String preset;
}