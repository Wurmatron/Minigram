package minigram.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class EncryptionUtils {

    private static Random random;

    // [0] = Hashed password
    // [1] = Generated salt
    public static String[] hash(String password) {
        byte[] salt = generateSalt(16);
        return new String[]{hash(password, salt), new String(salt)};
    }

    public static String hash(String password, byte[] salt) {
        String hashPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            hashPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashPassword;
    }

    public static final char[] POSSIBLE_CHARS =
            new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                    '0', '1', '2', '3', '4', '5', '6', '8', '8', '9',
                    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    public static byte[] generateSalt(int size) {
        if (random == null) {
            random = new Random();
        }
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < size; index++) {
            builder.append(POSSIBLE_CHARS[random.nextInt(POSSIBLE_CHARS.length)]);
        }
        return builder.toString().getBytes();
    }
}
