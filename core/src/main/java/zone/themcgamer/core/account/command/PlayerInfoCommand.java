package zone.themcgamer.core.account.command;

import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;
import zone.themcgamer.common.TimeUtils;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.data.Rank;
import zone.themcgamer.data.jedis.cache.CacheRepository;
import zone.themcgamer.data.jedis.cache.impl.PlayerStatusCache;

import java.util.Optional;

@AllArgsConstructor
public class PlayerInfoCommand {
    private final AccountManager accountManager;
    private final CacheRepository cacheRepository;

    @Command(name = "playerinfo", aliases = { "pinfo" }, description = "Get information about a player", ranks = { Rank.HELPER })
    public void onCommand(CommandProvider command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();
        if (args.length < 1) {
            sender.sendMessage(Style.main("Account", "Usage: /" + command.getLabel() + " <player>"));
            return;
        }
        accountManager.lookup(args[0], account -> {
            if (account == null) {
                sender.sendMessage(Style.invalidAccount("Account", args[0]));
                return;
            }

            Optional<PlayerStatusCache> playerStatusCacheOptional = cacheRepository.lookup(PlayerStatusCache.class, account.getUuid());
            sender.sendMessage("");
            sender.sendMessage(Style.color("&a&lPlayer Information"));
            sender.sendMessage(Style.color("&7Id: &c" + account.getId()));
            sender.sendMessage(Style.color("&7Player: &b" + account.getName()));
            sender.sendMessage(Style.color("&7Status: " + (playerStatusCacheOptional.isEmpty() ? "&cOffline" : "&aOnline")));
            sender.sendMessage(Style.color("&7Server: &b" + (playerStatusCacheOptional.isEmpty() ? "&cN/A" : playerStatusCacheOptional.get().getServer())));
            sender.sendMessage(Style.color("&7Registered At: &b" + TimeUtils.when(account.getFirstLogin())));
            sender.sendMessage(Style.color("&7Last Seen: &b" + TimeUtils.when(account.getLastLogin())));
            sender.sendMessage(Style.color("&7Rank: &b" + account.getPrimaryRank().getColor() + account.getPrimaryRank().getDisplayName()));
            sender.sendMessage(Style.color("&7Sub Ranks: &b" + (account.getSecondaryRanksNames().length == 0 ? "None" : String.join("§7, §f", account.getSecondaryRanksNames()))));
            sender.sendMessage(Style.color("&7Gold: &b" + account.getGold()));
            sender.sendMessage(Style.color("&7Gems: &b" + account.getGems()));
            sender.sendMessage("");
        });
    }
}
