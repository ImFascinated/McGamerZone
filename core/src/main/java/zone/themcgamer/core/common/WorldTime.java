package zone.themcgamer.core.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public enum  WorldTime {
    SUNRISE("Sunrise", new String[] {
            "&7Beginning of the Minecraft day.",
            "&7Villagers awaken and rise from their beds."
    }, 24000L),
    DAY("Day", new String[] {
            "&7Villagers begin their workday."
    }, 2000L),
    NOON("Noon", new String[] {
            "&7The sun is at its peak."
    }, 6000L),
    SUNSET("Sunset", new String[] {
            "&7Villagers go to their beds and sleep."
    }, 12000L),
    NIGHT("Night", new String[] {
            "&7First tick when monsters spawn outdoors in clear weather."
    }, 13000L),
    MIDNIGHT("Mid Night", new String[] {
            "&7The moon is at its peak."
    }, 18000L);

    private final String displayName;
    private final String[] description;
    private final long time;
}