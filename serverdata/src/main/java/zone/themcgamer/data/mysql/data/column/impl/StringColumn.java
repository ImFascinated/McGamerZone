package zone.themcgamer.data.mysql.data.column.impl;

import zone.themcgamer.data.mysql.data.Table;
import zone.themcgamer.data.mysql.data.column.Column;
import zone.themcgamer.data.mysql.data.column.ColumnType;

/**
 * @author Braydon
 * @implNote This class represents an {@link String} column in a MySQL {@link Table}
 */
public class StringColumn extends Column<String> {
    public StringColumn(String name, String value) {
        super(name, value);
    }

    public StringColumn(String name, int length, boolean nullable) {
        super(name, ColumnType.VARCHAR, length, nullable);
    }
}