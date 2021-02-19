package zone.themcgamer.skyblock.scoreboard;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import zone.themcgamer.common.DoubleUtils;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.common.scoreboard.WritableScoreboard;

import java.time.LocalDateTime;
import java.util.Optional;

public class SkyblockScoreboard  extends WritableScoreboard {
    public SkyblockScoreboard(Player player) {
        super(player);
    }

    @Override
    public String getTitle() {
        return "§2§lSkyblock §7Pirate";
    }

    @Override
    public void writeLines() {
        Optional<Account> optionalAccount = AccountManager.fromCache(player.getUniqueId());
        if (optionalAccount.isEmpty()) {
            writeBlank();
            return;
        }
        Account account = optionalAccount.get();

        LocalDateTime dateTime = LocalDateTime.now();
        write("§7" + dateTime.getMonth().getValue() + "/" + dateTime.getDayOfMonth() + "/" + dateTime.getYear());

        writeBlank();
        write("§e┋ Account");
        write("§e┋ §fRank: &7" + account.getPrimaryRank().getColor() + account.getPrimaryRank().getDisplayName());
        write("§e┋ &fMoney: &d" + PlaceholderAPI.setPlaceholders(player, "%vault_eco_balance_formatted%"));
        write("§e┋ &fMob Gems: &a0");
        write("§e┋ &fMcMMO: &60");

        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(player);
        if (superiorPlayer == null)
            return;
        if (superiorPlayer.getIsland() == null) {
            writeBlank();
            write("&7You do not have");
            write("&7an island!");
            write("&e/start &7to get started!");
        } else {
            writeBlank();
            write("§c┋ Island");
            write("§c┋ §fRole: &c" + superiorPlayer.getPlayerRole().getName());
            write("§c┋ §fLevel: &a" + superiorPlayer.getIsland().getIslandLevel());
            write("§c┋ &fSize: &b" + superiorPlayer.getIsland().getIslandSize() + "x" + superiorPlayer.getIsland().getIslandSize());
            write("§c┋ &fTeam: &3" + superiorPlayer.getIsland().getIslandMembers(true).size() + "/" + superiorPlayer.getIsland().getTeamLimit());
            write("§c┋ &fBank: &e" + DoubleUtils.format(superiorPlayer.getIsland().getIslandBank().getBalance().doubleValue(), true));
        }
        writeBlank();
        write("§bthemcgamer.zone");
    }
}