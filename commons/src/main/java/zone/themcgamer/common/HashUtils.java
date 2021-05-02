package zone.themcgamer.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Braydon
 */
public class HashUtils {
    /**
     * Encrypt the given {@link String} as SHA-256
     *
     * @param s the string to encrypt
     * @return the encrypted string
     */
    public static String encryptSha256(String s) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(s.getBytes());
            byte digest[] = messageDigest.digest();
            StringBuffer buffer = new StringBuffer();
            for (byte b : digest)
                buffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            return buffer.toString();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}