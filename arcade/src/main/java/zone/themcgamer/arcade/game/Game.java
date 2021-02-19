package zone.themcgamer.arcade.game;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import zone.themcgamer.arcade.player.GamePlayer;
import zone.themcgamer.arcade.team.Team;
import zone.themcgamer.core.game.MGZGame;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Braydon
 * @implNote This class represents a game
 */
@Setter @Getter
public abstract class Game implements Listener {
    private final MGZGame mgzGame;
    private final Team[] teams;

    // Game Flags
    protected boolean joinMessages = true;
    protected boolean quitMessages = true;
    protected boolean blockPlace;
    protected boolean blockBreak;

    // Data
    private long started;
    private final Set<GamePlayer> players = new HashSet<>();

    public abstract List<String> getScoreboard(GamePlayer gamePlayer, Player player);

    public Game(MGZGame mgzGame) {
        this(mgzGame, null);
    }

    public Game(MGZGame mgzGame, Team[] teams) {
        this.mgzGame = mgzGame;
        if (teams == null || (teams.length <= 0))
            teams = new Team[] { new Team("PLAYERS", ChatColor.WHITE, true) };
        this.teams = teams;
    }
}