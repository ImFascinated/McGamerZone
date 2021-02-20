package zone.themcgamer.core.deliveryMan.rewardPackage.impl;

import org.bukkit.entity.Player;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.common.SkullTexture;
import zone.themcgamer.core.deliveryMan.rewardPackage.RewardPackage;

/**
 * @author Nicholas
 */
public class DailyRewardPackage extends RewardPackage {
    @Override
    public String getIconTexture(Player player, Account account) {
        return SkullTexture.GOLD_CUBE;
    }
}