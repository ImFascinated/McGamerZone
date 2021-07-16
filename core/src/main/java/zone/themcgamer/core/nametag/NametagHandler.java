package zone.themcgamer.core.nametag;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import zone.themcgamer.core.common.ServerUtils;
import zone.themcgamer.core.common.ServerVersion;
import zone.themcgamer.core.common.Style;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Braydon (credits: https://github.com/sgtcaze/NametagEdit)
 */
public class NametagHandler implements Listener {
    public static final boolean DISABLE_PUSH = false;

    private final JavaPlugin plugin;
    private final NametagManager nametagManager;
    private final Map<UUID, Nametag> nametags = new HashMap<>();

    public NametagHandler(JavaPlugin plugin, NametagManager nametagManager) {
        this.plugin = plugin;
        this.nametagManager = nametagManager;
        Bukkit.getScheduler().runTaskTimer(plugin, this::applyTags, 0L, 20L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onJoin(PlayerJoinEvent event) {
        nametagManager.sendTeams(event.getPlayer());
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        nametagManager.reset(event.getPlayer().getName());
    }

    private void applyTags() {
        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(plugin, this::applyTags);
            return;
        }
        for (Player online : ServerUtils.getLoadedPlayers())
            applyTagToPlayer(online);
    }

    private void applyTagToPlayer(Player player) {
        // If on the primary thread, run async
        if (Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> applyTagToPlayer(player));
            return;
        }
        Nametag nametag = nametags.get(player.getUniqueId());
        if (nametag == null)
            return;
        Bukkit.getScheduler().runTask(plugin, () -> {
            nametagManager.setNametag(
                    player,
                    formatWithPlaceholders(player, nametag.getPrefix(), true),
                    formatWithPlaceholders(player, nametag.getSuffix(), true),
                    nametag.getPriority()
            );
        });
    }

    private String formatWithPlaceholders(Player player, String input, boolean limitChars) {
        if (input == null)
            return "";
        if (player == null)
            return input;
        String colored = Style.color(input);
        String s = limitChars && colored.length() > 128 ? colored.substring(0, 128) : colored;
        switch (ServerVersion.getVersion()) {
            case v1_13_R1:
            case v1_14_R1:
            case v1_14_R2:
            case v1_15_R1:
            case v1_16_R1:
            case v1_16_R2:
            case v1_16_R3: {
                return s;
            }
            default: {
                return limitChars && colored.length() > 16 ? colored.substring(0, 16) : colored;
            }
        }
    }
}