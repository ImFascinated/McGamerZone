package zone.themcgamer.skyblock;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.handlers.GridManager;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import zone.themcgamer.core.chat.ChatManager;
import zone.themcgamer.core.chat.component.IChatComponent;
import zone.themcgamer.core.chat.component.impl.BasicNameComponent;
import zone.themcgamer.core.chat.component.impl.BasicRankComponent;
import zone.themcgamer.core.common.scoreboard.ScoreboardHandler;
import zone.themcgamer.core.plugin.MGZPlugin;
import zone.themcgamer.core.plugin.Startup;
import zone.themcgamer.skyblock.chat.SkyblockChatLevelComponent;
import zone.themcgamer.skyblock.commands.StartCommand;
import zone.themcgamer.skyblock.listener.PlayerListener;
import zone.themcgamer.skyblock.scoreboard.SkyblockScoreboard;

@Getter
public class Skyblock extends MGZPlugin {
    public static Skyblock INSTANCE;

    @Override
    public void onEnable() {
        super.onEnable();
        INSTANCE = this;
    }

    @Startup
    public void loadSkyblockServer() {
        long time = System.currentTimeMillis();
        new PlayerListener(this);
        new ScoreboardHandler(this, SkyblockScoreboard.class, 3L);

        new ChatManager(this, accountManager, badSportSystem, new IChatComponent[] {
                new SkyblockChatLevelComponent(),
                new BasicRankComponent(),
                new BasicNameComponent()
        });

        commandManager.registerCommand(this, new StartCommand());
        getServer().getConsoleSender().sendMessage("Loaded skyblock module in " + (System.currentTimeMillis() - time) + "ms!");

        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.broadcastMessage("Recalculating islands...");
                SuperiorSkyblockAPI.calcAllIslands();
                Bukkit.broadcastMessage("Completed!");
            }
        }.runTaskTimer(this,0, 900 * 20);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player onlinePlayer : getServer().getOnlinePlayers()) {
                    SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(onlinePlayer);
                    if (superiorPlayer.getIsland() == null)
                        onlinePlayer.sendTitle("§bGet Started!", "§7Create an island - §e/start");
                }
            }
        }.runTaskTimer(this,0, 10 * 20);
    }
}
