package zone.themcgamer.core.badSportSystem.command;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.badSportSystem.menu.BadSportMenu;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.data.Rank;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Braydon
 */
@AllArgsConstructor
public class BadSportCommand {
    private final AccountManager accountManager;

    @Command(name = "badsport", aliases = { "bss" }, description = "Punish a player", ranks = { Rank.HELPER }, playersOnly = true)
    public void onCommand(CommandProvider command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();
        if (args.length < 1) {
            player.sendMessage(Style.main("Bad Sport", "Usage: /" + command.getLabel() + " <player> [reason] [-s]"));
            return;
        }
        Optional<Account> optionalExecutorAccount = AccountManager.fromCache(player.getUniqueId());
        if (!optionalExecutorAccount.isPresent())
            return;
        if (player.getName().equalsIgnoreCase(args[0]) && !optionalExecutorAccount.get().hasRank(Rank.JR_DEVELOPER) && args.length >= 2) {
            command.getSender().sendMessage(Style.error("Bad Sport","You cannot punish yourself!"));
            return;
        }
        accountManager.lookup(args[0], account -> {
            if (account == null) {
                player.sendMessage(Style.invalidAccount("Bad Sport", args[0]));
                return;
            }
            if (account.hasRank(Rank.HELPER) && !optionalExecutorAccount.get().hasRank(Rank.JR_DEVELOPER) && args.length >= 2) {
                command.getSender().sendMessage(Style.error("Bad Sport","You can not punish other staff!"));
                return;
            }
            new BadSportMenu(player, account, args.length >= 2 ? Arrays.stream(args).skip(1).collect(Collectors.joining(" ")) : "", false).open();
        });
    }
}