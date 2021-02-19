package zone.themcgamer.core.badSportSystem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import zone.themcgamer.common.MiscUtils;
import zone.themcgamer.common.TimeUtils;

/**
 * @author Braydon
 */
@AllArgsConstructor @RequiredArgsConstructor @Getter
public enum PunishmentCategory {
    KICK("Kick", "kicked", null, false, true, false, MiscUtils.arrayToString(
            "§2§lMc§6§lGamer§c§lZone §8» §a§lPunishment",
            "",
            "§7You have been §c§lKicked§7!",
            "§7Reason: §a$reason",
            "",
            "§7Kicked by:",
            "§c$staff"
    )),
    WARN("Warn", "warned", null, false, false, false, "You were warned by §6$staff §7for §f$reason"),
    MUTE("Mute", "muted", "unmuted", false, false, true,
            "You were muted by §6$staff §7for §b$duration §7because of §f$reason",
            "You were permanently muted by §6$staff §7because of §f$reason"
    ),
    BAN("Ban", "banned", "unbanned", false, true, true, MiscUtils.arrayToString(
            "§2§lMc§6§lGamer§c§lZone §8» §a§lPunishment",
            "",
            "§7You have been §c§lBanned§7!",
            "§7Reason: §a$reason",
            "",
            "§7Release date:",
            "§6$time",
            "",
            "§7Banned by:",
            "§c$staff",
            "",
            "§7Expires in:",
            "§6$duration",
            "",
            "§7If you believe this is an §6error§7, please",
            "§6create §7a support ticket on our §6website",
            "§bwww.mcgamerzone.net"
    ), MiscUtils.arrayToString(
            "§2§lMc§6§lGamer§c§lZone §8» §a§lPunishment",
            "",
            "§7You have been §c§lBanned§7!",
            "§7Reason: §a$reason",
            "",
            "§7Release date:",
            "§6$time",
            "",
            "§7Banned by:",
            "§c$staff",
            "",
            "§c§lThis punishment is permanent!",
            "§7If you believe this is an §6error§7, please",
            "§6create §7a support ticket on our §6website",
            "§bwww.mcgamerzone.net"
    )),
    BLACKLIST("Blacklist", "blacklisted", "unblacklisted", true, true, true, null, MiscUtils.arrayToString(
            "§2§lMc§6§lGamer§c§lZone §8» §a§lPunishment",
            "",
            "§7You have been §c§lBlacklisted§7!",
            "§7Reason: §a$reason",
            "",
            "§7Release date:",
            "§6$time",
            "",
            "§7Banned by:",
            "§c$staff",
            "",
            "§c§lThis punishment is permanent!",
            "§7If you believe this is an §6error§7, please",
            "§6create §7a support ticket on our §6website",
            "§bwww.mcgamerzone.net"
    ));

    private final String name, issuedMessage, removedMessage;
    private final boolean ip, kick, hasDuration;
    private String temporaryMessage;
    private final String permanentMessage;

    public static String format(Punishment punishment) {
        PunishmentCategory category = punishment.getCategory();
        String message;
        if (category.getTemporaryMessage() == null)
            message = category.getPermanentMessage();
        else message = punishment.isPermanent() ? category.getPermanentMessage() : category.getTemporaryMessage();
        message = message.replace("$staff", punishment.getStaffName());
        message = message.replace("$time", TimeUtils.when(punishment.getTimeIssued()));
        message = message.replace("$duration", TimeUtils.formatIntoDetailedString(punishment.getRemaining(), false));
        message = message.replace("$reason", punishment.getReason());
        return message;
    }
}