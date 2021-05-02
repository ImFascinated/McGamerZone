package zone.themcgamer.core.common.menu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * This class holds the {@link ItemStack} and {@link Consumer} for
 * the {@link InventoryClickEvent} for each button
 *
 * @author Braydon
 */
@RequiredArgsConstructor @AllArgsConstructor @Getter
public class Button {
    private final ItemStack item;
    private Consumer<InventoryClickEvent> consumer;
}