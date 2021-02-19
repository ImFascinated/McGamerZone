package zone.themcgamer.core.command.impl;

import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.common.Style;

public class StoreCommand {
    @Command(name = "store", aliases = "buy", description = "Visit our webstore and support us", playersOnly = true)
    public void onCommand(CommandProvider command) {
        command.getPlayer().sendMessage(Style.main("Store", "&7Buy &aRanks&7, &eBundles&7, &dBoosters &7and &9&lmore&7 at: &dstore.mcgamerzone.net"));
    }
}
