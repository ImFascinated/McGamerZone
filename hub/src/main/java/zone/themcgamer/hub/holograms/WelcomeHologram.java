package zone.themcgamer.hub.holograms;

import com.cryptomorin.xseries.XMaterial;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.game.MGZGame;
import zone.themcgamer.core.hologram.HologramManager;
import zone.themcgamer.core.module.Module;
import zone.themcgamer.core.module.ModuleInfo;
import zone.themcgamer.core.world.MGZWorld;
import zone.themcgamer.data.jedis.cache.CacheRepository;
import zone.themcgamer.data.jedis.cache.impl.PlayerStatusCache;
import zone.themcgamer.data.jedis.repository.RedisRepository;

import java.util.HashMap;
import java.util.Optional;

@ModuleInfo(name = "Welcome Hologram")
public class WelcomeHologram extends Module {

    private static final HashMap<String, XMaterial> reactionRoles = new HashMap<>();

    private int randomGameIndex;

    @Getter
    private final HologramManager hologramManager;

    public WelcomeHologram(JavaPlugin plugin) {
        super(plugin);
        hologramManager = Module.getModule(HologramManager.class);
        setup();
        run();
    }

    private void setup() {
        final Location location = MGZWorld.get(Bukkit.getWorlds().get(0)).getDataPoint("LOOK_AT");
        if (location != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Hologram hologram = hologramManager.addGlobalHologram("WELCOME", hologramManager.createGlobalHologram("WELCOME", location));
                    hologram.insertItemLine(0, XMaterial.CRAFTING_TABLE.parseItem());
                    hologram.insertTextLine(1, Style.color("&fWelcome to &2&lMc&6&lGamer&c&lZone&f!"));
                    hologram.insertTextLine(2, Style.color("&bUse the compass to warp to a &agame&b!"));
                    hologram.insertTextLine(3, Style.color(""));
                    hologram.insertTextLine(4, Style.color("&a/help &ffor help!"));
                    hologram.insertTextLine(5, Style.color("&a/store &ffor donating!"));
                    hologram.insertTextLine(6, Style.color("&a/discord &fjoin our discord!"));
                    hologram.insertTextLine(7, Style.color(""));
                    hologram.insertTextLine(8, Style.color("&ePlay with &b0 &eother gamers!"));
                }
            }.runTask(getPlugin());
        }
    }

    private void run(){
        Optional<CacheRepository> cacheRepository = RedisRepository.getRepository(CacheRepository.class);
        new BukkitRunnable() {
            @Override
            public void run() {
                int online = 0;
                if (cacheRepository.isPresent())
                    online += cacheRepository.get().getCached().stream().filter(cacheItem -> cacheItem instanceof PlayerStatusCache).count();
                hologramManager.setTextGlobalHologram("WELCOME", 8, "&ePlay with &b" + online + " &eother gamers!");

                if (++randomGameIndex >= MGZGame.values().length)
                    randomGameIndex = 0;
                MGZGame game = MGZGame.values()[randomGameIndex];
                hologramManager.setItemGlobalHologram("WELCOME", 0, game.getIcon());
            }
        }.runTaskTimer(getPlugin(), 2*20, 2*20);
    }
}
