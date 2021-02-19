package zone.themcgamer.core.command.impl.essentials;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.data.Rank;

public class TeleportCommand {
    @Command(name = "teleport", aliases = {"tp"}, ranks = Rank.HELPER, description = "Teleport to a player")
    public void onCommand(CommandProvider command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();
        if (args.length == 1) {
            if (command.isPlayer()) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    command.getSender().sendMessage(Style.error("Essentials", "Player is not online!"));
                    return;
                }
                player.teleport(target);
                command.getSender().sendMessage(Style.main("Essentials", "&7You have been teleported to &6" + target.getName() + "&7."));
            }
        } else if (args.length == 2) {
            Player target1 = Bukkit.getPlayer(args[0]);
            Player target2 = Bukkit.getPlayer(args[1]);
            if (target1 == null) {
                command.getSender().sendMessage(Style.error("Essentials", args[0] + " is not online!"));
                return;
            }
            if (target2 == null) {
                command.getSender().sendMessage(Style.error("Essentials", args[1] + " is not online!"));
                return;
            }
            target1.teleport(target2);
            command.getSender().sendMessage(Style.main("Essentials","&7You have teleported &6" + target1.getName() + " &7to &6" + target2.getName() + "&7."));
        } else if (args.length == 3) {
            double x = args[0].startsWith("~") ? player.getLocation().getX() + (args[0].length() > 1 ? Double.parseDouble(args[0].substring(1)) : 0) : Double.parseDouble(args[0]);
            double y = args[1].startsWith("~") ? player.getLocation().getY() + (args[1].length() > 1 ? Double.parseDouble(args[1].substring(1)) : 0) : Double.parseDouble(args[1]);
            double z = args[2].startsWith("~") ? player.getLocation().getZ() + (args[2].length() > 1 ? Double.parseDouble(args[2].substring(1)) : 0) : Double.parseDouble(args[2]);

            Location location = new Location(player.getWorld(), x, y, z, player.getLocation().getYaw(), player.getLocation().getPitch());
            player.teleport(location);
            player.sendMessage(Style.main("Essentials","Teleporting to location: &6" + location.getWorld().getName() + location.getBlockX() + location.getBlockY() + location.getBlockZ()));
        } else {
            command.getSender().sendMessage("");
            command.getSender().sendMessage(Style.color("&6&lTeleport &eHelp"));
            command.getSender().sendMessage(Style.color("&7 - &b/teleport <player>"));
            command.getSender().sendMessage(Style.color("&7 - &b/teleport <x> <y> <z>"));
            command.getSender().sendMessage(Style.color("&7 - &b/teleport <player> <player>"));
            command.getSender().sendMessage("");
        }
    }
}
