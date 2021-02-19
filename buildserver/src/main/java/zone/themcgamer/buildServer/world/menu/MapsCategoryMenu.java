package zone.themcgamer.buildServer.world.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import zone.themcgamer.buildServer.world.WorldManager;
import zone.themcgamer.core.common.ItemBuilder;
import zone.themcgamer.core.common.menu.Button;
import zone.themcgamer.core.common.menu.Menu;
import zone.themcgamer.core.common.menu.MenuType;
import zone.themcgamer.core.module.Module;
import zone.themcgamer.core.world.WorldCategory;

public class MapsCategoryMenu extends Menu {
    public MapsCategoryMenu(Player player) {
        super(player, "Select Category", 3, MenuType.CHEST);
    }

    @Override
    protected void onOpen() {
        WorldManager worldManager = Module.getModule(WorldManager.class);
        if (worldManager == null)
            return;
        for (WorldCategory worldCategory : WorldCategory.values()) {
            long worlds = worldManager.getWorlds().stream().filter(world -> world.getCategory() == worldCategory).count();
            add(new Button(new ItemBuilder(worldCategory.getIcon())
                    .setName("§b§l" + worldCategory.getName())
                    .setLore(
                            "§6" + worlds + " §7map" + (worlds == 1 ? "" : "s") + " in this category!",
                            "",
                            "§aClick to view this category."
                    ).toItemStack(), event -> {
                if (worlds <= 0L)
                    return;
                new MapsMenu(player, worldCategory).open();
            }));
        }
        set(2, 0, new Button(new ItemBuilder(Material.ARROW)
                .setName("§c« Go Back").toItemStack(), event -> new BuildManagerMenu(player).open()));
    }
}
