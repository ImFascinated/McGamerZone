package zone.themcgamer.core.account.menu;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.entity.Player;
import zone.themcgamer.common.DoubleUtils;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.common.ItemBuilder;
import zone.themcgamer.core.common.menu.Button;
import zone.themcgamer.core.common.menu.Menu;
import zone.themcgamer.core.common.menu.MenuType;

import java.util.Optional;

public class ProfileMenu extends Menu {
    public ProfileMenu(Player player) {
        super(player, "Profile » " + player.getName(), 3, MenuType.CHEST);
    }

    @Override
    protected void onOpen() {
        Optional<Account> optionalAccount = AccountManager.fromCache(player.getUniqueId());
        if (!optionalAccount.isPresent())
            return;
        Account account = optionalAccount.get();
        set(1, 1, new Button(new ItemBuilder(XMaterial.PLAYER_HEAD)
                .setSkullOwner(player.getName())
                .setName("§f" + account.getDisplayName() + "'s §a§lProfile")
                .setLore(
                        "",
                        "&fRank &7» &f" + account.getPrimaryRank().getColor() + account.getPrimaryRank().getDisplayName(),
                        "&fGold &7» &6" + DoubleUtils.format(account.getGold(), true) + " ⛃",
                        "&fGems &7» &2" + DoubleUtils.format(account.getGems(), true) + " ✦",
                        "",
                        "&fCoin Multiplier &7» &bx1.0",
                        "",
                        "&aClick to view your stats"
                ).toItemStack()));

        set(1, 3, new Button(new ItemBuilder(XMaterial.EXPERIENCE_BOTTLE)
                .setName("&b&lNetwork Level")
                .setLore(
                        "&f&lYou are now level &60",
                        "&fProgress &a||||&7|||||||||||",
                        "",
                        "&7By playing games you will",
                        "&7receive &dexperience &7points.",
                        "&7By leveling up you will receive",
                        "&7various features unlocked!",
                        "",
                        "&aClick to view rewards"
                ).toItemStack()));

        set(1, 4, new Button(new ItemBuilder(XMaterial.COMPARATOR)
                .setName("&b&lSettings")
                .setLore(
                        "",
                        "&7Here you can modify",
                        "&7account settings for features",
                        "&7across the network.",
                        "",
                        "&aClick to modify your settings"
                ).toItemStack()));

        set(1, 5, new Button(new ItemBuilder(XMaterial.EMERALD)
                .setName("&2&lGem Boxes")
                .setLore(
                        "&fYou currently have &c0 &fgem boxes!",
                        "",
                        "&7Collect &2gem&7 boxes",
                        "&7by &aplaying&7 different games",
                        "&7and completing &9missions &7& &6achievements&7.",
                        "",
                        "&aClick to view your boxes"
                ).toItemStack()));
    }
}