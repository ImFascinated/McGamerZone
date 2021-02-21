package zone.themcgamer.core.cooldown;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import zone.themcgamer.common.TimeUtils;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.common.scheduler.ScheduleType;
import zone.themcgamer.core.common.scheduler.event.SchedulerEvent;
import zone.themcgamer.core.module.Module;
import zone.themcgamer.core.module.ModuleInfo;

import java.util.*;

/**
 * @author Braydon
 */
@ModuleInfo(name = "Cooldowns")
public class CooldownHandler extends Module {
    private static final Map<Player, List<Cooldown>> cooldowns = new HashMap<>();

    public CooldownHandler(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    private void expireCooldowns(SchedulerEvent event) {
        if (event.getType() != ScheduleType.TICK)
            return;
        for (Player player : Bukkit.getOnlinePlayers()) {
            List<Cooldown> cooldowns = getCooldowns(player);
            cooldowns.removeIf(cooldown -> {
                if (cooldown.getRemaining() > 0)
                    return false;
                if (cooldown.isInform())
                    player.sendMessage(Style.main("Cooldown", "Your cooldown for §f" + cooldown.getName() + " §7has expired"));
                return true;
            });
        }
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        cooldowns.remove(event.getPlayer());
    }

    public static boolean canUse(Player player, String name, long time, boolean inform) {
        List<Cooldown> cooldowns = getCooldowns(player);
        Optional<Cooldown> optionalCooldown = cooldowns.stream()
                .filter(cooldown -> cooldown.getName().equalsIgnoreCase(name) && cooldown.getRemaining() > 0)
                .findFirst();
        if (optionalCooldown.isPresent()) {
            if (inform) {
                player.sendMessage(Style.error("Cooldown", "§f" + name + " §cis still on cooldown for another §f" +
                        TimeUtils.convertString(optionalCooldown.get().getRemaining())));
            }
            return false;
        }
        cooldowns.add(new Cooldown(name, time, System.currentTimeMillis(), inform));
        CooldownHandler.cooldowns.put(player, cooldowns);
        return true;
    }

    public static Cooldown getCooldown(Player player, String name) {
        return getCooldowns(player).stream().filter(cooldown -> cooldown.getName().equals(name)).findFirst().orElse(null);
    }

    private static List<Cooldown> getCooldowns(Player player) {
        return cooldowns.getOrDefault(player, new ArrayList<>());
    }

}