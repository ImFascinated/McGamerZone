package zone.themcgamer.hub.holograms;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.deliveryMan.DeliveryManManager;
import zone.themcgamer.core.deliveryMan.event.ClaimEvent;
import zone.themcgamer.core.hologram.HologramManager;
import zone.themcgamer.core.module.Module;
import zone.themcgamer.core.module.ModuleInfo;
import zone.themcgamer.core.world.MGZWorld;
import zone.themcgamer.hub.Hub;

import java.util.UUID;

import static zone.themcgamer.core.deliveryMan.DeliveryManManager.DELIVERY_MAN_NAME;

@ModuleInfo(name = "HologramHandler Manager")
public class HaroldHologram extends Module{

    @Getter private final DeliveryManManager deliveryManManager;
    @Getter private final HologramManager hologramManager;

    private String HAROLD_TITLE = "&a" + DELIVERY_MAN_NAME + "'s &fdelivery &cRewards";

    public HaroldHologram(JavaPlugin plugin) {
        super(plugin);
        deliveryManManager = Module.getModule(DeliveryManManager.class);
        hologramManager = Module.getModule(HologramManager.class);
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final Location location = MGZWorld.get(Bukkit.getWorlds().get(0)).getDataPoint("HAROLD");

        if (location != null) {
            location.setYaw(Hub.INSTANCE.getSpawn().getYaw());
            hologramManager.addPlayerHologram(player.getUniqueId(), DELIVERY_MAN_NAME, hologramManager.createPlayerHologram(location.clone().add(0,3.2,0), player))
                    .insertTextLine(0, Style.color(HAROLD_TITLE));
            updateHaroldLines(event.getPlayer());
        }

    }

    @EventHandler
    private void onDeliveryManClaim(ClaimEvent event) {
        updateHaroldLines(event.getPlayer());
    }

    private void updateHaroldLines(Player player){
        final UUID uuid = player.getUniqueId();
        hologramManager.fetchPlayerHologram(uuid, DELIVERY_MAN_NAME).ifPresent(hologram -> {
            deliveryManManager.lookup(uuid).ifPresent(client -> {
                if (client.getUnclaimedRewards(player) > 0) {
                    hologramManager.setTextPlayerHologram(uuid, DELIVERY_MAN_NAME,1,"&7You have &b" + client.getUnclaimedRewards(player) + " &7unclaimed deliveries!");
                    hologramManager.setTextPlayerHologram(uuid, DELIVERY_MAN_NAME,2, "");
                } else {
                    hologramManager.setTextPlayerHologram(uuid,DELIVERY_MAN_NAME,1,"&7There are no deliveries available!");
                    hologramManager.setTextPlayerHologram(uuid,DELIVERY_MAN_NAME,2,"&7Come back later...");
                }
            });
        });
    }
}
