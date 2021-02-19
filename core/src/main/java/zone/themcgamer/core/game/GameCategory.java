package zone.themcgamer.core.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public enum GameCategory {
    SURVIVE("§aSurvive"),
    MINIGAME("§2Minigame"),
    PVP("§cPvP"),
    FREEBUILD("§bFree Build"),
    COMPETITIVE("§3Competitive");

    private final String name;
}
