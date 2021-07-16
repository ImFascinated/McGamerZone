package zone.themcgamer.common;

import java.util.Arrays;
import java.util.List;

/**
 * @author Braydon
 */
public class DoubleUtils {
    /**
     * Format the given value into a readable format
     *
     * @param amount the value to format
     * @param shortSuffixes whether or not to have short suffixes
     * @return the formatted value
     * @author Ell (modified by Braydon)
     */
    public static String format(double amount, boolean shortSuffixes) {
        if (amount <= 0.0D)
            return "0";
        List<String> suffixes;
        if (shortSuffixes)
            suffixes = Arrays.asList("", "k", "m", "b", "t", "Qa", "Qu", "Se", "Sp", "o", "n", "d");
        else
            suffixes = Arrays.asList("", " Thousand", " Million", " Billion", " Trillion", " Quadrillion",
                    " Quintillion", " Sextillion", " Septillion", " Octillion", " Nonillion", " Decillion");
        double chunks = Math.floor(Math.floor(Math.log10(amount) / 3));
        amount/= Math.pow(10D, chunks * 3 - 1);
        amount/= 10D;
        String suffix = suffixes.get((int) chunks);
        String format = MathUtils.formatString(amount, 1);
        if (format.replace(".", "").length() > 5)
            format = format.substring(0, 5);
        return format + suffix;
    }
}