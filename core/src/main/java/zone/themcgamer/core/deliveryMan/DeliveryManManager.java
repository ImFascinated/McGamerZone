package zone.themcgamer.core.deliveryMan;

import com.cryptomorin.xseries.XSound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.account.MiniAccount;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.deliveryMan.command.DeliveryManCommand;
import zone.themcgamer.core.module.ModuleInfo;
import zone.themcgamer.data.mysql.MySQLController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Nicholas
 */
@ModuleInfo(name = "Delivery Man Manager")
public class DeliveryManManager extends MiniAccount<DeliveryManClient> {
    public static final String DELIVERY_MAN_NAME = "Harold";

    private final boolean displayNotification;

    private final DeliveryManRepository repository;

    public DeliveryManManager(JavaPlugin plugin, MySQLController mySQLController, boolean displayNotification) {
        super(plugin);
        repository = new DeliveryManRepository(mySQLController.getDataSource());
        this.displayNotification = displayNotification;
        registerCommand(new DeliveryManCommand(this));
    }

    @Override
    public DeliveryManClient getAccount(int accountId, UUID uuid, String name, String ip, String encryptedIp) {
        return new DeliveryManClient();
    }

    @Override
    public String getQuery(int accountId, UUID uuid, String name, String ip, String encryptedIp) {
        return "SELECT `rewardId`, `lastClaimed` FROM `deliveryMan` WHERE `accountId` = '" + accountId + "';";
    }

    @Override
    public void loadAccount(int accountId, UUID uuid, String name, String ip, String encryptedIp, ResultSet resultSet) throws SQLException {
        Optional<DeliveryManClient> client = lookup(uuid);
        if (client.isEmpty())
            return;
        while (resultSet.next()) {
            DeliveryManReward reward = DeliveryManReward.match(resultSet.getString("rewardId"));
            if (reward == null)
                continue;
            client.get().claim(reward, resultSet.getLong("lastClaimed"));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onJoin(PlayerJoinEvent event) {
        if (!displayNotification)
            return;
        Player player = event.getPlayer();
        Optional<Account> optionalAccount = AccountManager.fromCache(player.getUniqueId());
        if (optionalAccount.isEmpty())
            return;
        Optional<DeliveryManClient> optionalClient = lookup(player.getUniqueId());
        if (optionalClient.isEmpty())
            return;
        if (optionalClient.get().getUnclaimedRewards(player) > 0)
            player.sendMessage(Style.main(DELIVERY_MAN_NAME, "You have unclaimed rewards! Visit §b" + DELIVERY_MAN_NAME + " §7to claim them!"));
    }

    /**
     * Claims a {@link Player}'s {@link DeliveryManReward}
     *
     * @param player the player who is claiming the reward
     * @param reward the reward that's being claimed
     */
    public void claimReward(Player player, DeliveryManReward reward) {
        Optional<DeliveryManClient> optionalClient = lookup(player.getUniqueId());
        if (optionalClient.isEmpty())
            return;
        DeliveryManClient deliveryManClient = optionalClient.get();
        if (!deliveryManClient.canClaim(reward))
            return;
        Optional<Account> optionalAccount = AccountManager.fromCache(player.getUniqueId());
        if (optionalAccount.isEmpty())
            return;
        repository.claim(optionalAccount.get().getId(), reward);
        deliveryManClient.claim(reward);
        player.playSound(player.getEyeLocation(), XSound.ENTITY_PLAYER_LEVELUP.parseSound(), 0.9f, 1f);
        player.sendMessage(Style.main(DELIVERY_MAN_NAME, "You claimed §b" + reward.getDisplayName() + "§7."));
    }
}