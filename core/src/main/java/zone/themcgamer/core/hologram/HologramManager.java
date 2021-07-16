package zone.themcgamer.core.hologram;

import com.cryptomorin.xseries.XMaterial;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.module.Module;
import zone.themcgamer.core.module.ModuleInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ModuleInfo(name="HologramManager")
public class HologramManager extends Module {

    /*
     TODO Animation system for animated holograms
     TODO Once player settings is being created, a system to toggle all holograms on or off for FPS
     TODO Document everything
     */

    public HologramManager(JavaPlugin plugin) {
        super(plugin);
    }

    private final Map<String, Hologram> global = new HashMap<>(); // global holograms
    private final Map<UUID, Map<String, Hologram>> cache = new HashMap<>(); // personal

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        // Show public and private holograms to the player
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        removePlayerHolograms(destroyPlayerHolograms(event.getPlayer().getUniqueId()));
    }

    @EventHandler
    private void onDisable(PluginDisableEvent event) {
        destroyGlobalHolograms();
        removeGlobalHolograms();

        destroyPlayerHolograms();
        removePLayerHolograms();
    }

    /**
     * This method should add a single hologram with the given name, to the specified player.
     *
     * @param uuid the specified player uuid.
     * @param name the given name.
     * @param hologram the hologram.
     */
    public Hologram addPlayerHologram(UUID uuid, String name, Hologram hologram) {
        fetchPlayerHolograms(uuid).ifPresentOrElse(map -> map.put(name, hologram), () -> {
            cache.put(uuid, new HashMap<>());
            cache.get(uuid).put(name,hologram);
        });
        return hologram;
    }

    /**
     * This method should add a multiple holograms with the given name, to the specified player.
     *
     * @param player the specified player.
     * @param hologramMap the hologram objects.
     */
    public Map<String, Hologram> addPlayerHolograms(Player player, Map<String, Hologram> hologramMap) {
        return cache.put(player.getUniqueId(), hologramMap);
    }

    /**
     * This method should add a global single hologram with the given name.
     *
     * @param name the given name.
     * @param hologram the hologram.
     */
    public Hologram addGlobalHologram(String name, Hologram hologram) {
        global.put(name, hologram);
        return hologram;
    }

    /**
     * This method should add a global single hologram with the given name.
     *
     * @param hologramMap the holograms objects.
     */
    public Map<String, Hologram> addGlobalHolograms(Map<String, Hologram> hologramMap) {
        global.putAll(hologramMap);
        return hologramMap;
    }

    /**
     * This method should remove a single hologram with the given name.
     *
     * @param uuid the specified player uuid.
     * @param index the given hologram name.
     */
    public void removePlayerHologram(UUID uuid, String index) {
        fetchPlayerHolograms(uuid).ifPresent(map -> map.remove(index));
    }

    /**
     * This method should remove a global hologram with the given name.
     *
     * @param name the specified hologram name.
     */
    public void removeGlobalHologram(String name) {
        fetchGlobalHolograms().ifPresent(map -> map.remove(name));
    }

    /**
     * This method should remove a multiple player holograms with the given uuid.
     *
     * @param uuid the specified player uuid.
     */
    public void removePlayerHolograms(UUID uuid) {
        fetchPlayerHolograms(uuid).ifPresent(map -> {
            map.values().forEach(Hologram::delete);
            map.clear();
        });
    }

    /**
     * This method should remove all players from the cache.
     */
    public void removePLayerHolograms() {
        cache.clear();
    }

    /**
     * This method should delete all global holograms from the cache & delete the holograms.
     */
    public void removeGlobalHolograms() {
        fetchGlobalHolograms().ifPresent(map -> {
            map.values().forEach(Hologram::delete);
            map.clear();
        });
    }

    /**
     * This method should create a player hologram on the given location.
     *
     * @param location location of the hologram.
     * @param player the specified player.
     * @return returns the hologram object.
     */
    public Hologram createPlayerHologram(Location location, Player player) {
        final Hologram hologram = HologramsAPI.createHologram(getPlugin(), location);
        hologram.getVisibilityManager().setVisibleByDefault(false);
        hologram.getVisibilityManager().showTo(player);
        return hologram;
    }

    /**
     * This method should create a global hologram on the given location.
     *
     * @param location location of the hologram.
     * @return returns the hologram object.
     */
    public Hologram createGlobalHologram(String name, Location location) {
        final Hologram hologram = HologramsAPI.createHologram(getPlugin(), location);
        hologram.getVisibilityManager().setVisibleByDefault(true);
        return hologram;
    }

    /**
     * This method destroys the hologram and deletes the hologram.
     *
     * @param uuid the specified player uuid.
     * @param name the name of the hologram.
     * @return returns the name of the hologram.
     */
    public String destroyPlayerHologram(UUID uuid, String name) {
        fetchPlayerHolograms(uuid).filter(map -> map.containsKey(name))
                .ifPresent(map -> map.get(name).delete());
        return name;
    }

    /**
     * This method destroys the player hologram you specified.
     *
     * @param uuid the specified player uuid.
     * @return returns the player uuid.
     */
    public UUID destroyPlayerHolograms(UUID uuid) {
        fetchPlayerHolograms(uuid).ifPresent(map -> map.values().forEach(Hologram::delete));
        return uuid;
    }

    /**
     * This method destroys all player holograms in the server.
     */
    public void destroyPlayerHolograms() {
        cache.values().forEach(map -> map.values().forEach(Hologram::delete));
    }

    /**
     * This method destroys the specified global hologram in the server.
     *
     * @param name the specified hologram name.
     * @return returns the name of the hologram.
     */
    public String destroyGlobalHologram(String name) {
        fetchGlobalHolograms().filter(map -> map.containsKey(name))
                .ifPresent(map -> map.get(name).delete());
        return name;
    }

    /**
     * This method destroys all global holograms in the server.
     */
    public void destroyGlobalHolograms() {
        global.values().forEach(Hologram::delete);
    }

    /**
     * This method allows you to set textLines in a hologram, this is a method that recude client lag.
     *
     * @param uuid the specified player uuid.
     * @param index the name of the hologram.
     * @param line the line number.
     * @param text the text you want to set to.
     */
    public void setTextPlayerHologram(UUID uuid, String index, int line, String text) {
        fetchPlayerHologram(uuid, index).ifPresent(hologram -> {
            while (hologram.size() <= line + 1)
                hologram.insertTextLine(line, "");
            ((TextLine) hologram.getLine(line)).setText(Style.color(text));
        });
    }

    /**
     * This method allows you to remove a line from a player hologram
     *
     * @param uuid the specified player uuid.
     * @param index the name of the hologram.
     * @param line the line number.
     */
    public void removeTextPlayerHologram(UUID uuid, String index, int line) {
        fetchPlayerHologram(uuid, index).ifPresent(hologram -> {
            if (hologram.size() >= line + 1)
                hologram.removeLine(line);
        });
    }

    /**
     * This method allows you to set textLines in a global hologram, this is a method that recude client lag.
     *
     * @param index the name of the hologram.
     * @param line the line number.
     * @param text the text you want to set to.
     */
    public void setTextGlobalHologram(String index, int line, String text) {
        fetchGlobalHologram(index).ifPresent(hologram -> {
            while (hologram.size() <= line)
                hologram.insertTextLine(line, "");
            ((TextLine) hologram.getLine(line)).setText(Style.color(text));
        });
    }

    /**
     * This method allows you to set textLines in a global hologram, this is a method that recude client lag.
     *
     * @param index the name of the hologram.
     * @param line the line number.
     * @param item the item you want to set to.
     */
    public void setItemGlobalHologram(String index, int line, XMaterial item) {
        fetchGlobalHologram(index).ifPresent(hologram -> {
            while (hologram.size() <= line) {
                hologram.insertItemLine(line, item.parseItem());
                return;
            }
            ((ItemLine) hologram.getLine(line)).setItemStack(item.parseItem());
        });
    }

    /**
     * This method allows you to remove a line from a global hologram
     *
     * @param index the name of the hologram.
     * @param line the line number.
     */
    public void removeTextGlobalHologram(String index, int line) {
        fetchGlobalHologram(index).ifPresent(hologram -> {
            if (hologram.size() >= line + 1)
                hologram.removeLine(line);
        });
    }

    /**
     * This method allows you to hide a player hologram in the server.
     *
     * @param player the specified player.
     * @param index the specified hologram name.
     * @param visible if you want it to be visible or not.
     */
    public void setPlayerHologramVisible(Player player, String index, boolean visible) {
        fetchPlayerHologram(player.getUniqueId(), index).ifPresent(hologram -> {
            if (visible)
                hologram.getVisibilityManager().showTo(player);
            else
                hologram.getVisibilityManager().hideTo(player);
        });
    }

    /**
     * This method allows you to hide a global hologram in the server.
     *
     * @param index the specified hologram name.
     * @param visible if you want it to be visible or not.
     */
    public void setGlobalHologramVisible(Player player, String index, boolean visible) {
        fetchGlobalHologram( index).ifPresent(hologram -> {
            if (visible)
                hologram.getVisibilityManager().showTo(player);
            else
                hologram.getVisibilityManager().hideTo(player);
        });
    }

    public Optional<Map<String, Hologram>> fetchPlayerHolograms(UUID uuid) {
        return Optional.ofNullable(cache.get(uuid));
    }

    public Optional<Hologram> fetchPlayerHologram(UUID uuid, String index) {
        final Map<String, Hologram> map = cache.get(uuid);
        if (map != null){
            return Optional.ofNullable(map.get(index));
        }
        else
            return Optional.empty();
    }

    public Optional<Map<String, Hologram>> fetchGlobalHolograms() {
        return Optional.of(global);
    }

    public Optional<Hologram> fetchGlobalHologram(String name) {
        return Optional.ofNullable(global.get(name));
    }
}
