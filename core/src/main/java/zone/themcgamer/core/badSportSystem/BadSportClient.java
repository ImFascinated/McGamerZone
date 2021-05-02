package zone.themcgamer.core.badSportSystem;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Braydon
 */
@RequiredArgsConstructor @Setter @Getter
public class BadSportClient {
    private final String ip;
    private Set<Punishment> punishments = new HashSet<>();

    public Collection<Punishment> getPastOffenses(PunishmentOffense offense) {
        return filter(punishment -> punishment.getOffense() == offense && !punishment.wasRemoved());
    }

    /**
     * Get the currently active ban
     *
     * @return the optional ban
     */
    public Optional<Punishment> getBan() {
        return filterOne(punishment -> punishment.isActive()
                && (punishment.getCategory() == PunishmentCategory.BAN || punishment.getCategory() == PunishmentCategory.BLACKLIST));
    }

    /**
     * Get the currently active mute
     *
     * @return the optional mute
     */
    public Optional<Punishment> getMute() {
        return filterOne(punishment -> punishment.isActive() && (punishment.getCategory() == PunishmentCategory.MUTE));
    }

    /**
     * Get an optional {@link Punishment} that matches against the {@link Predicate}
     *
     * @param predicate the predicate to test against
     * @return the optional punishment
     */
    public Optional<Punishment> filterOne(Predicate<Punishment> predicate) {
        List<Punishment> punishments = new ArrayList<>(filter(predicate));
        if (punishments.isEmpty())
            return Optional.empty();
        return Optional.of(punishments.get(0));
    }

    /**
     * Get a {@link Collection} of punishments that matches against the {@link Predicate}
     *
     * @param predicate the predicate to test against
     * @return the collection of punishments
     */
    public Collection<Punishment> filter(Predicate<Punishment> predicate) {
        return punishments.stream().filter(predicate).collect(Collectors.toList());
    }
}