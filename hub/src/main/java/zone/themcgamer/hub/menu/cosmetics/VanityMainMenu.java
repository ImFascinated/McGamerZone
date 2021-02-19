package zone.themcgamer.hub.menu.cosmetics;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.entity.Player;
import zone.themcgamer.core.common.ItemBuilder;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.common.menu.Button;
import zone.themcgamer.core.common.menu.Menu;
import zone.themcgamer.core.common.menu.MenuType;

public class VanityMainMenu extends Menu {
    public VanityMainMenu(Player player) {
        super(player, "Vanity » Menu", 3, MenuType.CHEST);
    }

    @Override
    protected void onOpen() {
        set(1,4, new Button(new ItemBuilder(XMaterial.BARRIER).setName(Style.color("&c&lComing Soon!")).toItemStack()));
    }
}
