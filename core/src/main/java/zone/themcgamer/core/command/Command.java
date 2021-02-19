package zone.themcgamer.core.command;

import zone.themcgamer.data.Rank;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation holds information for a command
 * @author Braydon
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    String name();

    String[] aliases() default {};

    String usage() default "";

    String description() default "";

    Rank[] ranks() default { Rank.DEFAULT };

    boolean terminalOnly() default false;

    boolean playersOnly() default false;
}