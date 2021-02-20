package zone.themcgamer.core.deliveryMan;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Nicholas
 */
@RequiredArgsConstructor @Setter @Getter
public class DeliveryManClient {
    private final Map<DeliveryManReward, Long> lastClaimedRewards = new HashMap<>();
}