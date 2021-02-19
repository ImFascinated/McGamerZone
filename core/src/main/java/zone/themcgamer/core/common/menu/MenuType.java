package zone.themcgamer.core.common.menu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.inventory.InventoryType;

/**
 * @author Braydon
 */
@AllArgsConstructor @Getter
public enum MenuType {
    CHEST(InventoryType.CHEST),
    DISPENSER(InventoryType.DISPENSER),
    FURNACE(InventoryType.FURNACE),
    HOPPER(InventoryType.HOPPER);

    private final InventoryType inventoryType;
}