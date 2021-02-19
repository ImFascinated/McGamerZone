package zone.themcgamer.core.common.scheduler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

/**
 * @author Braydon
 */
@RequiredArgsConstructor @Getter
public enum ScheduleType {
    // Hours
    TEN_HOURS(TimeUnit.HOURS.toMillis(10L)),
    FIVE_HOURS(TimeUnit.HOURS.toMillis(5L)),
    THREE_HOURS(TimeUnit.HOURS.toMillis(3L)),
    HOUR(TimeUnit.HOURS.toMillis(1L)),

    // Minutes
    THIRTY_MINUTES(TimeUnit.MINUTES.toMillis(30L)),
    TWENTY_MINUTES(TimeUnit.MINUTES.toMillis(20L)),
    FIFTEEN_MINUTES(TimeUnit.MINUTES.toMillis(15L)),
    TEN_MINUTES(TimeUnit.MINUTES.toMillis(10L)),
    FIVE_MINUTES(TimeUnit.MINUTES.toMillis(5L)),
    THREE_MINUTES(TimeUnit.MINUTES.toMillis(3L)),
    MINUTE(TimeUnit.MINUTES.toMillis(1L)),

    // Seconds
    THIRTY_SECONDS(TimeUnit.SECONDS.toMillis(30L)),
    TWENTY_SECONDS(TimeUnit.SECONDS.toMillis(20L)),
    FIFTEEN_SECONDS(TimeUnit.SECONDS.toMillis(15L)),
    TEN_SECONDS(TimeUnit.SECONDS.toMillis(10L)),
    FIVE_SECONDS(TimeUnit.SECONDS.toMillis(5L)),
    THREE_SECONDS(TimeUnit.SECONDS.toMillis(3L)),
    SECOND(TimeUnit.SECONDS.toMillis(1L)),
    HALF_SECOND(500L),

    // Ticks (a tick is 50ms, so we multiply each tick by 50)
    THIRTY_TICKS(30L * 50L),
    TWENTY_TICKS(20L * 50L),
    FIFTEEN_TICKS(15L * 50L),
    TEN_TICKS(10L * 50L),
    FIVE_TICKS(5L * 50L),
    THREE_TICKS(3L * 50L),
    TICK(50L);

    private final long time;
    private boolean firstRun = true;
    private long lastRun;

    public void run() {
        firstRun = false;
        lastRun = System.currentTimeMillis();
    }
}