package zone.themcgamer.core.account.command.rank.arguments;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.data.Rank;

/**
 * @author Braydon
 */
@AllArgsConstructor @Getter
public class ClearArgument {
    private final AccountManager accountManager;

    @Command(name = "rank.clear", usage = "<player>", description = "Clear the ranks for a player", ranks = { Rank.ADMIN })
    public void onCommand(CommandProvider command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();
        if (args.length < 1) {
            sender.sendMessage(Style.main("Rank", "Usage: /rank clear <player>"));
            return;
        }
        accountManager.lookup(args[0], account -> {
            if (account == null) {
                sender.sendMessage(Style.invalidAccount("Rank", args[0]));
                return;
            }
            sender.sendMessage(Style.main("Rank", account.getDisplayName() + " §7had their ranks cleared"));
            accountManager.clearRanks(account);
        });
    }
}