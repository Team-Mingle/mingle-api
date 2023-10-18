package community.mingle.api.global.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class EmailHasher {
    public static String hashEmail(String email) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(email.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash email", e);
        }
    }
}
