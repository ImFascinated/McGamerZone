package zone.themcgamer.core.twoFactor;

import com.zaxxer.hikari.HikariDataSource;
import zone.themcgamer.data.mysql.data.column.Column;
import zone.themcgamer.data.mysql.data.column.impl.IntegerColumn;
import zone.themcgamer.data.mysql.data.column.impl.LongColumn;
import zone.themcgamer.data.mysql.data.column.impl.StringColumn;
import zone.themcgamer.data.mysql.repository.MySQLRepository;

import java.util.concurrent.CompletableFuture;

/**
 * @author Braydon
 */
public class TwoFactorRepository extends MySQLRepository {
    private static final String INSERT_TWO_FACTOR = "INSERT INTO `twoFactor` " +
            "(`accountId`, `dbKey`, `value`) VALUES " +
            "(?, ?, ?) ON DUPLICATE KEY UPDATE `value`=?";

    public TwoFactorRepository(HikariDataSource dataSource) {
        super(dataSource);
    }

    public void authenticate(int accountId, String secretKey, long lastAuthentication) {
        StringColumn secretKeyColumn = new StringColumn("value", secretKey);
        LongColumn lastAuthenticationColumn = new LongColumn("value", lastAuthentication);
        CompletableFuture.runAsync(() -> {
            for (TwoFactorDBKey dbKey : TwoFactorDBKey.values()) {
                Column<?> valueColumn = dbKey == TwoFactorDBKey.SECRET_KEY ? secretKeyColumn : lastAuthenticationColumn;
                executeInsert(INSERT_TWO_FACTOR, new Column[] {
                        new IntegerColumn("accountId", accountId),
                        new StringColumn("dbKey", dbKey.name()),
                        valueColumn,
                        valueColumn
                });
            }
        });
    }
}