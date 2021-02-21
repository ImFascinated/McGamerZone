package zone.themcgamer.core.twoFactor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.concurrent.TimeUnit;

/**
 * @author Braydon
 */
@NoArgsConstructor @Setter @Getter
public class TwoFactorClient {
    private String secretKey;
    private long lastAuthentication;

    public boolean requiresAuthentication() {
        return secretKey == null || (System.currentTimeMillis() - lastAuthentication) >= TimeUnit.DAYS.toMillis(1L);
    }
}