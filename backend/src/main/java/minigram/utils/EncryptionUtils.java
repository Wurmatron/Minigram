package minigram.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class EncryptionUtils {

    private static SecureRandom random;

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

    public static byte[] generateSalt(int size) {
        if (random == null) {
            random = new SecureRandom();
        }
        byte[] salt = new byte[size];
        random.nextBytes(salt);
        return salt;
    }
}
