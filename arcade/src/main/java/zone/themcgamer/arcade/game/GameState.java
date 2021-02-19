package zone.themcgamer.arcade.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Braydon
 * @implNote The state of the currently running game
 */
@AllArgsConstructor @Getter
public enum GameState {
    LOBBY("§aWaiting..."),
    STARTING("§6Starting"),
    PLAYING("§cPlaying"),
    ENDING("§9Ending");

    private final String displayName;
}