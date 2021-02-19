package zone.themcgamer.core.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.Arrays;

/**
 * @author Braydon
 */
@AllArgsConstructor @Getter
public enum ServerVersion {
    v1_8_R1(new String[] { "1.8", "1.8.1", "1.8.2" }, false),
    v1_8_R2(new String[] { "1.8.3" }, false),
    v1_8_R3(new String[] { "1.8.4", "1.8.5", "1.8.6", "1.8.7", "1.8.8", "1.8.9" }, false),
    v1_9_R1(new String[] { "1.9", "1.9.1", "1.9.2", "1.9.3" }, false),
    v1_9_R2(new String[] { "1.9.4" }, false),
    v1_10_R1(new String[] { "1.10", "1.10.1", "1.10.2" }, false),
    v1_11_R1(new String[] { "1.11", "1.11.1", "1.11.2" }, false),
    v1_12_R1(new String[] { "1.12", "1.12.1", "1.12.2" }, false),
    v1_13_R1(new String[] { "1.13" }, true),
    v1_13_R2(new String[] { "1.13.1", "1.13.2" }, true),
    v1_14_R1(new String[] { "1.14", "1.14.1", "1.14.2", "1.14.3" }, true),
    v1_14_R2(new String[] { "1.14.4" }, true),
    v1_15_R1(new String[] { "1.15", "1.15.1", "1.15.2" }, true),
    v1_16_R1(new String[] { "1.16.1" }, true),
    v1_16_R2(new String[] { "1.16.2", "1.16.3" }, true),
    v1_16_R3(new String[] { "1.16.4", "1.16.5" }, true);

    public static String NMS_VERSION;
    @Getter private static ServerVersion version;

    static {
        NMS_VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];;
        String bukkitVersion = Bukkit.getVersion().replace("(MC: ", "").replace(")", "").split(" ")[1].trim();
        ServerVersion.version = Arrays.stream(values())
                .filter(serverVersion -> Arrays.asList(serverVersion.getNames()).contains(bukkitVersion))
                .findFirst().orElse(null);
    }

    private final String[] names;
    private final boolean nativeVersion;

    public boolean isLegacy() {
        return !nativeVersion;
    }
}