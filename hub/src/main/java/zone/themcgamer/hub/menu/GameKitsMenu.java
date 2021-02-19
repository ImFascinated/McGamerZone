package zone.themcgamer.hub.menu;

import com.cryptomorin.xseries.XSound;
import org.bukkit.entity.Player;
import zone.themcgamer.core.common.ItemBuilder;
import zone.themcgamer.core.common.menu.Button;
import zone.themcgamer.core.common.menu.Menu;
import zone.themcgamer.core.common.menu.MenuType;
import zone.themcgamer.core.game.MGZGame;
import zone.themcgamer.core.game.kit.KitClient;
import zone.themcgamer.core.game.kit.KitDisplay;
import zone.themcgamer.core.game.kit.KitManager;
import zone.themcgamer.core.module.Module;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Braydon
 */
public class GameKitsMenu extends Menu {
    private final MGZGame game;

    public GameKitsMenu(Player player, MGZGame game) {
        super(player, game.getName() + " - Kits", 6, MenuType.DISPENSER);
        this.game = game;
    }

    @Override
    protected void onOpen() {
        List<String> lore = new ArrayList<>();
        lore.add("");
        for (String descriptionLine : game.getDescription())
            lore.add("§7" + descriptionLine);
        lore.add("");
        lore.add("§c« Click to go back");
        set(0, 1, new Button(new ItemBuilder(game.getIcon(), 1)
                .setName("§6§l" + game.getName())
                .setLore(lore)
                .toItemStack(), event -> new TravellerMenu(player).open()));

        KitManager kitManager = Module.getModule(KitManager.class);
        if (kitManager == null)
            return;
        Optional<KitClient> optionalKitClient = kitManager.lookup(player.getUniqueId());
        if (optionalKitClient.isEmpty())
            return;
        KitClient kitClient = optionalKitClient.get();

        int slot = 3;
        for (KitDisplay kit : game.getKitDisplays()) {
            boolean selected = kitClient.getKit(game).equals(kit);
            lore = new ArrayList<>();
            lore.add("");
            for (String descriptionLine : kit.getDescription())
                lore.add("§7" + descriptionLine);
            if (selected) {
                lore.add("");
                lore.add("§aSelected!");
            }
            set(slot++, new Button(new ItemBuilder(kit.getIcon())
                    .setName("§6§l" + kit.getName())
                    .setLore(lore).toItemStack(), event -> {
                if (selected) {
                    player.playSound(player.getEyeLocation(), XSound.ENTITY_VILLAGER_NO.parseSound(), 0.9f, 1f);
                    return;
                }
                // TODO: 1/31/21 select kit
            }));
        }
    }
}