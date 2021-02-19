package zone.themcgamer.core.common.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import zone.themcgamer.common.RandomUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Braydon
 */
public abstract class WritableScoreboard extends ScoreboardProvider {
    private final List<String> lines = new ArrayList<>();

    public WritableScoreboard(Player player) {
        super(player);
    }

    public abstract void writeLines();

    @Override
    public List<String> getLines() {
        lines.clear();
        writeLines();
        return lines;
    }

    protected void write(Object object) {
        lines.add(object.toString());
    }

    protected void writeBlank() {
        ChatColor color = RandomUtils.random(ChatColor.class);
        if (color == null)
            color = ChatColor.RESET;
        lines.add(color.toString());
    }
}