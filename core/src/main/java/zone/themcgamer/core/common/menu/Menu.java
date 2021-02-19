package zone.themcgamer.core.common.menu;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author Braydon
 */
@Getter
public abstract class Menu {
    /**
     * A map of the menus opened for each player
     */
    @Getter private static final Map<Player, Menu> menusMap = new HashMap<>();

    protected final Player player;
    private String title;
    private final int size;
    private final MenuType type;
    private final Set<MenuFlag> flags = new HashSet<>(Collections.singletonList(MenuFlag.CLOSEABLE));
    protected Inventory inventory;

    /**
     * The buttons for the menu (slot, button)
     */
    private final Map<Integer, Button> buttonMap = new HashMap<>();

    public Menu(Player player, String title, MenuType type) {
        this(player, title, 1, type);
    }

    public Menu(Player player, String title, int rows, MenuType type) {
        this.player = player;
        this.title = title;
        if (title == null)
            this.title = "";
        size = type == MenuType.CHEST ? rows * 9 : type.getInventoryType().getDefaultSize();
        if (type == MenuType.CHEST && rows > 6)
            Bukkit.getLogger().info("Be cautious whilst using inventory sizes larger than 6 rows for type '" + type.name() + "', " +
                    "the menu may not display properly for some players");
        this.type = type;
        setInventory();
    }

    /**
     * Called when the menu is opened
     */
    protected abstract void onOpen();

    protected void setTitle(String title) {
        this.title = title;
        setInventory();
    }

    /**
     * Open the menu to the player
     */
    public void open() {
        player.openInventory(inventory);
        onOpen();
        if (this instanceof UpdatableMenu)
            ((UpdatableMenu) this).onUpdate();
        player.updateInventory();
        menusMap.put(player, this);
    }

    /**
     * Close the player
     */
    public void close() {
        menusMap.remove(player);
        player.closeInventory();
    }

    /**
     * Add the defined flag to the menu
     * @param flag - The flag to add
     */
    protected void addFlag(MenuFlag flag) {
        flags.add(flag);
    }

    /**
     * Get whether the menu has the defined flag
     * @param flag - The flag to check
     * @return whether the menu has the flag
     */
    protected boolean hasFlag(MenuFlag flag) {
        return flags.contains(flag);
    }

    /**
     * Remove the defined flag from the menu
     * @param flag - The flag to remove
     */
    protected void removeFlag(MenuFlag flag) {
        flags.remove(flag);
    }

    /**
     * Fill the menu with the given button
     * @param button - The button to fill the menu with
     */
    protected void fill(Button button) {
        for (int i = 0; i < size; i++) {
            set(i, button);
        }
    }

    /**
     * Fill the menu with the given button at the given slots.
     * @see MenuPattern to get a list of slots
     * @param slots - The slots to fill
     * @param button - The button to fill the slots with
     */
    protected void fill(List<Integer> slots, Button button) {
        for (Integer slot : slots) {
            set(slot, button);
        }
    }

    /**
     * Fill the borders of the menu with the given button
     * @param button - The button to fill the borders with
     * @author NoneTaken
     */
    protected void fillBorders(Button button) {
        for (int i = 0; i < size; i++) {
            if (i < 10)
                set(i, button);
            if ((i % 9) == 0 && i != 9)
                set(i, button);
            if (i >= size - 9)
                set(i, button);
            if (i < 8)
                continue;
            if ((i / 8) - (i % 8) == 0)
                set(i - 1, button);
        }
    }

    /**
     * Fill the slots at the given column with the given button
     * @param column - The column to fill
     * @param button - The button to fill the column with
     */
    protected void fillColumn(int column, Button button) {
        for (int i = 0; i < size; i++) {
            if (i % 9 == column) {
                set(i, button);
            }
        }
    }

    /**
     * Fill the slots at the given row with the given button
     * @param row - The row to fill
     * @param button - The button to fill the row with
     */
    protected void fillRow(int row, Button button) {
        for (int i = 0; i < size; i++) {
            if (i / 9 == row) {
                set(i, button);
            }
        }
    }

    /**
     * Add a {@link Button} to the next available slot in the menu
     * @param button the button to add
     */
    public void add(Button button) {
        for (int slot = 0; slot < size; slot++) {
            if (get(slot) != null)
                continue;
            set(slot, button);
            break;
        }
    }

    /**
     * Set the slot at the given column and row to the given button
     * @param column - The column
     * @param row - The row
     * @param button - The button to set
     */
    protected void set(int column, int row, Button button) {
        set((column * 9) + row, button);
    }

    /**
     * Set the given slot in the menu to the given button
     * @param slot - The slot to set the button in
     * @param button - The button
     */
    protected void set(int slot, Button button) {
        if (slot >= size || (type != MenuType.CHEST && slot >= type.getInventoryType().getDefaultSize()))
            throw new ArrayIndexOutOfBoundsException("Slot must be inside of inventory for type '" + type.name() + "', default size=" +
                    type.getInventoryType().getDefaultSize());
        if (button == null) {
            buttonMap.remove(slot);
            inventory.setItem(slot, null);
            return;
        }
        if (button.getItem().getType() == Material.AIR)
            throw new IllegalArgumentException("Button item cannot be of type AIR");
        buttonMap.put(slot, button);
        inventory.setItem(slot, button.getItem());
    }

    /**
     * Get the button at the given column and row
     * @param column - The column
     * @param row - The row
     * @return the button
     */
    @Nullable
    protected Button get(int column, int row) {
        return get((column * 9) + row);
    }

    /**
     * Get the button at the given slot
     * @param slot - The slot to get the button for
     * @return the button
     */
    @Nullable
    protected Button get(int slot) {
        return buttonMap.get(slot);
    }

    /**
     * Set the inventory field. This method is called when the menu
     * is constructed or when the title is set
     */
    private void setInventory() {
        inventory = type == MenuType.CHEST ? Bukkit.createInventory(player, size, title) :
                Bukkit.createInventory(player, type.getInventoryType(), title);
    }

    /**
     * Get the opened menu for the given player
     * @param player - The player to get the menu for
     * @return the menu
     */
    @Nullable
    public static Menu getOpenedMenu(Player player) {
        return menusMap.get(player);
    }
}