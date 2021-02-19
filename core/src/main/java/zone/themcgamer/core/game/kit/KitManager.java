package zone.themcgamer.core.game.kit;

import org.bukkit.plugin.java.JavaPlugin;
import zone.themcgamer.common.EnumUtils;
import zone.themcgamer.core.account.MiniAccount;
import zone.themcgamer.core.game.MGZGame;
import zone.themcgamer.core.module.ModuleInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Braydon
 */
@ModuleInfo(name = "Kit Manager")
public class KitManager extends MiniAccount<KitClient> {
    public KitManager(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public KitClient getAccount(int accountId, UUID uuid, String name, String ip, String encryptedIp) {
        return new KitClient();
    }

    @Override
    public String getQuery(int accountId, UUID uuid, String name, String ip, String encryptedIp) {
        return "SELECT game, kit FROM `kits` WHERE `accountId` = '" + accountId + "';";
    }

    @Override
    public void loadAccount(int accountId, UUID uuid, String name, String ip, String encryptedIp, ResultSet resultSet) throws SQLException {
        Optional<KitClient> client = lookup(uuid);
        if (!client.isPresent())
            return;
        while (resultSet.next()) {
            MGZGame game = EnumUtils.fromString(MGZGame.class, resultSet.getString("game"));
            if (game == null)
                return;
            KitDisplay kitDisplay = game.getKitDisplay(resultSet.getString("kit"));
            if (kitDisplay == null)
                return;
            client.get().getSelectedKit().put(game, kitDisplay);
        }
    }
}