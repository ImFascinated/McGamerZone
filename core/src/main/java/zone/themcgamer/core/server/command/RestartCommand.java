package zone.themcgamer.core.server.command;

import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.server.ServerManager;
import zone.themcgamer.data.Rank;
import zone.themcgamer.data.jedis.data.ServerGroup;
import zone.themcgamer.data.jedis.data.server.MinecraftServer;
import zone.themcgamer.data.jedis.repository.impl.MinecraftServerRepository;
import zone.themcgamer.data.jedis.repository.impl.ServerGroupRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author Braydon
 */
@AllArgsConstructor
public class RestartCommand {
    private final ServerManager serverManager;
    private final ServerGroupRepository serverGroupRepository;
    private final MinecraftServerRepository minecraftServerRepository;

    @Command(name = "restart", aliases = { "reboot" }, description = "Restart a server or server group", ranks = { Rank.ADMIN })
    public void onCommand(CommandProvider command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();
        if (args.length < 1) {
            sender.sendMessage(Style.main("Server", "You must provide a server or server group to restart"));
            return;
        }
        Set<MinecraftServer> toRestart = new HashSet<>();
        Optional<ServerGroup> optionalServerGroup = serverGroupRepository.lookup(args[0]);
        optionalServerGroup.ifPresent(serverGroup -> toRestart.addAll(serverGroup.getServers()));
        if (toRestart.isEmpty()) {
            minecraftServerRepository.lookup(minecraftServer -> minecraftServer.getId().equals(args[0])
                    || minecraftServer.getName().equalsIgnoreCase(args[0]))
                    .ifPresent(toRestart::add);
        }
        // Static servers can't be restarted via this command as they may not come back online
        toRestart.removeIf(minecraftServer -> minecraftServer.getGroup().isStaticGroup());
        if (toRestart.isEmpty()) {
            sender.sendMessage(Style.error("Server", "&7Could not find any servers to restart!"));
            return;
        }
        for (MinecraftServer minecraftServer : toRestart)
            serverManager.restart(minecraftServer);
        sender.sendMessage(Style.main("Server", "Restarting &6" + toRestart.size() + " server" + (toRestart.size() == 1 ? "" : "s")));
    }
}