package zone.themcgamer.arcade.game.impl;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import zone.themcgamer.arcade.game.Game;
import zone.themcgamer.arcade.player.GamePlayer;
import zone.themcgamer.arcade.team.Team;
import zone.themcgamer.core.game.MGZGame;

import java.util.Arrays;
import java.util.List;

/**
 * @author Braydon
 */
public class TheBridgeGame extends Game {
    public TheBridgeGame() {
        super(MGZGame.THE_BRIDGE, new Team[] {
                new Team("Red", ChatColor.RED, false),
                new Team("Blue", ChatColor.BLUE, false)
        });
        blockBreak = true;
        blockPlace = true;
    }

    @Override
    public List<String> getScoreboard(GamePlayer gamePlayer, Player player) {
        return Arrays.asList(
                "&b&lGame Duration",
                "§e2:50",
                "",
                "§c§lRED",
                "§fPlayers: §c0",
                "§fNexus: §c100",
                "",
                "§9§lBLUE",
                "§fPlayers: §90",
                "§fNexus: §9100"
        );
    }
}