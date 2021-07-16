package zone.themcgamer.data.jedis.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import zone.themcgamer.data.APIAccessLevel;

/**
 * @author Braydon
 */
@AllArgsConstructor @Getter
public class APIKey {
    private final String key;
    private final APIAccessLevel accessLevel;
}

