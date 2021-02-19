package zone.themcgamer.core.animation;

import lombok.Getter;

/**
 * @author Braydon
 * @implNote Simple text animation base
 */
@Getter
public abstract class TextAnimation {
    private final String input;
    protected int index;

    public TextAnimation(String input) {
        this.input = input;
    }

    public abstract String next();
}