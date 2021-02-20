package zone.themcgamer.core.deliveryMan;

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
            client.get().getLastClaimedRewards().put(reward, resultSet.getLong("lastClaimed"));
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
        for (DeliveryManReward reward : DeliveryManReward.values()) {
            if ((canClaim(player, reward) && optionalAccount.get().hasRank(reward.getRequiredRank()))) {
                player.sendMessage(Style.main(DELIVERY_MAN_NAME, "You have unclaimed rewards! Visit §b" + DELIVERY_MAN_NAME + " §7to claim them!"));
                return;
            }
        }
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
        if (!canClaim(player, reward))
            return;
        Optional<Account> optionalAccount = AccountManager.fromCache(player.getUniqueId());
        if (optionalAccount.isEmpty())
            return;
        repository.claim(optionalAccount.get().getId(), reward);
        optionalClient.get().getLastClaimedRewards().put(reward, System.currentTimeMillis());
        player.sendMessage(Style.main(DELIVERY_MAN_NAME, "You claimed §b" + reward.getDisplayName() + "§7."));
    }

    /**
     * Checks if a {@link DeliveryManReward} can be claimed at the time of checking.
     *
     * @param player the player that we are checking from
     * @param reward the reward that we are seeing if it can be claimed
     * @return if the reward is claimable
     */
    public boolean canClaim(Player player, DeliveryManReward reward) {
        Optional<DeliveryManClient> optionalClient = lookup(player.getUniqueId());
        if (optionalClient.isEmpty())
            return false;
        if (!optionalClient.get().getLastClaimedRewards().containsKey(reward))
            return true;
        else
            return System.currentTimeMillis() - optionalClient.get().getLastClaimedRewards().get(reward) > reward.getClaimCooldown();
    }
}