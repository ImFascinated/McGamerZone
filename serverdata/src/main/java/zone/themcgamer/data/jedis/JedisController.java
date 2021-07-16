package zone.themcgamer.data.jedis;

import lombok.Getter;
import redis.clients.jedis.JedisPool;
import zone.themcgamer.data.jedis.cache.CacheRepository;
import zone.themcgamer.data.jedis.command.JedisCommandHandler;
import zone.themcgamer.data.jedis.command.impl.ServerStateChangeCommand;
import zone.themcgamer.data.jedis.data.server.MinecraftServer;
import zone.themcgamer.data.jedis.data.server.ServerState;
import zone.themcgamer.data.jedis.repository.RedisRepository;
import zone.themcgamer.data.jedis.repository.RedisRepositoryUpdateTask;
import zone.themcgamer.data.jedis.repository.impl.APIKeyRepository;
import zone.themcgamer.data.jedis.repository.impl.MinecraftServerRepository;
import zone.themcgamer.data.jedis.repository.impl.NodeRepository;
import zone.themcgamer.data.jedis.repository.impl.ServerGroupRepository;

import java.util.Map;

/**
 * @author Braydon
 * @implNote This class serves the purpose of connecting and initializing things that
 *           require redis, such as the {@link JedisCommandHandler} and {@link RedisRepository}'s
 */
@Getter
public class JedisController {
    private JedisPool pool;

    private MinecraftServerRepository minecraftServerRepository;

    /**
     * Start the controller. The initializing of the controller is done via a method
     * so repositories that aren't added in this method can be added before Redis is
     * initialized without running into problems
     */
    public JedisController start() {

        pool = new JedisPool(JedisConstants.HOST); // Configuring redis and connecting to the server
        new JedisCommandHandler(); // Starting the command handler to handle commands over the network

        // Adding repositories
        new CacheRepository(this);
        new NodeRepository(this);
        new ServerGroupRepository(this);
        new APIKeyRepository(this);
        minecraftServerRepository = new MinecraftServerRepository(this);


        // Adding a listener for the ServerStateChangeCommand that should remove the server from the cache if the
        // Current server state is STOPPING
        JedisCommandHandler.getInstance().addListener(jedisCommand -> {
            if (jedisCommand instanceof ServerStateChangeCommand) {
                ServerStateChangeCommand serverStateChangeCommand = (ServerStateChangeCommand) jedisCommand;
                if (!serverStateChangeCommand.getNewState().equals(ServerState.STOPPING))
                    return;
                MinecraftServer server = serverStateChangeCommand.getServer();
                System.out.println("Removed server: " + server.getId());
                //Only removed the cached server, do not actual delete the server from redis. This will only controller do!
                minecraftServerRepository.getCached().remove(server);
            }
        });

        new RedisRepositoryUpdateTask().start(); // Starting the repository update task
        return this;
    }
}