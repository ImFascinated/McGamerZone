package zone.themcgamer.core.command.impl.staff;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.plugin.MGZPlugin;
import zone.themcgamer.data.Rank;
import zone.themcgamer.data.jedis.command.JedisCommandHandler;

import java.util.Optional;

public class StaffChatCommand {
    @Command(name = "staffchat", aliases = { "sc" }, description = "Send a message to all online staff", ranks = { Rank.HELPER })
    public void onCommand(CommandProvider command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();
        if (args.length < 1) {
            command.getPlayer().sendMessage(Style.error("StaffChat", "Please define a message!"));
            return;
        }
        String prefix = "§4§lTerminal";
        if (sender instanceof Player) {
            Optional<Account> optionalAccount = AccountManager.fromCache(command.getPlayer().getUniqueId());
            if (optionalAccount.isEmpty())
                return;
            prefix = optionalAccount.get().getPrimaryRank().getPrefix();
            if (prefix == null)
                prefix = "";
        }
        JedisCommandHandler.getInstance().send(new zone.themcgamer.data.jedis.command.impl.StaffChatCommand(
                prefix, sender.getName(), MGZPlugin.getMinecraftServer().getName(), String.join(" ", args)));
    }
}