package zone.themcgamer.hub.command;

import lombok.RequiredArgsConstructor;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.hub.Hub;

@RequiredArgsConstructor
public class SpawnCommand {
    private final Hub hub;

    @Command(name = "spawn", description = "Teleport you to the spawn", playersOnly = true)
    public void onCommand(CommandProvider command) {
        command.getPlayer().teleport(hub.getSpawn());
    }
}