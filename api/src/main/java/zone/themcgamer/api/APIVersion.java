package zone.themcgamer.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The API version is used in {@link RestPath}. It represents the version the path is using
 *
 * @author Braydon
 */
@AllArgsConstructor @Getter
public enum APIVersion {
    V1("v1");

    private final String name;
}