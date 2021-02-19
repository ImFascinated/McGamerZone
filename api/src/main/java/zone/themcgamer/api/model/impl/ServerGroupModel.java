package zone.themcgamer.api.model.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import zone.themcgamer.api.model.IModel;
import zone.themcgamer.data.jedis.data.ServerGroup;

import java.util.HashMap;
import java.util.UUID;

/**
 * @author Braydon
 */
@RequiredArgsConstructor @Setter @Getter @ToString
public class ServerGroupModel implements IModel {
    private final String name;
    private final long memoryPerServer;
    private final String serverJar, templatePath, pluginJarName, worldPath, startupScript, privateAddress;
    private final UUID host;
    private final String game;
    private final int minPlayers, maxPlayers, minServers, maxServers;
    private final boolean kingdom, staticGroup;

    @Override
    public HashMap<String, Object> toMap() {
        return new HashMap<>() {{
            put("name", name);
            put("memoryPerServer", memoryPerServer);
            put("serverJar", serverJar);
            put("templatePath", templatePath);
            put("pluginJarName", pluginJarName);
            put("worldPath", worldPath);
            put("startupScript", startupScript);
            put("privateAddress", privateAddress);
            put("host", host);
            put("game", game);
            put("minPlayers", minPlayers);
            put("maxPlayers", maxPlayers);
            put("minServers", minServers);
            put("maxServers", maxServers);
            put("kingdom", kingdom);
            put("staticGroup", staticGroup);
        }};
    }

    public static ServerGroupModel fromServerGroup(ServerGroup serverGroup) {
        if (serverGroup == null)
            return null;
        return new ServerGroupModel(
                serverGroup.getName(),
                serverGroup.getMemoryPerServer(),
                serverGroup.getServerJar(),
                serverGroup.getTemplatePath(),
                serverGroup.getPluginJarName(),
                serverGroup.getWorldPath(),
                serverGroup.getStartupScript(),
                serverGroup.getPrivateAddress(),
                serverGroup.getHost(),
                serverGroup.getGame(),
                serverGroup.getMinPlayers(),
                serverGroup.getMaxPlayers(),
                serverGroup.getMinServers(),
                serverGroup.getMaxServers(),
                serverGroup.isKingdom(),
                serverGroup.isStaticGroup()
        );
    }
}