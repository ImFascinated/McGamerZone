package zone.themcgamer.core.server.command;

import lombok.AllArgsConstructor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.entity.Player;
import zone.themcgamer.common.MiscUtils;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.plugin.MGZPlugin;
import zone.themcgamer.core.traveler.ServerTraveler;
import zone.themcgamer.data.jedis.data.server.MinecraftServer;
import zone.themcgamer.data.jedis.repository.impl.MinecraftServerRepository;

import java.util.Optional;

/**
 * @author Braydon
 * TODO: Make it so you can do /join <game> and join the best server available for that game
 */
@AllArgsConstructor
public class ServerCommand {
    private final ServerTraveler traveler;
    private final MinecraftServerRepository minecraftServerRepository;

    @Command(name = "server", aliases = { "join", "play" }, description = "Join a server", playersOnly = true)
    public void onCommand(CommandProvider command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();
        MinecraftServer currentServer = MGZPlugin.getMinecraftServer();
        if (args.length < 1) {
            player.sendMessage(new ComponentBuilder(Style.main("Server", "You're currently on &6" + currentServer.getName()))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MiscUtils.arrayToString(
                            Style.color("&7Server Id: &6" + currentServer.getId()),
                            Style.color("&7Type: &6" + currentServer.getGroup().getName())
                    )).create())).create());
            return;
        }
        Optional<MinecraftServer> optionalServer = minecraftServerRepository
                .lookup(minecraftServer -> minecraftServer.getName().equalsIgnoreCase(args[0]));
        if (optionalServer.isEmpty()) {
            player.sendMessage(Style.error("Server", "&7A server with that name doesn't exist!"));
            return;
        }
        MinecraftServer minecraftServer = optionalServer.get();
        if (minecraftServer.equals(currentServer)) {
            player.sendMessage(new ComponentBuilder(Style.main("Server", "You're already connected to &6" + currentServer.getName()))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MiscUtils.arrayToString(
                            Style.color("&7Server Id: &6" + currentServer.getId()),
                            Style.color("&7Type: &6" + currentServer.getGroup().getName())
                    )).create())).create());
            return;
        }
        try {
            traveler.sendPlayer(player, minecraftServer);
        } catch (Exception ex) {
            player.sendMessage(Style.error("Server", "&7Cannot join &6" + minecraftServer.getName() + " &7at this time: &b" + ex.getLocalizedMessage()));
        }
    }
}