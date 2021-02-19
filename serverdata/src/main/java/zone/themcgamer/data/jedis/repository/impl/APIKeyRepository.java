package zone.themcgamer.data.jedis.repository.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import zone.themcgamer.data.jedis.JedisController;
import zone.themcgamer.data.jedis.data.APIKey;
import zone.themcgamer.data.jedis.repository.RedisRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Braydon
 */
public class APIKeyRepository extends RedisRepository<String, List<APIKey>> {
    private static final Gson GSON = new Gson();

    public APIKeyRepository(JedisController controller) {
        super(controller, "apiKeys");
    }

    @Override
    public Optional<List<APIKey>> lookup(String name) {
        if (getCached().isEmpty())
            return Optional.of(Collections.emptyList());
        return Optional.of(getCached().get(0).stream().filter(apiKey -> apiKey.getKey().equals(name)).collect(Collectors.toList()));
    }

    @Override
    public String getKey(List<APIKey> apiKeys) {
        return "apiKeys";
    }

    @Override
    public Optional<List<APIKey>> fromMap(Map<String, String> map) {
        return Optional.of(GSON.fromJson(map.get("keys"), new TypeToken<List<APIKey>>() {}.getType()));
    }

    @Override
    public long getExpiration(List<APIKey> apiKeys) {
        return -1;
    }

    @Override
    public Map<String, Object> toMap(List<APIKey> apiKeys) {
        Map<String, Object> data = new HashMap<>();
        data.put("keys", GSON.toJson(apiKeys));
        return data;
    }
}