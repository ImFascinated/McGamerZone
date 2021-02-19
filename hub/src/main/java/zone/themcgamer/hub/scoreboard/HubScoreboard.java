package zone.themcgamer.hub.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import zone.themcgamer.common.DoubleUtils;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.animation.impl.WaveAnimation;
import zone.themcgamer.core.common.scoreboard.WritableScoreboard;
import zone.themcgamer.core.plugin.MGZPlugin;
import zone.themcgamer.data.jedis.cache.CacheRepository;
import zone.themcgamer.data.jedis.cache.impl.PlayerStatusCache;
import zone.themcgamer.data.jedis.repository.RedisRepository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Braydon
 */
public class HubScoreboard extends WritableScoreboard {
    private WaveAnimation title;

    public HubScoreboard(Player player) {
        super(player);
    }

    @Override
    public String getTitle() {
        if (title == null) {
            title = new WaveAnimation("McGamerZone")
                    .withPrimary(ChatColor.GREEN.toString())
                    .withSecondary(ChatColor.GOLD.toString())
                    .withTertiary(ChatColor.RED.toString())
                    .withBold();
        }
        return title.next();
    }

    @Override
    public void writeLines() {
        Optional<Account> optionalAccount = AccountManager.fromCache(player.getUniqueId());
        if (!optionalAccount.isPresent()) {
            writeBlank();
            return;
        }
        Account account = optionalAccount.get();

        int online = 0;
        Optional<CacheRepository> cacheRepository = RedisRepository.getRepository(CacheRepository.class);
        if (cacheRepository.isPresent())
            online+= cacheRepository.get().getCached().stream().filter(cacheItem -> cacheItem instanceof PlayerStatusCache).count();

        LocalDateTime dateTime = LocalDateTime.now();

        write("§7" + dateTime.getMonth().getValue() + "/" + dateTime.getDayOfMonth() + "/" + dateTime.getYear());
        writeBlank();
        write("§fRank: &7" + account.getPrimaryRank().getColor() + account.getPrimaryRank().getDisplayName());
        write("&fGold: &6" + DoubleUtils.format(account.getGold(), true) + " \u26C3");
        write("&fGems: &2" + DoubleUtils.format(account.getGems(), true) + " \u2726");
        write("&fLevel: &b0 &a||||&7|||||||||||"); // TODO: 1/15/21 this is static for now until the stats system is completed
        writeBlank();
        write("§fLobby: &a#" + MGZPlugin.getMinecraftServer().getNumericId());
        write("§fPlayers: &a" + online);
        writeBlank();
        write("§ethemcgamer.zone");
    }
}