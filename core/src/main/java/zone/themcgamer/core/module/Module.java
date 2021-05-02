package zone.themcgamer.core.module;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import zone.themcgamer.core.command.CommandManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Braydon
 * @implNote The purpose of the class is to easily organize managers.
 *           Each module has a {@link #onEnable()} and {@link #onDisable()}
 *           method like a {@link JavaPlugin}.
 */
@Getter
public abstract class Module implements Listener {
    @Getter private static final Map<Class<?>, Module> modules = new HashMap<>();

    private final ModuleInfo info;
    private final JavaPlugin plugin;

    public Module(JavaPlugin plugin) {
        if (!getClass().isAnnotationPresent(ModuleInfo.class))
            throw new RuntimeException("Cannot initialize module \"" + getClass().getName() + "\" as the @ModuleInfo annotation is missing!");
        info = getClass().getAnnotation(ModuleInfo.class);
        this.plugin = plugin;
        log("Loading...");
        long started = System.currentTimeMillis();
        onEnable();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        log("Loaded in " + (System.currentTimeMillis() - started) + "ms");
        modules.put(getClass(), this);
    }

    public void onEnable() {} // Called when the module is enabled
    public void onDisable() {} // Called when the module is disabled

    public String getName() {
        return info.name();
    }

    public void registerCommand(Object command) {
        CommandManager commandManager = getModule(CommandManager.class);
        if (commandManager == null)
            throw new NullPointerException("commandManager is null");
        commandManager.registerCommand(plugin, command);
    }

    /**
     * Log a message to the terminal for this module
     *
     * @param message the message to log
     */
    public void log(String message) {
        Bukkit.getLogger().info("§b" + info.name() + " §8» §7" + message);
    }

    /**
     * Get the module by the provided class
     *
     * @param clazz the class of the module to get
     * @return the module
     */
    public static <T extends Module> T getModule(Class<T> clazz) {
        Module module = modules.get(clazz);
        if (module == null)
            return null;
        return clazz.cast(module);
    }
}