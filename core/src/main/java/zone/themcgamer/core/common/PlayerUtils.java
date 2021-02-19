package zone.themcgamer.core.common;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

/**
 * @author Braydon
 */
public class PlayerUtils {
    /**
     * Reset the given {@link Player} to it's original state
     * @param player the player to reset
     * @param effects whether or not to clear the player's effects
     * @param inventory whether or not to clear the player's inventory
     * @param gameMode the {@link GameMode} to put the player in
     */
    public static void reset(Player player, boolean effects, boolean inventory, GameMode gameMode) {
        if (effects) {
            for (PotionEffect potionEffect : player.getActivePotionEffects())
                player.removePotionEffect(potionEffect.getType());
            player.getActivePotionEffects().clear();
        }
        if (gameMode != null)
            player.setGameMode(gameMode);
        player.setAllowFlight(false);
        player.setSprinting(false);
        player.setFoodLevel(20);
        player.setSaturation(3.0f);
        player.setExhaustion(0.0f);
        player.setMaxHealth(20.0);
        player.setHealth(player.getMaxHealth());
        player.setFireTicks(0);
        player.setFallDistance(0.0f);
        player.setLevel(0);
        player.setExp(0.0f);
        player.setWalkSpeed(0.2f);
        player.setFlySpeed(0.1f);
        if (inventory) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.updateInventory();
        }
    }
}