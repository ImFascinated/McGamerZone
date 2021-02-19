package zone.themcgamer.api;

import zone.themcgamer.data.APIAccessLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Braydon
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RestPath {
    String path();

    APIVersion version();

    APIAccessLevel accessLevel() default APIAccessLevel.STANDARD;

    String[] headers() default {};
}