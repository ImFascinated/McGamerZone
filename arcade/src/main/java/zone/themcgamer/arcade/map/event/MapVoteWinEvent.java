package zone.themcgamer.arcade.map.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import zone.themcgamer.core.common.WrappedBukkitEvent;
import zone.themcgamer.core.world.MGZWorld;

/**
 * @author Braydon
 */
@AllArgsConstructor @Getter
public class MapVoteWinEvent extends WrappedBukkitEvent {
    private final MGZWorld map;
    private final int votes;
}