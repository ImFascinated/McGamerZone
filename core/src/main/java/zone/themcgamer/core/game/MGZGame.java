package zone.themcgamer.core.game;

import com.cryptomorin.xseries.XMaterial;
import lombok.AllArgsConstructor;
import lombok.Getter;
import zone.themcgamer.core.game.kit.KitDisplay;
import zone.themcgamer.core.game.kit.impl.WarriorKit;
import zone.themcgamer.core.world.WorldCategory;
import zone.themcgamer.data.jedis.data.ServerGroup;
import zone.themcgamer.data.jedis.data.server.MinecraftServer;
import zone.themcgamer.data.jedis.repository.RedisRepository;
import zone.themcgamer.data.jedis.repository.impl.ServerGroupRepository;

import java.util.*;

/**
 * @author Braydon
 * @implNote This represents a game on the server and its properties
 */
@AllArgsConstructor @Getter
public enum MGZGame {
    SKYBLOCK("Skyblock Pirate", new String[] {
            "Spawn on a floating island with",
            "limited resources. &bExpand and grow&7!",
            "",
            "&c&oAbove all don't fall!",
            "",
            "&a&lFEATURES",
            " &e» &7Custom Islands",
            " &e» &7McMMO",
            " &e» &7Minions",
            " &e» &7Player Shops",
            " &e» &7Player Warps",
            " &e» &7Economy"
    }, new WorldCategory[] {
            WorldCategory.SKYBLOCK
    }, WorldCategory.SKYBLOCK.getIcon(), GameCategory.SURVIVE, "Skyblock", 0, 200, null),

    PRISON("Prison", new String[] {
            "Start mining and rank-up to the",
            "highest level and prestige up!",
            "",
            "&a&lFEATURES",
            " &e» &7Economy"
    }, new WorldCategory[] {
            WorldCategory.PRISON
    }, WorldCategory.PRISON.getIcon(), GameCategory.SURVIVE, "Prison", 0, 200, null),

    ARCADE("Arcade", new String[] {
            "Arcade desc"
    }, new WorldCategory[] {
            WorldCategory.THE_BRIDGE,
            WorldCategory.CHAOSPVP,
            WorldCategory.DISASTERS
    }, XMaterial.NOTE_BLOCK, GameCategory.MINIGAME,"Arcade", -1, -1, null),

    THE_BRIDGE("The Bridge", new String[] {
            "Battle it out and destroy each",
            "others nexus to earn the top spot!"
    }, new WorldCategory[] {
            WorldCategory.THE_BRIDGE
    }, WorldCategory.THE_BRIDGE.getIcon(), GameCategory.MINIGAME, "TheBridge", 2, 32, new KitDisplay[] {
            new WarriorKit()
    }),

    CHAOSPVP("ChaosPvP", new String[] {
            "An arena of chaos, gear up",
            "and survive on this battlefield,",
            "alone or with your friends!"
    }, new WorldCategory[] {
            WorldCategory.CHAOSPVP
    }, WorldCategory.CHAOSPVP.getIcon(), GameCategory.PVP,"ChaosPvP", 6, 32, null),

    DISASTERS("Disasters", new String[] {
            "Survive in a world where various",
            "disasters will come!"
    }, new WorldCategory[] {
            WorldCategory.DISASTERS
    }, WorldCategory.DISASTERS.getIcon(), GameCategory.MINIGAME,"Disasters", 6, 32, null);

    private final String name;
    private final String[] description;
    private final WorldCategory[] worldCategories;
    private final XMaterial icon;
    private final GameCategory gameCategory;
    private final String serverGroup;
    private final int minPlayers, maxPlayers;
    private final KitDisplay[] kitDisplays;

    public KitDisplay getKitDisplay(String name) {
        if (kitDisplays == null)
            return null;
        return Arrays.stream(kitDisplays)
                .filter(kitDisplay -> kitDisplay.getClass().getSimpleName().equalsIgnoreCase(name) || kitDisplay.getId().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    /**
     * Get the amount of players playing this game
     *
     * @return the amount of players playing
     */
    public int getPlaying() {
        int players = 0;
        Optional<ServerGroupRepository> optionalServerGroupRepository = RedisRepository.getRepository(ServerGroupRepository.class);
        if (optionalServerGroupRepository.isPresent()) {
            Optional<ServerGroup> optionalServerGroup = optionalServerGroupRepository.get().lookup(serverGroup);
            if (optionalServerGroup.isPresent()) {
                for (MinecraftServer server : optionalServerGroup.get().getServers()) {
                    players+= server.getOnline();
                }
            }
        }
        return players;
    }

    /**
     * Get the best {@link MinecraftServer} to join for this game
     *
     * @return the optional server
     */
    public Optional<MinecraftServer> getBestServer() {
        Optional<ServerGroupRepository> optionalServerGroupRepository = RedisRepository.getRepository(ServerGroupRepository.class);
        if (optionalServerGroupRepository.isEmpty())
            return Optional.empty();
        ServerGroupRepository serverGroupRepository = optionalServerGroupRepository.get();
        Optional<ServerGroup> optionalServerGroup = serverGroupRepository.lookup(serverGroup);
        if (optionalServerGroup.isEmpty())
            return Optional.empty();
        List<MinecraftServer> servers = new ArrayList<>(optionalServerGroup.get().getServers());
        servers.removeIf(minecraftServer -> !minecraftServer.isRunning());
        servers.sort((a, b) -> Integer.compare(b.getOnline(), a.getOnline()));
        if (servers.isEmpty())
            return Optional.empty();
        Collections.shuffle(servers);
        return Optional.of(servers.get(0));
    }
}