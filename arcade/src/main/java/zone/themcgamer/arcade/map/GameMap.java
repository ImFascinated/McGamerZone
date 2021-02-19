package zone.themcgamer.arcade.map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.World;
import zone.themcgamer.core.world.MGZWorld;

/**
 * @author Braydon
 */
@AllArgsConstructor @Getter
public class GameMap {
    private final MGZWorld mgzWorld;
    private final World bukkitWorld;
}