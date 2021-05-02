package zone.themcgamer.core.command.help;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This class holds information for a help menu color scheme
 *
 * @author Braydon
 */
@AllArgsConstructor @Getter
public class HelpColorScheme {
    private final String header, primaryColor, secondaryColor;
}