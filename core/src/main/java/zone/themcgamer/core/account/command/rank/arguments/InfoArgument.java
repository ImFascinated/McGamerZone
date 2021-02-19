package zone.themcgamer.core.account.command.rank.arguments;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.data.Rank;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Braydon
 */
@AllArgsConstructor @Getter
public class InfoArgument {
    private final AccountManager accountManager;

    @Command(name = "rank.info", usage = "<player>", description = "View rank info for a player", ranks = { Rank.MODERATOR })
    public void onCommand(CommandProvider command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();
        if (args.length < 1) {
            sender.sendMessage(Style.main("Rank", "Usage: /rank info <player>"));
            return;
        }
        accountManager.lookup(args[0], account -> {
            if (account == null) {
                sender.sendMessage(Style.invalidAccount("Rank", args[0]));
                return;
            }
            sender.sendMessage(Style.main("Rank", "Rank information for " + account.getDisplayName() + "§7:"));
            sender.sendMessage(" §8- §7Primary Rank §f" + account.getPrimaryRank().getDisplayName());
            sender.sendMessage(" §8- §7Sub Ranks §f" + (account.getSecondaryRanks().length < 1 ? "None" :
                    Arrays.stream(account.getSecondaryRanks()).map(Rank::getDisplayName).collect(Collectors.joining("§7, §f"))));
        });
    }
}