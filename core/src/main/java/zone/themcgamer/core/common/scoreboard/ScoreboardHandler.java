package zone.themcgamer.core.common.scoreboard;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Objective;
import zone.themcgamer.core.common.Style;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Braydon
 */
@Getter
public class ScoreboardHandler implements Listener {
    @Getter private static ScoreboardHandler instance;

    private final JavaPlugin plugin;

    private final Class<? extends ScoreboardProvider> boardClass;
    private Thread thread;
    private final long delay;
    private boolean running;

    private final Map<Player, ScoreboardProvider> boards = new ConcurrentHashMap<>();

    public ScoreboardHandler(JavaPlugin plugin, Class<? extends ScoreboardProvider> boardClass, long delay) {
        instance = this;
        this.plugin = plugin;
        this.boardClass = boardClass;
        this.delay = delay;
        running = true;

        (thread = new Thread("Scoreboard Thread") {
            @Override
            public void run() {
                while (running) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ScoreboardProvider provider = boards.get(player);
                        if (provider == null)
                            continue;
                        Objective objective = provider.getObjective();
                        String title = provider.getTitle();

                        if (!objective.getDisplayName().equals(title))
                            objective.setDisplayName(title);

                        List<String> lines = provider.getLines();
                        if (lines == null || (lines.isEmpty())) {
                            provider.getEntries().forEach(ScoreboardEntry::remove);
                            provider.getEntries().clear();
                        } else {
                            Collections.reverse(lines);
                            if (provider.getEntries().size() > lines.size()) {
                                for (int i = lines.size(); i < provider.getEntries().size(); i++) {
                                    ScoreboardEntry entry = provider.getEntryAtPosition(i);
                                    if (entry != null)
                                        entry.remove();
                                }
                            }
                            for (int i = 0; i < lines.size(); i++) {
                                ScoreboardEntry entry = provider.getEntryAtPosition(i);
                                String line = Style.color(lines.get(i));
                                int lineNumber = i + 1;
                                if (entry == null) {
                                    entry = new ScoreboardEntry(provider, line);
                                    entry.setText(line);
                                    entry.display(lineNumber);
                                } else {
                                    if (!entry.getText().equals(line)) {
                                        entry.setText(line);
                                        entry.display(lineNumber);
                                    }
                                }
                            }
                        }
                    }
                    try {
                        sleep(delay * 50L);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }).start();
        for (Player player : Bukkit.getOnlinePlayers())
            giveBoard(player);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * When a player joins the server, we wanna give them
     * the scoreboard
     */
    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        giveBoard(event.getPlayer());
    }

    /**
     * When a player leaves the server, we wanna remove the
     * scoreboard from them
     */
    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        removeBoard(event.getPlayer());
    }

    /**
     * When the plugin is disabled, we wanna stop the
     * scoreboard thread and remove the scoreboard from
     * all online players
     */
    @EventHandler
    private void onDisable(PluginDisableEvent event) {
        if (event.getPlugin().equals(plugin)) {
            if (thread != null) {
                thread.stop();
                thread = null;
            }
            running = false;
            for (Player player : Bukkit.getOnlinePlayers())
                removeBoard(player);
        }
    }

    /**
     * Give the provided player the scoreboard
     *
     * @param player - The player you would like to give the scoreboard to
     */
    public void giveBoard(Player player) {
        if (boards.containsKey(player))
            throw new IllegalStateException("Player '" + player.getName() + "' already has the scoreboard");
        try {
            ScoreboardProvider provider = (ScoreboardProvider) boardClass.getConstructors()[0].newInstance(player);
            boards.put(player, provider);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Remove the scoreboard from the provided player
     *
     * @param player - The player you would like to remove the scoreboard from
     */
    public void removeBoard(Player player) {
        if (!boards.containsKey(player))
            throw new IllegalStateException("Player '" + player.getName() + "' does not have the scoreboard");
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        boards.remove(player);
    }
}