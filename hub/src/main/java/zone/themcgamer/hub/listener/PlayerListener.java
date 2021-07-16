package zone.themcgamer.hub.listener;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.account.menu.ProfileMenu;
import zone.themcgamer.core.common.*;
import zone.themcgamer.core.cooldown.CooldownHandler;
import zone.themcgamer.core.world.MGZWorld;
import zone.themcgamer.data.jedis.cache.CacheRepository;
import zone.themcgamer.data.jedis.cache.impl.PlayerStatusCache;
import zone.themcgamer.data.jedis.repository.RedisRepository;
import zone.themcgamer.hub.Hub;
import zone.themcgamer.hub.menu.HubsMenu;
import zone.themcgamer.hub.menu.TravelerMenu;
import zone.themcgamer.hub.menu.cosmetics.VanityMainMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author Braydon
 */
public class PlayerListener implements Listener {
    private static final ItemStack TRAVELER = new ItemBuilder(XMaterial.COMPASS)
            .setName("§a§lTraveler §8» §7Select game")
            .setLore("§7Click to teleport to a game!")
            .toItemStack();
    private static final ItemStack HUB_SELECTOR = new ItemBuilder(XMaterial.BEACON)
            .setName("§a§lLobbies §8» §7Select lobby")
            .setLore("§7Click to view hub servers")
            .toItemStack();
    private static final ItemStack COSMETICS = new ItemBuilder(XMaterial.CHEST)
            .setName("§a§lVanity §8» §7Select vanity")
            .setLore("§7Click to view cosmetics!")
            .toItemStack();
    private static final ItemStack SETTINGS = new ItemBuilder(XMaterial.COMPARATOR)
            .setName("§a§lSettings §8» §7Account settings")
            .setLore("§7Click to change your settings!")
            .toItemStack();
    private static final ItemBuilder PROFILE = new ItemBuilder(XMaterial.PLAYER_HEAD)
            .setName("§a§lAccount §8» §7View your account")
            .setLore("§7Click to view your profile!");

    private final Hub hub;

