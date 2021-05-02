package zone.themcgamer.data.jedis.data.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import zone.themcgamer.data.jedis.command.JedisCommandHandler;
import zone.themcgamer.data.jedis.command.impl.ServerStateChangeCommand;
import zone.themcgamer.data.jedis.data.Node;
import zone.themcgamer.data.jedis.data.ServerGroup;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Braydon
 */
@AllArgsConstructor @Setter @Getter @ToString
public class MinecraftServer {
    private final String id;
    private final int numericId;
    private final String name;
    private final Node node;
    private final ServerGroup group;
    private final String address;
    private final long port;

    private int usedRam, maxRam;
    private ServerState state;
    private long lastStateChange;

    private int online, maxPlayers;
    private double tps;
    private UUID host;
    private String game;

    private String metaData;
    private final long created;
    private long lastHeartbeat;

    /**
     * Set the state of the server to the given state
     *
     * @param state the state
     */
    public void setState(ServerState state) {
        JedisCommandHandler.getInstance().send(new ServerStateChangeCommand(this, this.state, state));
        this.state = state;
        lastStateChange = System.currentTimeMillis();
    }

    /**
     * Return whether or not the Minecraft server is running by checking
     * the dead state and the server state
     *
     * @return the running state
     */
    public boolean isRunning() {
        if (isDead())
            return false;
        return state == ServerState.RUNNING;
    }

    /**
     * Return whether or not the Minecraft server is lagging by
     * checking if ths tps is 15 or below
     *
     * @return the lagging state
     */
    public boolean isLagging() {
        return tps <= 15;
    }

    /**
     * Return whether or not the Minecraft server is dead.
     * A server is considered dead if it hasn't sent a heartbeat
     * within 8 seconds and the server is older than 30 seconds
     *
     * @return the dead state
     */
    public boolean isDead() {
        if (isNew())
            return false;
        return (System.currentTimeMillis() - lastHeartbeat) >= TimeUnit.SECONDS.toMillis(8L);
    }

    /**
     * Return whether or not the server was created in the last minute
     *
     * @return the new state
     */
    public boolean isNew() {
        return getUptime() < TimeUnit.SECONDS.toMillis(30L);
    }

    /**
     * Get the uptime of the server in millis
     *
     * @return the uptime
     */
    public long getUptime() {
        return System.currentTimeMillis() - created;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        MinecraftServer that = (MinecraftServer) other;
        return numericId == that.numericId
                && Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(node, that.node)
                && Objects.equals(group, that.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, numericId, name, node, group);
    }
}