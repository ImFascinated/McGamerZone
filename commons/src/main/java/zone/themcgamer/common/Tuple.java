package zone.themcgamer.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Braydon
 */
@AllArgsConstructor @Setter @Getter
public class Tuple<L, R> implements Cloneable {
    private L left;
    private R right;

    @Override
    public Tuple<L, R> clone() {
        try {
            return (Tuple<L, R>) super.clone();
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}