package zone.themcgamer.arcade.manager;

import com.cryptomorin.xseries.XSound;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import zone.themcgamer.arcade.Arcade;
import zone.themcgamer.arcade.commands.GameCommand;
import zone.themcgamer.arcade.commands.arguments.*;
import zone.themcgamer.arcade.event.GameStateChangeEvent;
import zone.themcgamer.arcade.game.Game;
import zone.themcgamer.arcade.game.GameManager;
import zone.themcgamer.arcade.game.GameState;
import zone.themcgamer.arcade.map.GameMap;
import zone.themcgamer.arcade.map.MapManager;
import zone.themcgamer.arcade.map.MapVotingManager;
import zone.themcgamer.arcade.map.event.MapVoteWinEvent;
import zone.themcgamer.arcade.player.GamePlayer;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.common.PlayerUtils;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.common.scheduler.ScheduleType;
import zone.themcgamer.core.common.scheduler.event.SchedulerEvent;
import zone.themcgamer.core.module.Module;
import zone.themcgamer.core.module.ModuleInfo;
import zone.themcgamer.core.traveller.ServerTraveller;
import zone.themcgamer.core.world.MGZWorld;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * @author Braydon
 */
@ModuleInfo(name = "Arcade Manager") @Getter
public class ArcadeManager extends Module {
    private static final int DEFAULT_COUNTDOWN = 10;

    private final GameManager gameManager;

    private Game game;
    private GameState state = GameState.LOBBY;
    private int countdown = -1;

    private GameMap map;

    private boolean mapsLoaded;
    private MapVotingManager mapVotingManager;

    public ArcadeManager(JavaPlugin plugin, ServerTraveller traveller) {
        super(plugin);
        new LobbyManager(plugin, this, traveller);
        gameManager = new GameManager(plugin);
        game = gameManager.getGames().get(0);
        MapManager mapManager = new MapManager(plugin);
        mapManager.withConsumer(future -> {
            future.whenComplete((maps, throwable) -> {
                mapsLoaded = true;
                mapVotingManager = new MapVotingManager(plugin, this, mapManager);
            });
        });
        registerCommand(new GameCommand());
        registerCommand(new GameForceMapCommand(game));
        registerCommand(new GameHostCommand());
        registerCommand(new GameMaxPlayersCommand());
        registerCommand(new GameMinPlayersCommand());
        registerCommand(new GamePlayerTeamCommand());
        registerCommand(new GameStartCommand(this));
        registerCommand(new GameStopCommand(game));
    }

    @EventHandler
    private void onLogin(PlayerLoginEvent event) {
        if (!mapsLoaded)
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cPlease wait whilst we load the maps...");
    }

