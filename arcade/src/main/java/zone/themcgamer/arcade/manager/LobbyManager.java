package zone.themcgamer.arcade.manager;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import zone.themcgamer.arcade.Arcade;
import zone.themcgamer.arcade.game.GameState;
import zone.themcgamer.arcade.map.menu.MapVotingMenu;
import zone.themcgamer.arcade.map.menu.TimeVoteMenu;
import zone.themcgamer.arcade.team.menu.SelectTeamMenu;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.common.ItemBuilder;
import zone.themcgamer.core.common.SkullTexture;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.module.Module;
import zone.themcgamer.core.module.ModuleInfo;
import zone.themcgamer.core.traveller.ServerTraveller;
import zone.themcgamer.data.Rank;

import java.util.Optional;

/**
 * @author Braydon
 */
@ModuleInfo(name = "Lobby Manager")
public class LobbyManager extends Module {
    private static final ItemStack INFORMATION = new ItemBuilder(XMaterial.WRITTEN_BOOK)
            .setName("§a§lGame §8» §7How to play?")
            .addLoreLine("&7Click to get a small documentation about this game").toItemStack();
    private static final ItemStack KITS = new ItemBuilder(XMaterial.FEATHER)
            .setName("§a§lKits §8» §7Select Kit")
            .addLoreLine("&7Click to select a kit").toItemStack();

    private static final ItemStack TEAM_SELECTOR = new ItemBuilder(XMaterial.PLAYER_HEAD)
            .setSkullOwner(SkullTexture.TEAM_UNDYED)
            .setName("§a§lTeams §8» §7Select Team")
            .addLoreLine("&7Click to select a team").toItemStack();
    public static final ItemStack MAP_VOTE = new ItemBuilder(XMaterial.BOOKSHELF)
            .setName("§a§lVote §8» §7Select Map")
            .toItemStack();
    private static final ItemStack TIME_VOTE = new ItemBuilder(XMaterial.CLOCK)
            .setName("§a§lVote §8» §7Select Time")
            .addLoreLine("&7Click to select a time").toItemStack();

    private static final ItemStack GO_BACK_LOBBY = new ItemBuilder(XMaterial.ORANGE_BED)
            .setName("§a§lGame §8» §c§lGo back to lobby")
            .addLoreLine("&7Click to go back to a lobby server").toItemStack();

    private final ArcadeManager arcadeManager;
    private final ServerTraveller traveller;

    public LobbyManager(JavaPlugin plugin, ArcadeManager arcadeManager, ServerTraveller traveller) {
        super(plugin);
        this.arcadeManager = arcadeManager;
        this.traveller = traveller;
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        GameState state = arcadeManager.getState();
        if (state == GameState.PLAYING || state == GameState.ENDING)
            return;
        player.getInventory().setItem(0, INFORMATION);
        player.getInventory().setItem(1, KITS);
        player.getInventory().setItem(3, TEAM_SELECTOR);
        player.getInventory().setItem(4, MAP_VOTE);
        player.getInventory().setItem(5, TIME_VOTE);
        player.getInventory().setItem(8, GO_BACK_LOBBY);
        player.getInventory().setHeldItemSlot(0);
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        GameState state = arcadeManager.getState();
        if (state == GameState.PLAYING || state == GameState.ENDING)
            return;
        if (inventory == null)
            return;
        if (player.getInventory().equals(inventory) && player.getGameMode() != GameMode.CREATIVE)
            event.setCancelled(true);
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL)
            return;
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null)
            return;
        if (item.isSimilar(TEAM_SELECTOR))
            new SelectTeamMenu(player, arcadeManager.getGame()).open();
        else if (item.isSimilar(MAP_VOTE)) {
            if (!arcadeManager.getMapVotingManager().isVoting()) {
                player.sendMessage(Style.main("Voting", "&cYou can not vote at this moment, waiting for more players..."));
                return;
            }
            new MapVotingMenu(player, arcadeManager.getMapVotingManager()).open();
        } else if (item.isSimilar(TIME_VOTE)) {
            Optional<Account> optionalAccount = AccountManager.fromCache(player.getUniqueId());
            if (optionalAccount.isPresent() && (!optionalAccount.get().hasRank(Rank.GAMER))) {
                player.sendMessage(Style.rankRequired(Rank.GAMER));
                return;
            }
            new TimeVoteMenu(player).open();
        } else if (item.isSimilar(GO_BACK_LOBBY))
            traveller.sendPlayer(player,"Hub");
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        GameState state = arcadeManager.getState();
        if (state == GameState.PLAYING || state == GameState.ENDING)
            return;
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID && entity instanceof Player)
            entity.teleport(Arcade.INSTANCE.getSpawn());
        event.setCancelled(true);
    }

    @EventHandler
    private void onPickupItem(PlayerAttemptPickupItemEvent event) {
        GameState state = arcadeManager.getState();
        if (state == GameState.PLAYING || state == GameState.ENDING)
            return;
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
            event.setCancelled(true);
    }

    @EventHandler
    private void onDropItem(PlayerDropItemEvent event) {
        GameState state = arcadeManager.getState();
        if (state == GameState.PLAYING || state == GameState.ENDING)
            return;
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
            event.setCancelled(true);
    }

    @EventHandler
    private void onFoodLevelChange(FoodLevelChangeEvent event) {
        GameState state = arcadeManager.getState();
        if (state == GameState.PLAYING || state == GameState.ENDING)
            return;
        event.setCancelled(true);
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        GameState state = arcadeManager.getState();
        if (state == GameState.PLAYING || state == GameState.ENDING)
            return;
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;
        event.setCancelled(true);
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        GameState state = arcadeManager.getState();
        if (state == GameState.PLAYING || state == GameState.ENDING)
            return;
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;
        event.setCancelled(true);
    }

    @EventHandler
    private void onWeatherChange(WeatherChangeEvent event) {
        GameState state = arcadeManager.getState();
        if (state == GameState.PLAYING || state == GameState.ENDING)
            return;
        event.setCancelled(true);
    }

    @EventHandler
    private void onTnTPrime(ExplosionPrimeEvent event) {
        GameState state = arcadeManager.getState();
        if (state == GameState.PLAYING || state == GameState.ENDING)
            return;
        event.setCancelled(true);
    }

    @EventHandler
    private void onLeaveDecay(LeavesDecayEvent event) {
        GameState state = arcadeManager.getState();
        if (state == GameState.PLAYING || state == GameState.ENDING)
            return;
        event.setCancelled(true);
    }

    @EventHandler
    private void entityChangeSoil(PlayerInteractEvent event) {
        GameState state = arcadeManager.getState();
        if (state == GameState.PLAYING || state == GameState.ENDING)
            return;
        if (event.getAction() != Action.PHYSICAL)
            return;
        if (event.getClickedBlock().getType() == XMaterial.FARMLAND.parseMaterial())
            event.setCancelled(true);
    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        GameState state = arcadeManager.getState();
        if (state == GameState.PLAYING || state == GameState.ENDING)
            return;
        if (event.getRemover() instanceof Player)
            event.setCancelled(true);
    }
}