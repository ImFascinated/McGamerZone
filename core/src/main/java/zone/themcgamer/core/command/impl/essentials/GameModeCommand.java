package zone.themcgamer.core.command.impl.essentials;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import zone.themcgamer.common.EnumUtils;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.data.Rank;

public class GameModeCommand {
    @Command(name = "gamemode", aliases = { "gm", "gmc", "gms", "gma", "gmsp" }, description = "Change your gamemode",
            ranks = { Rank.ADMIN })
    public void onCommand(CommandProvider command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        GameMode gamemode = getGamemode(command.getLabel());
        if (args.length == 1 && gamemode == null)
            gamemode = getGamemode(args[0]);
        else if (args.length >= 2) gamemode = getGamemode(args[1]);

        Player target;
        if (args.length < 1 && command.isPlayer())
            target = command.getPlayer();
        else target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(Style.error("Gamemode", "§c" + (!command.isPlayer() ? "You must provide a player" : "Player is not online")));
            return;
        }
        if (gamemode == null) {
            sender.sendMessage(Style.error("Gamemode", "§cInvalid gamemode given"));
            return;
        }
        String gamemodeName = WordUtils.capitalize(gamemode.name().toLowerCase());
        target.setGameMode(gamemode);
        sender.sendMessage(Style.main("Gamemode", "Updated §f" + target.getName() + "'s §7gamemode to §b" + gamemodeName));
        if (command.isPlayer() && (!command.getPlayer().equals(target)))
            target.sendMessage(Style.main("Gamemode", "Your gamemode was updated to §f" + gamemodeName));
    }

    private GameMode getGamemode(String s) {
        GameMode gamemode = EnumUtils.fromString(GameMode.class, s.toUpperCase());
        if (gamemode != null)
            return gamemode;
        switch (s.toLowerCase()) {
            case "gms":
            case "s":
            case "0": {
                return GameMode.SURVIVAL;
            }
            case "gmc":
            case "c":
            case "1": {
                return GameMode.CREATIVE;
            }
            case "gma":
            case "a":
            case "2": {
                return GameMode.ADVENTURE;
            }
            case "gmsp":
            case "3": {
                return GameMode.SPECTATOR;
            }
        }
        return null;
    }
}
