package zone.themcgamer.core.deliveryMan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import zone.themcgamer.core.deliveryMan.rewardPackage.RewardPackage;
import zone.themcgamer.core.deliveryMan.rewardPackage.impl.DailyRewardPackage;
import zone.themcgamer.core.deliveryMan.rewardPackage.impl.MonthlyRewardPackage;
import zone.themcgamer.core.deliveryMan.rewardPackage.impl.WeeklyRewardPackage;
import zone.themcgamer.data.Rank;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author Nicholas
 */
@AllArgsConstructor @Getter
public enum DeliveryManReward {
    DAILY("dailyReward", "Daily Reward", Rank.DEFAULT, TimeUnit.DAYS.toMillis(1L), new DailyRewardPackage()),
    WEEKLY("weeklyReward", "Weekly Reward", Rank.DEFAULT, TimeUnit.DAYS.toMillis(7L), new WeeklyRewardPackage()),
    MONTHLY("monthlyReward", "Monthly Reward", Rank.GAMER, TimeUnit.DAYS.toMillis(30L), new MonthlyRewardPackage());

    private final String id, displayName;
    private final Rank requiredRank;
    private final long claimCooldown;
    private final RewardPackage rewardPackage;

    /**
     * Get the {@link DeliveryManReward} matching the given id
     *
     * @param id the id
     * @return the reward, otherwise null
     */
    public static DeliveryManReward match(String id) {
        return Arrays.stream(values()).filter(reward -> reward.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }
}