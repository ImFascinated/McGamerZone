package zone.themcgamer.api;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import spark.Spark;
import zone.themcgamer.api.model.IModel;
import zone.themcgamer.api.model.ModelSerializer;
import zone.themcgamer.api.model.impl.*;
import zone.themcgamer.api.repository.AccountRepository;
import zone.themcgamer.api.route.AccountRoute;
import zone.themcgamer.api.route.ChatRoute;
import zone.themcgamer.api.route.PlayerStatusRoute;
import zone.themcgamer.api.route.ServersRoute;
import zone.themcgamer.data.APIAccessLevel;
import zone.themcgamer.data.jedis.JedisController;
import zone.themcgamer.data.jedis.data.APIKey;
import zone.themcgamer.data.jedis.repository.RedisRepository;
import zone.themcgamer.data.jedis.repository.impl.APIKeyRepository;
import zone.themcgamer.data.mysql.MySQLController;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Braydon
 */
public class API {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(IModel.class, new ModelSerializer())
            .serializeNulls()
            .setPrettyPrinting()
            .create();
    private static final List<Class<? extends IModel>> models = new ArrayList<>();
    private static final Map<Object, List<Method>> routes = new HashMap<>();
    private static final boolean requiresAuthentication = true;

    // Rate Limiting
    private static final int rateLimit = 120; // The amount of requests a minute
    private static final Cache<String, Integer> requests = CacheBuilder.newBuilder() // The logged requests (ip, count)
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

    public static void main(String[] args) {
        // Initializing Redis
        new JedisController().start();

        // Initializing MySQL and Repositories
        MySQLController mySQLController = new MySQLController(false);
        AccountRepository accountRepository = new AccountRepository(mySQLController.getDataSource());

        // Adding models
        addModel(MinecraftServerModel.class);
        addModel(NodeModel.class);
        addModel(ServerGroupModel.class);
        addModel(AccountModel.class);
        addModel(PlayerStatusModel.class);

        // Adding the routes
        addRoute(new ServersRoute());
        addRoute(new AccountRoute(accountRepository));
        addRoute(new PlayerStatusRoute());
        addRoute(new ChatRoute());

        // 404 Handling
        Spark.notFound((request, response) -> {
            response.type("application/json");
            HashMap<String, String> map = new HashMap<>();
            map.put("success", "false");
            map.put("error", "Not found");

            System.err.println("Requested url not found: " + request.pathInfo());

            return gson.toJson(map);
        });

        // Handling the routes
        APIKeyRepository apiKeyRepository = RedisRepository.getRepository(APIKeyRepository.class).orElse(null);
        if (apiKeyRepository == null)
            throw new NullPointerException();
        for (Map.Entry<Object, List<Method>> entry : routes.entrySet()) {
            for (Method method : entry.getValue()) {
                RestPath restPath = method.getAnnotation(RestPath.class);
                String path = "/" + restPath.version().getName() + restPath.path();
                System.out.println("Registered Path: " + path + " (accessLevel = " + restPath.accessLevel().name() + ")");
                Spark.get(path, (request, response) -> {
                    response.type("application/json");

                    JsonObject jsonObject = new JsonObject();
                    try {
                        Integer requestCount = requests.getIfPresent(request.ip());
                        if (requestCount == null)
                            requestCount = 0;
                        int remaining = Math.max(rateLimit - requestCount, 0);

                        // Display the rate limit and the remaining request count using headers
                        response.header("x-ratelimit-limit", "" + rateLimit);
                        response.header("x-ratelimit-remaining", "" + remaining);

                        // If the rate limit has been exceeded, set the status code to 429 (Too Many Requests) and display an error
                        if (++requestCount > rateLimit) {
                            System.out.println("Incoming request from \"" + request.ip() + "\" using path \"" + request.pathInfo() + "\" was rate limited");
                            response.status(429);
                            throw new APIException("Rate limit exceeded");
                        }
                        requests.put(request.ip(), requestCount);

                        System.out.println("Handling incoming request from \"" + request.ip() + "\" using path \"" + request.pathInfo() + "\"");

                        APIKey key = new APIKey(UUID.randomUUID().toString(), APIAccessLevel.STANDARD);
                        // If authentication is enabled, handle the api key checking and display a Unauthorized error
                        // if there is a problem checking the key or the key is invalid
                        if (requiresAuthentication) {
                            String apiKey = request.headers("key");
                            if (apiKey == null) {
                                response.status(401);
                                throw new APIException("Unauthorized");
                            }
                            // Check if the provided API key is valid
                            Optional<List<APIKey>> keys = apiKeyRepository.lookup(apiKey);
                            if (keys.isEmpty() || (keys.get().isEmpty())) {
                                response.status(401);
                                throw new APIException("Unauthorized");
                            }
                            key = keys.get().get(0);
                            if (restPath.accessLevel() == APIAccessLevel.DEV && (restPath.accessLevel() != key.getAccessLevel())) {
                                response.status(401);
                                throw new APIException("Unauthorized");
                            }
                        }
                        // Checking if the request has the appropriate headers for the get route
                        if (restPath.headers().length > 0) {
                            for (String header : restPath.headers()) {
                                if (!request.headers().contains(header)) {
                                    throw new APIException("Inappropriate headers");
                                }
                            }
                        }
                        if (method.getParameterTypes().length != 3)
                            throw new APIException("Invalid route defined");
                        Object object = method.invoke(entry.getKey(), request, response, key);
                        jsonObject.addProperty("success", "true"); // Marking the request as successful in the JsonObject

                        // Format the Object returned from the route accordingly
                        if (object instanceof IModel) {
                            if (models.contains(object.getClass()))
                                jsonObject.add("value", gson.toJsonTree(((IModel) object).toMap(), HashMap.class));
                            else throw new APIException("Undefined model: " + object.toString());
                        } else if (object instanceof ArrayList)
                            jsonObject.add("value", gson.toJsonTree(object, ArrayList.class));
                        else if (object instanceof HashMap)
                            jsonObject.add("value", gson.toJsonTree(object, HashMap.class));
                        else jsonObject.addProperty("value", object.toString());
                    } catch (Throwable ex) {
                        // If there is an error thrown from a route, we wanna fetch the error from the invoke method
                        // and get the cause of the exception
                        if (ex instanceof InvocationTargetException)
                            ex = ex.getCause();
                        String message = ex.getLocalizedMessage();
                        // If the exception has no message associated with it, display the type of exception instead
                        if (message == null || (message.trim().isEmpty()))
                            message = ex.getClass().getSimpleName();
                        jsonObject.addProperty("success", "false");
                        jsonObject.addProperty("error", message);
                        // If the caught exception is not an API exception, print the stacktrace
                        if (!(ex instanceof APIException)) {
                            response.status(500);
                            System.err.println("The route \"" + entry.getKey().getClass().getSimpleName() + "\" raised an exception:");
                            ex.printStackTrace();
                        } else if (response.status() == 200)
                            response.status(502);
                    }
                    return gson.toJson(jsonObject);
                });
            }
        }
    }

    /**
     * Adding a {@link IModel}
     *
     * @param modelClass the class of the model to add
     */
    private static void addModel(Class<? extends IModel> modelClass) {
        models.add(modelClass);
    }

    /**
     * Adding a route
     *
     * @param object the instance of the route to add
     */
    private static void addRoute(Object object) {
        List<Method> methods = routes.getOrDefault(object, new ArrayList<>());
        for (Method method : object.getClass().getMethods()) {
            if (method.isAnnotationPresent(RestPath.class)) {
                methods.add(method);
            }
        }
        routes.put(object, methods);
    }
}