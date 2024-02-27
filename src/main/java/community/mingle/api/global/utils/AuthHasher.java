package community.mingle.api.global.utils;

// EmailHasher.java

import lombok.NoArgsConstructor;

import java.security.MessageDigest;

@NoArgsConstructor
public class AuthHasher {

    public static String hashString(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(plainText.getBytes());
            byte[] byteData = md.digest();
            StringBuffer sb = new StringBuffer();

            for(int i = 0; i < byteData.length; ++i) {
                sb.append(Integer.toString((byteData[i] & 255) + 256, 16).substring(1));
            }

            StringBuffer hexString = new StringBuffer();

            for(int i = 0; i < byteData.length; ++i) {
                String hex = Integer.toHexString(255 & byteData[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }

                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception var7) {
            var7.printStackTrace();
            throw new RuntimeException();
        }
    }
}
