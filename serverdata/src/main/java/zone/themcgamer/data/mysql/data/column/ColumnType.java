package zone.themcgamer.data.mysql.data.column;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Braydon
 * @implNote A list of supported column types and its max length
 */
@AllArgsConstructor @Getter
public enum ColumnType {
    VARCHAR(65_535),
    INT(11),
    LONG(11),
    DOUBLE(11);

    private final int maxLength;
}