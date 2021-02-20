package zone.themcgamer.data.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import zone.themcgamer.data.mysql.data.Table;
import zone.themcgamer.data.mysql.data.column.Column;
import zone.themcgamer.data.mysql.data.column.impl.IntegerColumn;
import zone.themcgamer.data.mysql.data.column.impl.LongColumn;
import zone.themcgamer.data.mysql.data.column.impl.StringColumn;

import java.sql.SQLException;

/**
 * @author Braydon
 */
@Getter
public class MySQLController {
    private final HikariDataSource dataSource;

    public MySQLController(boolean production) {
        // Connecting to the MySQL server
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + MySQLConstants.HOST + ":3306/" + MySQLConstants.USERNAME + "_" + (production ? "production" : "dev") + "?allowMultiQueries=true");
        config.setUsername(MySQLConstants.USERNAME);
        config.setPassword(MySQLConstants.AUTH);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        dataSource = new HikariDataSource(config);

        // Creating the tables
        Table[] tables = new Table[] {
                new Table("accounts", new Column[] {
                        new IntegerColumn("id", true, false),
                        new StringColumn("uuid", 36, false),
                        new StringColumn("name", 16, false),
                        new StringColumn("primaryRank", 25, false),
                        new StringColumn("secondaryRanks", 255, false),
                        new IntegerColumn("gold", false, false),
                        new IntegerColumn("gems", false, false),
                        new StringColumn("ipAddress", 255, false),
                        new LongColumn("firstLogin", false),
                        new LongColumn("lastLogin", false)
                }, new String[] { "id" }),

                new Table("punishments", new Column[] {
                        new IntegerColumn("id", true, false),
                        new StringColumn("targetIp", 255, false),
                        new StringColumn("targetUuid", 36, true),
                        new StringColumn("category", 20, false),
                        new StringColumn("offense", 20, false),
                        new IntegerColumn("severity", false, false),
                        new StringColumn("staffUuid", 36, true),
                        new StringColumn("staffName", 16, false),
                        new LongColumn("timeIssued", false),
                        new LongColumn("duration", false),
                        new StringColumn("reason", 255, false),
                        new StringColumn("removeStaffUuid", 36, true),
                        new StringColumn("removeStaffName", 16, true),
                        new StringColumn("removeReason", 255, true),
                        new LongColumn("timeRemoved", false),
                }, new String[] { "id" }),

                new Table("tasks", new Column[] {
                        new IntegerColumn("accountId", false, false),
                        new StringColumn("task", 255, false)
                }, new String[] { "accountId" }),

                new Table("deliveryMan", new Column[] {
                        new IntegerColumn("accountId", false, false),
                        new StringColumn("rewardId", 50, false),
                        new LongColumn("lastClaimed", false)
                }, new String[] { "accountId", "rewardId" }),

                new Table("kits", new Column[] {
                        new IntegerColumn("accountId", false, false),
                        new StringColumn("game", 50, false),
                        new StringColumn("kit", 50, false)
                }, new String[] { "accountId" })
        };
        for (Table table : tables) {
            try {
                table.create(this, true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}