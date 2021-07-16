package zone.themcgamer.core.deliveryMan.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import zone.themcgamer.core.common.WrappedBukkitEvent;
import zone.themcgamer.core.deliveryMan.DeliveryManReward;

/*
  this event get called when player claims a reward.
 */
@AllArgsConstructor
@Getter
public class ClaimEvent extends WrappedBukkitEvent {
    private final Player player;
    private final DeliveryManReward deliveryManReward;
}
