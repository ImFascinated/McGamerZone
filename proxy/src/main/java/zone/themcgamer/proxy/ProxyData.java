package zone.themcgamer.proxy;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Braydon
 */
@AllArgsConstructor @Getter
public class ProxyData {
    private final MOTD motd;
    private final boolean maintenance;
    private final TABLIST tablist;

    @AllArgsConstructor @Getter
    public static class MOTD {
        private final String header, text;
    }

    @AllArgsConstructor @Getter
    public static class TABLIST {
        private final String header, footer;
    }
}