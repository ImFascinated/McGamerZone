package zone.themcgamer.api.route;

import spark.Request;
import spark.Response;
import zone.themcgamer.api.APIException;
import zone.themcgamer.api.APIVersion;
import zone.themcgamer.api.RestPath;
import zone.themcgamer.api.model.impl.StatusModel;
import zone.themcgamer.data.APIAccessLevel;
import zone.themcgamer.data.jedis.cache.CacheRepository;
import zone.themcgamer.data.jedis.cache.ICacheItem;
import zone.themcgamer.data.jedis.cache.ItemCacheType;
import zone.themcgamer.data.jedis.cache.impl.PlayerStatusCache;
import zone.themcgamer.data.jedis.data.APIKey;
import zone.themcgamer.data.jedis.repository.RedisRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Braydon
 */
public class StatusRoute {
    private final CacheRepository repository;

    public StatusRoute() {
        repository = RedisRepository.getRepository(CacheRepository.class).orElse(null);
    }

    @RestPath(path = "/status", version = APIVersion.V1)
    public Map<String, Object> getStatuses(Request request, Response response, APIKey apiKey) throws APIException {
        List<ICacheItem<?>> statuses = repository.filter(cacheItem -> cacheItem.getType() == ItemCacheType.PLAYER_STATUS);
        StringBuilder namesBuilder = new StringBuilder();
        for (ICacheItem<?> status : statuses)
            namesBuilder.append(((PlayerStatusCache) status).getPlayerName()).append(", ");
        String names = namesBuilder.toString();
        if (!names.isEmpty())
            names = names.substring(0, names.length() - 2);
        String finalNames = names;
        return new HashMap<>() {{
            put("total", statuses.size());
            put("names", apiKey.getAccessLevel() == APIAccessLevel.STANDARD ? "Unauthorized" : finalNames);
        }};
    }

    @RestPath(path = "/status/:name", version = APIVersion.V1)
    public StatusModel getStatus(Request request, Response response, APIKey apiKey) throws APIException {
        String name = request.params(":name");
        if (name == null || (name.trim().isEmpty() || name.length() > 16))
            throw new APIException("Invalid username");
        PlayerStatusCache statusCache = repository
                .lookup(PlayerStatusCache.class, playerStatusCache -> playerStatusCache.getPlayerName().equals(name))
                .stream().findFirst().orElse(null);
        if (statusCache == null)
            throw new APIException("Player not found");
        StatusModel model = new StatusModel(statusCache.getUuid(), statusCache.getPlayerName(), statusCache.getServer(), statusCache.getTimeJoined());
        if (apiKey.getAccessLevel() == APIAccessLevel.STANDARD)
            model.setServer("Unauthorized");
        return model;
    }
}