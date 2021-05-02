package zone.themcgamer.core.common;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import zone.themcgamer.common.EnumUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author Braydon
 */
@UtilityClass
public class MojangUtils {
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    private static final Cache<String, UUID> UUID_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(16, TimeUnit.HOURS)
            .build();
    private static final Cache<UUID, Map<String, Long>> NAME_CHANGE_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();
    private static final Cache<UUID, SkinData> SKIN_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    /**
     * Get the Mojang service statuses
     *
     * @return service statuses
     */
    public static Map<MojangService, ServiceStatus> getServiceStatus() {
        Map<MojangService, ServiceStatus> serviceStatusMap = new HashMap<>();
        try {
            Request request = new Request.Builder()
                    .url("https://status.mojang.com/check")
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.code() == 200) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        List<Map<String, String>> data = gson.fromJson(body.string(), new TypeToken<List<Map<String, String>>>() {}.getType());
                        for (Map<String, String> map : data) {
                            for (Map.Entry<String, String> entry : map.entrySet()) {
                                MojangService service = MojangService.lookupFromPath(entry.getKey());
                                if (service == null) {
                                    System.err.println("Failed to find Mojang service: " + entry.getKey());
                                    continue;
                                }
                                ServiceStatus status = EnumUtils.fromString(ServiceStatus.class, entry.getValue().toUpperCase());
                                if (status == null) {
                                    System.err.println("Failed to find status for service '" + service.name() + "': " + entry.getValue());
                                    continue;
                                }
                                serviceStatusMap.put(service, status);
                            }
                        }
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return serviceStatusMap;
    }

