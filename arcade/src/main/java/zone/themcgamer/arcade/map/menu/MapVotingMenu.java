package zone.themcgamer.arcade.map.menu;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import zone.themcgamer.arcade.map.MapVotingManager;
import zone.themcgamer.common.RandomUtils;
import zone.themcgamer.core.common.ItemBuilder;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.common.menu.Button;
import zone.themcgamer.core.common.menu.Menu;
import zone.themcgamer.core.common.menu.MenuType;
import zone.themcgamer.core.world.MGZWorld;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Braydon
 */
public class MapVotingMenu extends Menu {
    private final MapVotingManager mapVotingManager;

    public MapVotingMenu(Player player, MapVotingManager mapVotingManager) {
        super(player, "Map Voting", 3, MenuType.CHEST);
        this.mapVotingManager = mapVotingManager;
    }

    @Override
    protected void onOpen() {
        set(1, 1, new Button(new ItemBuilder(XMaterial.BOOKSHELF)
                .setName("§6§lRandom")
                .setLore(
                        "",
                        "§7Click to vote for a random map"
                ).toItemStack(), event -> vote(null)));

        int slot = 3;
        for (Map.Entry<MGZWorld, Integer> entry : mapVotingManager.getMaps().entrySet()) {
            MGZWorld map = entry.getKey();
            set(1, slot++, new Button(new ItemBuilder(XMaterial.PAPER)
                    .setName("§6§l" + map.getName())
                    .setLore(
                            "",
                            "§7Votes §f" + entry.getValue(),
                            "§7Made By §f" + map.getAuthor()
                    ).toItemStack(), event -> vote(map)));
        }
    }

    private void vote(@Nullable MGZWorld map) {
        close();
        if (mapVotingManager.getVoted().contains(player.getUniqueId())) {
            player.sendMessage(Style.error("Voting", "§cYou already voted!"));
            player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_BASS.parseSound(), 10, 2);
        } else {
            if (map == null)
                map = RandomUtils.random(new ArrayList<>(mapVotingManager.getMaps().keySet()));
            if (map == null)
                player.sendMessage(Style.error("Voting", "§cInvalid map!"));
            else {
                mapVotingManager.getVoted().add(player.getUniqueId());
                mapVotingManager.getMaps().put(map, mapVotingManager.getMaps().getOrDefault(map, 0) + 1);
                player.playSound(player.getLocation(), XSound.UI_BUTTON_CLICK.parseSound(),10, 2);
                player.sendMessage(Style.main("Voting", "You voted for §f" + map.getName()));
            }
        }
    }
}