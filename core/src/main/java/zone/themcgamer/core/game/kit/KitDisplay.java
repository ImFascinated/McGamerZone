package zone.themcgamer.core.game.kit;

import com.cryptomorin.xseries.XMaterial;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Braydon
 */
@AllArgsConstructor @Getter
public class KitDisplay {
    private final String id, name;
    private final String[] description;
    private final XMaterial icon;
}