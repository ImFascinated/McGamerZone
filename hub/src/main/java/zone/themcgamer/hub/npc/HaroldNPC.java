package zone.themcgamer.hub.npc;

import lombok.Getter;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;
import zone.themcgamer.core.deliveryMan.DeliveryManManager;
import zone.themcgamer.core.deliveryMan.DeliveryManMenu;
import zone.themcgamer.core.module.Module;
import zone.themcgamer.core.module.ModuleInfo;
import zone.themcgamer.core.npc.NpcManager;
import zone.themcgamer.core.world.MGZWorld;
import zone.themcgamer.hub.Hub;

@ModuleInfo(name = "NPCHandler")
public class HaroldNPC extends Module {

    @Getter private final NpcManager npcManager;
    @Getter private final DeliveryManManager deliveryManManager;

    public HaroldNPC(JavaPlugin plugin) {
        super(plugin);
        deliveryManManager = Module.getModule(DeliveryManManager.class);
        npcManager = Module.getModule(NpcManager.class);

        if (plugin.getServer().getPluginManager().isPluginEnabled("Citizens"))
            loadHarold();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onNPCLeftClick(NPCLeftClickEvent event) {
        NpcManager.getNpc("HAROLD").ifPresent(npc -> {
            if (event.getNPC() == npc && deliveryManManager != null)
                new DeliveryManMenu(event.getClicker(), deliveryManManager).open();
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onNPCRightClick(NPCRightClickEvent event) {
        NpcManager.getNpc("HAROLD").ifPresent(npc -> {
            if (event.getNPC() == npc && deliveryManManager != null)
                new DeliveryManMenu(event.getClicker(), deliveryManManager).open();
        });
    }

    private void loadHarold(){
        final Location location = MGZWorld.get(Bukkit.getWorlds().get(0)).getDataPoint("HAROLD");
        if (location != null) {
            location.setYaw(Hub.INSTANCE.getSpawn().getYaw());
            npcManager.addNpc("HAROLD", npcManager.createNpc("&7", EntityType.VILLAGER, location));
            //Have to be 20 ticks delay, otherwise you get a nullpointer due the npc's is'n spawned in yet.
            Bukkit.getScheduler().runTaskLater(getPlugin(), this::setDefaults, 20);
        }
    }

    private void setDefaults() {
        NpcManager.getNpc("HAROLD").ifPresent(npc -> {
            npc.setProtected(true);
            npc.getEntity().setSilent(true);
            npc.getEntity().setCustomNameVisible(false);
        });
        return;
    }
}
