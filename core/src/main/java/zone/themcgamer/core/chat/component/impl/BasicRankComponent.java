package zone.themcgamer.core.chat.component.impl;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import zone.themcgamer.common.MiscUtils;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.chat.component.IChatComponent;
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
                account.getPrimaryRank().getPrefix(),
                "§f" + account.getPrimaryRank().getDescription())).create()));
        return new TextComponent(componentBuilder.create());
    }
}
