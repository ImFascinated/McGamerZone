package zone.themcgamer.hub;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.chat.ChatManager;
import zone.themcgamer.core.chat.component.IChatComponent;
import zone.themcgamer.core.chat.component.impl.BasicNameComponent;
import zone.themcgamer.core.chat.component.impl.BasicRankComponent;
import zone.themcgamer.core.common.MathUtils;
import zone.themcgamer.core.common.scoreboard.ScoreboardHandler;
import zone.themcgamer.core.deliveryMan.DeliveryManManager;
import zone.themcgamer.core.kingdom.KingdomManager;
import zone.themcgamer.core.plugin.MGZPlugin;
import zone.themcgamer.core.plugin.Startup;
import zone.themcgamer.core.world.MGZWorld;
import zone.themcgamer.hub.command.SpawnCommand;
import zone.themcgamer.hub.listener.PlayerListener;
import zone.themcgamer.hub.listener.WorldListener;
import zone.themcgamer.hub.scoreboard.HubScoreboard;

/**
 * @author Braydon
 */
@Getter
public class Hub extends MGZPlugin {
    public static Hub INSTANCE;

    private Location spawn;

    @Override
    public void onEnable() {
        super.onEnable();
        INSTANCE = this;
    }

    @Startup
    public void loadHub() {
        MGZWorld world = MGZWorld.get(Bukkit.getWorlds().get(0));
        spawn = world.getDataPoint("SPAWN");
        if (spawn != null)
            spawn.setYaw(MathUtils.getFacingYaw(spawn, world.getDataPoints("LOOK_AT")));
        else spawn = new Location(world.getWorld(), 0, 150, 0);

        //AccountManager.addMiniAccount(new KitManager(this));

        new PlayerListener(this);
        new WorldListener(this);
        new ScoreboardHandler(this, HubScoreboard.class, 3L);

        new ChatManager(this, accountManager, badSportSystem, new IChatComponent[] {
                new BasicRankComponent(),
                new BasicNameComponent()
        });
        new KingdomManager(this, traveler);

        AccountManager.addMiniAccount(new DeliveryManManager(this, mySQLController, true));

        commandManager.registerCommand(new SpawnCommand(this));
    }
}