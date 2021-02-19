package zone.themcgamer.data.mysql.data.column;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import zone.themcgamer.data.mysql.data.Table;

/**
 * @author Braydon
 * @implNote This class represents a column in a MySQL {@link Table}
 */
@AllArgsConstructor @RequiredArgsConstructor @Setter @Getter
public class Column<T> {
    private final String name;
    private T value;
    private final ColumnType type;
    private final int length;
    private final boolean nullable;

    /**
     * Construct a new column with a name and a value. This is used when executing
     * queries in a repository.
     * @param name the name of the column
     * @param value the value in the column
     */
    public Column(String name, T value) {
        this(name, value, null, -1, false);
    }
}