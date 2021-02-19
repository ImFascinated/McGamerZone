package zone.themcgamer.core.command;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.help.GenericCommandHelpTopic;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.HelpTopicComparator;
import org.bukkit.help.IndexHelpTopic;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import zone.themcgamer.common.TriTuple;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.command.help.HelpCommand;
import zone.themcgamer.core.command.impl.DiscordCommand;
import zone.themcgamer.core.command.impl.RulesCommand;
import zone.themcgamer.core.command.impl.StoreCommand;
import zone.themcgamer.core.command.impl.StressTestCommand;
import zone.themcgamer.core.command.impl.essentials.GameModeCommand;
import zone.themcgamer.core.command.impl.essentials.TeleportCommand;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.module.Module;
import zone.themcgamer.core.module.ModuleInfo;
import zone.themcgamer.data.Rank;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Braydon
 */
@ModuleInfo(name = "Command Manager")
public class CommandManager extends Module implements CommandExecutor {
    /**
     * An array of default commands to disable on the server. "Default" commands meaning
     * Minecraft, Bukkit, and Spigot commands that come with the server jar
     */
    private static final String[] DISABLED_COMMANDS = new String[] {
            "op",
            "deop",
            "kick",
            "ban",
            "banlist",
            "pardon",
            "pardon-ip",
            "reload",
            "rl",
            "stop",
            "restart",
            "me",
            "say",
            "about",
            "ver",
            "version",
            "icanhasbukkit",
            "trigger",
            "ban-ip",
            "execute",
            "function",
            "spigot",
            "plugins",
            "pl",
            "stats"
    };

    private CommandMap commandMap;
    private Map<String, org.bukkit.command.Command> knownCommands;
    @Getter private final Map<String, TriTuple<Method, Object, Long>> commands = new HashMap<>();
    private final Map<Plugin, List<Object>> registrationQueue = new HashMap<>();

