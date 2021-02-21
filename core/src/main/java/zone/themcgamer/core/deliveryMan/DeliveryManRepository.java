package zone.themcgamer.core.deliveryMan;

import com.zaxxer.hikari.HikariDataSource;
import zone.themcgamer.data.mysql.data.column.Column;
import zone.themcgamer.data.mysql.data.column.impl.IntegerColumn;
import zone.themcgamer.data.mysql.data.column.impl.LongColumn;
import zone.themcgamer.data.mysql.data.column.impl.StringColumn;
import zone.themcgamer.data.mysql.repository.MySQLRepository;

import java.util.concurrent.CompletableFuture;

/**
 * @author Nicholas
 */
public class DeliveryManRepository extends MySQLRepository {
    private static final String CLAIM_REWARD = "INSERT INTO `deliveryMan` " +
            "(`accountId`, `rewardId`, `lastClaimed`) VALUES " +
            "(?, ?, ?) ON DUPLICATE KEY UPDATE `lastClaimed`=?";

    public DeliveryManRepository(HikariDataSource dataSource) {
        super(dataSource);
    }

    /**
     * Inserts the claimed reward or updates the current value if it already exists
     *
     * @param accountId the id of the account that is claiming the reward
     * @param reward the reward that is being claimed
     */
    public void claim(int accountId, DeliveryManReward reward) {
        long now = System.currentTimeMillis();
        CompletableFuture.runAsync(() -> {
            executeInsert(CLAIM_REWARD, new Column[] {
                    new IntegerColumn("accountId", accountId),
                    new StringColumn("rewardId", reward.getId()),
                    new LongColumn("lastClaimed", now),
                    new LongColumn("lastClaimed", now)
            });
        });
    }
}