package zone.themcgamer.hub.menu;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.entity.Player;
import zone.themcgamer.core.common.ItemBuilder;
import zone.themcgamer.core.common.SkullTexture;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.common.menu.Button;
import zone.themcgamer.core.common.menu.MenuPattern;
import zone.themcgamer.core.common.menu.MenuType;
import zone.themcgamer.core.common.menu.UpdatableMenu;
import zone.themcgamer.core.game.MGZGame;
import zone.themcgamer.core.module.Module;
import zone.themcgamer.core.traveler.ServerTraveler;
import zone.themcgamer.data.jedis.data.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Braydon
 */
public class TravelerMenu extends UpdatableMenu {
    private static final String[] ARROW_COLORS = new String[] { "§2", "§6", "§c" };

    private int arrowIndex, randomGameIndex;

    public TravelerMenu(Player player) {
        super(player, "Traveler", 6, MenuType.CHEST, 700L);
    }

    @Override
    protected void onOpen() {
        fill(new Button(new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE).setName("&f").toItemStack()));
        fillRow(0, new Button(new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setName("&f").toItemStack()));
        set(0, new Button(new ItemBuilder(XMaterial.OAK_SIGN).setName("&e&lHelpful").toItemStack()));
        set(4, new Button(new ItemBuilder(XMaterial.OAK_SIGN).setName("&e&lGamemodes").toItemStack()));
        set(8, new Button(new ItemBuilder(XMaterial.OAK_SIGN).setName("&e&lLobby Spots").toItemStack()));

        set(1, 8, new Button(new ItemBuilder(XMaterial.CHEST).setName("&6&lGem Boxes").toItemStack()));
        set(2, 8, new Button(new ItemBuilder(XMaterial.FISHING_ROD).setName("&c&lDuels").toItemStack()));
        set(3, 8, new Button(new ItemBuilder(XMaterial.LEATHER_BOOTS).setName("&e&lParkour").toItemStack()));
        set(4, 8, new Button(new ItemBuilder(XMaterial.SNOWBALL).setName("&a&lPaintball").toItemStack()));

        set(1, 0, new Button(new ItemBuilder(XMaterial.BOOK)
                .setName("&b/help")
                .addLoreLine("&7Get a list with commands")
                .addLoreLine("&7that you can use. And a link")
                .addLoreLine("&7to our forum with a lot of helpful page's!")
                .addLoreLine("")
                .addLoreLine("&aClick to get help!")
                .toItemStack(), event -> {
            player.performCommand("help");
            player.sendMessage(Style.color("&a&l(!) &e&lClick&7 this link to visit our forums: &amcgamerzone.net/help"));
        }));
        set(2, 0, new Button(new ItemBuilder(XMaterial.PLAYER_HEAD)
                .setSkullOwner(SkullTexture.DISCORD)
                .setName("&b/discord")
                .addLoreLine("&7A discord server for gamers to chat")
                .addLoreLine("&7with other &czoners&7. And be the first")
                .addLoreLine("&7to &eread &7our &6updates&7, &dgiveaways&7 & &a&lmore&7!")
                .addLoreLine("")
                .addLoreLine("&aClick to join our discord!")
                .toItemStack(), event -> {
            player.performCommand("discord");
        }));
        set(3, 0, new Button(new ItemBuilder(XMaterial.DIAMOND)
                .setName("&b/store")
                .addLoreLine("&7Purchase some goodies")
                .addLoreLine("&7from our webstore and support the server!")
                .addLoreLine("&7We have &eranks&7, &dbundles&7, &6and much more&7!")
                .addLoreLine("")
                .addLoreLine("&aClick to visit our store!")
                .toItemStack(), event -> {
            player.performCommand("store");
        }));
        set(4, 0, new Button(new ItemBuilder(XMaterial.WRITTEN_BOOK)
                .setName("&b/vote")
                .addLoreLine("&eVote for us!")
                .addLoreLine("&7Receive a lot of cool &drewards&7, and it helps us too!")
                .addLoreLine("")
                .addLoreLine("&aClick to vote for us!")
                .toItemStack(), event -> {
            player.performCommand("vote");
        }));
    }

    @Override
    public void onUpdate() {
        List<Integer> slots = MenuPattern.getSlots(
                "XXXXXXXXX",
                "XXXOOOXXX",
                "XXXOOOXXX",
                "XXXOOOXXX",
                "XXXXXXXXX",
                "XXXXXXXXX"
        );
        fill(slots,new Button(new ItemBuilder(XMaterial.BARRIER).setName("&c???").toItemStack()));
        if (++arrowIndex >= ARROW_COLORS.length)
            arrowIndex = 0;
        int index = 0;
        for (MGZGame game : MGZGame.values()) {
            int playing = game.getPlaying();
            boolean hasKits = game.getKitDisplays() != null && (game.getKitDisplays().length > 0);

            List<String> lore = new ArrayList<>();
            lore.add("§8Category: §b" + game.getGameCategory().getName());
            lore.add("");
            for (String descriptionLine : game.getDescription())
                lore.add("§7" + descriptionLine);
            lore.add("");
            if (hasKits) {
                lore.add("§eRight-Click to view kits");
                lore.add("");
            }
            lore.add((ARROW_COLORS[arrowIndex]) + "► §7Click to play with §f" + playing + " §7other player" + (playing == 1 ? "" : "s"));
            set(slots.get(index++), new Button(new ItemBuilder(game.getIcon(), 1)
                    .setName("§6§l" + game.getName())
                    .setLore(lore)
                    .toItemStack(), event -> {
                if (event.isRightClick() && hasKits) {
                    new GameKitsMenu(player, game).open();
                    return;
                }
                close();
                sendToGame(game);
            }));
        }
        if (++randomGameIndex >= MGZGame.values().length)
            randomGameIndex = 0;
        MGZGame game = MGZGame.values()[randomGameIndex];

        List<String> lore = new ArrayList<>();
        lore.add("");
        for (MGZGame mgzGame : MGZGame.values())
            lore.add((mgzGame == game ? "§6► §f" : "§7 ") + mgzGame.getName());
        lore.add("");
        lore.add("§7Click to play a random game");
        set(4, 4, new Button(new ItemBuilder(game.getIcon(), 1)
                .setName("§6Join a random game")
                .setLore(lore)
                .toItemStack(), event -> {
            close();
            sendToGame(game);
        }));

    }

    private void sendToGame(MGZGame game) {
        Optional<MinecraftServer> optionalMinecraftServer = game.getBestServer().filter(MinecraftServer::isRunning);
        if (optionalMinecraftServer.isEmpty()) {
            player.sendMessage(Style.error("Traveler", "§7There is no available game server found, please try again in a moment."));
            return;
        }
        ServerTraveler traveler = Module.getModule(ServerTraveler.class);
        if (traveler != null)
            traveler.sendPlayer(player, optionalMinecraftServer.get());
    }
}