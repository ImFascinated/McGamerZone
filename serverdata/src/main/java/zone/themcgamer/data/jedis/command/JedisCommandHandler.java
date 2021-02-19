package zone.themcgamer.data.jedis.command;

import com.google.gson.Gson;
import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import zone.themcgamer.data.jedis.JedisConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Braydon
 */
public class JedisCommandHandler {
    @Getter private static JedisCommandHandler instance;
    private static final Gson GSON = new Gson();
    private static final boolean DEBUGGING = false;

    private final JedisPool pool;

    /**
     * A list of listeners that are called when a {@link JedisCommand} is received
     */
    private final List<Consumer<JedisCommand>> listeners = new ArrayList<>();

    /**
     * Starts a new thread to handle incoming commands over the network
     */
    public JedisCommandHandler() {
        instance = this;
        pool = new JedisPool(JedisConstants.HOST);
        new Thread(() -> {
            try (Jedis jedis = pool.getResource()) {
                jedis.auth(JedisConstants.AUTH);
                jedis.psubscribe(new JedisPubSub() {
                    @Override
                    public void onPMessage(String pattern, String channel, String message) {
                        if (DEBUGGING)
                            System.out.println("Received Redis command on channel \"" + channel + "\" with message \"" + message + "\"");
                        try {
                            String commandName = channel.split(":")[2];
                            Class<?> clazz = Class.forName(commandName);
                            Object commandObject = GSON.fromJson(message, clazz);
                            if (!(commandObject instanceof JedisCommand))
                                return;
                            for (Consumer<JedisCommand> listener : listeners)
                                listener.accept((JedisCommand) commandObject);
                        } catch (ClassNotFoundException ignored) {
                            // This is ignored as all servers on the network may not be up-to-date and will not have the same
                            // command classes
                        }
                    }
                }, "mcGamerZone:commands:*");
            }
        }, "Jedis Command Thread").start();
    }

    /**
     * Add a command listener
     * @param consumer The listener to add
     */
    public void addListener(Consumer<JedisCommand> consumer) {
        listeners.add(consumer);
    }

    /**
     * Send a {@link JedisCommand} across the network
     * @param command The command to send
     */
    public void send(JedisCommand command) {
        command.setTimeSent(System.currentTimeMillis());
        String className = command.getClass().getName();
        String json = GSON.toJson(command);
        if (DEBUGGING)
            System.out.println("Dispatching Redis command for class \"" + className + "\" with json \"" + json + "\"");
        try (Jedis jedis = pool.getResource()) {
            jedis.auth(JedisConstants.AUTH);
            jedis.publish("mcGamerZone:commands:" + className, json);
        }
    }
}