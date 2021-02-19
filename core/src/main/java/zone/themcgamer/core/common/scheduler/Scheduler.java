package zone.themcgamer.core.common.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import zone.themcgamer.core.common.scheduler.event.SchedulerEvent;

/**
 * @author Braydon
 */
public class Scheduler {
    /**
     * This will loop through all scheduler types and check when the last
     * elapsed time was for that type. If the elapsed time is greater or equal
     * to the time specified in the type, then we will call the {@code SchedulerEvent}
     * for that type and run the {@code scheduleType.run()} method. The benefits of this
     * is so you don't need to create schedulers to say run something every x amount of
     * time, you can simply just listen to this event. This will also help with performance
     * so you don't need to create multiple schedulers and you can run everything off of one
     */
    public Scheduler(JavaPlugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (ScheduleType scheduleType : ScheduleType.values()) {
                if ((System.currentTimeMillis() - scheduleType.getLastRun()) < scheduleType.getTime())
                    continue;
                Bukkit.getPluginManager().callEvent(new SchedulerEvent(scheduleType));
                scheduleType.run();
            }
        }, 1L, 1L);
    }
}