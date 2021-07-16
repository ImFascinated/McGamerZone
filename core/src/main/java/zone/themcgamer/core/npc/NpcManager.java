package zone.themcgamer.core.npc;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import zone.themcgamer.core.module.Module;
import zone.themcgamer.core.module.ModuleInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ModuleInfo(name="Npc manager")
public class NpcManager extends Module {

    private static final Map<String, NPC> cache = new HashMap<>();

    public void onDisable() {
        cache.forEach((name, npc) -> {
            despawnNpc(npc);
            destroyNpc(npc);
        });
        cache.clear();
    }

    public NpcManager(JavaPlugin plugin) {
        super(plugin);
    }

    public void addNpc(String name, NPC npc) {
        cache.put(name, npc);
    }

    public NPC removeNpc(String name) {
        return cache.remove(name);
    }

    public static Optional<NPC> getNpc(String name) {
        return Optional.ofNullable(cache.get(name));
    }

    public boolean hasNpc(NPC npc) {
        return cache.containsValue(npc);
    }

    public NPC createNpc(String displayName, EntityType type, Location location) {
        final NPC npc = CitizensAPI.getNPCRegistry().createNPC(type, displayName);
        Bukkit.getScheduler().runTask(getPlugin(), () -> npc.spawn(location));
        return npc;
    }

    public NPC despawnNpc(NPC npc) {
        npc.despawn(); // So that the npc can be spawned once again in the future
        return npc;
    }

    public NPC destroyNpc(NPC npc) {
        npc.destroy();
        return npc;
    }

}
