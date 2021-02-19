package zone.themcgamer.core.account.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.common.WrappedBukkitEvent;

import java.util.UUID;

/**
 * @author Braydon
 * @implNote This event is called when an {@link Account} is finished loading
 */
@AllArgsConstructor @Setter @Getter
public class AccountPreLoadEvent extends WrappedBukkitEvent {
    private final UUID uuid;
    private final String name, ipAddress;
}