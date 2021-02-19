package zone.themcgamer.core.chat.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.data.Rank;

public class ClearChatCommand {
    @Command(name = "clearchat", aliases = { "cc" }, description = "Clear the chat", ranks = { Rank.HELPER }, playersOnly = true)
    public void onCommand(CommandProvider command) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            for (int i = 0; i < 150; i++)
                onlinePlayer.sendMessage(" ");
            onlinePlayer.sendMessage(Style.main("Chat", "The chat has been cleared by &6" + command.getPlayer().getName()));
        }
    }
}
