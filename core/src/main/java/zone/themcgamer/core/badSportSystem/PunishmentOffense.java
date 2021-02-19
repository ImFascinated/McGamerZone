package zone.themcgamer.core.badSportSystem;

import com.cryptomorin.xseries.XMaterial;
import lombok.AllArgsConstructor;
import lombok.Getter;
import zone.themcgamer.common.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Braydon
 */
@AllArgsConstructor @Getter
public enum PunishmentOffense {
    KICK("Kick", XMaterial.LEATHER_BOOTS, (byte) 0, 0, new Tuple[] {}),
    WARNING("Warning", XMaterial.PAPER, (byte) 0, 0, new Tuple[] {}),
    PERMANENT_MUTE("Permanent Mute", XMaterial.WRITABLE_BOOK, (byte) 0, 0, new Tuple[] {}),
    PERMANENT_BAN("Permanent Ban", XMaterial.REDSTONE_BLOCK, (byte) 0, 0, new Tuple[] {}),
    CHAT("Chat", XMaterial.WRITABLE_BOOK, (byte) 0, 3, new Tuple[] {
            new Tuple<>(PunishmentCategory.MUTE, TimeUnit.SECONDS.toMillis(20L))
    }),
    GAMEPLAY("Gameplay", XMaterial.HOPPER, (byte) 0, 1, new Tuple[] {
            new Tuple<>(PunishmentCategory.MUTE, TimeUnit.SECONDS.toMillis(20L))
    }),
    HACKING("Hacking", XMaterial.IRON_SWORD, (byte) 0, 3, new Tuple[] {
            new Tuple<>(PunishmentCategory.BAN, TimeUnit.DAYS.toMillis(7L)),
            new Tuple<>(PunishmentCategory.BAN, TimeUnit.DAYS.toMillis(30L)),
            new Tuple<>(PunishmentCategory.BAN, -1L),
    });

    private final String name;
    private final XMaterial icon;
    private final byte data;
    private final int severities;
    private final Tuple<PunishmentCategory, Long>[] durations;

    public Tuple<PunishmentCategory, Long> calculatePunishmentDuration(BadSportClient client, int severiy) {
        List<Punishment> pastPunishments = new ArrayList<>(client.getPastOffenses(this));
        long lastDuration = durations[0].getRight();
        if (!pastPunishments.isEmpty()) { // If the last duration is -1, loop through the punishments and try and find the last duration for this offense
            pastPunishments.sort((a, b) -> Long.compare(b.getTimeIssued(), a.getTimeIssued()));
            lastDuration = pastPunishments.get(0).getDuration();
        }
        Tuple<PunishmentCategory, Long> durationTuple;
        if (pastPunishments.size() < durations.length) {
            durationTuple = durations[pastPunishments.size()];
            if (severiy > 1 && (severiy <= durations.length))
                durationTuple = durations[severiy - 1];
            else if (severiy > 1) durationTuple.setRight(durationTuple.getRight() + calculatePunishmentDuration(client, severiy - 1).getRight());
        } else {
            durationTuple = durations[durations.length - 1].clone();
            long duration = lastDuration;
            if (lastDuration != -1L)
                duration = lastDuration * 2L;
            durationTuple.setRight(duration * severiy);
        }
        return durationTuple;
    }
}