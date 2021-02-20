package zone.themcgamer.core.server.command;

import org.bukkit.command.CommandSender;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.data.Rank;
import zone.themcgamer.data.jedis.cache.CacheRepository;
import zone.themcgamer.data.jedis.cache.ICacheItem;
import zone.themcgamer.data.jedis.cache.ItemCacheType;
import zone.themcgamer.data.jedis.cache.impl.PlayerStatusCache;
import zone.themcgamer.data.jedis.repository.RedisRepository;

import java.util.List;

/**
 * @author Nicholas
 */
public class FindCommand {
    @Command(name = "find", aliases = {"locate", "where"}, description = "Find a player on the network", ranks = Rank.HELPER)
    public void onCommand(CommandProvider command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();
        if (args.length < 1) {
            sender.sendMessage(Style.main("Find", "Usage: /" + command.getLabel() + " <player>"));
            return;
        }
        CacheRepository cacheRepository = RedisRepository.getRepository(CacheRepository.class).orElse(null);
        if (cacheRepository == null)
            return;
        List<ICacheItem<?>> playerStatuses = cacheRepository
                .filter(iCacheItem -> iCacheItem.getType() == ItemCacheType.PLAYER_STATUS && ((PlayerStatusCache) iCacheItem).getPlayerName().equalsIgnoreCase(args[0]));
        if (playerStatuses.isEmpty()) {
            sender.sendMessage(Style.error("Find", "§b" + args[0] + " §7was not found."));
            return;
        }
        PlayerStatusCache playerStatusCacheOptional = (PlayerStatusCache) playerStatuses.get(0);
        sender.sendMessage(Style.main("Find", "§b" + playerStatusCacheOptional.getPlayerName() + " §7is online on §b" + playerStatusCacheOptional.getServer() + "§7."));
    }
}