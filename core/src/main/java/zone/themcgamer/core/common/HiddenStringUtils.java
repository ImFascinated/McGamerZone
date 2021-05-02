package zone.themcgamer.core.common;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

import java.nio.charset.StandardCharsets;

/**
 * @author Braydon
 */
@UtilityClass
public class HiddenStringUtils {
    private static final String SEQUENCE_HEADER = "§r§n§r";
    private static final String SEQUENCE_FOOTER = "§r§o§r";

    /**
     * Encode the given {@link String} and return the
     * encoded string
     *
     * @param s - The string to encode
     * @return the encoded string
     */
    public static String encode(String s) {
        if (s == null)
            return null;
        return SEQUENCE_HEADER + stringToColors(s) + SEQUENCE_FOOTER;
    }

    /**
     * Replace the given from hidden {@link String} with
     * the to {@link String}
     *
     * @param from - The from
     * @param to - The to
     * @return the replaced string
     */
    public static String replaceHiddenString(String from, String to) {
        if (from == null)
            return null;
        int start = from.indexOf(SEQUENCE_HEADER);
        int end = from.indexOf(SEQUENCE_FOOTER);
        if (start < 0 || end < 0)
            return null;
        return from.substring(0, start + SEQUENCE_HEADER.length()) + stringToColors(to) + from.substring(end);
    }

    /**
     * Get whether or not the provided {@link String} is
     * a hidden {@link String}
     *
     * @param s - The string to check
     * @return whether or not the {@link String} is a hidden {@link String}
     */
    public static boolean hasHiddenString(String s) {
        if (s == null)
            return false;
        return s.contains(SEQUENCE_HEADER) && s.contains(SEQUENCE_FOOTER);
    }

    /**
     * Decode the given {@link String}
     * 
     * @param s - The string to decode
     * @return the decoded string
     */
    public static String decode(String s) {
        if (s == null)
            return null;
        int start = s.indexOf(SEQUENCE_HEADER);
        int end = s.indexOf(SEQUENCE_FOOTER);

        if (start < 0 || end < 0)
            return null;

        s = s.substring(start + SEQUENCE_HEADER.length(), end);

        s = s.toLowerCase().replace("" + ChatColor.COLOR_CHAR, "");
        if (s.length() % 2 != 0)
            s = s.substring(0, (s.length() / 2) * 2);
        char[] chars = s.toCharArray();
        byte[] bytes = new byte[chars.length / 2];

        for (int i = 0; i < chars.length; i+= 2)
            bytes[i / 2] = hexToByte(chars[i], chars[i + 1]);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private String stringToColors(String s) {
        if (s == null)
            return null;
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        char[] chars = new char[bytes.length * 4];

        for (int i = 0; i < bytes.length; i++) {
            char[] hex = byteToHex(bytes[i]);
            chars[i * 4] = ChatColor.COLOR_CHAR;
            chars[i * 4 + 1] = hex[0];
            chars[i * 4 + 2] = ChatColor.COLOR_CHAR;
            chars[i * 4 + 3] = hex[1];
        }

        return new String(chars);
    }

    private byte hexToByte(char hex1, char hex0) {
        return (byte) (((hexToUnsignedInt(hex1) << 4) | hexToUnsignedInt(hex0)) + Byte.MIN_VALUE);
    }

    private int hexToUnsignedInt(char c) {
        if (c >= '0' && c <= '9') {
            return c - 48;
        } else if (c >= 'a' && c <= 'f') {
            return c - 87;
        } else {
            throw new IllegalArgumentException("Hex char out of range");
        }
    }

    private char[] byteToHex(byte b) {
        int unsignedByte = (int) b - Byte.MIN_VALUE;
        return new char[] { unsignedIntToHex((unsignedByte >> 4) & 0xf), unsignedIntToHex(unsignedByte & 0xf) };
    }

    private char unsignedIntToHex(int i) {
        if (i >= 0 && i <= 9)
            return (char) (i + 48);
        else if (i >= 10 && i <= 15)
            return (char) (i + 87);
        else throw new IllegalArgumentException("Hex int out of range");
    }
}