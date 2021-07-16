package zone.themcgamer.buildServer.listener;

import com.google.common.base.Strings;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import zone.themcgamer.buildServer.Build;
import zone.themcgamer.buildServer.world.WorldManager;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.common.PlayerUtils;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.world.MGZWorld;
import zone.themcgamer.data.Rank;

import java.util.Optional;

/**
 * @author Braydon
 */
public class PlayerListener implements Listener {
    private final WorldManager worldManager;

    public PlayerListener(JavaPlugin plugin, WorldManager worldManager) {
        this.worldManager = worldManager;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getGameMode() != GameMode.CREATIVE)
                    player.setGameMode(GameMode.CREATIVE);
                if (!player.getAllowFlight())
                    player.setAllowFlight(true);
            }
        }, 20L, 20L);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onLogin(PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED)
            return;
        Player player = event.getPlayer();
        Optional<Account> optionalAccount = AccountManager.fromCache(player.getUniqueId());
        if (player.isOp() || player.isWhitelisted() || (optionalAccount.isPresent() && (optionalAccount.get().hasRank(Rank.BUILDER))))
            return;
        event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cOnly builders can join this server!");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerUtils.reset(player, true, false, GameMode.CREATIVE);

        player.sendMessage(Style.color("&8&m" + Strings.repeat("-", 30)));
        player.sendMessage("");
        player.sendMessage(Style.color(" &e➢ &6&lBuild"));
        player.sendMessage("");
        player.sendMessage(Style.color("    &7For a list of commands, use &f/help"));
        player.sendMessage(Style.color("    &7Wanna learn how to use the build system? Use &f/tutorial"));
        player.sendMessage("");
        player.sendMessage(Style.color("&8&m" + Strings.repeat("-", 30)));

        player.teleport(Build.INSTANCE.getMainWorld().getSpawnLocation());
        event.setJoinMessage(Style.color("&8[&a+&8] &7" + player.getName()));
    }

    @EventHandler
    private void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Optional<Account> optionalAccount = AccountManager.fromCache(player.getUniqueId());
        if (optionalAccount.isEmpty()) {
            player.sendMessage(Style.error("Chat", "§cCannot send chat message"));
            return;
        }
        MGZWorld mgzWorld = worldManager.getWorld(player.getWorld());
        event.setFormat((mgzWorld == null ? "" : "§7[" + mgzWorld.getName() + "§7] ") + "§f" +
                optionalAccount.get().getPrimaryRank().getColor() + player.getName() + "§7: §f" + event.getMessage().replaceAll("%", "%%"));
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            event.setCancelled(true);
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID)
                entity.teleport(entity.getWorld().getSpawnLocation());
        }
    }

    @EventHandler
    private void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(Style.color("&8[&c-&8] &7" + event.getPlayer().getName()));
    }

    @EventHandler
    private void onWorldChange(PlayerChangedWorldEvent event) {
        event.getFrom().save();
    }
}