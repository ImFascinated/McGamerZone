package zone.themcgamer.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Braydon
 */
@AllArgsConstructor
@Getter
public enum ChatFilterLevel {
    LOW(false, true),
    MEDIUM(true, true),
    HIGH(true, true);

    private final boolean urls, ips;
}