package zone.themcgamer.core.common;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Braydon
 * @implNote Easy to use wrapped Bukkit {@link Event}.
 */
public class WrappedBukkitEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}