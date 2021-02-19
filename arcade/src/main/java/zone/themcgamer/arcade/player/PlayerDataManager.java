package zone.themcgamer.arcade.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import zone.themcgamer.core.common.scheduler.ScheduleType;
import zone.themcgamer.core.common.scheduler.event.SchedulerEvent;
import zone.themcgamer.core.module.Module;
import zone.themcgamer.core.module.ModuleInfo;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Braydon
 */
@ModuleInfo(name = "Player Data Manager")
public class PlayerDataManager extends Module {
    // The amount of time a player has to rejoin the game after being disconnected.
    // If the player does not join within the given time, their stats for this current
    // game will be cleared
    private static final long MAX_REJOIN_TIME = TimeUnit.MINUTES.toMillis(3L);

    public PlayerDataManager(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    private void expirePlayers(SchedulerEvent event) {
        if (event.getType() != ScheduleType.SECOND)
            return;
        GamePlayer.getCache().entrySet().removeIf(entry -> {
            GamePlayer gamePlayer = entry.getValue();
            if (gamePlayer.getBukkitPlayer() != null)
                return false;
            return (System.currentTimeMillis() - gamePlayer.getLogoutTime()) >= MAX_REJOIN_TIME;
        });
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (GamePlayer.getPlayer(uuid) == null)
            new GamePlayer(uuid);
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        GamePlayer gamePlayer = GamePlayer.getPlayer(event.getPlayer().getUniqueId());
        if (gamePlayer != null)
            gamePlayer.setLogoutTime(System.currentTimeMillis());
    }
}