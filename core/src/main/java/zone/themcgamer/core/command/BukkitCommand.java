package zone.themcgamer.core.command;

import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * This class is an override for the default {@link org.bukkit.command.defaults.BukkitCommand}
 * class
 * @author Braydon
 */
public class BukkitCommand extends Command {
    private final Plugin plugin;
    private final CommandExecutor executor;
    protected BukkitCompleter completer;

    public BukkitCommand(String label, Plugin plugin, CommandExecutor executor) {
        super(label);
        this.plugin = plugin;
        this.executor = executor;
        usageMessage = "";
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!plugin.isEnabled())
            return false;
        if (!testPermission(sender))
            return true;
        boolean success;
        try {
            success = executor.onCommand(sender, this, label, args);
        } catch (Exception ex) {
            throw new CommandException("Unhandled exception executing command '" + label + "' in plugin " + plugin.getDescription().getFullName(), ex);
        }
        if (!success && usageMessage.length() > 0) {
            for (String line : usageMessage.replace("<command>", label).split("\n")) {
                sender.sendMessage(line);
            }
        }
        return success;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws CommandException, IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");
        List<String> completions = null;
        try {
            if (completer != null)
                completions = completer.onTabComplete(sender, this, alias, args);
            if (completions == null && executor instanceof TabCompleter)
                completions = ((TabCompleter) executor).onTabComplete(sender, this, alias, args);
        } catch (Exception ex) {
            StringBuilder message = new StringBuilder();
            message.append("Unhandled exception during tab completion for command '/").append(alias).append(' ');
            for (String arg : args)
                message.append(arg).append(" ");
            message.deleteCharAt(message.length() - 1).append("' in plugin ").append(plugin.getDescription().getFullName());
            throw new CommandException(message.toString(), ex);
        }
        if (completions == null)
            return super.tabComplete(sender, alias, args);
        return completions;
    }
}