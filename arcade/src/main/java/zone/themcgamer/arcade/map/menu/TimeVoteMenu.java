package zone.themcgamer.arcade.map.menu;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.entity.Player;
import zone.themcgamer.core.common.ItemBuilder;
import zone.themcgamer.core.common.WorldTime;
import zone.themcgamer.core.common.menu.Button;
import zone.themcgamer.core.common.menu.MenuPattern;
import zone.themcgamer.core.common.menu.MenuType;
import zone.themcgamer.core.common.menu.UpdatableMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimeVoteMenu extends UpdatableMenu {
    public TimeVoteMenu(Player player) {
        super(player, "Time Vote", 3, MenuType.CHEST);
    }

    @Override
    public void onUpdate() {        
        fill(new Button(new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setName("&7").toItemStack()));
        List<Integer> slots = MenuPattern.getSlots(
                "XXXXXXXXX",
                "XOXOXOXOX",
                "XXXXXXXXX"
        );
        int index = 0;
        for (WorldTime time : Arrays.asList(WorldTime.SUNRISE, WorldTime.DAY, WorldTime.SUNSET, WorldTime.MIDNIGHT)) {
            List<String> lore = new ArrayList<>();
            for (String descriptionLine : time.getDescription())
                lore.add("§7" + descriptionLine);
            lore.add("");
            lore.add("§e▪ &7Current votes: &b0");
            lore.add("");
            lore.add("&aClick to vote!");
            ItemBuilder icon = new ItemBuilder(XMaterial.CLOCK).setName("&e&l" + time.getDisplayName());
            icon.setLore(lore);
            set(slots.get(index++), new Button(icon.toItemStack(), event -> {
                //TODO do the vote shit here
            }));
        }
    }
}