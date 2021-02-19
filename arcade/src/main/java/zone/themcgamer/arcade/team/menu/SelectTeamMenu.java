package zone.themcgamer.arcade.team.menu;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.entity.Player;
import zone.themcgamer.arcade.game.Game;
import zone.themcgamer.arcade.player.GamePlayer;
import zone.themcgamer.arcade.team.Team;
import zone.themcgamer.core.common.ItemBuilder;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.common.menu.Button;
import zone.themcgamer.core.common.menu.MenuPattern;
import zone.themcgamer.core.common.menu.MenuType;
import zone.themcgamer.core.common.menu.UpdatableMenu;

import java.util.ArrayList;
import java.util.List;

public class SelectTeamMenu extends UpdatableMenu {
    private final GamePlayer gamePlayer;
    private final Game game;

    public SelectTeamMenu(Player player, Game game) {
        super(player, null, 3, MenuType.CHEST);
        this.game = game;
        gamePlayer = GamePlayer.getPlayer(player.getUniqueId());
        if (gamePlayer == null)
            return;
        setTitle((gamePlayer.getTeam() == null ? "Pick a Team" : "Current ▪ " + gamePlayer.getTeam().getColor() + gamePlayer.getTeam().getName()));
    }

    @Override
    public void onUpdate() {
        if (gamePlayer == null)
            return;
        fillBorders(new Button(new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE)
                .setName("&7").toItemStack()));
        List<Integer> slots = MenuPattern.getSlots(
                "XXXXXXXXX",
                "XOOOOOOOX",
                "XXXXXXXXX"
        );
        int index = 0;
        for (Team team : game.getTeams()) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("§e▪ &7Players: " + team.getColor() + "0");
            lore.add("");
            if (isInTeam(gamePlayer, team))
                lore.add("§aSelected!");
            else lore.add("&7Click to join &f" + team.getColor() + team.getName() + " &7team!");
            set(slots.get(index++), new Button(new ItemBuilder(XMaterial.PLAYER_HEAD)
                    .setGlow(isInTeam(gamePlayer, team))
                    .setSkullOwner(team.getSkullTexture())
                    .setName(team.getColor() + team.getName())
                    .setLore(lore).toItemStack(), event -> {
                if (isInTeam(gamePlayer, team)) {
                    player.sendMessage(Style.main("Teams", "You're already on this team!"));
                    return;
                }
                close();
                //TODO Check if team is unbalanced

                gamePlayer.setTeam(team);
                player.getInventory().setItem(3, new ItemBuilder(XMaterial.PLAYER_HEAD)
                        .setSkullOwner(team.getSkullTexture())
                        .setName("§a§lTeams §8» §7Select team")
                        .addLoreLine("&7Click to select a team").toItemStack());
                player.sendMessage(Style.main("Teams","You've joined the §f" + team.getColor() + team.getName() + " &7team!"));
            }));
        }
    }

    protected boolean isInTeam(GamePlayer gamePlayer, Team team) {
        return (gamePlayer.getTeam() != null && (gamePlayer.getTeam().equals(team)));
    }
}
