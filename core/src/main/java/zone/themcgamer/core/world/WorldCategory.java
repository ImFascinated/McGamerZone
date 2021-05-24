package zone.themcgamer.core.world;

import com.cryptomorin.xseries.XMaterial;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author Braydon
 */
@AllArgsConstructor @Getter
public enum WorldCategory {
    // Games
    THE_BRIDGE("The Bridge", XMaterial.END_STONE),
    DISASTERS("Disasters", XMaterial.LAVA_BUCKET),
    CHAOSPVP("Chaospvp", XMaterial.IRON_SWORD),
    TNT_WARS("TnT Wars", XMaterial.TNT),
    SKYBLOCK("Skyblock", XMaterial.GRASS_BLOCK),
    PRISON("Prison", XMaterial.IRON_BARS),

    // Other
    HUB("Hub", XMaterial.MAP),
    GAME_LOBBY("Waiting Lobby", XMaterial.FILLED_MAP),
    PACKS("Packs", XMaterial.OAK_SAPLING), // E.g: tree packs
    PERSONAL("Personal", XMaterial.PLAYER_HEAD),
    OLD("Old", XMaterial.REDSTONE_BLOCK),
    OTHER("Other", XMaterial.CAULDRON);

    private final String name;
    private final XMaterial icon;

    public static WorldCategory lookup(String s) {
        return Arrays.stream(values())
                .filter(category -> category.name().equalsIgnoreCase(s) || category.getName().equalsIgnoreCase(s))
                .findFirst().orElse(null);
    }
}