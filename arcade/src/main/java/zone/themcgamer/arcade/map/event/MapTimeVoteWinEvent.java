package zone.themcgamer.arcade.map.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import zone.themcgamer.core.common.WorldTime;
import zone.themcgamer.core.common.WrappedBukkitEvent;

/**
 * @author Braydon
 */
@AllArgsConstructor @Getter
public class MapTimeVoteWinEvent extends WrappedBukkitEvent {
    private final WorldTime worldTime;
    private final int votes;
}