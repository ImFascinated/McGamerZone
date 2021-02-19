package zone.themcgamer.core.badSportSystem.menu;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import zone.themcgamer.common.TimeUtils;
import zone.themcgamer.common.Tuple;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.badSportSystem.*;
import zone.themcgamer.core.common.ItemBuilder;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.common.menu.Button;
import zone.themcgamer.core.common.menu.Menu;
import zone.themcgamer.core.common.menu.MenuType;
import zone.themcgamer.core.module.Module;
import zone.themcgamer.data.Rank;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Braydon
 */
public class BadSportMenu extends Menu {
    private static final String GUIDELINES_NODE = "Refer to the guidelines for info.";

    private final Account target;
    private String reason;
    private final boolean history, silent;

    public BadSportMenu(Player player, Account target, String reason, boolean history) {
        super(player, "BadSport ｜ " + target.getName(), 6, MenuType.CHEST);
        this.target = target;
        this.reason = reason.replace("-s", "");
        if (this.reason.isEmpty())
            history = true;
        this.history = history;
        silent = history || reason.toLowerCase().contains("-s");
    }

    @Override
    protected void onOpen() {
        BadSportSystem badSportSystem = Module.getModule(BadSportSystem.class);
        if (badSportSystem == null)
            return;
        Optional<BadSportClient> optionalBadSportClient = badSportSystem.lookup(target.getUuid());
        if (!optionalBadSportClient.isPresent())
            return;
        BadSportClient badSportClient = optionalBadSportClient.get();

        List<Punishment> punishments = new ArrayList<>(badSportClient.getPunishments());
        punishments.sort((a, b) -> Long.compare(b.getTimeIssued(), a.getTimeIssued()));

        // History Menu
        if (history) {
            int column = 1;
            int slot = 1;
            for (Punishment punishment : punishments) {
                set(column, slot++, getPunishmentButton(punishment));
                if (get(column, 7) != null) {
                    column++;
                    slot = 1;
                }
            }
            if (!reason.trim().isEmpty()) {
                set(5, 4, new Button(new ItemBuilder(XMaterial.RED_BED)
                        .setName("§c« Go Back").toItemStack(), event -> new BadSportMenu(player, target, reason, false).open()));
            }
            return;
        }
        Optional<Account> optionalAccount = AccountManager.fromCache(player.getUniqueId());
        if (!optionalAccount.isPresent())
            return;
        Account staffAccount = optionalAccount.get();

        // Player Head
        set(0, 4, new Button(new ItemBuilder(XMaterial.PLAYER_HEAD)
                .setSkullOwner(target.getName())
                .setName("§a§l" + target.getName()).setLore(
                        "§fReason: §7" + reason
                ).toItemStack()));

        // Left Buttons
        set(1, 0, new Button(new ItemBuilder(XMaterial.LEATHER_BOOTS)
                .setName("§a§lKick").setLore("§7" + GUIDELINES_NODE).toItemStack(), event -> {
            close();
            punish(PunishmentCategory.KICK, PunishmentOffense.KICK, 1, -1L);
        }));
        set(2, 0, new Button(new ItemBuilder(XMaterial.PAPER)
                .setName("§a§lWarning").setLore("§7" + GUIDELINES_NODE).toItemStack(), event -> {
            close();
            punish(PunishmentCategory.WARN, PunishmentOffense.WARNING, 1, -1L);
        }));
        if (staffAccount.hasRank(Rank.MODERATOR)) {
            set(3, 0, new Button(new ItemBuilder(XMaterial.WRITABLE_BOOK)
                    .setName("§a§lPermanent Mute").setLore("§7" + GUIDELINES_NODE).toItemStack(), event -> {
                close();
                punish(PunishmentCategory.MUTE, PunishmentOffense.PERMANENT_MUTE, 1, -1L);
            }));
        }
        if (staffAccount.hasRank(Rank.ADMIN)) {
            set(4, 0, new Button(new ItemBuilder(XMaterial.REDSTONE_BLOCK)
                    .setName("§a§lPermanent Ban").setLore("§7" + GUIDELINES_NODE).toItemStack(), event -> {
                close();
                punish(PunishmentCategory.BAN, PunishmentOffense.PERMANENT_BAN, 1, -1L);
            }));
        }

        // Offenses and Severities
        int slot = 2;
        for (PunishmentOffense offense : PunishmentOffense.values()) {
            if (offense.getSeverities() <= 0)
                continue;
            set(1, slot, new Button(new ItemBuilder(offense.getIcon(), 1, offense.getData())
                    .setName("§a§l" + offense.getName()).toItemStack()));
            int column = 2;

            for (int severity = 1; severity <= offense.getSeverities(); severity++) {
                if (severity == 2 && !staffAccount.hasRank(Rank.MODERATOR))
                    continue;
                if (severity == 3 && !staffAccount.hasRank(Rank.ADMIN))
                    continue;
                XMaterial icon = XMaterial.GREEN_STAINED_GLASS_PANE;
                ChatColor color = ChatColor.GREEN;
                if (severity == 2) { 
                    icon = XMaterial.YELLOW_STAINED_GLASS_PANE;
                    color = ChatColor.YELLOW;
                } else if (severity == 3) {
                    icon = XMaterial.RED_STAINED_GLASS_PANE;
                    color = ChatColor.RED;
                }
                Tuple<PunishmentCategory, Long> durationTuple = offense.calculatePunishmentDuration(badSportClient, severity);
                PunishmentCategory category = durationTuple.getLeft();
                long duration = durationTuple.getRight();
                int finalSeverity = severity;
                set(column++, slot, new Button(new ItemBuilder(icon, severity)
                        .setName(color.toString() + "§lSeverity " + severity).setLore(
                                "§fPast Offenses: §e" + badSportClient.getPastOffenses(offense).size(),
                                "§f" + category.getName() + " Duration: §e" + TimeUtils.convertString(duration),
                                "",
                                "§7" + GUIDELINES_NODE
                        ).toItemStack(), event -> {
                    close();
                    punish(category, offense, finalSeverity, duration);
                }));
            }
            slot+= 2;
        }

        // Right Column History
        for (int column = 0; column < Math.min(punishments.size(), punishments.size() > 6 ? 5 : 6); column++)
            set(column, 8, getPunishmentButton(punishments.get(column)));
        if (punishments.size() > 6) {
            int extra = punishments.size() - 6;
            set(5, 8, new Button(new ItemBuilder(XMaterial.OAK_SIGN)
                    .setName("§a§lMore History").setLore(
                            "",
                            "§7Click to view §6" + extra + " §7more punishment" + (extra == 1 ? "" : "s") + " on record"
                    ).toItemStack(), event -> new BadSportMenu(player, target, reason, true).open()));
        }
    }