    public PlayerListener(Hub hub) {
        this.hub = hub;
        Bukkit.getPluginManager().registerEvents(this, hub);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerUtils.reset(player, true, true, GameMode.ADVENTURE);

        player.getInventory().setItem(0, TRAVELER);
        player.getInventory().setItem(1, HUB_SELECTOR);
        player.getInventory().setItem(4, COSMETICS);
        player.getInventory().setItem(7, SETTINGS);
        player.getInventory().setItem(8, PROFILE.setSkullOwner(player.getName()).toItemStack());
        player.getInventory().setHeldItemSlot(0);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 900000, 2));

        Optional<Account> optionalAccount = AccountManager.fromCache(player.getUniqueId());
        if (optionalAccount.isEmpty())
            return;
        int online = 0;
        Optional<CacheRepository> cacheRepository = RedisRepository.getRepository(CacheRepository.class);
        if (cacheRepository.isPresent())
            online += cacheRepository.get().getCached().stream().filter(cacheItem -> cacheItem instanceof PlayerStatusCache).count();

        for (int i = 0; i < 5; i++)
            player.sendMessage("");
        player.sendMessage(Style.color(" Welcome &7" + optionalAccount.get().getDisplayName() + " &fto &2&lMc&6&lGamer&c&lZone"));
        player.sendMessage(Style.color(" &fThere " + (online == 1 ? "is" : "are") + " &b" + online + " &fplayer" + (online == 1 ? "" : "s") + " online!"));
        player.sendMessage("");

        List<BaseComponent> components = new ArrayList<>();
        components.addAll(Arrays.asList(new ComponentBuilder(Style.color("  ")).create()));
        components.addAll(Arrays.asList(new ComponentBuilder(Style.color("§d§lSTORE"))
                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://store.mcgamerzone.net"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Style.color("§dClick to visit our webstore.")).create()))
                .create()));
        components.addAll(Arrays.asList(new ComponentBuilder(Style.color(" §8\u25AA ")).create()));
        components.addAll(Arrays.asList(new ComponentBuilder(Style.color("&e&lSITE"))
                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://mcgamerzone.net"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Style.color("&eClick to visit our website!")).create()))
                .create()));
        components.addAll(Arrays.asList(new ComponentBuilder(Style.color(" §8\u25AA ")).create()));
        components.addAll(Arrays.asList(new ComponentBuilder(Style.color("&a&lVOTE"))
                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://vote.mcgamerzone.net"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Style.color("&aClick to vote for us!")).create()))
                .create()));
        components.addAll(Arrays.asList(new ComponentBuilder(Style.color(" §8\u25AA ")).create()));
        components.addAll(Arrays.asList(new ComponentBuilder(Style.color("&9&lDISCORD"))
                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.mcgamerzone.net"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Style.color("&9Click to join our community!")).create()))
                .create()));
        player.sendMessage(components.toArray(new BaseComponent[0]));
        player.sendMessage("");
        player.sendMessage(Style.color("&7 For a list of commands, use &f/help"));
        player.sendMessage(Style.color("&7 Wanna learn more about this server? Use &f/wiki"));
        player.sendMessage("");
        player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_PLING.parseSound(), 1f, 1f);

        player.sendTitle(Style.color("&e&lWelcome to"), Style.color("&bThe &2&lMc&6&lGamer&c&lZone"), 20, 30, 20);
        player.teleport(hub.getSpawn());
        event.setJoinMessage(null);
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL)
            return;
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null)
            return;
        if (item.isSimilar(TRAVELER))
            new TravelerMenu(player).open();
        else if (item.isSimilar(HUB_SELECTOR))
            new HubsMenu(player).open();
        else if (item.isSimilar(COSMETICS))
            new VanityMainMenu(player).open();
        else if (item.isSimilar(SETTINGS))
            player.sendMessage("Settings");
        else if (item.isSimilar(PROFILE.setSkullOwner(player.getName()).toItemStack()))
            new ProfileMenu(player).open();
    }

    @EventHandler
    private void onLaunchpad(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            Block block = event.getClickedBlock();
            if (block != null && (block.getType() == Material.IRON_PLATE)) {
                Player player = event.getPlayer();
                event.setCancelled(true);
                player.setVelocity(player.getLocation().getDirection().multiply(1D).setY(1D));
                player.playSound(player.getEyeLocation(), XSound.ENTITY_CHICKEN_EGG.parseSound(), 0.9f, 1f);
            }
        }
    }

    @EventHandler
    private void onBlockInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;
        Block block = event.getClickedBlock();
        if (block == null)
            return;
        if (BlockTag.TRAPDOOR.isType(block)
                || BlockTag.DOOR.isType(block)
                || BlockTag.FENCE_GATE.isType(block)
                || BlockTag.STORAGE.isType(block)
                || BlockTag.MUSIC.isType(block)
                || BlockTag.ANVIL.isType(block)
                || block.getType() == Material.FURNACE
                || block.getType() == Material.WORKBENCH) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onPortal(EntityPortalEnterEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player))
            return;
        if (!CooldownHandler.canUse(((Player) entity), "Hub Portal", 500, false))
            return;
        Player player = (Player) entity;
        MGZWorld world = MGZWorld.get(Bukkit.getWorlds().get(0));
        Location spawn = world.getDataPoint("PORTAL_SPAWN");
        if (spawn != null)
            spawn.setYaw(MathUtils.getFacingYaw(spawn, world.getDataPoints("LOOK_AT")));
        else spawn = hub.getSpawn();
        player.teleport(spawn);
        new TravelerMenu(player).open();
        player.playSound(player.getEyeLocation(), XSound.ENTITY_PLAYER_LEVELUP.parseSound(), 0.9f, 1f);
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID && entity instanceof Player)
            entity.teleport(hub.getSpawn());
        event.setCancelled(true);
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
            event.setCancelled(true);
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
            event.setCancelled(true);
    }

    @EventHandler
    private void onDropItem(PlayerDropItemEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
            event.setCancelled(true);
    }

    @EventHandler
    private void onPickupItem(PlayerPickupItemEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
            event.setCancelled(true);
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        if (inventory == null)
            return;
        if (player.getInventory().equals(inventory) && player.getGameMode() != GameMode.CREATIVE)
            event.setCancelled(true);
    }

    @EventHandler
    private void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }
}