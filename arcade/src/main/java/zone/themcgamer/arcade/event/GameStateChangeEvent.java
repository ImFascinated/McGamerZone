package zone.themcgamer.arcade.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import zone.themcgamer.arcade.game.Game;
import zone.themcgamer.arcade.game.GameState;
import zone.themcgamer.core.common.WrappedBukkitEvent;

/**
 * @author Braydon
 * @implNote This event is called when the {@link GameState} is changed
 */
@AllArgsConstructor @Getter
public class GameStateChangeEvent extends WrappedBukkitEvent {
    private final Game game;
    private final GameState from, to;
}