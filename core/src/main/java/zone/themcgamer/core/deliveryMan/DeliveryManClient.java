package zone.themcgamer.core.deliveryMan;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.account.AccountManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Nicholas
 */
@RequiredArgsConstructor @Setter @Getter
public class DeliveryManClient {
    private final Map<DeliveryManReward, Long> lastClaimedRewards = new HashMap<>();

    public int getUnclaimedRewards(Player player) {
        Optional<Account> optionalAccount = AccountManager.fromCache(player.getUniqueId());
        if (optionalAccount.isEmpty())
            return 0;
        return Math.toIntExact(Arrays.stream(DeliveryManReward.values())
                .filter(reward -> canClaim(reward) && optionalAccount.get().hasRank(reward.getRequiredRank()))
                .count());
    }

    /**
     * Claim the provided {@link DeliveryManReward}
     *
     * @param reward the reward to claim
     */
    public void claim(DeliveryManReward reward) {
        claim(reward, System.currentTimeMillis());
    }

    /**
     * Claim the provided {@link DeliveryManReward}
     *
     * @param reward the reward to claim
     * @param time the time the reward was claimed at
     */
    public void claim(DeliveryManReward reward, long time) {
        lastClaimedRewards.put(reward, time);
    }

    /**
     * Check whether or not the provided {@link DeliveryManReward} can be claimed
     *
     * @param reward the reward to check
     * @return the claimable state
     */
    public boolean canClaim(DeliveryManReward reward) {
        return getTimeSinceLastClaim(reward) > reward.getClaimCooldown();
    }

    /**
     * Get the elapsed time since the provided {@link DeliveryManReward} was claimed
     *
     * @param reward the reward to check
     * @return the elapsed time
     */
    public long getTimeSinceLastClaim(DeliveryManReward reward) {
        return System.currentTimeMillis() - getLastClaim(reward);
    }

    /**
     * Get the time the provided {@link DeliveryManReward} was claimed
     *
     * @param reward the reward to check
     * @return the time claimed
     */
    public long getLastClaim(DeliveryManReward reward) {
        return lastClaimedRewards.getOrDefault(reward, -1L);
    }
}