package zone.themcgamer.data.jedis.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import zone.themcgamer.data.jedis.data.server.MinecraftServer;
import zone.themcgamer.data.jedis.repository.RedisRepository;
import zone.themcgamer.data.jedis.repository.impl.MinecraftServerRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Braydon
 * @implNote A server group is the owner of a {@link MinecraftServer}, it contains properties
 *           such as how much ram to use, which jar to use, and more
 */
@AllArgsConstructor @Getter @ToString
public class ServerGroup {
    private final String name;
    private final long memoryPerServer;
    private final String serverJar, templatePath, pluginJarName, worldPath, startupScript, privateAddress;
    private final UUID host;
    private final String game;
    private final int minPlayers, maxPlayers, minServers, maxServers;
    private final boolean kingdom, staticGroup;

    /**
     * Get a list of {@link MinecraftServer}'s running under this server group
     * @return the list of servers
     */
    public Collection<MinecraftServer> getServers() {
        Set<MinecraftServer> servers = new HashSet<>();
        Optional<MinecraftServerRepository> minecraftServerRepository = RedisRepository.getRepository(MinecraftServerRepository.class);
        if (!minecraftServerRepository.isPresent())
            return servers;
        servers.addAll(new ArrayList<>(minecraftServerRepository.get().getCached()).parallelStream()
                .filter(minecraftServer -> minecraftServer.getGroup().equals(this))
                .collect(Collectors.toList()));
        return servers;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        ServerGroup group = (ServerGroup) other;
        return Objects.equals(name, group.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}