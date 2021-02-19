package zone.themcgamer.data.jedis.repository.impl;

import lombok.SneakyThrows;
import zone.themcgamer.data.jedis.JedisController;
import zone.themcgamer.data.jedis.data.Node;
import zone.themcgamer.data.jedis.repository.RedisRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Braydon
 * @implNote This class serves the purpose of fetching all {@link Node}'s
 *           from Redis
 */
public class NodeRepository extends RedisRepository<String, Node> {
    public NodeRepository(JedisController controller) {
        super(controller, "node:*");
    }

    @Override @SneakyThrows
    public Optional<Node> lookup(String name) {
        while (getCached().isEmpty()) {
            updateCache();
            Thread.sleep(10L);
        }
        return getCached().stream().filter(node -> node.getName().equalsIgnoreCase(name)).findFirst();
    }

    @Override
    public String getKey(Node node) {
        return "node:" + node.getName();
    }

    @Override
    public Optional<Node> fromMap(Map<String, String> map) {
        return Optional.of(new Node(
                map.get("name"),
                map.get("address"),
                map.get("portRange")
        ));
    }

    @Override
    public long getExpiration(Node node) {
        return -1;
    }

    @Override
    public Map<String, Object> toMap(Node node) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", node.getName());
        data.put("address", node.getAddress());
        data.put("portRange", node.getPortRange());
        return data;
    }
}
