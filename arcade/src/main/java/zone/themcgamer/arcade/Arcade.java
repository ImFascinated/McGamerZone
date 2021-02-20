package zone.themcgamer.arcade;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import zone.themcgamer.arcade.manager.ArcadeManager;
import zone.themcgamer.arcade.player.PlayerDataManager;
import zone.themcgamer.arcade.scoreboard.ArcadeScoreboard;
import zone.themcgamer.core.chat.ChatManager;
import zone.themcgamer.core.chat.component.IChatComponent;
import zone.themcgamer.core.chat.component.impl.BasicNameComponent;
import zone.themcgamer.core.chat.component.impl.BasicRankComponent;
import zone.themcgamer.core.common.MathUtils;
import zone.themcgamer.core.common.scoreboard.ScoreboardHandler;
import zone.themcgamer.core.plugin.MGZPlugin;
import zone.themcgamer.core.plugin.Startup;
import zone.themcgamer.core.world.MGZWorld;

/**
 * @author Braydon
 */
@Getter
public class Arcade extends MGZPlugin {
    public static Arcade INSTANCE;

    private ArcadeManager arcadeManager;
    private Location spawn;

    @Override
    public void onEnable() {
        super.onEnable();
        INSTANCE = this;
    }

    @Startup
    public void loadArcade() {
        new PlayerDataManager(this);
        arcadeManager = new ArcadeManager(this, traveler);

        new ScoreboardHandler(this, ArcadeScoreboard.class, 3L);

        new ChatManager(this, badSportSystem, new IChatComponent[] {
                new BasicRankComponent(),
                new BasicNameComponent()
        });

        MGZWorld world = MGZWorld.get(Bukkit.getWorlds().get(0));
        spawn = world.getDataPoint("SPAWN");
        if (spawn != null)
            spawn.setYaw(MathUtils.getFacingYaw(spawn, world.getDataPoints("LOOK_AT")));
        else spawn = new Location(world.getWorld(), 0, 150, 0);
    }
}