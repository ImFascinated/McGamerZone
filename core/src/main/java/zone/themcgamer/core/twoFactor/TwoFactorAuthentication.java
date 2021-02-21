package zone.themcgamer.core.twoFactor;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;
import zone.themcgamer.common.EnumUtils;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.account.MiniAccount;
import zone.themcgamer.core.common.ItemBuilder;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.module.ModuleInfo;
import zone.themcgamer.core.twoFactor.image.QRImageGenerator;
import zone.themcgamer.core.twoFactor.image.QRMapRenderer;
import zone.themcgamer.data.Rank;
import zone.themcgamer.data.mysql.MySQLController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Braydon
 */
@ModuleInfo(name = "Two Factor")
public class TwoFactorAuthentication extends MiniAccount<TwoFactorClient> {
    private final TwoFactorRepository repository;
    private final GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
    private final List<UUID> authenticating = new ArrayList<>();

    public TwoFactorAuthentication(JavaPlugin plugin, MySQLController mySQLController) {
        super(plugin);
        repository = new TwoFactorRepository(mySQLController.getDataSource());
    }

    @Override
    public TwoFactorClient getAccount(int accountId, UUID uuid, String name, String ip, String encryptedIp) {
        return new TwoFactorClient();
    }

    @Override
    public String getQuery(int accountId, UUID uuid, String name, String ip, String encryptedIp) {
        return "SELECT dbKey, value FROM `twoFactor` WHERE `accountId` = '" + accountId + "';";
    }