    @EventHandler
    private void handleCountdown(SchedulerEvent event) {
        if (countdown == -1)
            return;
        ChatColor color = ChatColor.GREEN;
        switch (countdown) {
            case 4: {
                color = ChatColor.YELLOW;
                break;
            }
            case 3: {
                color = ChatColor.GOLD;
                break;
            }
            case 2: {
                color = ChatColor.RED;
                break;
            }
            case 1: {
                color = ChatColor.DARK_RED;
                break;
            }
        }
        if (event.getType() == ScheduleType.SECOND) {
            if (Bukkit.getOnlinePlayers().size() < game.getMgzGame().getMinPlayers()) {
                countdown = -1;
                setState(GameState.LOBBY);
                Bukkit.broadcastMessage(Style.error(game.getMgzGame().getName(), "§cCountdown stopped, there are not enough players!"));
                return;
            }
            if (countdown > 0) {
                for (Player player : Bukkit.getOnlinePlayers())
                    player.sendActionBar("§aGame starting in §f" + color + countdown + "§a...");
            }
            if (countdown % 10 == 0 || countdown <= 5) {
                if (countdown <= 0) {
                    startGame();
                    return;
                }
                float pitch = (float) countdown / 2f;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(player.getEyeLocation(), XSound.ENTITY_PLAYER_LEVELUP.parseSound(), 0.9f, pitch);
                    player.sendMessage(Style.main(game.getMgzGame().getName(), "Game starting in §f" + color + countdown + " second" + (countdown == 1 ? "" : "s") + "§7..."));
                    //player.sendTitle((countdown == 5 ? color + "\u277A" : (countdown == 4 ? color + "\u2779" : (countdown == 3 ? color + "\u2778" : (countdown == 2 ? color + "\u2777" : (countdown == 1 ? color + "\u2776" : ""))))), "",5,5,5);
                }
            }
            countdown--;
        }
    }

    @EventHandler
    private void handleActionBar(SchedulerEvent event) {
        if (event.getType() == ScheduleType.SECOND) {
            if (state != GameState.LOBBY)
                return;
            String actionBar = "&aWaiting for &b" + Math.abs(game.getMgzGame().getMaxPlayers() - Bukkit.getOnlinePlayers().size()) +  " &amore players...";
            if (mapVotingManager != null && (mapVotingManager.isVoting())) {
                actionBar = "&a&lPlease vote a map!";
                //TODO 2 colors animation for the vote map just do something you like
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendActionBar(Style.color(actionBar));
            }
        }
    }

    @EventHandler
    private void onMapVoteWin(MapVoteWinEvent event) {
        if (state != GameState.LOBBY)
            return;
        setMap(event.getMap());
        startCountdown(DEFAULT_COUNTDOWN);
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerUtils.reset(player, true, true, GameMode.SURVIVAL);
        if (state == GameState.LOBBY || state == GameState.STARTING)
            player.teleport(Arcade.INSTANCE.getSpawn());
        else {
            // TODO: 1/27/21 teleport the player to the game map
        }

        Optional<Account> optionalAccount = AccountManager.fromCache(player.getUniqueId());
        if (optionalAccount.isPresent() && game.isJoinMessages())
            event.setJoinMessage("§a§lJoin §8» §7" + optionalAccount.get().getDisplayName() + " §7has joined the game!");
        else event.setJoinMessage(null);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Optional<Account> optionalAccount = AccountManager.fromCache(player.getUniqueId());
        if (optionalAccount.isPresent() && game.isQuitMessages())
            event.setQuitMessage("§4§lQuit §8» §7" + optionalAccount.get().getDisplayName() + " §7left the game!");
        else event.setQuitMessage(null);
    }

    public void setMap(@Nullable MGZWorld mgzWorld) {
        if (state != GameState.LOBBY)
            throw new IllegalStateException("The map cannot be updated in this state: " + state.name());
        if (map != null) {
            Bukkit.unloadWorld(map.getBukkitWorld(), true);
            FileUtils.deleteQuietly(map.getBukkitWorld().getWorldFolder());
        }
        if (mgzWorld == null)
            return;
        try {
            FileUtils.copyDirectory(mgzWorld.getDataFile().getParentFile(), new File(mgzWorld.getName()));

            WorldCreator creator = new WorldCreator(mgzWorld.getName());
            creator.environment(World.Environment.NORMAL);
            creator.type(WorldType.FLAT);
            if (mgzWorld.getPreset() != null)
                creator.generatorSettings(mgzWorld.getPreset());
            creator.generateStructures(false);
            World world = creator.createWorld();

            long time = 6000L;
            if (mgzWorld.getName().toLowerCase().contains("christmas"))
                time = 12000L;
            else if (mgzWorld.getName().toLowerCase().contains("halloween"))
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

            map = new GameMap(mgzWorld, world);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Set the current game to the provided {@link Game}
     * @param game the game to set
     */
    public void setGame(Game game) {
        if (state != GameState.LOBBY)
            stopGame();
        this.game = game;
        setState(GameState.LOBBY);
    }

    /**
     * Start the game instantly
     */
    public void startCountdown() {
        startCountdown(0);
    }

    /**
     * Start the game with the given countdown
     * @param countdown the countdown
     */
    public void startCountdown(int countdown) {
        if (state != GameState.LOBBY)
            return;
        setState(GameState.STARTING);
        this.countdown = countdown;
        if (countdown <= 0)
            startGame();
    }

    public void startGame() {
        if (state == GameState.PLAYING || state == GameState.ENDING)
            throw new IllegalStateException("The game is already in a running state: " + state.name());
        setState(GameState.PLAYING);
        countdown = -1;

        // Setting up the game
        game.setStarted(System.currentTimeMillis());

        for (Player player : Bukkit.getOnlinePlayers()) {
            // TODO: 1/31/21 check if player is a staff member and is vanished

            PlayerUtils.reset(player, true, true, GameMode.SURVIVAL);

            GamePlayer gamePlayer = GamePlayer.getPlayer(player.getUniqueId());

            // TODO: 1/31/21 team calculations

            player.teleport(map.getBukkitWorld().getSpawnLocation());
        }
    }

    /**
     * Stop the currently running game
     */
    public void stopGame() {
        if (state != GameState.PLAYING)
            return;
        setState(GameState.LOBBY);
    }

    /**
     * Set the game state to the given {@link GameState}
     * @param state the state to set
     */
    private void setState(GameState state) {
        Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(game, this.state, state));
        this.state = state;
    }
}