package zone.themcgamer.core.badSportSystem;

import com.zaxxer.hikari.HikariDataSource;
import zone.themcgamer.data.mysql.data.column.Column;
import zone.themcgamer.data.mysql.data.column.impl.IntegerColumn;
import zone.themcgamer.data.mysql.data.column.impl.LongColumn;
import zone.themcgamer.data.mysql.data.column.impl.StringColumn;
import zone.themcgamer.data.mysql.repository.MySQLRepository;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @author Braydon
 */
public class BadSportRepository extends MySQLRepository {
    private static final String INSERT_PUNISHMENT = "INSERT INTO `punishments` " +
            "(`id`, `targetIp`, `targetUuid`, `category`, `offense`, `severity`, `staffUuid`, `staffName`, `timeIssued`, `duration`, `reason`, `removeStaffUuid`, `removeStaffName`, `removeReason`, `timeRemoved`) VALUES " +
            "(NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, NULL, NULL, '-1');";
    private static final String REMOVE_PUNISHMENT = "UPDATE `punishments` SET `removeStaffUuid` = ?, `removeStaffName` = ?, `removeReason` = ?, `timeRemoved` = ? WHERE `id` = ?;";

    public BadSportRepository(HikariDataSource dataSource) {
        super(dataSource);
    }

    /**
     * Post a {@link Punishment} to MySQL
     *
     * @param punishment the punishment
     */
    public void punish(Punishment punishment, Consumer<Integer> idConsumer) {
        punish(
                punishment.getTargetIp(),
                punishment.getTargetUuid(),
                punishment.getCategory(),
                punishment.getOffense(),
                punishment.getSeverity(),
                punishment.getStaffUuid(),
                punishment.getStaffName(),
                punishment.getTimeIssued(),
                punishment.getDuration(),
                punishment.getReason(),
                idConsumer
        );
    }

    /**
     * Post a {@link Punishment} to MySQL
     *
     * @param encryptedIpAddress the target encrypted ip of the punishment
     * @param uuid the target uuid of the punishment
     * @param category the category of the punishment
     * @param offense the offense of the punishment
     * @param staffUuid the staff uuid of the punishment
     * @param staffName the staff name of the punishment
     * @param timeIssued the time the punishment was issued
     * @param duration the duration of the punishment
     * @param reason the reason of the punishment
     */
    public void punish(String encryptedIpAddress, UUID uuid, PunishmentCategory category, PunishmentOffense offense, int severity,
                       UUID staffUuid, String staffName, long timeIssued, long duration, String reason, Consumer<Integer> idConsumer) {
        CompletableFuture.runAsync(() -> {
            executeInsert(INSERT_PUNISHMENT, new Column[] {
                    new StringColumn("targetIp", encryptedIpAddress),
                    new StringColumn("targetUuid", uuid.toString()),
                    new StringColumn("category", category.name()),
                    new StringColumn("offense", offense.name()),
                    new IntegerColumn("severity", severity),
                    new StringColumn("staffUuid", staffUuid.toString()),
                    new StringColumn("staffName", staffName),
                    new LongColumn("timeIssued", timeIssued),
                    new LongColumn("duration", duration),
                    new StringColumn("reason", reason)
            }, resultSet -> {
                try {
                    while (resultSet.next()) {
                        idConsumer.accept(resultSet.getInt(1));
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });
        });
    }

    public void remove(Punishment punishment) {
        CompletableFuture.runAsync(() -> {
            executeInsert(REMOVE_PUNISHMENT, new Column[] {
                    new StringColumn("removeStaffUuid", punishment.getRemoveStaffUuid() == null ? null : punishment.getRemoveStaffUuid().toString()),
                    new StringColumn("removeStaffName", punishment.getRemoveStaffName() == null ? null : punishment.getRemoveStaffName()),
                    new StringColumn("removeReason", punishment.getRemoveReason() == null ? null : punishment.getRemoveReason()),
                    new LongColumn("timeRemoved", punishment.getTimeRemoved()),
                    new IntegerColumn("id", punishment.getId())
            });
        });
    }
}