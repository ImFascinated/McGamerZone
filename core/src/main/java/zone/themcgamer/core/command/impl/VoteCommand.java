package zone.themcgamer.core.command.impl;

import lombok.AllArgsConstructor;
import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;

/**
 * @author Nicholas
 */
@AllArgsConstructor
public class VoteCommand {
    @Command(name = "vote", description = "Vote for McGamerZone", playersOnly = true)
    public void onCommand(CommandProvider command) {
        command.getPlayer().sendMessage(Style.main("Vote", "Vote for McGamerZone at §dhttps://vote.mcgamerzone.net§7."));
    }
}