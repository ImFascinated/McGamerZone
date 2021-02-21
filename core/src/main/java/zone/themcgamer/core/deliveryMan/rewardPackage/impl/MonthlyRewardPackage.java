package zone.themcgamer.core.deliveryMan.rewardPackage.impl;

import org.bukkit.entity.Player;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.common.SkullTexture;
import zone.themcgamer.core.deliveryMan.rewardPackage.RewardPackage;
import zone.themcgamer.data.Rank;

/**
 * @author Nicholas
 */
public class MonthlyRewardPackage extends RewardPackage {
    @Override
    public String getIconTexture(Player player, Account account) {
        if (account.hasRank(Rank.ULTIMATE))
            return SkullTexture.BACKPACK_PURPLE;
        else if (account.hasRank(Rank.EXPERT))
            return SkullTexture.BACKPACK_LIGHT_BLUE;
        else if (account.hasRank(Rank.HERO))
            return SkullTexture.BACKPACK_YELLOW;
        else if (account.hasRank(Rank.SKILLED))
            return SkullTexture.BACKPACK_ORANGE;
        else if (account.hasRank(Rank.GAMER))
            return SkullTexture.BACKPACK_GREEN;
        else
            return SkullTexture.BACKPACK_GRAY;
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