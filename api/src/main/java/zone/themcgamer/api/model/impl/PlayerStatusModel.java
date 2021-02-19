package zone.themcgamer.api.model.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import zone.themcgamer.api.model.IModel;

import java.util.HashMap;
import java.util.UUID;

/**
 * @author Braydon
 */
@AllArgsConstructor @Setter @Getter @ToString
public class PlayerStatusModel implements IModel {
    private final UUID uuid;
    private final String playerName;
    private String server;
    private final long timeJoined;

    @Override
    public HashMap<String, Object> toMap() {
        return new HashMap<>() {{
            put("uuid", uuid);
            put("name", playerName);
            put("server", server);
            put("timeJoined", timeJoined);
        }};
    }
}