    @Override
    public void loadAccount(int accountId, UUID uuid, String name, String ip, String encryptedIp, ResultSet resultSet) throws SQLException {
        Optional<TwoFactorClient> optionalTwoFactorClient = lookup(uuid);
        if (optionalTwoFactorClient.isEmpty())
            return;
        TwoFactorClient twoFactorClient = optionalTwoFactorClient.get();
        while (resultSet.next()) {
            TwoFactorDBKey databaseKey = EnumUtils.fromString(TwoFactorDBKey.class, resultSet.getString("dbKey"));
            if (databaseKey == null)
                continue;
            if (databaseKey == TwoFactorDBKey.SECRET_KEY)
                twoFactorClient.setSecretKey(resultSet.getString("value"));
            else twoFactorClient.setLastAuthentication(resultSet.getLong("value"));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onLogin(PlayerLoginEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Optional<Account> optionalAccount = AccountManager.fromCache(uuid);
        if (optionalAccount.isEmpty())
            return;
        Account account = optionalAccount.get();
        if (!account.hasRank(Rank.HELPER))
            return;
        Optional<TwoFactorClient> optionalTwoFactorClient = lookup(uuid);
        if (optionalTwoFactorClient.isEmpty())
            return;
        // If the player hasn't setup 2fa, the player hasn't authenticated in 24 hours, or their ip has changed, make them authenticate
        if (optionalTwoFactorClient.get().requiresAuthentication() || !account.getLastEncryptedIpAddress().equals(account.getEncryptedIpAddress()))
            authenticating.add(uuid);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Optional<Account> optionalAccount = AccountManager.fromCache(player.getUniqueId());
        if (optionalAccount.isEmpty())
            return;
        Account account = optionalAccount.get();
        if (!account.hasRank(Rank.HELPER))
            return;
        if (!authenticating.contains(player.getUniqueId())) {
            player.sendMessage(Style.main(getName(), "§aAuthenticated!"));
            return;
        }
        Optional<TwoFactorClient> optionalTwoFactorClient = lookup(player.getUniqueId());
        if (optionalTwoFactorClient.isEmpty())
            return;
        TwoFactorClient twoFactorClient = optionalTwoFactorClient.get();
        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
            if (twoFactorClient.getSecretKey() == null) {
                String secretKey = googleAuthenticator.createCredentials().getKey();
                twoFactorClient.setSecretKey(secretKey);

                // QR Map
                ItemStack firstSlotItem = player.getInventory().getItem(0);
                if (firstSlotItem != null)
                    twoFactorClient.setFirstSlotItem(firstSlotItem);

                // Creating the map
                Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), new QRImageGenerator(player, twoFactorClient.getSecretKey(), image -> {
                    MapView mapView = Bukkit.createMap(player.getWorld());
                    mapView.getRenderers().removeIf(mapView::removeRenderer);
                    mapView.addRenderer(new QRMapRenderer(player, image));
                    player.getInventory().setItem(0, new ItemBuilder(XMaterial.FILLED_MAP, 1, (byte) mapView.getId()).toItemStack());
                    player.getInventory().setHeldItemSlot(0);
                }));

                player.sendMessage(Style.main(getName(), "Hey §b" + player.getName() + "§7, you have not setup your two factor authentication yet!"));
                player.sendMessage(new ComponentBuilder(Style.main(getName(), "To begin, open your authentication app of choice and scan the QR code on the map or enter " +
                        "the code §6" + secretKey + " §7manually. Once done, type the 6 digit code provided by your authentication app into the chat"))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aClick to copy").create()))
                        .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, secretKey)).create());
                return;
            }
            player.sendMessage(Style.main(getName(), "§cYou need to re-authenticate!"));
            player.sendMessage(Style.main(getName(), "Type the 6 digit code provided by your authentication app into the chat"));
        }, 1L);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!authenticating.contains(player.getUniqueId()))
            return;
        Optional<Account> optionalAccount = AccountManager.fromCache(player.getUniqueId());
        if (optionalAccount.isEmpty())
            return;
        Optional<TwoFactorClient> optionalTwoFactorClient = lookup(player.getUniqueId());
        if (optionalTwoFactorClient.isEmpty())
            return;
        event.setCancelled(true);
        String message = event.getMessage().replaceAll(" ", "");
        int code;
        try {
            code = Integer.parseInt(message);
        } catch (NumberFormatException ex) {
            player.sendMessage(Style.main(getName(), "§cInvalid authentication code!"));
            return;
        }
        TwoFactorClient twoFactorClient = optionalTwoFactorClient.get();
        String secretKey = twoFactorClient.getSecretKey();
        if (!googleAuthenticator.authorize(secretKey, code)) // If the provided code is incorrect, show an error to the player
            player.sendMessage(Style.main(getName(), "§cInvalid authentication code!"));
        else {
            // If the code is correct, we wanna authenticate the player
            twoFactorClient.setLastAuthentication(System.currentTimeMillis());
            repository.authenticate(optionalAccount.get().getId(), secretKey, twoFactorClient.getLastAuthentication());
            authenticating.remove(player.getUniqueId());
            player.playSound(player.getEyeLocation(), XSound.ENTITY_PLAYER_LEVELUP.parseSound(), 0.9f, 1f);
            if (twoFactorClient.getFirstSlotItem() != null)
                player.getInventory().setItem(0, twoFactorClient.getFirstSlotItem());
            player.sendMessage(Style.main(getName(), "§aAuthenticated!"));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onCommand(PlayerCommandPreprocessEvent event) {
        cancelEvent(event);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ())
            cancelEvent(event);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onInventoryClick(InventoryClickEvent event) {
        cancelEvent((Player) event.getWhoClicked(), event);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onFoodLevelChange(FoodLevelChangeEvent event) {
        cancelEvent(event);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player)
            cancelEvent((Player) entity, event);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onHeldItemChange(PlayerItemHeldEvent event) {
        cancelEvent(event);
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        authenticating.remove(event.getPlayer().getUniqueId());
    }

    /**
     * Check whetherr or not the provided {@link Player} is authenticating
     *
     * @param player the player to check
     * @return whether or not they're authenticating
     */
    public boolean isAuthenticating(Player player) {
        return authenticating.contains(player.getUniqueId());
    }

    private void cancelEvent(Cancellable cancellable) {
        if (!(cancellable instanceof PlayerEvent))
            return;
        cancelEvent(((PlayerEvent) cancellable).getPlayer(), cancellable);
    }

    private void cancelEvent(Player player, Cancellable cancellable) {
        if (authenticating.contains(player.getUniqueId()))
            cancellable.setCancelled(true);
    }
}