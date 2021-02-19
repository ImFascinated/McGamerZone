package zone.themcgamer.core.command.impl;

import lombok.AllArgsConstructor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.entity.Player;
import zone.themcgamer.common.MiscUtils;
import zone.themcgamer.common.TriTuple;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandManager;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.PageBuilder;
import zone.themcgamer.core.common.Style;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class HelpCommand {
    private final CommandManager commandManager;

    @Command(name = "help", aliases = { "?", "h" }, description = "View the help menu", playersOnly = true)
    public void onCommand(CommandProvider command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();
        int page = 1;
        if (args.length >= 1) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
                page = -1;
            }
        }
        Optional<Account> optionalAccount = AccountManager.fromCache(player.getUniqueId());
        if (optionalAccount.isEmpty())
            return;
        Account account = optionalAccount.get();

        Map<String, Command> commandsMap = new HashMap<>();
        for (TriTuple<Method, Object, Long> triTuple : commandManager.getCommands().values()) {
            Method method = triTuple.getLeft();
            if (!method.isAnnotationPresent(Command.class))
                continue;
            Command annotation = method.getAnnotation(Command.class);
            if (!account.hasRank(annotation.ranks()[0]))
                continue;
            String commandName = annotation.name();
            if (commandName.contains("."))
                continue;
            commandsMap.put(commandName, annotation);
        }
        PageBuilder<?> pageBuilder = new PageBuilder<>(new ArrayList<>(commandsMap.entrySet()), (place, entry) -> {
            player.sendMessage(new ComponentBuilder(Style.color("&e ▸ &b/" + entry.getKey() + " &8- &7" + entry.getValue().description()))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Style.color(MiscUtils.arrayToString(
                            "&7Aliases: &b" + (entry.getValue().aliases().length == 0 ? "None" : String.join("&6, &b", entry.getValue().aliases())),
                            "&aClick to execute this command"
                    ))).create()))
                    .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + entry.getKey() + " " + entry.getValue().usage()))
                    .create());
        });
        int maxPage = pageBuilder.getMaxPages();
        if (page <= 0 || page > maxPage) {
            player.sendMessage("§cPage out of bounds. There " + (maxPage == 1 ? "is" : "are") + " only §l" + maxPage + " §cpage" + (maxPage == 1 ? "" : "s") + ".");
            return;
        }
        player.sendMessage("");
        player.sendMessage("§2§lMc§6§lGamer§c§lZone §7(Page " + page + " / " + maxPage + ")");
        player.sendMessage("§6<>§7 = required, §6[]§7 = optional");
        player.sendMessage("");
        pageBuilder.send(player, page);
        player.sendMessage("");
    }
}