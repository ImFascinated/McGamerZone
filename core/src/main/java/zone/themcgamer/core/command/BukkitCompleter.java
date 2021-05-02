package zone.themcgamer.core.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is an override for the default class
 *
 * @author Braydon
 */
public class BukkitCompleter implements TabCompleter {
    private final Map<String, Map.Entry<Method, Object>> completers = new HashMap<>();

    @Override @SuppressWarnings("unchecked")
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        for (int i = args.length; i >= 0; i--) {
            StringBuilder builder = new StringBuilder();
            builder.append(label.toLowerCase());
            for (int j = 0; j < i; j++) {
                if (!args[j].equals("") && !args[j].equals(" ")) {
                    builder.append(".").append(args[j].toLowerCase());
                }
            }
            String commandLabel = builder.toString();
            if (completers.containsKey(commandLabel)) {
                Map.Entry<Method, Object> entry = completers.get(commandLabel);
                try {
                    List<String> completions = (List<String>) entry.getKey().invoke(entry.getValue(),
                            new CommandProvider(sender, command, commandLabel, args, commandLabel.split("\\.").length - 1));
                    if (completions == null || (completions.isEmpty()))
                        return null;
                    return completions;
                } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    public void addCompleter(String label, Method method, Object object) {
        completers.put(label, new AbstractMap.SimpleEntry<>(method, object));
    }
}