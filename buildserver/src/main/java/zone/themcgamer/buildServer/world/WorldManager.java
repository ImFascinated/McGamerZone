package zone.themcgamer.buildServer.world;

import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;
import zone.themcgamer.buildServer.Build;
import zone.themcgamer.buildServer.backup.BackupTask;
import zone.themcgamer.buildServer.parse.ParseTask;
import zone.themcgamer.buildServer.parse.command.ParseCommand;
import zone.themcgamer.buildServer.world.command.*;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.module.Module;
import zone.themcgamer.core.module.ModuleInfo;
import zone.themcgamer.core.world.MGZWorld;
import zone.themcgamer.core.world.WorldCategory;
import zone.themcgamer.core.world.WorldGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Braydon
 */
@ModuleInfo(name = "World Manager") @Getter
public class WorldManager extends Module {
    private static final String[] permissions = new String[] {
            "gopaint.use",
            "gobrush.use",
            "minecraft.command.difficulty",
            "fawe.permpack.basic",
            "fawe.voxelbrush",
            "fawe.confirm",
            "worldedit.*",
            "voxelsniper.*",
            "builders.util.secretblocks",
            "builders.util.banner",
            "builders.util.color",
            "builders.util.noclip",
            "builders.util.nightvision",
            "builders.util.advancedfly",
            "builders.util.tpgm3",
            "headdb.open",
            "headdb.phead",
            "headdb.free.*",
            "headdb.category.*",
            "astools.*",
            "chars"
    };

    private final List<MGZWorld> worlds = new ArrayList<>();
    private final List<ParseTask> parseTasks = new ArrayList<>();
    private final Map<Player, PermissionAttachment> permissionAttachments = new HashMap<>();

