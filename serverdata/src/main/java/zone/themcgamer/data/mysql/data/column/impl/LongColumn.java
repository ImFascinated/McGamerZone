package zone.themcgamer.data.mysql.data.column.impl;

import lombok.Getter;
import zone.themcgamer.data.mysql.data.Table;
import zone.themcgamer.data.mysql.data.column.Column;
import zone.themcgamer.data.mysql.data.column.ColumnType;

/**
 * @author Braydon
 * @implNote This class represents an {@link Long} column in a MySQL {@link Table}
 */
@Getter
public class LongColumn extends Column<Long> {
    public LongColumn(String name, Long value) {
        super(name, value);
    }

    public LongColumn(String name, boolean nullable) {
        this(name, 0, nullable);
    }

    public LongColumn(String name, int length, boolean nullable) {
        super(name, ColumnType.LONG, length, nullable);
    }
}