package zone.themcgamer.arcade.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import zone.themcgamer.arcade.team.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Braydon
 */
@Setter @Getter
public class GamePlayer {
    @Getter private static final Map<UUID, GamePlayer> cache = new HashMap<>();

    private final UUID uuid;
    private Team team;
    private boolean spectating;
    private long logoutTime;

    public GamePlayer(UUID uuid) {
        this.uuid = uuid;
        cache.put(uuid, this);
    }

    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public void remove() {
        cache.remove(uuid);
    }

    public static GamePlayer getPlayer(UUID uuid) {
        return cache.get(uuid);
    }
}