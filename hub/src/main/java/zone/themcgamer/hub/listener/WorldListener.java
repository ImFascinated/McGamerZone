package zone.themcgamer.hub.listener;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import zone.themcgamer.hub.Hub;

/**
 * @author Braydon
 */
public class WorldListener implements Listener {
    public WorldListener(Hub hub) {
        Bukkit.getPluginManager().registerEvents(this, hub);
        for (World world : Bukkit.getWorlds()) {
            long time = 6000L;
            if (world.getName().toLowerCase().contains("christmas"))
                time = 12000L;
            else if (world.getName().toLowerCase().contains("halloween"))
                time = 17000L;
            world.setTime(time);
            world.setThundering(false);
            world.setStorm(false);
            world.setSpawnLocation(0, 50, 0);
            world.setGameRuleValue("randomTickSpeed", "0");
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("showDeathMessages", "false");
            world.setGameRuleValue("doFireTick", "false");
            world.setGameRuleValue("mobGriefing", "false");
            world.setGameRuleValue("doMobLoot", "false");
            world.setGameRuleValue("doMobSpawning", "false");
        }
    }

    @EventHandler
    private void onWeatherChange(WeatherChangeEvent event) {
        if (event.toWeatherState())
            event.setCancelled(true);
    }

    @EventHandler
    private void onTnTPrime(ExplosionPrimeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onLeaveDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void entityChangeSoil(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL)
            return;
        if (event.getClickedBlock().getType() == XMaterial.FARMLAND.parseMaterial())
            event.setCancelled(true);
    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (event.getRemover() instanceof Player)
            event.setCancelled(true);
    }
}