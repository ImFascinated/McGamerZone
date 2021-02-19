package zone.themcgamer.core.account.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.common.WrappedBukkitEvent;

/**
 * @author Braydon
 * @implNote This event is called when an {@link Account} is unloaded
 */
@AllArgsConstructor @Setter @Getter
public class AccountUnloadEvent extends WrappedBukkitEvent {
    private final Account account;
}