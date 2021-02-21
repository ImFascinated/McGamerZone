package zone.themcgamer.core.deliveryMan.rewardPackage.impl;

import org.bukkit.entity.Player;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.common.SkullTexture;
import zone.themcgamer.core.deliveryMan.rewardPackage.RewardPackage;

/**
 * @author Nicholas
 */
public class WeeklyRewardPackage extends RewardPackage {
    @Override
    public String getIconTexture(Player player, Account account) {
        return SkullTexture.DIAMOND_CUBE;
    }

    /**
     * Get the list of reward names for this reward package
     *
     * @param player  the player to get the list for
     * @param account the account to get the list for
     * @return the list
     */
    @Override
    public String[] getRewardNames(Player player, Account account) {
        return new String[0];
    }
}