package zone.themcgamer.core.plugin;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.announce.AnnounceManager;
import zone.themcgamer.core.badSportSystem.BadSportSystem;
import zone.themcgamer.core.command.CommandManager;
import zone.themcgamer.core.common.ServerUtils;
import zone.themcgamer.core.common.menu.MenuManager;
import zone.themcgamer.core.common.scheduler.Scheduler;
import zone.themcgamer.core.cooldown.CooldownHandler;
import zone.themcgamer.core.module.Module;
import zone.themcgamer.core.nametag.NametagManager;
import zone.themcgamer.core.plugin.command.BuildDataCommand;
import zone.themcgamer.core.plugin.command.PluginsCommand;
import zone.themcgamer.core.server.ServerManager;
import zone.themcgamer.core.task.TaskManager;
import zone.themcgamer.core.traveler.ServerTraveler;
import zone.themcgamer.core.twoFactor.TwoFactorAuthentication;
import zone.themcgamer.core.update.ServerUpdater;
import zone.themcgamer.data.jedis.JedisController;
import zone.themcgamer.data.jedis.data.server.MinecraftServer;
import zone.themcgamer.data.jedis.data.server.ServerState;
import zone.themcgamer.data.jedis.repository.RedisRepository;
import zone.themcgamer.data.jedis.repository.impl.MinecraftServerRepository;
import zone.themcgamer.data.jedis.repository.impl.ServerGroupRepository;
import zone.themcgamer.data.mysql.MySQLController;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Braydon
 */
public abstract class MGZPlugin extends JavaPlugin {
    @Getter private static MinecraftServer minecraftServer;

    protected JedisController jedisController;
    protected MySQLController mySQLController;
    protected CommandManager commandManager;
    protected ServerTraveler traveler;
    protected AccountManager accountManager;
    protected BadSportSystem badSportSystem;
    protected NametagManager nametagManager;

