package zone.themcgamer.buildServer;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import zone.themcgamer.buildServer.listener.PlayerListener;
import zone.themcgamer.buildServer.world.WorldManager;
import zone.themcgamer.core.chat.ChatManager;
import zone.themcgamer.core.chat.component.IChatComponent;
import zone.themcgamer.core.chat.component.impl.BasicNameComponent;
import zone.themcgamer.core.chat.component.impl.BasicRankComponent;
import zone.themcgamer.core.plugin.MGZPlugin;
import zone.themcgamer.core.plugin.Startup;

/**
 * @author Braydon
 */
@Getter
public class Build extends MGZPlugin {
    public static Build INSTANCE;

    private World mainWorld;

    @Override
    public void onEnable() {
        super.onEnable();
        INSTANCE = this;
    }

    @Startup
    public void loadBuildServer() {
        mainWorld = Bukkit.getWorlds().get(0);
        WorldManager worldManager = new WorldManager(this);
        new PlayerListener(this, worldManager);

        new ChatManager(this, accountManager, badSportSystem, new IChatComponent[] {
                new BasicRankComponent(),
                new BasicNameComponent()
        });
    }
}