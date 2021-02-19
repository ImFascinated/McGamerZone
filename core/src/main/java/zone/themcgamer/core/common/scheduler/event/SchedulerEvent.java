package zone.themcgamer.core.common.scheduler.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import zone.themcgamer.core.common.WrappedBukkitEvent;
import zone.themcgamer.core.common.scheduler.ScheduleType;

/**
 * @author Braydon
 */
@AllArgsConstructor @Getter
public class SchedulerEvent extends WrappedBukkitEvent {
    private final ScheduleType type;
}