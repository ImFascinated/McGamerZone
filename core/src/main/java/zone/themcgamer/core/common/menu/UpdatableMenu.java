package zone.themcgamer.core.common.menu;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

/**
 * @author Braydon
 */
@Getter
public abstract class UpdatableMenu extends Menu {
    private static final long DEFAULT_TIME = TimeUnit.SECONDS.toMillis(2L);

    private final long delay;
    private long lastUpdate;

    public UpdatableMenu(Player player, String title, MenuType type) {
        this(player, title, type, DEFAULT_TIME);
    }

    public UpdatableMenu(Player player, String title, MenuType type, long delay) {
        super(player, title, type);
        this.delay = delay;
    }

    public UpdatableMenu(Player player, String title, int rows, MenuType type) {
        this(player, title, rows, type, DEFAULT_TIME);
    }

    public UpdatableMenu(Player player, String title, int rows, MenuType type, long delay) {
        super(player, title, rows, type);
        this.delay = delay;
    }

    /**
     * Called when the menu is updated
     */
    public abstract void onUpdate();

    /**
     * This method is optional for this menu type
     */
    @Override
    protected void onOpen() {}

    protected void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}