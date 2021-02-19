package zone.themcgamer.hub.menu;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.common.ItemBuilder;
import zone.themcgamer.core.common.SkullTexture;
import zone.themcgamer.core.common.menu.Button;
import zone.themcgamer.core.common.menu.MenuPattern;
import zone.themcgamer.core.common.menu.MenuType;
import zone.themcgamer.core.common.menu.UpdatableMenu;
import zone.themcgamer.core.module.Module;
import zone.themcgamer.core.plugin.MGZPlugin;
import zone.themcgamer.core.traveller.ServerTraveller;
import zone.themcgamer.data.Rank;
import zone.themcgamer.data.jedis.data.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Braydon
 */
public class HubsMenu extends UpdatableMenu {
    public HubsMenu(Player player) {
        super(player, "Hubs", 4, MenuType.CHEST);
    }

    @Override
    public void onUpdate() {
        List<Integer> slots = MenuPattern.getSlots(
                "XXXXXXXXX",
                "XOOOOOOOX",
                "XOOOOOOOX",
                "XXXXXXXXX"
        );
        fillBorders(new Button(new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setName("").toItemStack()));
        Optional<Account> optionalAccount = AccountManager.fromCache(player.getUniqueId());
        if (optionalAccount.isEmpty())
            return;
        MinecraftServer currentServer = MGZPlugin.getMinecraftServer();
        for (MinecraftServer server : currentServer.getGroup().getServers()) {
            if (!server.isRunning() || server.getNumericId() > 54)
                continue;
            boolean full = server.getOnline() >= server.getMaxPlayers();
            boolean canJoinFull = optionalAccount.get().hasRank(Rank.GAMER);
            boolean connected = currentServer.equals(server);

            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("§aPlayers §8» §f" + server.getOnline() + "§7/§f" + server.getMaxPlayers());
            lore.add("");
            if (connected)
                lore.add("§aConnected!");
            else lore.add(full && !canJoinFull ? "§cFull!" : "§7Click to join!");

            ChatColor color = ChatColor.GRAY;
            String texture = SkullTexture.DIAMOND_BLOCK;
            if (connected) {
                color = ChatColor.GREEN;
                texture = SkullTexture.EMERALD_BLOCK;
            } else if (full && !canJoinFull) {
                color = ChatColor.RED;
                texture = SkullTexture.IRON_BLOCK;
            }
            int slot = server.getNumericId() - 1;
            if (slot >= slots.size())
                continue;
            set(slots.get(slot), new Button(new ItemBuilder(XMaterial.PLAYER_HEAD)
                    .setSkullOwner(texture)
                    .setGlow(connected)
                    .setName(color.toString() + server.getName())
                    .setLore(lore).toItemStack(), event -> {
                if (connected || (full && !canJoinFull)) {
                    player.playSound(player.getEyeLocation(), XSound.ENTITY_VILLAGER_NO.parseSound(), 0.9f, 1f);
                    return;
                }
                close();
                ServerTraveller traveller = Module.getModule(ServerTraveller.class);
                if (traveller != null)
                    traveller.sendPlayer(player, server.getName());
            }));
        }
    }
}