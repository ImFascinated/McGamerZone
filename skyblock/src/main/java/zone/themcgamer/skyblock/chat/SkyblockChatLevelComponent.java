package zone.themcgamer.skyblock.chat;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.*;
import org.bukkit.entity.Player;
import zone.themcgamer.common.DoubleUtils;
import zone.themcgamer.common.MiscUtils;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.chat.component.IChatComponent;
import zone.themcgamer.core.common.Style;

import java.util.Optional;

public class SkyblockChatLevelComponent implements IChatComponent {
    @Override
    public BaseComponent getComponent(Player player) {
        Optional<Account> optionalAccount = AccountManager.fromCache(player.getUniqueId());
        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(player);
        if (optionalAccount.isEmpty() || (superiorPlayer == null || (superiorPlayer.getIsland() == null)))
            return new TextComponent(Style.color("&a[0]"));
        double level = Double.parseDouble(String.valueOf(superiorPlayer.getIsland().getIslandLevel()));
        ComponentBuilder componentBuilder = new ComponentBuilder(Style.color("&a[" + DoubleUtils.format(level, true) + "]"));
        componentBuilder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MiscUtils.arrayToString(
                "",
                Style.color("§e┋ &lAccount"),
                Style.color("§e┋ §fRank: &7" + optionalAccount.get().getPrimaryRank().getColor() + optionalAccount.get().getPrimaryRank().getDisplayName()),
                Style.color("§e┋ &fMoney: &d" + PlaceholderAPI.setPlaceholders(player, "%vault_eco_balance_formatted%")),
                Style.color("§e┋ &fMob Gems: &a0"),
                Style.color("§e┋ &fMcMMO: &60"),
                "",
                Style.color("§c┋ &lIsland &7(" + superiorPlayer.getIsland().getName() + "&7)"),
                Style.color("§c┋ §fRole: &c" + superiorPlayer.getPlayerRole().getName()),
                Style.color("§c┋ §fLevel: &a" + superiorPlayer.getIsland().getIslandLevel()),
                Style.color("§c┋ &fSize: &b" + superiorPlayer.getIsland().getIslandSize() + "x" + superiorPlayer.getIsland().getIslandSize()),
                Style.color("§c┋ &fTeam: &3" + superiorPlayer.getIsland().getIslandMembers(true).size() + "/" + superiorPlayer.getIsland().getTeamLimit()),
                "",
                Style.color("&eClick to visit this island!")
        )).create()));
        componentBuilder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/island visit " + superiorPlayer.getIsland().getName())).create();
        return new TextComponent(componentBuilder.create());
    }
}
