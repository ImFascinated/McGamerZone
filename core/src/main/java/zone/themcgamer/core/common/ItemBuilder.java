package zone.themcgamer.core.common;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.*;

/**
 * @author Braydon
 */
public class ItemBuilder {
    private final ItemStack item;

    /**
     * Create a new item builder from an existing {@link ItemStack}
     *
     * @param item - The {@link ItemStack} you would like to create the item builder with
     */
    public ItemBuilder(ItemStack item) {
        this.item = item;
    }

    /**
     * Create a new item builder with the provided {@link XMaterial}
     *
     * @param xMaterial - The {@link XMaterial} of the item you would like to create
     */
    public ItemBuilder(XMaterial xMaterial) {
        this(xMaterial, 1, xMaterial.getData());
    }

    /**
     * Create a new item builder with the provided {@link Material}
     *
     * @param material - The {@link Material} of the item you would like to create
     */
    public ItemBuilder(Material material) {
        this(material, 1);
    }

    /**
     * Create a new item builder with the provided {@link XMaterial}, and amount
     *
     * @param xMaterial - The {@link XMaterial} of the item you would like to create
     * @param amount - The amount of the item you would like to create
     */
    public ItemBuilder(XMaterial xMaterial, int amount) {
        Material material = xMaterial.parseMaterial();
        if (material == null)
            material = Material.STONE;
        item = new ItemStack(material, amount, xMaterial.getData());
    }

    /**
     * Create a new item builder with the provided {@link Material}, and amount
     *
     * @param material - The {@link Material} of the item you would like to create
     * @param amount - The amount of the item you would like to create
     */
    public ItemBuilder(Material material, int amount) {
        item = new ItemStack(material, amount);
    }

    /**
     * Create a new item builder with the provided {@link XMaterial}, amount, and dye color
     *
     * @param xMaterial - The {@link XMaterial} of the item you would like to create
     * @param amount - The amount of the item you would like to create
     * @param color - The dye color of the item you would like to create
     */
    public ItemBuilder(XMaterial xMaterial, int amount, DyeColor color) {
        this(xMaterial, amount, color.getDyeData());
    }

    /**
     * Create a new item builder with the provided {@link Material}, amount, and dye color
     *
     * @param material - The {@link Material} of the item you would like to create
     * @param amount - The amount of the item you would like to create
     * @param color - The dye color of the item you would like to create
     */
    public ItemBuilder(Material material, int amount, DyeColor color) {
        this(material, amount, color.getDyeData());
    }

    /**
     * Create a new item builder with the provided {@link XMaterial}, amount, and data
     *
     * @param xMaterial - The {@link XMaterial} of the item you would like to create
     * @param amount - The amount of the item you would like to create
     * @param data - The data of the item you would like to create
     */
    public ItemBuilder(XMaterial xMaterial, int amount, byte data) {
        Material material = xMaterial.parseMaterial();
        if (material == null)
            material = Material.STONE;
        item = new ItemStack(material, amount, data);
    }

    /**
     * Create a new item builder with the provided {@link Material}, amount, and data
     *
     * @param material - The {@link Material} of the item you would like to create
     * @param amount - The amount of the item you would like to create
     * @param data - The data of the item you would like to create
     */
    public ItemBuilder(Material material, int amount, byte data) {
        item = new ItemStack(material, amount, data);
    }

    /**
     * Sets the type of your item to the provided {@link XMaterial}
     *
     * @param xMaterial - The {@link XMaterial} you would like to set your item to
     * @return the item builder
     */
    public ItemBuilder setType(XMaterial xMaterial) {
        Material material = xMaterial.parseMaterial();
        if (material == null)
            material = Material.STONE;
        return setType(material);
    }

    /**
     * Sets the type of your item to the provided {@link Material}
     *
     * @param material - The {@link Material} you would like to set your item to
     * @return the item builder
     */
    public ItemBuilder setType(Material material) {
        item.setType(material);
        return this;
    }

    /**
     * Sets the data of your item to the provided data
     *
     * @param data - The data you would like to set your item to
     * @return the item builder
     */
    public ItemBuilder setData(byte data) {
        item.setDurability(data);
        return this;
    }

