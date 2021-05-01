package zone.themcgamer.core.announce.command;

import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import zone.themcgamer.common.EnumUtils;
import zone.themcgamer.core.announce.AnnounceManager;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.data.Rank;
import zone.themcgamer.data.jedis.command.impl.announce.AnnounceType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Nicholas
 */
@AllArgsConstructor
public class AnnounceCommand {
    private final AnnounceManager announceManager;

    @Command(name = "announce", usage = "<type> <message ...>", description = "Announce a message", ranks = { Rank.ADMIN })
    public void onCommand(CommandProvider command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();
        if (args.length < 2) {
            sender.sendMessage(Style.main("Announce", "Usage: /" + command.getLabel() + " <type> <message ...>"));
            return;
        }
        if (EnumUtils.fromString(AnnounceType.class, args[0].toUpperCase()) == null) {
            sender.sendMessage(Style.error("Announce", "Invalid announcement type!"));
            List<AnnounceType> types = Arrays.asList(AnnounceType.values());
            sender.sendMessage(Style.main("", "Available Types: §f" +
                    types.stream().map(AnnounceType::name).collect(Collectors.joining("§7, §f"))));
            return;
        }
        AnnounceType type = AnnounceType.valueOf(args[0].toUpperCase());
        String message = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
        if (message.isEmpty() || ChatColor.stripColor(Style.color(message)).isEmpty()) {
            sender.sendMessage(Style.error("Announce", "Invalid message!"));
            return;
        }
        announceManager.sendAnnouncement(type, message);
    }
}