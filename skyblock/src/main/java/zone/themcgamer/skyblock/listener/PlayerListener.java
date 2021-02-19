package zone.themcgamer.skyblock.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import zone.themcgamer.skyblock.Skyblock;

public class PlayerListener implements Listener {
    public PlayerListener(Skyblock skyblock) {
        Bukkit.getPluginManager().registerEvents(this, skyblock);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }
}
