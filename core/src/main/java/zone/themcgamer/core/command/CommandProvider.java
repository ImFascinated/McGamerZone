package zone.themcgamer.core.command;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This class is constructed everytime a command is executed.
 * It holds information such as the sender, label, and arguments
 * @author Braydon
 */
@Getter
public class CommandProvider {
    private final CommandSender sender;
    private final org.bukkit.command.Command bukkitCommand;
    private final String label;
    private final String[] args;

    public CommandProvider(CommandSender sender, org.bukkit.command.Command bukkitCommand, String label, String[] args, int subCommands) {
        this.sender = sender;
        this.bukkitCommand = bukkitCommand;

        StringBuilder builder = new StringBuilder();
        builder.append(label);
        for (int i = 0; i < subCommands; i++)
            builder.append(".").append(args[i]);
        this.label = builder.toString();

        String[] newArgs = new String[args.length - subCommands];
        if (args.length - subCommands >= 0)
            System.arraycopy(args, subCommands, newArgs, 0, args.length - subCommands);
        this.args = newArgs;
    }

    public boolean isPlayer() {
        return getPlayer() != null;
    }

    public Player getPlayer() {
        return sender instanceof Player ? (Player) sender : null;
    }
}