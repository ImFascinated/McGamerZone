package zone.themcgamer.core.command.impl.staff;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.data.Rank;

import java.util.Optional;

public class StaffModeCommand {

    @Command(name = "staffmode", aliases = {"sm"}, description = "Open the staff gui", ranks = {Rank.HELPER})
    public void onCommand(CommandProvider command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

//        Assuming that the sender is already a staff, due to the provided requirement that this command
//        can be ran by HELPER and above
//        If there are no args, the staff gui should be open to the sender
//        only if the sender is a player
//        if there is one argument, if it's a player, open it for that player (maybe force open)
//        more than one argument should result in an error saying command mis-use

        switch (args.length) {
            case 0:
                if (sender instanceof ConsoleCommandSender) {
                    command.getSender().sendMessage(Style.error("StaffMode",
                            "Provide a player for the command to be used on!"));
                    return;
                } else if (sender instanceof Player) {
                    //TODO open the staff gui for the sender
                    //command.getPlayer().openInventory(null); EXAMPLE
                }
                break;
            case 1:
                Optional.ofNullable(Bukkit.getPlayerExact(args[0])).ifPresentOrElse(player ->
                        AccountManager.fromCache(player.getUniqueId()).ifPresent(account -> {
                            if (account.hasRank(Rank.HELPER)) {
                                //TODO open the staff gui for the given player if found
                                player.sendMessage(Style.main("StaffMode", "Opened Staff gui by Console!"));
                            }
                        }), () -> command.getSender().sendMessage(Style.error("StaffMode",
                        "Provide a valid player for the command to be used on!")));
                break;
            default:
                command.getPlayer().sendMessage(Style.error("StaffMode", "Command mis-used!"));
                break;
        }

    }
}
