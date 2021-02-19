package zone.themcgamer.core.game.kit;

import lombok.Getter;
import zone.themcgamer.core.game.MGZGame;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Braydon
 */
@Getter
public class KitClient {
    private final Map<MGZGame, KitDisplay> selectedKit = new HashMap<>();

    public KitDisplay getKit(MGZGame game) {
        KitDisplay kitDisplay = selectedKit.get(game);
        if (kitDisplay == null && (game.getKitDisplays().length > 0))
            kitDisplay = game.getKitDisplays()[0];
        return kitDisplay;
    }
}