    /**
     * Get the {@link UUID} of the given player
     *
     * @param playerName - The name of the player to get the uuid for
     * @return the uuid
     */
    @Nullable
    public static UUID getUUIDSync(String playerName) {
        UUID uuid = UUID_CACHE.getIfPresent(playerName);
        if (uuid == null) {
            try {
                Request request = new Request.Builder()
                        .url("https://api.mojang.com/users/profiles/minecraft/" + playerName)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (response.code() == 200) {
                        ResponseBody body = response.body();
                        if (body != null) {
                            Map<String, String> data = gson.fromJson(body.string(), new TypeToken<Map<String, String>>() {}.getType());
                            if (data.containsKey("id")) {
                                uuid = parseUUID(data.get("id"));
                                UUID_CACHE.put(playerName, uuid);
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return uuid;
    }

    /**
     * Get the {@link UUID} of the given player asynchronously
     *
     * @param playerName - The name of the player to get the uuid for
     * @param callback - The consumer that that will contain the uuid response, null if none
     */
    public static void getUUIDAsync(String playerName, Consumer<UUID> callback) {
        UUID cachedUUID = UUID_CACHE.getIfPresent(playerName);
        if (cachedUUID != null) {
            callback.accept(cachedUUID);
            return;
        }
        Request request = new Request.Builder()
                .url("https://api.mojang.com/users/profiles/minecraft/" + playerName)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    callback.accept(null);
                    return;
                }
                ResponseBody body = response.body();
                if (body == null) {
                    callback.accept(null);
                    return;
                }
                Map<String, String> data = gson.fromJson(body.string(), new TypeToken<Map<String, String>>() {}.getType());
                if (data.containsKey("id")) {
                    UUID uuid = parseUUID(data.get("id"));
                    UUID_CACHE.put(playerName, uuid);
                    callback.accept(uuid);
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException ex) {
                callback.accept(null);
            }
        });
    }

    /**
     * Get a list of changes names for the given uuid
     *
     * The response is: name, timestamp (-1 if first name)
     * @param uuid - The {@link UUID} to get the changed names for
     * @return the changed names
     */
    public static Map<String, Long> getNameChangesSync(UUID uuid) {
        Map<String, Long> nameChanges = NAME_CHANGE_CACHE.getIfPresent(uuid);
        if (nameChanges == null) {
            nameChanges = new HashMap<>();
            try {
                Request request = new Request.Builder()
                        .url("https://api.mojang.com/user/profiles/" + uuid.toString().replaceAll("-", "") + "/names")
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (response.code() == 200) {
                        ResponseBody body = response.body();
                        if (body != null) {
                            List<Map<String, String>> data = gson.fromJson(body.string(), new TypeToken<List<Map<String, String>>>() {}.getType());
                            for (Map<String, String> map : data) {
                                nameChanges.put(map.get("name"), map.containsKey("changedToAt") ? Long.parseLong(map.get("changedToAt")) : -1L);
                            }
                            NAME_CHANGE_CACHE.put(uuid, nameChanges);
                        }
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return nameChanges;
    }

    /**
     * Get a list of changes names for the given uuid asynchronously
     * The response is: name, timestamp (-1 if first name)
     *
     * @param uuid - The {@link UUID} to get the changed names for
     * @param callback - The consumer that that will contain the list of changed names
     */
    public static void getNameChangesAsync(UUID uuid, Consumer<Map<String, Long>> callback) {
        Map<String, Long> nameChangeCache = NAME_CHANGE_CACHE.getIfPresent(uuid);
        if (nameChangeCache != null) {
            callback.accept(nameChangeCache);
            return;
        }
        Request request = new Request.Builder()
                .url("https://api.mojang.com/user/profiles/" + uuid.toString().replaceAll("-", "") + "/names")
                .build();
        Map<String, Long> nameChanges = new HashMap<>();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    callback.accept(nameChanges);
                    return;
                }
                ResponseBody body = response.body();
                if (body == null) {
                    callback.accept(nameChanges);
                    return;
                }
                List<Map<String, String>> data = gson.fromJson(body.string(), new TypeToken<List<Map<String, String>>>() {}.getType());
                for (Map<String, String> map : data) {
                    nameChanges.put(map.get("name"), map.containsKey("changedToAt") ? Long.parseLong(map.get("changedToAt")) : -1L);
                }
                NAME_CHANGE_CACHE.put(uuid, nameChanges);
                callback.accept(nameChanges);
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException ex) {
                callback.accept(nameChanges);
            }
        });
    }

    /**
     * Get the skin textures for the given {@link UUID}
     *
     * @param uuid - The uuid to get the textures for
     * @return the skin data
     */
    public static SkinData getSkinTexturesSync(UUID uuid) {
        SkinData skinData = SKIN_CACHE.getIfPresent(uuid);
        if (skinData == null) {
            skinData = new SkinData("", null);
            try {
                Request request = new Request.Builder()
                        .url("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replaceAll("-", "") + "?unsigned=false")
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (response.code() == 200) {
                        ResponseBody body = response.body();
                        if (body != null) {
                            JSONObject properties = (JSONObject) ((JSONArray) ((JSONObject) new JSONParser().parse(body.string())).get("properties")).get(0);
                            skinData.setValue((String) properties.get("value"));
                            skinData.setSignature((String) properties.get("signature"));
                            SKIN_CACHE.put(uuid, skinData);
                        }
                    }
                }
            } catch (IOException | ParseException ex) {
                ex.printStackTrace();
            }
        }
        return skinData;
    }

    /**
     * Get the skin textures for the given {@link UUID} asynchronously
     *
     * @param uuid - The uuid to get the textures for
     * @param callback - The consumer that that will contain the skin data
     */
    public static void getSkinTexturesAsync(UUID uuid, Consumer<SkinData> callback) {
        SkinData texturesCache = SKIN_CACHE.getIfPresent(uuid);
        if (texturesCache != null) {
            callback.accept(texturesCache);
            return;
        }
        SkinData skinData = new SkinData("", null);
        Request request = new Request.Builder()
                .url("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replaceAll("-", "") + "?unsigned=false")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    callback.accept(skinData);
                    return;
                }
                ResponseBody body = response.body();
                if (body != null) {
                    try {
                        JSONObject properties = (JSONObject) ((JSONArray) ((JSONObject) new JSONParser().parse(body.string())).get("properties")).get(0);
                        skinData.setValue((String) properties.get("value"));
                        skinData.setSignature((String) properties.get("signature"));
                        SKIN_CACHE.put(uuid, skinData);
                        callback.accept(skinData);
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException ex) {
                callback.accept(skinData);
            }
        });
    }

    /**
     * Parse a uuid without dashes to a proper {@link UUID}
     *
     * @param s - The un-parsed uuid
     * @return the uuid
     */
    private UUID parseUUID(String s) {
        return UUID.fromString(s.substring(0, 8) + "-" + s.substring(8, 12) + "-" + s.substring(12, 16) + "-" + s.substring(16, 20) + "-" + s.substring(20, 32));
    }

    @AllArgsConstructor @Getter
    public enum MojangService {
        MINECRAFT_WEBSITE("minecraft.net"),
        SESSION("session.minecraft.net"),
        ACCOUNT("account.mojang.com"),
        AUTH_SERVER("authserver.mojang.com"),
        SESSION_SERVER("sessionserver.mojang.com"),
        API("api.mojang.com"),
        TEXTURES("textures.minecraft.net"),
        MOJANG_WEBSITE("mojang.com");

        private final String path;

        /**
         * Get the Mojang service with the given path
         * @param path - The path
         * @return the Mojang service
         */
        @Nullable
        public static MojangService lookupFromPath(String path) {
            for (MojangService service : values()) {
                if (service.getPath().equals(path)) {
                    return service;
                }
            }
            return null;
        }
    }

    public enum ServiceStatus {
        GREEN, YELLOW, RED
    }

    @AllArgsConstructor @Setter @Getter
    public static class SkinData {
        private String value;
        @Nullable private String signature;
    }
}