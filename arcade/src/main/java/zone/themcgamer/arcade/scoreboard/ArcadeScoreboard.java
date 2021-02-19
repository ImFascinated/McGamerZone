package zone.themcgamer.arcade.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import zone.themcgamer.arcade.Arcade;
import zone.themcgamer.arcade.game.GameState;
import zone.themcgamer.arcade.manager.ArcadeManager;
import zone.themcgamer.arcade.map.GameMap;
import zone.themcgamer.arcade.player.GamePlayer;
import zone.themcgamer.common.DoubleUtils;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.animation.impl.WaveAnimation;
import zone.themcgamer.core.common.scoreboard.WritableScoreboard;
import zone.themcgamer.core.plugin.MGZPlugin;
import zone.themcgamer.data.jedis.data.server.MinecraftServer;

import java.util.Optional;

/**
 * @author Braydon
 */
public class ArcadeScoreboard extends WritableScoreboard {
    private final GamePlayer gamePlayer;
    private WaveAnimation title;

    public ArcadeScoreboard(Player player) {
        super(player);
        gamePlayer = GamePlayer.getPlayer(player.getUniqueId());
    }

    @Override
    public String getTitle() {
        ArcadeManager arcadeManager = Arcade.INSTANCE.getArcadeManager();
        String title = arcadeManager.getGame().getMgzGame().getName();
        if (this.title == null || (!this.title.getInput().equals(title))) {
            this.title = new WaveAnimation(title)
                    .withPrimary(ChatColor.GOLD.toString())
                    .withSecondary(ChatColor.WHITE.toString())
                    .withBold();
        }
        return this.title.next();

    }

    @Override
    public void writeLines() {
        Optional<Account> optionalAccount = AccountManager.fromCache(player.getUniqueId());
        if (optionalAccount.isEmpty()) {
            writeBlank();
            return;
        }
        Account account = optionalAccount.get();
        ArcadeManager arcadeManager = Arcade.INSTANCE.getArcadeManager();
        GameState state = arcadeManager.getState();
        MinecraftServer minecraftServer = MGZPlugin.getMinecraftServer();

        GameMap map = arcadeManager.getMap();

        writeBlank();
        switch (state) {
            case LOBBY:
            case STARTING: {
                write("§e▪ §fKit: §bWarrior");
                write("§e▪ §fGold: §6" + DoubleUtils.format(account.getGold(), true) + " \u26C3");
                writeBlank();
                write("§e▪ §fMap: §b" + (arcadeManager.getMapVotingManager().isVoting() ? "Voting..." : (map == null ? "None" : map.getMgzWorld().getName())));
                write("§e▪ §fState: §b" + state.getDisplayName());
                write("§e▪ §fPlayers: §a" + Bukkit.getOnlinePlayers().size() + "§7/§c" + arcadeManager.getGame().getMgzGame().getMaxPlayers());
                writeBlank();
                write("§e▪ §fGame: §b" + minecraftServer.getId().replace(minecraftServer.getGroup().getName().toLowerCase(), ""));
                break;
            }
            case PLAYING:
            case ENDING: {
                for (String line : arcadeManager.getGame().getScoreboard(gamePlayer, player)) {
                    if (line.trim().isEmpty())
                        writeBlank();
                    else write(line);
                }
                break;
            }
        }
        writeBlank();
        write("§6themcgamer.zone");
    }
}