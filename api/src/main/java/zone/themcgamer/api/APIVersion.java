package zone.themcgamer.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Braydon
 */
@AllArgsConstructor @Getter
public enum APIVersion {
    V1("v1");

    private final String name;
}