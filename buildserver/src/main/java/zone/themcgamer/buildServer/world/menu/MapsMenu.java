package zone.themcgamer.buildServer.world.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import zone.themcgamer.buildServer.world.WorldManager;
import zone.themcgamer.core.common.ItemBuilder;
import zone.themcgamer.core.common.menu.Button;
import zone.themcgamer.core.common.menu.Menu;
import zone.themcgamer.core.common.menu.MenuType;
import zone.themcgamer.core.module.Module;
import zone.themcgamer.core.world.MGZWorld;
import zone.themcgamer.core.world.WorldCategory;

import java.util.List;
import java.util.stream.Collectors;

public class MapsMenu extends Menu {
    private final WorldCategory category;

    public MapsMenu(Player player, @Nullable WorldCategory category) {
        super(player, category == null ? "My Maps" : "Maps - " + category.getName(), 6, MenuType.CHEST);
        this.category = category;
    }

    @Override
    protected void onOpen() {
        WorldManager worldManager = Module.getModule(WorldManager.class);
        if (worldManager == null)
            return;
        List<MGZWorld> worlds = worldManager.getWorlds().stream()
                .filter(world -> category == null ? world.hasPrivileges(player) : world.getCategory() == category)
                .sorted((a, b) -> Boolean.compare(a.getOriginalCreator().equals(player.getName()), b.getOriginalCreator().equals(player.getName())))
                .collect(Collectors.toList());
        for (MGZWorld world : worlds) {
            add(new Button(new ItemBuilder(world.getCategory().getIcon())
                    .setName("§a" + world.getName() + (world.hasPrivileges(player) ? " §c(Access)" : "")).setLore(
                            "",
                            " §8- §7Author §f" + world.getAuthor() + (world.getAuthor().equals(world.getOriginalCreator()) ? "" : " §7(original: " + world.getOriginalCreator() + ")"),
                            " §8- §7Preset §f" + world.getPreset(),
                            " §8- §7Category §f" + world.getCategory().getName(),
                            " §8- §7Admins §f" + (world.getAdmins().isEmpty() ? "None" : String.join("§7, §f", world.getAdmins())),
                            "",
                            "§aClick to teleport to this map."
                    ).toItemStack(), event -> {
                player.chat(String.format("/map %s %s", world.getName(), world.getCategory().name()));
            }));
        }
        set(5, 0, new Button(new ItemBuilder(Material.ARROW)
                .setName("§c« Go Back").toItemStack(), event -> {
            if (category == null)
                new BuildManagerMenu(player).open();
            else new MapsCategoryMenu(player).open();
        }));
    }
}