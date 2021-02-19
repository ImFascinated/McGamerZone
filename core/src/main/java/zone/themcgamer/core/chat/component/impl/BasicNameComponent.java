package zone.themcgamer.core.chat.component.impl;

import net.md_5.bungee.api.chat.*;
import org.bukkit.entity.Player;
import zone.themcgamer.common.MiscUtils;
import zone.themcgamer.core.chat.component.IChatComponent;
import zone.themcgamer.core.common.Style;

/**
 * @author Braydon
 */
public class BasicNameComponent implements IChatComponent {
    @Override
    public BaseComponent getComponent(Player player) {
        ComponentBuilder componentBuilder = new ComponentBuilder("§7" + player.getName());
        componentBuilder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MiscUtils.arrayToString(
                Style.color("&7Player: &6" + player.getName()),
                Style.color("&aClick to message me!"))).create()));
        componentBuilder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + player.getName() + " (message)")).create();
        return new TextComponent(componentBuilder.create());
    }
}