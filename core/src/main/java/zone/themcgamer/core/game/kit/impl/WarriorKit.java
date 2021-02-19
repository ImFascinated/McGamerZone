package zone.themcgamer.core.game.kit.impl;

import com.cryptomorin.xseries.XMaterial;
import zone.themcgamer.core.game.kit.KitDisplay;

/**
 * @author Braydon
 */
public class WarriorKit extends KitDisplay {
    public WarriorKit() {
        super("warrior", "Warrior", new String[] {
                "&6&lItems",
                "&b▸ &7Stone Sword",
                "&b▸ &7Wooden Pickaxe",
                "&b▸ &7Wooden Axe",
                "&b▸ &7Wooden Shovel",
                "&b▸ &7Crafting Table",
                "&b▸ &7Shears"
        }, XMaterial.WOODEN_SWORD);
    }
}