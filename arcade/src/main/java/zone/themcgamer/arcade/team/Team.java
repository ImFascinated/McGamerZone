package zone.themcgamer.arcade.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import zone.themcgamer.core.common.SkullTexture;

/**
 * @author Braydon
 */
@AllArgsConstructor @Getter
public class Team {
    private final String name;
    private final ChatColor color;
    private final boolean friendlyFire;

    public String getSkullTexture() {
        switch (color) {
            case DARK_GREEN: {
                return SkullTexture.TEAM_GREEN;
            }
            case DARK_AQUA: {
                return SkullTexture.TEAM_CYAN;
            }
            case DARK_RED:
            case RED: {
                return SkullTexture.TEAM_RED;
            }
            case DARK_PURPLE: {
                return SkullTexture.TEAM_PURPLE;
            }
            case GOLD: {
                return SkullTexture.TEAM_ORANGE;
            }
            case GRAY: {
                return SkullTexture.TEAM_LIGHT_GRAY;
            }
            case DARK_GRAY: {
                return SkullTexture.TEAM_GRAY;
            }
            case BLUE: {
                return SkullTexture.TEAM_BLUE;
            }
            case GREEN: {
                return SkullTexture.TEAM_LIME;
            }
            case AQUA: {
                return SkullTexture.TEAM_LIGHT_BLUE;
            }
            case LIGHT_PURPLE: {
                return SkullTexture.TEAM_MAGENTA;
            }
            case YELLOW: {
                return SkullTexture.TEAM_YELLOW;
            }
        }
        return SkullTexture.TEAM_WHITE;
    }
}