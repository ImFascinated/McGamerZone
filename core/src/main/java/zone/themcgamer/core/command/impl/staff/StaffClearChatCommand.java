package zone.themcgamer.core.command.impl.staff;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.data.Rank;

import java.util.function.Predicate;

public class StaffClearChatCommand {

    @Command(name = "clearchat", aliases = {"cc"}, description = "Clears the chat.", ranks = {Rank.HELPER})
    public void onCommand(CommandProvider commandProvider) {
        // Assuming that the Sender has sufficient permissions due to the provided condition that the rank
        // must be HELPER

        // If there are no arguments, 100 new lines will sent instantly to make the current chat disappear

        // If there is one argument, if the argument is an integer, n new lines will be sent instantly to make
        // the current chat disappear.

        // If there is more than one argument, an error message should be sent to the sender saying command
        // mis-used

        switch (commandProvider.getArgs().length) {
            case 0:
                if (commandProvider.isPlayer()) {
                    send(100, player -> player.getUniqueId() != commandProvider.getPlayer().getUniqueId());
                } else {
                    send(100, $ -> true);
                }
                break;
            case 1:
                try {
                    int lines = Integer.parseInt(commandProvider.getArgs()[0]);

                    if (commandProvider.isPlayer()) {
                        send(lines, player -> player.getUniqueId() != commandProvider.getPlayer().getUniqueId());
                    } else {
                        send(lines, $ -> true);
                    }
                }catch (Exception ignored) {
                    commandProvider.getSender().sendMessage(Style.error("ClearChat",
                            "Arguemnt is not an integer!"));
                }
                break;
            default:
                commandProvider.getSender().sendMessage(Style.error("ClearChat",
                        "Command mis-used!"));
                break;
        }
    }

    private void send(int lines, Predicate<Player> condition) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (condition.test(player))
                for (int index = 0; index < lines; index++)
                    player.sendMessage("");
        });
    }
}
