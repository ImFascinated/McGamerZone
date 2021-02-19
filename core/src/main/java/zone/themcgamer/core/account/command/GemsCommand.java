package zone.themcgamer.core.account.command;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import zone.themcgamer.common.DoubleUtils;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;

@AllArgsConstructor
public class GemsCommand {
    private final AccountManager accountManager;

    @Command(name = "gems", description = "View your gems", playersOnly = true)
    public void onCommand(CommandProvider command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();
        String target = player.getName();
        if (args.length > 0)
            target = args[0];
        String finalTarget = target;
        accountManager.lookup(target, account -> {
            if (account == null) {
                player.sendMessage(Style.invalidAccount("Account", finalTarget));
                return;
            }
            String gems = DoubleUtils.format(account.getGems(), false);
            if (player.getName().equals(account.getName()))
                player.sendMessage(Style.main("Account", String.format("You have &2%s Gem" + (account.getGems() == 1 ? "" : "s") + "&7!", gems)));
            else player.sendMessage(Style.main("Account", String.format("&b%s &7has &2%s Gem" + (account.getGems() == 1 ? "" : "s") + "&7!", account.getDisplayName(), gems)));
        });
    }
}