    public void punish(PunishmentCategory category, PunishmentOffense offense, int severity, long duration) {
        BadSportSystem badSportSystem = Module.getModule(BadSportSystem.class);
        if (badSportSystem == null) {
            player.sendMessage(Style.error("Bad Sport", "Cannot issue punishment!"));
            throw new NullPointerException();
        }
        badSportSystem.punish(target.getEncryptedIpAddress(), target.getUuid(), target.getDisplayName(), category, offense, severity,
                player.getUniqueId(), player.getName(), duration, reason, silent);
    }

    private Button getPunishmentButton(Punishment punishment) {
        List<String> lore = new ArrayList<>();
        lore.add("§fPunishment Type: §e" + punishment.getOffense().getName());
        lore.add("§fSeverity: §e" + punishment.getSeverity());
        lore.add("§fStaff: §e" + punishment.getStaffName());
        lore.add("§fDate: §e" + TimeUtils.when(punishment.getTimeIssued()));
        if (punishment.getCategory() != PunishmentCategory.KICK && punishment.getCategory() != PunishmentCategory.WARN)
            lore.add("§fLength: §e" + TimeUtils.convertString(punishment.getDuration()));
        lore.add("");
        lore.add("§fReason: §e" + punishment.getReason());
        if (punishment.isActive()) {
            lore.add("");
            lore.add("§fExpires On: §e" + TimeUtils.when(System.currentTimeMillis() + punishment.getRemaining()) +
                    " (" + (punishment.isPermanent() ? "Permanent" : TimeUtils.convertString(punishment.getRemaining())) + ")");
        }
        if (punishment.wasRemoved() && !punishment.wasOverriden()) {
            lore.add("");
            lore.add("§fRemove Staff: §e" + punishment.getRemoveStaffName());
            lore.add("§fRemove Reason: §e" + punishment.getRemoveReason());
        }
        lore.add("");
        if (punishment.isActive()) {
            lore.add("§cClick to remove");
        } else lore.add("§aClick to reapply punishment");
        return new Button(new ItemBuilder(punishment.getOffense().getIcon(), 1, punishment.getOffense().getData())
                .setGlow(punishment.isActive())
                .setName("§a§l" + punishment.getOffense().getName())
                .setLore(lore).toItemStack(), event -> {
            if (punishment.getCategory() == PunishmentCategory.WARN)
                return;
            if (punishment.isActive()) {
                close();
                BadSportSystem badSportSystem = Module.getModule(BadSportSystem.class);
                if (badSportSystem != null)
                    badSportSystem.remove(punishment, target.getDisplayName(), player.getUniqueId(), player.getName(), reason, silent, false);
            } else {
                if (!reason.toLowerCase().contains("reapplied"))
                    reason = (reason.trim().isEmpty() ? punishment.getReason() : reason) + " - Reapplied";
                close();
                punish(punishment.getCategory(), punishment.getOffense(), punishment.getSeverity(),
                        (punishment.getDuration() == -1L ? -1L : punishment.getDuration() * 2));
            }
        });
    }
}