    @SneakyThrows
    @Override
    public void onEnable() {
        // Connect to Redis and setup the controller
        getLogger().info("Connecting to Redis...");
        jedisController = new JedisController().start();

        getLogger().info("Setting up Minecraft server...");
        Optional<MinecraftServerRepository> optionalMinecraftServerRepository = RedisRepository.getRepository(MinecraftServerRepository.class);
        if (optionalMinecraftServerRepository.isEmpty()) {
            getLogger().severe("Cannot find Minecraft server repository, stopping...");
            getServer().shutdown();
            return;
        }
        MinecraftServerRepository minecraftServerRepository = optionalMinecraftServerRepository.get();
        minecraftServerRepository.addUpdateListener(servers -> {
            if (minecraftServer != null || servers.isEmpty())
                return;
            Optional<MinecraftServer> optionalMinecraftServer = servers.stream()
                    .filter(server -> server.getNode() != null)
                    .filter(server -> {
                        try {
                            return server.getNode().getName().equals(InetAddress.getLocalHost().getHostName()) && server.getPort() == Bukkit.getPort();
                        } catch (UnknownHostException ex) {
                            ex.printStackTrace();
                        }
                        return false;
                    }).findFirst();
            // If there is no MinecraftServer found with this server ip and port or the server found is not
            // in a starting state, we wanna attempt to load the MinecraftServer information from a local file
            if (optionalMinecraftServer.isEmpty() || (optionalMinecraftServer.get().getState() != ServerState.STARTING)) {
                File detailsFile = new File(getDataFolder(), "details.yml");
                if (detailsFile.exists()) { // If the details file exists, try and load the MinecraftServer from it
                    try {
                        FileConfiguration configuration = YamlConfiguration.loadConfiguration(detailsFile);

                        String name = configuration.getString("name");
                        String groupName = configuration.getString("group");

                        ServerGroupRepository serverGroupRepository = RedisRepository.getRepository(ServerGroupRepository.class).orElse(null);
                        if (serverGroupRepository == null)
                            throw new NullPointerException();
                        long now = System.currentTimeMillis();
                        optionalMinecraftServer = Optional.of(new MinecraftServer(
                                name,
                                Integer.parseInt(name.split("-")[1]),
                                name,
                                null,
                                serverGroupRepository.lookup(groupName).orElse(null),
                                "168.119.4.237",
                                getServer().getPort(),
                                0,
                                0,
                                ServerState.STARTING,
                                now,
                                0,
                                0,
                                20.0D,
                                null,
                                "",
                                "",
                                now,
                                now
                        ));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        getServer().shutdown();
                        return;
                    }
                } else { // If the details file doesn't exist and the server wasn't found in Redis, display an error and stop the server
                    getLogger().severe("Cannot find Minecraft server, stopping...:");
                    if (!servers.isEmpty()) {
                        getLogger().info("Minecraft Servers:");
                        for (MinecraftServer server : servers)
                            getLogger().info("  " + server.toString());
                    }
                    getServer().shutdown();
                    return;
                }
            }
            minecraftServer = optionalMinecraftServer.get();
            minecraftServer.setState(ServerState.RUNNING); // Set the MinecraftServer to the running state

            // Updating the MinecraftServer in Redis
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
                Runtime runtime = Runtime.getRuntime();

                minecraftServer.setUsedRam((int) formatMemory(runtime.totalMemory() - runtime.freeMemory()));
                minecraftServer.setMaxRam((int) formatMemory(runtime.maxMemory()));
                minecraftServer.setOnline(Bukkit.getOnlinePlayers().size());
                minecraftServer.setMaxPlayers(Bukkit.getMaxPlayers());
                minecraftServer.setTps(ServerUtils.getTps());
                minecraftServer.setLastHeartbeat(System.currentTimeMillis());

                minecraftServerRepository.post(minecraftServer);
            }, 0L, 2L * 20L);

            // Starting up MySQL
            getLogger().info("Connecting to MySQL...");
            mySQLController = new MySQLController(false);

            // Loading utilities
            getLogger().info("Loading utilities...");
            new Scheduler(this);
            new MenuManager(this);

            // Loading essential modules that will always be enabled
            getLogger().info("Loading essential modules...");

            commandManager = new CommandManager(this);
            commandManager.registerCommand(this, new BuildDataCommand());
            commandManager.registerCommand(this, new PluginsCommand());

            new CooldownHandler(this);
            AccountManager.addMiniAccount(new TwoFactorAuthentication(this, mySQLController));
            nametagManager = new NametagManager(this);

            accountManager = new AccountManager(this, mySQLController, nametagManager);
            traveler = new ServerTraveler(this);
            new ServerUpdater(this, traveler);
            new ServerManager(this, traveler);

            badSportSystem = new BadSportSystem(this, mySQLController, accountManager);
            AccountManager.addMiniAccount(new TaskManager(this));

            new AnnounceManager(this);

            // Running the @Startup methods for the plugin
            getLogger().info("Running @Startup methods...");
            List<Method> methods = Arrays.stream(getClass().getMethods())
                    .filter(method -> method.isAnnotationPresent(Startup.class))
                    .sorted(Comparator.comparingInt(a -> a.getAnnotation(Startup.class).priority()))
                    .collect(Collectors.toList());
            for (Method method : methods) {
                try {
                    method.invoke(this);
                } catch (IllegalAccessException | InvocationTargetException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onDisable() {
        minecraftServer.setState(ServerState.STOPPING); // Set the MinecraftServer to the stopping state
        commandManager.cleanup(); // Cleanup the command manager
        for (Module module : Module.getModules().values()) // Disable all modules
            module.onDisable();
        nametagManager.cleanup();
    }

    /**
     * Format the given memory into megabytes
     *
     * @param memory the memory to format
     * @return the formatted memory
     */
    private double formatMemory(double memory) {
        if (memory < 1024)
            return memory;
        return Math.round(memory / 1024 / 1024);
    }
}