package zone.themcgamer.core.deliveryMan.rewardPackage;

import org.bukkit.entity.Player;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.common.SkullTexture;
import zone.themcgamer.core.deliveryMan.menu.DeliveryManMenu;

/**
 * @author Nicholas
 */
public abstract class RewardPackage {
    /**
     * Gets the {@link SkullTexture} which is shown in the {@link DeliveryManMenu}.
     *
     * @param player the player to get the texture for
     * @param account the account to get the texture for
     * @return the texture
     */
    public abstract String getIconTexture(Player player, Account account);
}