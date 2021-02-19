package zone.themcgamer.core.server.menu;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import zone.themcgamer.common.CpuMonitor;
import zone.themcgamer.common.TimeUtils;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.common.ItemBuilder;
import zone.themcgamer.core.common.MiscUtils;
import zone.themcgamer.core.common.ServerUtils;
import zone.themcgamer.core.common.menu.Button;
import zone.themcgamer.core.common.menu.MenuType;
import zone.themcgamer.core.common.menu.UpdatableMenu;
import zone.themcgamer.core.plugin.MGZPlugin;
import zone.themcgamer.data.Rank;
import zone.themcgamer.data.jedis.data.server.MinecraftServer;

import java.lang.management.ManagementFactory;
import java.util.Optional;

public class ServerMonitorMenu extends UpdatableMenu {
    public ServerMonitorMenu(Player player) {
        super(player, "Server Monitor", 1, MenuType.CHEST);
    }

    @Override
    public void onUpdate() {
        Optional<Account> optionalAccount = AccountManager.fromCache(player.getUniqueId());
        if (optionalAccount.isEmpty())
            return;
        int slot = 0;
        if (optionalAccount.get().hasRank(Rank.JR_DEVELOPER)) {
            MinecraftServer minecraftServer = MGZPlugin.getMinecraftServer();
            set(slot++, new Button(new ItemBuilder(XMaterial.MAP)
                    .setName("§6§lServer Statistics").setLore(
                            "§b" + minecraftServer.getName(),
                            "",
                            "§7CPU §f" + zone.themcgamer.common.MiscUtils.percent(CpuMonitor.systemLoad10SecAvg(), 1D),
                            "§7Memory §f" + minecraftServer.getUsedRam() + "/" + minecraftServer.getMaxRam() + " §7MB",
                            "§7TPS §f" + MiscUtils.formatTps(ServerUtils.getTps()),
                            "§7Uptime §f" + TimeUtils.formatIntoDetailedString(ManagementFactory.getRuntimeMXBean().getUptime(), false),
                            "§7Version §f" + Bukkit.getBukkitVersion(),
                            "§7Players §f" + Bukkit.getOnlinePlayers().size() + "§7/§f" + Bukkit.getMaxPlayers(),
                            "",
                            "§7Node §f" + minecraftServer.getNode().getName(),
                            "§7Host §f" + minecraftServer.getAddress() + ":" + minecraftServer.getPort(),
                            "",
                            "§7Click to manage profilers"
                    ).toItemStack(), event -> player.sendMessage("manage profilers")));
        }
        set(slot, new Button(new ItemBuilder(XMaterial.FEATHER)
                .setName("§6§lChat").setLore(
                        "",
                        "§7Click to manage the chat"
                ).toItemStack()));
    }
}
