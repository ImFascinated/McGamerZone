package zone.themcgamer.core.badSportSystem;

import lombok.*;

import java.util.Objects;
import java.util.UUID;

/**
 * @author Braydon
 */
@AllArgsConstructor @RequiredArgsConstructor @Getter @ToString
public class Punishment {
    @Setter private int id;
    private final String targetIp;
    private final UUID targetUuid;
    private final PunishmentCategory category;
    private final PunishmentOffense offense;
    private final int severity;
    private final UUID staffUuid;
    private final String staffName;
    private final long timeIssued, duration;
    private final String reason;
    private UUID removeStaffUuid;
    private String removeStaffName, removeReason;
    private long timeRemoved = -1L;

    public void remove(UUID staffUuid, String staffName, String reason) {
        if (!isActive())
            throw new IllegalStateException("Cannot remove punishment, it's not active");
        removeStaffUuid = staffUuid;
        removeStaffName = staffName;
        removeReason = reason;
        timeRemoved = System.currentTimeMillis();
    }

    public boolean isIP() {
        return category.isIp();
    }

    public boolean issuedByConsole() {
        return staffUuid == null;
    }

    public boolean isActive() {
        if (category == PunishmentCategory.KICK || category == PunishmentCategory.WARN)
            return false;
        if (wasOverriden())
            return false;
        if (wasRemoved())
            return false;
        if (isPermanent())
            return true;
        return !hasExpired();
    }

    public boolean hasExpired() {
        return getRemaining() <= 0L;
    }

    public long getRemaining() {
        return isPermanent() ? -1L : (timeIssued + duration) - System.currentTimeMillis();
    }

    public boolean isPermanent() {
        return duration == -1L;
    }

    public boolean wasOverriden() {
        return removeStaffUuid == null && removeStaffName == null && timeRemoved != -1L;
    }

    public boolean wasRemoved() {
        return removeStaffName != null && timeRemoved != -1L;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        Punishment that = (Punishment) other;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}