package zone.themcgamer.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Braydon
 * @implNote This class holds data for the current build
 */
@AllArgsConstructor @Getter @ToString
public class BuildData {
    @Getter private static BuildData build;

    static {
        InputStream inputStream = BuildData.class.getClassLoader().getResourceAsStream("git.properties");
        if (inputStream != null) {
            try {
                Properties properties = new Properties();
                properties.load(inputStream);
                build = new BuildData(
                        properties.getProperty("git.branch"),
                        properties.getProperty("git.build.host"),
                        properties.getProperty("git.build.user.email"),
                        properties.getProperty("git.build.user.name"),
                        properties.getProperty("git.build.version"),
                        properties.getProperty("git.commit.id"),
                        properties.getProperty("git.commit.id.abbrev"),
                        properties.getProperty("git.commit.message.full"),
                        properties.getProperty("git.commit.message.short"),
                        properties.getProperty("git.commit.time"),
                        properties.getProperty("insane_module")
                );
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private final String branch, host, email, username, version, commitId, commitIdAbbreviation,
            commitMessageFull, commitMessageShort, time, module;
}