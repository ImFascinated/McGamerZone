package zone.themcgamer.buildServer.world.command;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import zone.themcgamer.buildServer.world.WorldManager;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.world.MGZWorld;
import zone.themcgamer.data.Rank;

/**
 * @author Braydon
 */
@AllArgsConstructor
public class AdminCommand {
    private final WorldManager worldManager;

    @Command(name = "admin", description = "Add or remove an admin from a map", ranks = { Rank.BUILDER }, playersOnly = true)
    public void onCommand(CommandProvider command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();
        if (args.length < 1) {
            player.sendMessage(Style.main("Map", "Usage: /admin <player>"));
            return;
        }
        MGZWorld world = worldManager.getWorld(player.getWorld());
        if (world == null) {
            player.sendMessage(Style.error("Map", "§cYou cannot modify the admins of this map."));
            return;
        }
        if (!world.getOriginalCreator().equals(player.getName()) && !player.isOp()) {
            player.sendMessage(Style.error("Map", "§cYou cannot modify the admins of this map."));
            return;
        }
        if (args[0].equalsIgnoreCase(world.getOriginalCreator())) {
            player.sendMessage(Style.error("Map", "§cThe original author can't have their admin privileges modified."));
            return;
        }
        if (worldManager.beingParsed(world)) {
            player.sendMessage(Style.error("Map", "§cThis map is currently being parsed, changing the admins list has been disabled"));
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (world.getAdmins().remove(args[0].toLowerCase())) {
            player.sendMessage(Style.main("Map", "§b" + args[0] + " §7is no-longer an admin on §6" + world.getName()));
            if (target != null && (!player.equals(target)))
                target.sendMessage(Style.main("Map", "§cYour admin privileges on §b" + world.getName() + " §cwere removed"));
        } else {
            world.getAdmins().add(args[0].toLowerCase());
            player.sendMessage(Style.main("Map", "§b" + args[0] + " §7is now an admin on §6" + world.getName()));
            if (target != null && (!player.equals(target)))
                target.sendMessage(Style.main("Map", "§aYou were given admin privileges on §b" + world.getName()));
        }
        world.save();
    }
}