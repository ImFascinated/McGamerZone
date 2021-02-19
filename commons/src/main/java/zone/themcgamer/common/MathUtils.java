package zone.themcgamer.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * @author Braydon
 */
public class MathUtils {
    public static double round(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();
        return new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }

    public static double format(double number, int additional) {
        return Double.parseDouble(formatString(number, additional));
    }

    public static String formatString(double number, int additional) {
        return new DecimalFormat("#.#" + "#".repeat(Math.max(0, additional - 1)),
                new DecimalFormatSymbols(Locale.CANADA)).format(number);
    }
}