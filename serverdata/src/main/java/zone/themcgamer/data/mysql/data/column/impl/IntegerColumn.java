package zone.themcgamer.data.mysql.data.column.impl;

import lombok.Getter;
import zone.themcgamer.data.mysql.data.Table;
import zone.themcgamer.data.mysql.data.column.Column;
import zone.themcgamer.data.mysql.data.column.ColumnType;

/**
 * @author Braydon
 * @implNote This class represents an {@link Integer} column in a MySQL {@link Table}
 */
@Getter
public class IntegerColumn extends Column<Integer> {
    private final boolean autoIncrement;

    public IntegerColumn(String name, Integer value) {
        super(name, value);
        autoIncrement = false;
    }

    public IntegerColumn(String name, boolean autoIncrement, boolean nullable) {
        this(name, 0, autoIncrement, nullable);
    }

    public IntegerColumn(String name, int length, boolean autoIncrement, boolean nullable) {
        super(name, ColumnType.INT, length, nullable);
        this.autoIncrement = autoIncrement;
    }
}