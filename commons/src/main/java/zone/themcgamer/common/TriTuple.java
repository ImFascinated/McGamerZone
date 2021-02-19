package zone.themcgamer.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Braydon
 */
@AllArgsConstructor @Setter @Getter
public class TriTuple<A, B, C> {
    private A left;
    private B middle;
    private C right;
}