package zone.themcgamer.core.account.command.rank.arguments;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.data.Rank;

import java.util.Optional;

/**
 * @author Braydon
 */
@AllArgsConstructor @Getter
public class SetArgument {
    private final AccountManager accountManager;

    @Command(name = "rank.set", usage = "<player> <rank>", description = "Set the rank of a player", ranks = { Rank.ADMIN })
    public void onCommand(CommandProvider command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();
        if (args.length < 2) {
            sender.sendMessage(Style.main("Rank", "Usage: /rank set <player> <rank>"));
            return;
        }
        Optional<Rank> optionalRank = Rank.lookup(args[1]);
        if (!optionalRank.isPresent()) {
            sender.sendMessage(Style.error("Rank", "§cThat rank does not exist."));
            return;
        }
        Rank rank = optionalRank.get();
        if (rank.getCategory() == Rank.RankCategory.SUB) {
            sender.sendMessage(Style.error("Rank", "§cA player cannot have their primary rank set to a sub rank."));
            return;
        }
        if (command.isPlayer()) {
            Optional<Account> optionalAccount = AccountManager.fromCache(((Player) sender).getUniqueId());
            if (!optionalAccount.isPresent()) {
                sender.sendMessage(Style.error("Rank", "§cError whilst fetching account, please try again later..."));
                return;
            }
            Account account = optionalAccount.get();
            if (account.getPrimaryRank() == rank || !account.hasRank(rank)) {
                sender.sendMessage(Style.error("Rank", "§cYou cannot set a player's rank to a rank higher or equal to than your own."));
                return;
            }
        }
        accountManager.lookup(args[0], account -> {
            if (account == null) {
                sender.sendMessage(Style.invalidAccount("Rank", args[0]));
                return;
            }
            sender.sendMessage(Style.main("Rank", "Updated " + account.getDisplayName() + "'s §7rank to §f" + rank.getColor() + rank.getDisplayName()));
            accountManager.setRank(account, rank);
        });
    }
}