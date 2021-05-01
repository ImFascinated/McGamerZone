package zone.themcgamer.core.announce;

import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.messages.ActionBar;
import com.destroystokyo.paper.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.module.Module;
import zone.themcgamer.core.module.ModuleInfo;
import zone.themcgamer.data.jedis.command.JedisCommandHandler;
import zone.themcgamer.data.jedis.command.impl.announce.AnnounceCommand;
import zone.themcgamer.data.jedis.command.impl.announce.AnnounceType;

/**
 * @author Nicholas
 */
@ModuleInfo(name = "Announce Manager")
public class AnnounceManager extends Module {
    public AnnounceManager(JavaPlugin plugin) {
        super(plugin);
        registerCommand(new zone.themcgamer.core.announce.command.AnnounceCommand(this));
        JedisCommandHandler.getInstance().addListener(command -> {
            if (command instanceof AnnounceCommand) {
                AnnounceCommand announceCommand = (AnnounceCommand) command;
                AnnounceType type = announceCommand.getType();
                boolean sendTitle = type == AnnounceType.TITLE || type == AnnounceType.ALL;
                boolean sendActionBar = type == AnnounceType.ACTION_BAR || type == AnnounceType.ALL;
                boolean sendChat = type == AnnounceType.CHAT || type == AnnounceType.ALL;
                if (sendTitle) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendTitle(new Title(
                                "§a§lAnnouncement",
                                "§7" + Style.color(announceCommand.getMessage()),
                                20, 3 * 20, 20));
                    }
                }
                if (sendActionBar) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ActionBar.sendActionBar(player, "§a§lAnnouncement §8» §7" + Style.color(announceCommand.getMessage()));
                    }
                }
                if (sendChat) {
                    Bukkit.broadcastMessage(Style.main("Announcement", announceCommand.getMessage()));
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(player.getEyeLocation(), XSound.BLOCK_NOTE_BLOCK_PLING.parseSound(), 0.9f, 1f);
                }
            }
        });
    }

    /**
     * Send a server-wide announcement
     *
     * @param type the {@link AnnounceType} to use for the announcement
     * @param message the message that will be sent in the announcement
     */
    public void sendAnnouncement(AnnounceType type, String message) {
        JedisCommandHandler.getInstance().send(new AnnounceCommand(type, message));
    }
}