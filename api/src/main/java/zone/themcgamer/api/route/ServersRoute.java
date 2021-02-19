package zone.themcgamer.api.route;

import spark.Request;
import spark.Response;
import zone.themcgamer.api.APIException;
import zone.themcgamer.api.APIVersion;
import zone.themcgamer.api.RestPath;
import zone.themcgamer.api.model.impl.MinecraftServerModel;
import zone.themcgamer.api.model.impl.ServerGroupModel;
import zone.themcgamer.data.APIAccessLevel;
import zone.themcgamer.data.jedis.data.APIKey;
import zone.themcgamer.data.jedis.data.ServerGroup;
import zone.themcgamer.data.jedis.data.server.MinecraftServer;
import zone.themcgamer.data.jedis.repository.RedisRepository;
import zone.themcgamer.data.jedis.repository.impl.MinecraftServerRepository;
import zone.themcgamer.data.jedis.repository.impl.ServerGroupRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Braydon
 */
public class ServersRoute {
    private final ServerGroupRepository serverGroupRepository;
    private final MinecraftServerRepository minecraftServerRepository;

    public ServersRoute() {
        serverGroupRepository = RedisRepository.getRepository(ServerGroupRepository.class).orElse(null);
        minecraftServerRepository = RedisRepository.getRepository(MinecraftServerRepository.class).orElse(null);
    }

    /*
        Server Groups
     */

    @RestPath(path = "/serverGroups", version = APIVersion.V1, accessLevel = APIAccessLevel.DEV)
    public List<ServerGroupModel> getGroups(Request request, Response response, APIKey apiKey) throws APIException {
        List<ServerGroupModel> models = new ArrayList<>();
        for (ServerGroup serverGroup : serverGroupRepository.getCached())
            models.add(ServerGroupModel.fromServerGroup(serverGroup));
        return models;
    }

    @RestPath(path = "/serverGroup/:name", version = APIVersion.V1, accessLevel = APIAccessLevel.DEV)
    public ServerGroupModel getServerGroup(Request request, Response response, APIKey apiKey) throws APIException {
        String name = request.params(":name");
        if (name == null || (name.trim().isEmpty()))
            throw new APIException("Invalid Server Group");
        Optional<ServerGroup> optionalServerGroup = serverGroupRepository.lookup(name);
        if (optionalServerGroup.isEmpty())
            throw new APIException("Server group not found");
        return ServerGroupModel.fromServerGroup(optionalServerGroup.get());
    }

    /*
        Minecraft Servers
     */

    @RestPath(path = "/minecraftServers", version = APIVersion.V1, accessLevel = APIAccessLevel.DEV)
    public List<MinecraftServerModel> getMinecraftServers(Request request, Response response, APIKey apiKey) throws APIException {
        List<MinecraftServerModel> models = new ArrayList<>();
        for (MinecraftServer minecraftServer : minecraftServerRepository.getCached())
            models.add(MinecraftServerModel.fromMinecraftServer(minecraftServer));
        return models;
    }

    @RestPath(path = "/minecraftServer/:id", version = APIVersion.V1, accessLevel = APIAccessLevel.DEV)
    public MinecraftServerModel getMinecraftServer(Request request, Response response, APIKey apiKey) throws APIException {
        String id = request.params(":id");
        if (id == null || (id.trim().isEmpty()))
            throw new APIException("Invalid Minecraft Server");
        Optional<MinecraftServer> optionalMinecraftServer = minecraftServerRepository.lookup(id);
        if (optionalMinecraftServer.isEmpty())
            throw new APIException("Minecraft server not found");
        return MinecraftServerModel.fromMinecraftServer(optionalMinecraftServer.get());
    }
}