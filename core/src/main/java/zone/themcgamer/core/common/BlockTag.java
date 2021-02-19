package zone.themcgamer.core.common;

import com.cryptomorin.xseries.XMaterial;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Arrays;

/**
 * @author Braydon
 * @implNote This class serves the purpose of checking if a {@link XMaterial}, {@link Material}, or {@link Block} is
 *           of a specific type.
 * @usage {@code BlockTag.TRAPDOOR.hasType(block)} check if the given {@link Block} is a type of trapdoor
 */
@AllArgsConstructor @Getter
public enum BlockTag {
    TRAPDOOR(new XMaterial[] {
            XMaterial.OAK_TRAPDOOR,
            XMaterial.SPRUCE_TRAPDOOR,
            XMaterial.BIRCH_TRAPDOOR,
            XMaterial.JUNGLE_TRAPDOOR,
            XMaterial.ACACIA_TRAPDOOR,
            XMaterial.DARK_OAK_TRAPDOOR,
            XMaterial.CRIMSON_TRAPDOOR,
            XMaterial.WARPED_TRAPDOOR,
            XMaterial.IRON_TRAPDOOR
    }),
    DOOR(new XMaterial[] {
            XMaterial.OAK_DOOR,
            XMaterial.SPRUCE_DOOR,
            XMaterial.BIRCH_DOOR,
            XMaterial.JUNGLE_DOOR,
            XMaterial.ACACIA_DOOR,
            XMaterial.DARK_OAK_DOOR,
            XMaterial.CRIMSON_DOOR,
            XMaterial.WARPED_DOOR,
            XMaterial.IRON_DOOR
    }),
    FENCE_GATE(new XMaterial[] {
            XMaterial.OAK_FENCE_GATE,
            XMaterial.SPRUCE_FENCE_GATE,
            XMaterial.BIRCH_FENCE_GATE,
            XMaterial.JUNGLE_FENCE_GATE,
            XMaterial.ACACIA_FENCE_GATE,
            XMaterial.DARK_OAK_FENCE_GATE,
            XMaterial.CRIMSON_FENCE_GATE,
            XMaterial.WARPED_FENCE_GATE
    }),
    CHEST(new XMaterial[] {
            XMaterial.CHEST,
            XMaterial.TRAPPED_CHEST,
            XMaterial.ENDER_CHEST
    }),
    STORAGE(new XMaterial[] {
            XMaterial.CHEST,
            XMaterial.TRAPPED_CHEST,
            XMaterial.ENDER_CHEST,
            XMaterial.CHEST_MINECART,
            XMaterial.SHULKER_BOX,
            XMaterial.WHITE_SHULKER_BOX,
            XMaterial.ORANGE_SHULKER_BOX,
            XMaterial.MAGENTA_SHULKER_BOX,
            XMaterial.LIGHT_BLUE_SHULKER_BOX,
            XMaterial.YELLOW_SHULKER_BOX,
            XMaterial.LIME_SHULKER_BOX,
            XMaterial.PINK_SHULKER_BOX,
            XMaterial.GRAY_SHULKER_BOX,
            XMaterial.LIGHT_GRAY_SHULKER_BOX,
            XMaterial.CYAN_SHULKER_BOX,
            XMaterial.PURPLE_SHULKER_BOX,
            XMaterial.BLUE_SHULKER_BOX,
            XMaterial.BROWN_SHULKER_BOX,
            XMaterial.GREEN_SHULKER_BOX,
            XMaterial.RED_SHULKER_BOX,
            XMaterial.BLACK_SHULKER_BOX,
            XMaterial.BARREL,
            XMaterial.DISPENSER,
            XMaterial.DROPPER,
            XMaterial.HOPPER
            // TODO: 1/29/21 add BUNDLE from Minecraft 1.17
    }),
    MUSIC(new XMaterial[] {
            XMaterial.JUKEBOX,
            XMaterial.NOTE_BLOCK
    }),
    ANVIL(new XMaterial[] {
            XMaterial.ANVIL,
            XMaterial.CHIPPED_ANVIL,
            XMaterial.DAMAGED_ANVIL
    });

    private final XMaterial[] types;

    /**
     * Check if the given {@link Block} {@link Material} is apart of this {@link BlockTag}
     * @param block the block to check
     * @return if the block material is apart of this tag
     */
    public boolean isType(Block block) {
        return isType(block.getType());
    }

    /**
     * Check if the given {@link Material} is apart of this {@link BlockTag}
     * @param material the material to check
     * @return if the material is apart of this tag
     */
    public boolean isType(Material material) {
        return isType(XMaterial.matchXMaterial(material));
    }

    /**
     * Check if the given {@link XMaterial} is apart of this {@link BlockTag}
     * @param material the material to check
     * @return if the material is apart of this tag
     */
    public boolean isType(XMaterial material) {
        return Arrays.asList(types).contains(material);
    }
}