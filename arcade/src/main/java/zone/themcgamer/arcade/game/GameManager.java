package zone.themcgamer.arcade.game;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import zone.themcgamer.arcade.game.impl.TheBridgeGame;
import zone.themcgamer.core.module.Module;
import zone.themcgamer.core.module.ModuleInfo;

import java.util.Arrays;
import java.util.List;

/**
 * @author Braydon
 */
@ModuleInfo(name = "Game Manager") @Getter
public class GameManager extends Module {
    private final List<Game> games = Arrays.asList(
            new TheBridgeGame()
    );

    public GameManager(JavaPlugin plugin) {
        super(plugin);
    }
}