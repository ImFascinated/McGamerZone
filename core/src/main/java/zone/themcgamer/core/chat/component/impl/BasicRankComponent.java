package zone.themcgamer.core.chat.component.impl;

import net.md_5.bungee.api.chat.*;
import org.bukkit.entity.Player;
import zone.themcgamer.common.MiscUtils;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.chat.component.IChatComponent;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.data.Rank;

import java.util.Optional;

/**
 * @author Braydon
 */
public class BasicRankComponent implements IChatComponent {
    @Override
    public BaseComponent getComponent(Player player) {
        Optional<Account> optionalAccount = AccountManager.fromCache(player.getUniqueId());
        Account account;
        if (optionalAccount.isEmpty() || ((account = optionalAccount.get()).getPrimaryRank() == Rank.DEFAULT))
            return null;
        ComponentBuilder componentBuilder = new ComponentBuilder(account.getPrimaryRank().getPrefix());
        componentBuilder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MiscUtils.arrayToString(
                Style.color("&7This is &b" + account.getPrimaryRank().getDisplayName() + " &7rank"),
                Style.color("&7Do you also want to stand out in the &achat&7?"),
                Style.color("&e&lClick Me &7to donate and support the server!"))).create()));
        componentBuilder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/store")).create();
        return new TextComponent(componentBuilder.create());
    }
}