    public WorldManager(JavaPlugin plugin) {
        super(plugin);
        for (World world : Bukkit.getWorlds())
            setupWorld(world);
        // Loading maps from the maps directory
        for (WorldCategory category : WorldCategory.values()) {
            File categoryDirectory = new File("maps" + File.separator + category.name());
            if (!categoryDirectory.exists())
                continue;
            File[] files = categoryDirectory.listFiles();
            if (files == null)
                continue;
            for (File directory : files) {
                if (!directory.isDirectory())
                    continue;
                try {
                    worlds.add(new MGZWorld(new File(directory, MGZWorld.FILE_NAME)));
                } catch (Exception ex) {
                    plugin.getLogger().severe("Failed to add world \"" + directory.getPath() + "\"");
                    ex.printStackTrace();
                }
            }
        }
        // Deleting worlds from the main directory
        for (MGZWorld world : worlds) {
            String worldName = world.getCategory().name() + "-" + world.getName();
            World bukkitWorld = Bukkit.getWorld(worldName);
            if (bukkitWorld != null)
                Bukkit.unloadWorld(bukkitWorld, true);
            File worldDirectory = new File(Bukkit.getWorldContainer(), worldName);
            if (!worldDirectory.exists())
                continue;
            File targetDirectory = new File("maps" + File.separator + world.getCategory().name() + File.separator + world.getName());
            if (!targetDirectory.exists())
                targetDirectory.mkdirs();
            copyWorld(worldDirectory, targetDirectory);
            FileUtils.deleteQuietly(worldDirectory);
        }
        // Creating the parse directory and deleting old parsed worlds
        File parseDirectory = new File("parse");
        if (!parseDirectory.exists())
            parseDirectory.mkdirs();
        else {
            File[] files = parseDirectory.listFiles();
            if (files != null) {
                for (File directory : files) {
                    if (!directory.isDirectory())
                        continue;
                    FileUtils.deleteQuietly(directory);
                    System.out.println("Deleted old parsed world: " + directory.getPath());
                }
            }
        }
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (World world : Bukkit.getWorlds()) {
                MGZWorld mgzWorld = getWorld(world);
                if (mgzWorld != null) {
                    if (world.getPlayers().isEmpty()) {
                        setupWorld(world);
                        world.save();
                        Bukkit.unloadWorld(world, true);
                        unloadWorld(mgzWorld);
                        mgzWorld.setWorld(null);
                        new BackupTask(plugin, mgzWorld);
                        Bukkit.broadcastMessage(Style.main("Map", "Unloading map §b" + mgzWorld.getName()));
                    }
                }
                for (Player player : world.getPlayers()) {
                    if (player.isOp())
                        continue;
                    PermissionAttachment attachment = permissionAttachments.get(player);
                    if (attachment == null)
                        continue;
                    for (String permission : permissions) {
                        if (mgzWorld == null || (!mgzWorld.hasPrivileges(player))) {
                            if (player.hasPermission(permission))
                                attachment.setPermission(permission, false);
                        } else if (mgzWorld.hasPrivileges(player) && !player.hasPermission(permission))
                            attachment.setPermission(permission, true);
                    }
                }
            }
            parseTasks.removeIf(parseTask -> {
                if (parseTask.isCompleted())
                    return true;
                parseTask.run();
                return false;
            });
        }, 0L, 1L);
        registerCommand(new CreateCommand(this));
        registerCommand(new MapsCommand(this));
        registerCommand(new MenuCommand());
        registerCommand(new MapCommand(this));
        registerCommand(new MapInfoCommand(this));
        registerCommand(new AdminCommand(this));
        registerCommand(new AuthorCommand(this));
        registerCommand(new RenameCommand(this));
        registerCommand(new CategoryCommand(this));
        registerCommand(new SaveCommand(this));
        registerCommand(new DeleteCommand(this));
        registerCommand(new ReloadWorldEditCommand());

        registerCommand(new ParseCommand(this));
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        permissionAttachments.put(player, event.getPlayer().addAttachment(getPlugin()));
    }

    @EventHandler
    private void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        MGZWorld world = getWorld(player.getWorld());
        if ((world == null && !player.isOp()) || (world != null && (!world.hasPrivileges(player))))
            event.setCancelled(true);
    }

    @EventHandler
    private void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        MGZWorld world = getWorld(player.getWorld());
        if ((world == null && !player.isOp()) || (world != null && (!world.hasPrivileges(player))))
            event.setCancelled(true);
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        MGZWorld world = getWorld(player.getWorld());
        if ((world == null && !player.isOp()) || (world != null && (!world.hasPrivileges(player))))
            event.setCancelled(true);
    }

    @EventHandler
    private void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntityType() != EntityType.ARMOR_STAND)
            event.setCancelled(true);
    }

    @EventHandler
    private void onIgnite(BlockIgniteEvent event) {
        BlockIgniteEvent.IgniteCause cause = event.getCause();
        if (cause == BlockIgniteEvent.IgniteCause.LAVA || cause == BlockIgniteEvent.IgniteCause.SPREAD)
            event.setCancelled(true);
    }

    @EventHandler
    private void onBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onSpread(BlockSpreadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onBlockFade(BlockFadeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onLeafDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onBlockForm(BlockFormEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onWeatherChange(WeatherChangeEvent event) {
        if (event.toWeatherState())
            event.setCancelled(true);
    }

    @EventHandler
    private void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        MGZWorld toWorld = getWorld(player.getWorld());
        if (toWorld != null) {
            player.chat("/mapinfo");
            if (!toWorld.hasPrivileges(player)) {
                player.sendMessage(Style.error("Maps", "You do not have privileges to build on this map, " +
                        "please contact the map author to request build privileges!"));
            }
        }
        DeleteCommand.getConfirmDelete().remove(player);
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        permissionAttachments.remove(player);
        DeleteCommand.getConfirmDelete().remove(player);
    }

    public void loadWorld(MGZWorld world) {
        if (world == null)
            return;
        File target = new File(Bukkit.getWorldContainer(),world.getCategory().name() + "-" + world.getName());
        copyWorld(new File("maps" + File.separator + world.getCategory().name() + File.separator + world.getName()), target);
        world.setDataFile(new File(target, MGZWorld.FILE_NAME));
    }

    public void unloadWorld(MGZWorld world) {
        if (world == null)
            return;
        File target = new File("maps" + File.separator + world.getCategory().name() + File.separator + world.getName());
        copyWorld(world.getWorld().getWorldFolder(), target);
        FileUtils.deleteQuietly(world.getWorld().getWorldFolder());
        world.setDataFile(new File(target, MGZWorld.FILE_NAME));
    }

    public boolean isIllegalName(String name) {
        File worldContainer = Bukkit.getWorldContainer();
        File[] files = worldContainer.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    public World create(String name, String author, WorldCategory category, WorldGenerator generator, String preset) {
        name = name.replaceAll(" ", "_");
        MGZWorld mgzWorld = getWorld(name, category);
        if (mgzWorld != null)
            throw new IllegalArgumentException("Map with name \"" + name + "\" already exists under category " + category.name());
        if (preset == null)
            preset = generator.getPreset();
        if (preset == null)
            throw new IllegalArgumentException("Preset is null for generator type " + generator.name());

        // Setting up the vanilla world
        World world = getWorldCreator(category.name() + "-" + name, preset).createWorld();
        setupWorld(world);
        world.save();

        // Creating the MGZWorld
        File file = new File(world.getWorldFolder(), MGZWorld.FILE_NAME);
        mgzWorld = new MGZWorld(world, file, name, author, preset, category);
        mgzWorld.save();
        worlds.add(mgzWorld);

        return world;
    }

    public MGZWorld getWorld(World world) {
        List<MGZWorld> worlds = lookup(mgzWorld -> mgzWorld.getWorld() != null && (mgzWorld.getWorld().getName().equals(world.getName())));
        if (worlds.isEmpty())
            return null;
        return worlds.get(0);
    }

    public MGZWorld getWorld(String name, WorldCategory category) {
        List<MGZWorld> worlds = lookup(mgzWorld -> mgzWorld.getName().equalsIgnoreCase(name) && mgzWorld.getCategory() == category);
        if (worlds.isEmpty())
            return null;
        return worlds.get(0);
    }

    public List<MGZWorld> getWorld(String name) {
        return lookup(world -> world.getName().equalsIgnoreCase(name));
    }

    public List<MGZWorld> lookup(Predicate<MGZWorld> predicate) {
        return worlds.stream().filter(predicate).collect(Collectors.toList());
    }

    public void parse(MGZWorld mgzWorld, Location center, int radius, List<Integer> ids) {
        if (beingParsed(mgzWorld))
            throw new IllegalStateException("World \"" + mgzWorld.getName() + "\" is already being parsed");
        World world = mgzWorld.getWorld();
        for (Player worldPlayer : world.getPlayers())
            worldPlayer.teleport(Build.INSTANCE.getMainWorld().getSpawnLocation());
        Bukkit.unloadWorld(world, true);
        unloadWorld(mgzWorld);
        mgzWorld.setWorld(null);

        ParseTask parseTask = new ParseTask(mgzWorld, radius, ids);
        parseTasks.add(parseTask);

        try {
            File parseWorldDirectory = new File("parse" + File.separator + mgzWorld.getCategory().name() + File.separator +
                    mgzWorld.getName().replaceAll(" ", "_"));
            FileUtils.copyDirectory(new File("maps" + File.separator + mgzWorld.getCategory().name() + File.separator + mgzWorld.getName()), parseWorldDirectory);
            World parseWorld = getWorldCreator(parseWorldDirectory.getPath(), mgzWorld.getPreset()).createWorld();
            setupWorld(parseWorld);
            center.setWorld(parseWorld);
            parseTask.start(parseWorld, center);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public boolean beingParsed(MGZWorld world) {
        return parseTasks.stream().anyMatch(parseTask -> parseTask.getMgzWorld().equals(world));
    }

    public void copyWorld(File oldDirectory, File newDirectory) {
        try {
            FileUtils.copyDirectory(oldDirectory, new File(newDirectory.getPath().replaceAll(" ", "_")));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public WorldCreator getWorldCreator(String name, String preset) {
        WorldCreator creator = new WorldCreator(name);
        creator.environment(World.Environment.NORMAL);
        creator.type(WorldType.FLAT);
        if (preset != null)
            creator.generatorSettings(preset);
        creator.generateStructures(false);
        return creator;
    }

    public void setupWorld(World world) {
        long time = 6000L;
        if (world.getName().toLowerCase().contains("christmas"))
            time = 12000L;
        else if (world.getName().toLowerCase().contains("halloween"))
            time = 17000L;
        world.setTime(time);
        world.setThundering(false);
        world.setStorm(false);
        world.setSpawnLocation(0, 150, 0);
        world.setGameRuleValue("randomTickSpeed", "0");
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("showDeathMessages", "false");
        world.setGameRuleValue("doFireTick", "false");
        world.setGameRuleValue("mobGriefing", "false");
        world.setGameRuleValue("doMobLoot", "false");
        world.setGameRuleValue("doMobSpawning", "false");
    }
}