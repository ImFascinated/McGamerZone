package zone.themcgamer.core.chat.component;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

/**
 * @author Braydon
 */
public interface IChatComponent {
    BaseComponent getComponent(Player player);
}