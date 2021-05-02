package zone.themcgamer.data.mysql.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import zone.themcgamer.data.mysql.MySQLController;
import zone.themcgamer.data.mysql.data.column.Column;
import zone.themcgamer.data.mysql.data.column.impl.IntegerColumn;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

/**
 * @author Braydon
 * @implNote This class represents a table in MySQL
 */
@AllArgsConstructor @RequiredArgsConstructor @Getter
public class Table {
    private final String name;
    private final Column<?>[] columns;
    private String[] primaryKeys;

    /**
     * Create the table in the MySQL database
     *
     * @param mySQLController the MySQL controller
     * @param ignoreExisting whether or not to ignore the existing table
     * @throws SQLException exception
     */
    public void create(MySQLController mySQLController, boolean ignoreExisting) throws SQLException {
        // Checking the auto incrementing column count
        int autoIncrementingTables = 0;
        for (Column<?> column : columns) {
            if (column instanceof IntegerColumn && ((IntegerColumn) column).isAutoIncrement()) {
                autoIncrementingTables++;
            }
        }
        if (autoIncrementingTables > 1)
            throw new SQLException("Cannot create table with more than 1 auto incrementing table: " + autoIncrementingTables);
        // Constructing the query string
        StringBuilder queryBuilder = new StringBuilder("CREATE TABLE " + (ignoreExisting ? "IF NOT EXISTS " : "") + "`" + name + "` (");

        for (Column<?> column : columns) {
            boolean autoIncrement = column instanceof IntegerColumn && ((IntegerColumn) column).isAutoIncrement();

            queryBuilder.append("`").append(column.getName()).append("` ").append(column.getType().name());
            // If the column length is larger than 0, we wanna add (x) to the column type
            if (column.getLength() > 0)
                queryBuilder.append("(").append(column.getLength()).append(")");
            // If the column isn't nullable, add "NOT NULL" to the query
            if (!column.isNullable())
                queryBuilder.append(" NOT NULL");
            // If the column is set to auto increment, add "AUTO_INCREMENT" to the query
            if (autoIncrement)
                queryBuilder.append(" AUTO_INCREMENT");
            queryBuilder.append(", ");
        }
        String query = queryBuilder.toString();
        query = query.substring(0, query.length() - 2);

        // Appending the primary key(s) to the query
        if (primaryKeys != null && (primaryKeys.length > 0)) {
            query+= ", PRIMARY KEY (";
            for (String primaryKey : primaryKeys)
                query+= "`" + primaryKey + "`, ";
            query = query.substring(0, query.length() - 2) + ")";
        }
        query+= ");";

        // Creating the table
        String finalQuery = query;
        CompletableFuture.runAsync(() -> {
            Connection connection = null;
            try {
                connection = mySQLController.getDataSource().getConnection();
                connection.prepareStatement(finalQuery).execute();
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
}