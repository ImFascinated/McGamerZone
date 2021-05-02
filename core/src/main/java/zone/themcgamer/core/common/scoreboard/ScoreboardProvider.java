package zone.themcgamer.core.common.scoreboard;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import zone.themcgamer.core.common.HiddenStringUtils;

import java.util.*;

/**
 * @author Braydon
 */
@Getter
public abstract class ScoreboardProvider {
    protected final Player player;
    private final Set<String> identifiers = new HashSet<>();
    private final List<ScoreboardEntry> entries = new ArrayList<>();

    private Scoreboard scoreboard;
    private Objective objective;

    public ScoreboardProvider(Player player) {
        this.player = player;
        give();
    }

    public abstract String getTitle();
    public abstract List<String> getLines();

    /**
     * Get an entry at the provided index
     *
     * @param index - The index of the entry you would like to get
     * @return the entry at the provided index, null if none
     */
    protected ScoreboardEntry getEntryAtPosition(int index) {
        if (index < 0 || index >= entries.size())
            return null;
        return entries.get(index);
    }

    /**
     * Get a unique name for a team
     *
     * @return the unique name for a team
     */
    public String getTeamName() {
        String identifier = UUID.randomUUID().toString().replaceAll("-", "");
        identifier = HiddenStringUtils.encode(identifier).substring(0, 16);
        identifiers.add(identifier);
        return identifier;
    }

    /**
     * Give the player the scoreboard
     */
    private void give() {
        if (player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard()))
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        else scoreboard = player.getScoreboard();
        if (scoreboard.getObjective("KauriBoard") == null)
            objective = scoreboard.registerNewObjective("KauriBoard", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(getTitle());
        player.setScoreboard(scoreboard);
    }
}