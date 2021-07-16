package zone.themcgamer.data.mysql.repository;

import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;
import zone.themcgamer.data.mysql.data.column.Column;

import java.sql.*;
import java.util.function.Consumer;

/**
 * @author Braydon
 */

// Maybe do all the CRUD operations?

@AllArgsConstructor
public class MySQLRepository {
    protected final HikariDataSource dataSource;

    /**
     * Insert the given columns using the provided query
     *
     * @param query the query to execute
     * @param columns the columns to insert
     * @return the amount of rows affected
     */
    protected int executeInsert(String query, Column<?>[] columns) {
        return executeInsert(query, columns, null);
    }

    /**
     * Insert the given columns using the provided query
     *
     * @param query the query to execute
     * @param columns the columns to insert
     * @param onComplete the oncomplete consumer
     * @return the amount of rows affected
     */
    protected int executeInsert(String query, Column<?>[] columns, @Nullable Consumer<ResultSet> onComplete) {
        return executeInsert(query, columns, onComplete, null);
    }

    /**
     * Insert the given columns using the provided query
     *
     * @param query the query to execute
     * @param columns the columns to insert
     * @param onComplete the oncomplete consumer
     * @param onException the exception consumer
     * @return the amount of rows affected
     */
    protected int executeInsert(String query, Column<?>[] columns, @Nullable Consumer<ResultSet> onComplete,
                                @Nullable Consumer<SQLException> onException) {
        try (Connection connection = dataSource.getConnection()) {
            return executeInsert(connection, query, columns, onComplete, onException);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    /**
     * Insert the given columns using the provided query
     *
     * @param connection the connection to execute the query on
     * @param query the query to execute
     * @param columns the columns to insert
     * @return the amount of rows affected
     */
    protected int executeInsert(Connection connection, String query, Column<?>[] columns) {
        return executeInsert(connection, query, columns, null);
    }

    /**
     * Insert the given columns using the provided query
     *
     * @param connection the connection to execute the query on
     * @param query the query to execute
     * @param columns the columns to insert
     * @param onComplete the oncomplete consumer
     * @return the amount of rows affected
     */
    protected int executeInsert(Connection connection, String query, Column<?>[] columns, @Nullable Consumer<ResultSet> onComplete) {
        return executeInsert(connection, query, columns, onComplete, null);
    }

    /**
     * Insert the given columns using the provided query
     *
     * @param connection the connection to execute the query on
     * @param query the query to execute
     * @param columns the columns to insert
     * @param onComplete the oncomplete consumer
     * @param onException the exception consumer
     * @return the amount of rows affected
     */
    protected int executeInsert(Connection connection, String query, Column<?>[] columns, @Nullable Consumer<ResultSet> onComplete,
                                @Nullable Consumer<SQLException> onException) {
        int questionMarks = 0;
        for (char character : query.toCharArray()) {
            if (character == '?') {
                questionMarks++;
            }
        }
        if (questionMarks != columns.length)
            throw new IllegalArgumentException("Invalid amount of columns for query \"" + query + "\"");
        int affectedRows = 0;
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            int columnIndex = 1;
            for (Column<?> column : columns)
                statement.setString(columnIndex++, column.getValue() == null ? null : column.getValue().toString());
            affectedRows = statement.executeUpdate();
            if (onComplete != null)
                onComplete.accept(statement.getGeneratedKeys());
        } catch (SQLException ex) {
            if (onException != null)
                onException.accept(ex);
            ex.printStackTrace();
        }
        return affectedRows;
    }

    /**
     * Execute the given query
     *
     * @param query the query to execute
     * @param columns the columns to use in the query
     * @param onComplete the oncomplete consumer
     */
    protected void executeQuery(String query, Column<?>[] columns, Consumer<ResultSet> onComplete) {
        executeQuery(query, columns, onComplete, null);
    }

    /**
     * Execute the given query
     *
     * @param query the query to execute
     * @param columns the columns to use in the query
     * @param onComplete the oncomplete consumer
     * @param onException the exception consumer
     */
    protected void executeQuery(String query, Column<?>[] columns, Consumer<ResultSet> onComplete,
                                     @Nullable Consumer<SQLException> onException) {
        try (Connection connection = dataSource.getConnection()) {
            executeQuery(connection, query, columns, onComplete, onException);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Execute the given query
     *
     * @param connection the connection to execute the query on
     * @param query the query to execute
     * @param columns the columns to use in the query
     * @param onComplete the oncomplete consumer
     */
    protected void executeQuery(Connection connection, String query, Column<?>[] columns, Consumer<ResultSet> onComplete) {
        executeQuery(connection, query, columns, onComplete, null);
    }

    /**
     * Execute the given query
     *
     * @param connection the connection to execute the query on
     * @param query the query to execute
     * @param columns the columns to use in the query
     * @param onComplete the oncomplete consumer
     * @param onException the exception consumer
     */
    protected void executeQuery(Connection connection, String query, Column<?>[] columns, Consumer<ResultSet> onComplete,
                         @Nullable Consumer<SQLException> onException) {
        int questionMarks = 0;
        for (char character : query.toCharArray()) {
            if (character == '?') {
                questionMarks++;
            }
        }
        if (questionMarks != columns.length)
            throw new IllegalArgumentException("Invalid amount of columns for query \"" + query + "\"");
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            int columnIndex = 1;
            for (Column<?> column : columns)
                statement.setString(columnIndex++, (column.getValue() == null ? null : column.getValue().toString()));
            try (ResultSet resultSet = statement.executeQuery()) {
                onComplete.accept(resultSet);
            } catch (SQLException ex) {
                if (onException != null)
                    onException.accept(ex);
                ex.printStackTrace();
            }
        } catch (SQLException ex) {
            if (onException != null)
                onException.accept(ex);
            ex.printStackTrace();
        }
    }
}