    /**
     * Sets the display name of your item to the provided string
     *
     * @param name - The name you would like to set your item to
     * @return the item builder
     */
    public ItemBuilder setName(String name) {
        ItemMeta meta = item.getItemMeta();
        if (name != null)
            meta.setDisplayName(Style.color(name));
        else meta.setDisplayName("§a");
        item.setItemMeta(meta);
        return this;
    }

    /**
     * Sets the lore of your item to the provided string array
     *
     * @param array - The array of strings you would like to set your lore to
     * @return the item builder
     */
    public ItemBuilder setLore(String... array) {
        return setLore(Arrays.asList(array));
    }

    /**
     * Sets the lore of your item to the provided string list
     *
     * @param list - The list of strings you would like to set your lore to
     * @return the item builder
     */
    public ItemBuilder setLore(List<String> list) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Style.colorLines(list));
        item.setItemMeta(meta);
        return this;
    }

    /**
     * Add a string to the item lore at the provided index
     *
     * @param index - The index you would like to add the lore line to
     * @param s - The text you would like to add at the provided index
     * @return the item builder
     */
    public ItemBuilder addLoreLine(int index, String s) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>(meta.getLore());
        lore.set(index, Style.color(s));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return this;
    }

    /**
     * Add a string to the item lore
     *
     * @param s - The text you would like to add to your item lore
     * @return the item builder
     */
    public ItemBuilder addLoreLine(String s) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (meta.hasLore())
            lore = new ArrayList<>(meta.getLore());
        lore.add(Style.color(s));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return this;
    }

    /**
     * Remove a string from the lore at the provided index
     *
     * @param index - The index you would like to remove the lore line from
     * @return the item builder
     */
    public ItemBuilder removeLoreLine(int index) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>(meta.getLore());
        if (index < 0 || index > lore.size())
            return this;
        lore.remove(index);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return this;
    }

    /**
     * Remove a string from the lore that matches the provided string
     *
     * @param s - The string you would like to remove from the lore
     * @return the item builder
     */
    public ItemBuilder removeLoreLine(String s) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>(meta.getLore());
        if (!lore.contains(s))
            return this;
        lore.remove(s);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return this;
    }

    /**
     * Clears the item lore
     *
     * @return the item builder
     */
    public ItemBuilder clearLore() {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>(meta.getLore());
        lore.clear();
        meta.setLore(lore);
        item.setItemMeta(meta);
        return this;
    }

    /**
     * Set the amount of the itemstack
     *
     * @param amount - The amount you would like your item to be
     * @return the item builder
     */
    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    /**
     * Set the durability of the itemstack
     *
     * @param durability - The durability you would like your item to have
     * @return the item builder
     */
    public ItemBuilder setDurability(short durability) {
        item.setDurability(durability);
        return this;
    }

    /**
     * Add a glow effect to your item
     *
     * @return the item builder
     */
    public ItemBuilder addGlow() {
        return setGlow(true);
    }

    /**
     * Add a glow effect to your item
     *
     * @return the item builder
     */
    public ItemBuilder setGlow(boolean glow) {
        Enchantment enchantment = XEnchantment.ARROW_DAMAGE.parseEnchantment();
        if (enchantment == null)
            return this;
        if (glow) {
            item.addUnsafeEnchantment(enchantment, 1);
            ItemMeta meta = item.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        } else item.removeEnchantment(enchantment);
        return this;
    }

    /**
     * Adds enchantments to your item from a map ({@link XEnchantment}, {@link Integer})
     *
     * @param enchantments - A map of enchantments with the key as the enchant, and the value as the level
     * @return the item builder
     */
    public ItemBuilder addXEnchantments(Map<XEnchantment, Integer> enchantments) {
        Map<Enchantment, Integer> bukkitEnchantments = new HashMap<>();
        for (Map.Entry<XEnchantment, Integer> entry : enchantments.entrySet()) {
            Enchantment bukkitEnchantment = entry.getKey().parseEnchantment();
            if (bukkitEnchantment == null)
                continue;
            bukkitEnchantments.put(bukkitEnchantment, entry.getValue());
        }
        return addEnchantments(bukkitEnchantments);
    }

    /**
     * Adds enchantments to your item from a map ({@link Enchantment}, {@link Integer})
     *
     * @param enchantments - A map of enchantments with the key as the enchant, and the value as the level
     * @return the item builder
     */
    public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
        item.addEnchantments(enchantments);
        return this;
    }

    /**
     * Add an enchantment to your item
     *
     * @param xEnchantment - The {@link XEnchantment} you would like to add
     * @param level - The level of the enchantment to add
     * @return the item builder
     */
    public ItemBuilder addEnchant(XEnchantment xEnchantment, int level) {
        Enchantment enchantment = xEnchantment.parseEnchantment();
        if (enchantment == null)
            return this;
        return addEnchant(enchantment, level);
    }

    /**
     * Add an enchantment to your item
     *
     * @param enchantment - The {@link Enchantment} you would like to add
     * @param level - The level of the enchantment to add
     * @return the item builder
     */
    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(enchantment, level, true);
        item.setItemMeta(meta);
        return this;
    }

    /**
     * Add an unsafe enchantment to your item
     *
     * @param xEnchantment - The {@link XEnchantment} you would like to add
     * @param level - The level of the enchant
     * @return the item builder
     */
    public ItemBuilder addUnsafeEnchantment(XEnchantment xEnchantment, int level) {
        Enchantment enchantment = xEnchantment.parseEnchantment();
        if (enchantment == null)
            return this;
        return addUnsafeEnchantment(enchantment, level);
    }

    /**
     * Add an unsafe enchantment to your item
     *
     * @param enchantment - The {@link Enchantment} you would like to add
     * @param level - The level of the enchant
     * @return the item builder
     */
    public ItemBuilder addUnsafeEnchantment(Enchantment enchantment, int level) {
        item.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    /**
     * Remove an enchantment from your item
     *
     * @param xEnchantment - The {@link XEnchantment} you would like to remove
     * @return the item builder
     */
    public ItemBuilder removeEnchantment(XEnchantment xEnchantment) {
        Enchantment enchantment = xEnchantment.parseEnchantment();
        if (enchantment == null)
            return this;
        return removeEnchantment(enchantment);
    }

    /**
     * Remove an enchantment from your item
     *
     * @param enchantment - The {@link Enchantment} you would like to remove
     * @return the item builder
     */
    public ItemBuilder removeEnchantment(Enchantment enchantment) {
        item.removeEnchantment(enchantment);
        return this;
    }


    /**
     * Clear all enchantments from your item
     *
     * @return the item builder
     */
    public ItemBuilder clearEnchantments() {
        for (Enchantment enchantment : item.getItemMeta().getEnchants().keySet()) {
            item.removeEnchantment(enchantment);
        }
        return this;
    }

    /**
     * Set the skull texture of your item with the provided player's skin
     *
     * @param identifier - The identifier for the skull
     * @return the item builder
     */
    public ItemBuilder setSkullOwner(String identifier) {
        Material targetMaterial = XMaterial.PLAYER_HEAD.parseMaterial();
        if (targetMaterial == null)
            return this;
        if (item.getType() != targetMaterial)
            throw new IllegalStateException("You cannot set the skull owner with type '" + item.getType().name() + "', it must be of type " + targetMaterial.name());
        item.setItemMeta(SkullUtils.applySkin(item.getItemMeta(), identifier));
        return this;
    }

    /**
     * Set the color of your item
     *
     * @param color - The color you would like to set your leather armor to
     * @return the item builder
     */
    public ItemBuilder setLeatherArmorColor(Color color) {
        if (!item.getType().name().contains("LEATHER") || item.getType() == Material.LEATHER)
            throw new IllegalStateException("You cannot set the leather armor color with type '" + item.getType().name() + "', it must be a piece of leather armor");
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(color);
        item.setItemMeta(meta);
        return this;
    }

    /**
     * Create the item we just created with the item builder
     *
     * @return the constructed {@link ItemStack}
     */
    public ItemStack toItemStack() {
        return item;
    }
}