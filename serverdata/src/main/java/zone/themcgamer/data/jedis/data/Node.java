package zone.themcgamer.data.jedis.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;
import zone.themcgamer.data.jedis.data.server.MinecraftServer;
import zone.themcgamer.data.jedis.repository.RedisRepository;
import zone.themcgamer.data.jedis.repository.impl.MinecraftServerRepository;

import java.net.InetAddress;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Braydon
 * @implNote A "Node" is a Dedicated Server, each node can be used to host a
 *           {@link MinecraftServer}
 */
@AllArgsConstructor @Getter @ToString
public class Node {
    private final String name, address, portRange;

    /**
     * Check whether or not the Node is reachable
     *
     * @return the reachable state
     */
    @SneakyThrows
    public boolean isReachable() {
        return InetAddress.getByName(address).isReachable(5000);
    }

    public long getNextAvailablePort(Set<Long> used) {
        if (!portRange.contains("-"))
            return -1;
        String[] split = portRange.split("-");
        long min, max;
        try {
            min = Long.parseLong(split[0]);
            max = Long.parseLong(split[1]);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            return -1;
        }
        for (long port = min; port <= max; port++) {
            long finalPort = port;
            if (used.contains(port) || getServers().stream().anyMatch(minecraftServer -> minecraftServer.getPort() == finalPort))
                continue;
            return port;
        }
        return -1;
    }

    /**
     * Get a list of {@link MinecraftServer}'s running under this node
     *
     * @return the list of servers
     */
    public Collection<MinecraftServer> getServers() {
        Set<MinecraftServer> servers = new HashSet<>();
        Optional<MinecraftServerRepository> minecraftServerRepository = RedisRepository.getRepository(MinecraftServerRepository.class);
        if (minecraftServerRepository.isEmpty())
            return servers;
        servers.addAll(new ArrayList<>(minecraftServerRepository.get().getCached()).parallelStream()
                .filter(minecraftServer -> minecraftServer.getNode() != null && (minecraftServer.getNode().equals(this)))
                .collect(Collectors.toList()));
        return servers;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        Node node = (Node) other;
        return Objects.equals(name, node.name)
                && Objects.equals(address, node.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address);
    }
}
