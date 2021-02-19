package zone.themcgamer.data.mysql.data.column.impl;

import lombok.Getter;
import zone.themcgamer.data.mysql.data.Table;
import zone.themcgamer.data.mysql.data.column.Column;
import zone.themcgamer.data.mysql.data.column.ColumnType;

/**
 * @author Braydon
 * @implNote This class represents an {@link Double} column in a MySQL {@link Table}
 */
@Getter
public class DoubleColumn extends Column<Double> {
    public DoubleColumn(String name, Double value) {
        super(name, value);
    }

    public DoubleColumn(String name, boolean nullable) {
        this(name, 0, nullable);
    }

    public DoubleColumn(String name, int length, boolean nullable) {
        super(name, ColumnType.DOUBLE, length, nullable);
    }
}