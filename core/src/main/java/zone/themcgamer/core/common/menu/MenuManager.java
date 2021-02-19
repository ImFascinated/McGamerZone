package zone.themcgamer.core.common.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import zone.themcgamer.core.common.scheduler.ScheduleType;
import zone.themcgamer.core.common.scheduler.event.SchedulerEvent;

/**
 * @author Braydon
 */
public class MenuManager implements Listener {
    private final JavaPlugin plugin;

    public MenuManager(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void onClick(InventoryClickEvent event) {
        HumanEntity entity = event.getWhoClicked();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            int slot = event.getRawSlot();
            InventoryAction action = event.getAction();
            Menu menu;
            if (slot == -999 || action == InventoryAction.NOTHING || (menu = Menu.getOpenedMenu(player)) == null)
                return;
            if (menu.hasFlag(MenuFlag.DEBUG)) {
                ItemStack item = event.getCurrentItem();
                ItemStack cursor = event.getCursor();
                player.sendMessage("slot=" + slot + " (type=" + event.getSlotType().name() + ")");
                player.sendMessage("action=" + action.name());
                player.sendMessage("clickType=" + event.getClick());
                player.sendMessage("item=" + (item == null ? "null" : item.toString()));
                player.sendMessage("cursor=" + (cursor == null ? "null" : cursor.toString()));
            }
            if (event.isShiftClick()) {
                event.setCancelled(true);
                return;
            }
            if (!event.getClickedInventory().equals(menu.getInventory()))
                return;
            event.setCancelled(true);
            Button button = menu.getButtonMap().get(slot);
            if (button != null && (button.getConsumer() != null))
                button.getConsumer().accept(event);
        }
    }

    @EventHandler
    private void onUpdate(SchedulerEvent event) {
        if (event.getType() != ScheduleType.THREE_TICKS)
            return;
        for (Player player : Bukkit.getOnlinePlayers()) {
            Menu menu = Menu.getOpenedMenu(player);
            if (menu == null)
                continue;
            if (menu instanceof UpdatableMenu) {
                UpdatableMenu updatableMenu = (UpdatableMenu) menu;
                if ((System.currentTimeMillis() - updatableMenu.getLastUpdate()) >= updatableMenu.getDelay()) {
                    updatableMenu.onUpdate();
                    updatableMenu.setLastUpdate(System.currentTimeMillis());
                    player.updateInventory();
                }
            }
        }
    }

    @EventHandler
    private void onClose(InventoryCloseEvent event) {
        HumanEntity entity = event.getPlayer();
        if (entity instanceof Player) {
            Menu menu = Menu.getMenusMap().remove(entity);
            if (menu != null) {
                if (!menu.hasFlag(MenuFlag.CLOSEABLE)) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, menu::open, 1L);
                    if (menu.hasFlag(MenuFlag.DEBUG))
                        Bukkit.broadcastMessage("Re-opening menu (" + menu.getTitle() + "§f) for player " + entity.getName() + ", the menu is not closeable");
                } else {
                    if (menu.hasFlag(MenuFlag.DEBUG))
                        Bukkit.broadcastMessage("Removing " + entity.getName() + " from menu map, they closed the menu (" + menu.getTitle() + "§f)");
                }
            }
        }
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Menu menu = Menu.getMenusMap().remove(player);
        if (menu != null && (menu.hasFlag(MenuFlag.DEBUG)))
            Bukkit.broadcastMessage("Removing " + player.getName() + " from menu map, they left the server (menu: " + menu.getTitle() + "§f)");
    }

    @EventHandler
    private void onDisable(PluginDisableEvent event) {
        Plugin plugin = event.getPlugin();
        if (plugin.equals(this.plugin)) {
            for (Menu menu : Menu.getMenusMap().values()) {
                if (menu.hasFlag(MenuFlag.DEBUG))
                    Bukkit.broadcastMessage("Closing menu (" + menu.getTitle() + "§f) for " + menu.getPlayer().getName() + " due to plugin disable: " + plugin.getName());
                menu.close();
            }
            Menu.getMenusMap().clear();
        }
    }
}