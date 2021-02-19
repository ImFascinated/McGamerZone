package zone.themcgamer.controller;

import lombok.Getter;
import lombok.Setter;
import zone.themcgamer.data.jedis.data.ServerGroup;

/**
 * @author Braydon
 */
@Setter @Getter
public class ServerGroupCreator {
    private String name;
    private long memoryPerServer = -1L;
    private String templatePath, pluginJarName, worldPath, startupScript, privateAddress;
    private boolean staticGroup;

    public ServerGroup build() {
        return new ServerGroup(
                name,
                memoryPerServer,
                "server.jar",
                templatePath,
                pluginJarName,
                worldPath,
                startupScript,
                privateAddress,
                null,
                "",
                1,
                20,
                1,
                1,
                false,
                staticGroup
        );
    }
}