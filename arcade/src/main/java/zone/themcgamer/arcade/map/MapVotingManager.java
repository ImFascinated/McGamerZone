package zone.themcgamer.arcade.map;

import com.cryptomorin.xseries.XSound;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import zone.themcgamer.arcade.game.GameState;
import zone.themcgamer.arcade.manager.ArcadeManager;
import zone.themcgamer.arcade.manager.LobbyManager;
import zone.themcgamer.arcade.map.event.MapVoteWinEvent;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.common.scheduler.ScheduleType;
import zone.themcgamer.core.common.scheduler.event.SchedulerEvent;
import zone.themcgamer.core.module.Module;
import zone.themcgamer.core.module.ModuleInfo;
import zone.themcgamer.core.world.MGZWorld;
import zone.themcgamer.core.world.WorldCategory;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Braydon
 */
@ModuleInfo(name = "Map Voting")
public class MapVotingManager extends Module {
    private final ArcadeManager arcadeManager;
    private final MapManager mapManager;

    @Getter private boolean voting;
    private long startedVoting, votingTime;
    @Getter private final Map<MGZWorld, Integer> maps = new HashMap<>();
    @Getter private final Set<UUID> voted = new HashSet<>();

    public MapVotingManager(JavaPlugin plugin, ArcadeManager arcadeManager, MapManager mapManager) {
        super(plugin);
        this.arcadeManager = arcadeManager;
        this.mapManager = mapManager;
    }

    @EventHandler
    private void onSchedule(SchedulerEvent event) {
        if (event.getType() != ScheduleType.SECOND
                || !voting
                || startedVoting == -1L
                || (System.currentTimeMillis() - startedVoting) < votingTime)
            return;
        List<Map.Entry<MGZWorld, Integer>> entries = new ArrayList<>(maps.entrySet());
        entries.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        Map.Entry<MGZWorld, Integer> entry = entries.get(0);
        MGZWorld map = entry.getKey();
        int votes = entry.getValue();
        Bukkit.getPluginManager().callEvent(new MapVoteWinEvent(map, votes));

        Bukkit.broadcastMessage(Style.main("Voting", "Map §f" + map.getName() + " §7won with §6" +
                votes + " §7vote" + (votes == 1 ? "" : "s") + "!"));
        stopVoting();
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        if (voting || arcadeManager.getState() != GameState.LOBBY)
            return;
        if (Bukkit.getOnlinePlayers().size() >= arcadeManager.getGame().getMgzGame().getMinPlayers()) {
            startVoting();
        }
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        if (Bukkit.getOnlinePlayers().size() - 1 < arcadeManager.getGame().getMgzGame().getMinPlayers() && voting) {
            stopVoting();
            Bukkit.broadcastMessage(Style.main("Voting", "§cMap voting has ended as there are not enough players!"));
        }
    }

    public void startVoting() {
        startVoting(TimeUnit.SECONDS.toMillis(30L));
    }

    public void startVoting(long time) {
        List<WorldCategory> worldCategories = Arrays.asList(arcadeManager.getGame().getMgzGame().getWorldCategories());
        List<MGZWorld> maps = mapManager.getMaps().stream()
                .filter(mgzWorld -> worldCategories.contains(mgzWorld.getCategory()))
                .collect(Collectors.toList());
        if (maps.isEmpty())
            return;
        Collections.shuffle(maps);
        voting = true;
        startedVoting = System.currentTimeMillis();
        votingTime = time;
        for (int i = 0; i < Math.min(maps.size(), 5); i++)
            this.maps.put(maps.get(i), 0);
        if (!voting)
            return;
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getEyeLocation(), XSound.ENTITY_VILLAGER_AMBIENT.parseSound(), 0.9F, 0.1F);
            player.sendMessage(Style.main("Voting", "Now let's vote for a map you like, &6" + player.getName()));
        }
    }

    public void stopVoting() {
        voting = false;
        startedVoting = votingTime = -1L;
        maps.clear();
        voted.clear();
        for (Player player : Bukkit.getOnlinePlayers())
            player.getInventory().remove(LobbyManager.MAP_VOTE);
    }
}