package zone.themcgamer.core.twoFactor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

/**
 * @author Braydon
 */
@NoArgsConstructor @Setter @Getter
public class TwoFactorClient {
    private String secretKey;
    private long lastAuthentication;

    // This is not stored in MySQL, this is just a copy of the item in the first slot of the player's inventory
    // when they join. We save this item as we place the QR map in that slot and we give them the item when they
    // authenticate
    private ItemStack firstSlotItem;

    public boolean requiresAuthentication() {
        return secretKey == null || (System.currentTimeMillis() - lastAuthentication) >= TimeUnit.DAYS.toMillis(1L);
    }
}