package zone.themcgamer.api.model.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import zone.themcgamer.api.model.IModel;
import zone.themcgamer.data.jedis.data.Node;

import java.util.HashMap;

/**
 * @author Braydon
 */
@AllArgsConstructor @Getter @ToString
public class NodeModel implements IModel {
    private final String name, address, portRange;

    @Override
    public HashMap<String, Object> toMap() {
        return new HashMap<>() {{
            put("name", name);
            put("address", address);
            put("portRange", portRange);
        }};
    }

    public static NodeModel fromNode(Node node) {
        if (node == null)
            return null;
        return new NodeModel(
                node.getName(),
                node.getAddress(),
                node.getPortRange()
        );
    }
}