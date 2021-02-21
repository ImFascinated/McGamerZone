package zone.themcgamer.core.chat.command;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import zone.themcgamer.core.chat.ChatManager;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;

import java.util.Map;

/**
 * @author Braydon
 */
@AllArgsConstructor
public class EmotesCommand {
    private final ChatManager chatManager;

    @Command(name = "emotes", aliases = { "emote" }, description = "View chat emotes", playersOnly = true)
    public void onCommand(CommandProvider command) {
        Player player = command.getPlayer();
        player.sendMessage(Style.main("Chat", "Chat Emotes:"));
        for (Map.Entry<String, String> entry : chatManager.getEmotes().entrySet())
            player.sendMessage("  §6" + entry.getKey() + " §7-> §b" + entry.getValue());
    }
}