    public CommandManager(JavaPlugin plugin) {
        super(plugin);
        SimplePluginManager simplePluginManager = (SimplePluginManager) Bukkit.getPluginManager();
        try {
            Field field = SimplePluginManager.class.getDeclaredField("commandMap");
            field.setAccessible(true);
            commandMap = (CommandMap) field.get(simplePluginManager);

            Field knownCommandsField = commandMap.getClass().getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            knownCommands = (Map<String, org.bukkit.command.Command>) knownCommandsField.get(commandMap);
        } catch (IllegalArgumentException | SecurityException | IllegalAccessException | NoSuchFieldException ex) {
            ex.printStackTrace();
        }

        // Registering default commands
        registerCommand(plugin, new StressTestCommand(plugin));
        registerCommand(plugin, new RulesCommand());
        registerCommand(plugin, new TeleportCommand());
        registerCommand(plugin, new GameModeCommand());
        registerCommand(plugin, new zone.themcgamer.core.command.impl.HelpCommand(this));
        registerCommand(plugin, new DiscordCommand());
        registerCommand(plugin, new StoreCommand());

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            for (String disabledCommand : DISABLED_COMMANDS) {
                String[] prefixes = new String[] { "minecraft", "bukkit", "spigot" };
                List<String> commands = new ArrayList<>(Collections.singletonList(disabledCommand.toLowerCase()));
                for (String prefix : prefixes)
                    commands.add(prefix + ":" + disabledCommand.toLowerCase());
                for (String command : commands)
                    unregisterCommand(command);
            }
            for (Map.Entry<Plugin, List<Object>> entry : registrationQueue.entrySet()) {
                for (Object object : entry.getValue()) {
                    for (Method method : object.getClass().getMethods()) {
                        if (method.getAnnotation(Command.class) != null) {
                            Command command = method.getAnnotation(Command.class);
                            if (method.getParameterTypes().length != 1 || !method.getParameterTypes()[0].equals(CommandProvider.class)) {
                                plugin.getLogger().warning("Unable to register command " + method.getName() + ". Unexpected method arguments");
                                continue;
                            }
                            List<String> knownAliases = new ArrayList<>(Collections.singletonList(command.name().toLowerCase()));
                            for (String alias : command.aliases())
                                knownAliases.add(alias.toLowerCase());
                            for (String knownAlias : knownAliases) {
                                if (knownCommands.containsKey(knownAlias))
                                    unregisterCommand(knownAlias);
                                registerCommand(plugin, command, method, object, knownAlias);
                            }
                        } else if (method.getAnnotation(TabComplete.class) != null) {
                            TabComplete tabComplete = method.getAnnotation(TabComplete.class);
                            if (method.getParameterTypes().length != 1 || !method.getParameterTypes()[0].equals(CommandProvider.class)) {
                                plugin.getLogger().warning("Unable to register tab completer " + method.getName() + ". Unexpected method arguments");
                                continue;
                            }
                            if (!method.getReturnType().equals(List.class)) {
                                plugin.getLogger().warning("Unable to register tab completer " + method.getName() + ". Unexpected return type");
                                continue;
                            }
                            registerTabComplete(plugin, tabComplete.name(), method, object);
                            for (String alias : tabComplete.aliases())
                                registerTabComplete(plugin, alias, method, object);
                        }
                    }
                }
            }
            registerHelp(plugin);
        }, 13L);
    }

    /**
     * Register a command
     * @param plugin - The owner of the command
     * @param object - The instance of the command class
     */
    public void registerCommand(Plugin plugin, Object object) {
        if (plugin == null || object == null)
            throw new IllegalArgumentException("Plugin or command object provided is null");
        List<Object> queue = registrationQueue.getOrDefault(plugin, new ArrayList<>());
        queue.add(object);
        registrationQueue.put(plugin, queue);
    }

    /**
     * Add all of the currently registered commands to the help menu
     * @param plugin - The plugin owner of the commands to add to
     *                 the help menu
     */
    private void registerHelp(Plugin plugin) {
        try {
            Set<HelpTopic> help = new TreeSet<>(HelpTopicComparator.helpTopicComparatorInstance());
            for (String alias : commands.keySet()) {
                if (!alias.contains(".")) {
                    org.bukkit.command.Command bukkitCommand = commandMap.getCommand(alias);
                    if (bukkitCommand == null)
                        bukkitCommand = new BukkitCommand(alias, plugin, this);
                    HelpTopic topic = new GenericCommandHelpTopic(bukkitCommand);
                    help.add(topic);
                }
            }
            IndexHelpTopic topic = new IndexHelpTopic(plugin.getName(), "All commands for " + plugin.getName(), null, help,
                    "Below is a list of all " + plugin.getName() + " commands:");
            Bukkit.getServer().getHelpMap().addTopic(topic);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void unregisterCommand(String alias) {
        org.bukkit.command.Command bukkitCommand = knownCommands.get(alias);
        if (bukkitCommand == null || (bukkitCommand instanceof BukkitCommand))
            return;
        log("Unregistered \"/" + alias + "\"");
        knownCommands.remove(alias);
        bukkitCommand.unregister(commandMap);
    }

    /**
     * Unregister all commands
     */
    public void cleanup() {
        for (String alias : commands.keySet()) {
            org.bukkit.command.Command bukkitCommand = knownCommands.remove(alias.toLowerCase());
            if (bukkitCommand == null)
                continue;
            bukkitCommand.unregister(commandMap);
        }
    }

    private void registerCommand(Plugin plugin, Command commandAnnotation, Method method, Object object, String rawLabel) {
        long time = System.nanoTime();
        commands.put(rawLabel.toLowerCase(), new TriTuple<>(method, object, time));
        commands.put(plugin.getName() + ':' + rawLabel.toLowerCase(), new TriTuple<>(method, object, time));
        String label = rawLabel.replace(".", ",").split(",")[0].toLowerCase();
        if (commandMap.getCommand(label) == null) {
            org.bukkit.command.Command cmd = new BukkitCommand(label, plugin, this);
            commandMap.register(plugin.getName(), cmd);
        }
        if (!commandAnnotation.usage().isEmpty() && label.equals(rawLabel))
            commandMap.getCommand(label).setUsage(commandAnnotation.usage());
        if (!commandAnnotation.description().isEmpty() && label.equals(rawLabel))
            commandMap.getCommand(label).setDescription(commandAnnotation.description());
    }

    private void registerTabComplete(Plugin plugin, String rawLabel, Method method, Object object) {
        String label = rawLabel.replace(".", ",").split(",")[0].toLowerCase();
        if (commandMap.getCommand(label) == null) {
            org.bukkit.command.Command command = new BukkitCommand(label, plugin, this);
            commandMap.register(plugin.getName(), command);
        }
        if (commandMap.getCommand(label) instanceof BukkitCommand) {
            BukkitCommand command = (BukkitCommand) commandMap.getCommand(label);
            if (command.completer == null)
                command.completer = new BukkitCompleter();
            command.completer.addCompleter(rawLabel, method, object);
        } else if (commandMap.getCommand(label) instanceof PluginCommand) {
            try {
                Object command = commandMap.getCommand(label);
                Field field = command.getClass().getDeclaredField("completer");
                field.setAccessible(true);
                if (field.get(command) == null) {
                    BukkitCompleter completer = new BukkitCompleter();
                    completer.addCompleter(rawLabel, method, object);
                    field.set(command, completer);
                } else if (field.get(command) instanceof BukkitCompleter) {
                    BukkitCompleter completer = (BukkitCompleter) field.get(command);
                    completer.addCompleter(rawLabel, method, object);
                } else {
                    plugin.getLogger().warning("Unable to register tab completer " + method.getName() +
                            ". A tab completer is already registered for that command!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command bukkitCommand, String rawLabel, String[] args) {
        if (rawLabel.contains(":")) {
            String[] split = rawLabel.split(":");
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                if (split[0].equalsIgnoreCase(plugin.getName())) {
                    rawLabel = split[1];
                    break;
                }
            }
        }
        for (int i = args.length; i >= 0; i--) {
            StringBuilder builder = new StringBuilder();
            builder.append(rawLabel.toLowerCase());
            for (int j = 0; j < i; j++)
                builder.append(".").append(args[j].toLowerCase());
            String label = builder.toString();
            if (commands.containsKey(label)) {
                Method method = commands.get(label).getLeft();
                Object object = commands.get(label).getMiddle();
                Command command = method.getAnnotation(Command.class);
                if (command.terminalOnly() && sender instanceof Player)
                    sender.sendMessage(Style.color("&cThis command can only be executed via the terminal."));
                else if (command.playersOnly() && sender instanceof ConsoleCommandSender)
                    sender.sendMessage(Style.color("&cThis command can only be executed from in-game."));
                else {
                    if (sender instanceof Player) {
                        Optional<Account> optionalAccount = AccountManager.fromCache(((Player) sender).getUniqueId());
                        if (!optionalAccount.isPresent()) {
                            sender.sendMessage(Style.error("Account","&cCannot fetch account"));
                            return true;
                        }
                        if (Arrays.stream(command.ranks()).anyMatch(rank -> !optionalAccount.get().hasRank(rank))) {
                            Optional<Rank> rank = Arrays.stream(command.ranks()).findFirst();
                            rank.ifPresent(value -> sender.sendMessage(Style.rankRequired(rank.get())));
                            return true;
                        }
                    }
                    if (object instanceof HelpCommand) {
                        Map<Command, Long> childCommandsMap = new HashMap<>();
                        for (Map.Entry<String, TriTuple<Method, Object, Long>> entry : commands.entrySet()) {
                            String s = entry.getKey();
                            if (s.contains(".") && (s.split("\\.")[0].equalsIgnoreCase(command.name()))) {
                                TriTuple<Method, Object, Long> triTuple = entry.getValue();
                                childCommandsMap.put(triTuple.getLeft().getAnnotation(Command.class), triTuple.getRight());
                            }
                        }
                        List<Command> childCommands = new ArrayList<>(childCommandsMap.keySet());
                        childCommands.sort(Comparator.comparingLong(childCommandsMap::get));
                        HelpCommand helpCommand = (HelpCommand) object;

                        int page = 1;
                        if (args.length >= 1) {
                            try {
                                page = Integer.parseInt(args[0]);
                            } catch (NumberFormatException ignored) {
                                page = -1;
                            }
                        }
                        if (helpCommand.sendHelp(sender, label, command, childCommands, page) == HelpCommand.HelpResponse.INVALID_PAGE)
                            sender.sendMessage(Style.color("&cInvalid page."));
                        return true;
                    }
                    try {
                        method.invoke(object, new CommandProvider(sender, bukkitCommand, rawLabel, args,
                                label.split("\\.").length - 1));
                    } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
                        sender.sendMessage(Style.color("&cAn error has occurred whilst executing the command, this exception has been logged! §f" + ex.getLocalizedMessage()));
                        ex.printStackTrace();
                    }
                }
                return true;
            }
        }
        sender.sendMessage(Style.color("&cFailed to handle command"));
        return true;
    }
}