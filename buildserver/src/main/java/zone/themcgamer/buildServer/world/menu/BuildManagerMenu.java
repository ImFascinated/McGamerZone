package zone.themcgamer.buildServer.world.menu;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.entity.Player;
import zone.themcgamer.buildServer.world.WorldManager;
import zone.themcgamer.core.common.ItemBuilder;
import zone.themcgamer.core.common.menu.Button;
import zone.themcgamer.core.common.menu.Menu;
import zone.themcgamer.core.common.menu.MenuType;
import zone.themcgamer.core.module.Module;
import zone.themcgamer.core.world.MGZWorld;

public class BuildManagerMenu extends Menu {
    public BuildManagerMenu(Player player) {
        super(player, "Build Manager", 1, MenuType.CHEST);
    }

    @Override
    protected void onOpen() {
        set(0, new Button(new ItemBuilder(XMaterial.LEAD)
                .setName("§6My Maps").setLore(
                        "",
                        "§7Click to view the worlds you have access to"
                ).toItemStack(), event -> {
            new MapsMenu(player, null).open();
        }));

        set(1, new Button(new ItemBuilder(XMaterial.FILLED_MAP)
                .setName("§6All Maps").setLore(
                        "",
                        "§7Click to view all maps"
                ).toItemStack(), event -> {
            new MapsCategoryMenu(player).open();
        }));

        MGZWorld world;
        WorldManager worldManager = Module.getModule(WorldManager.class);
        if (worldManager == null || ((world = worldManager.getWorld(player.getWorld())) == null || !world.hasPrivileges(player)))
            return;
        set(8, new Button(new ItemBuilder(world.getCategory().getIcon())
                .setName("§6" + world.getName()).setLore(
                        "",
                        "§7Click to manage your map!"
                ).toItemStack()));
    }
}
