package com.delta.depend.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

/**
 * The {@code CryptoUtil} class provides encoding
 * and decoding for {@code String} base on DES
 *
 * @author Jim Zhang
 * @since Delta1.0
 */
@SuppressWarnings("ALL")
public final class CryptoUtil {
    private static final String ALGORITHM = "DES/CBC/PKCS5Padding";
    private CryptoUtil(){}

    private static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String str;
        for (int n = 0; b != null && n < b.length; n++) {
            str = Integer.toHexString(b[n] & 0XFF);
            if (str.length() == 1)
                hs.append('0');
            hs.append(str);
        }
        return hs.toString().toUpperCase();
    }

    private static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0)
            throw new IllegalArgumentException();
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

    /**
     * @param plaintext plaintext
     * @param key       password
     * @return encrypted text[recommended Base64 encoding]
     * @throws InvalidKeyException
     */
    public static String encode(String key, String plaintext) {
        if (key == null) {
            return plaintext;
        }
        if (plaintext != null) {
            try {
                DESKeySpec dks = new DESKeySpec(key.getBytes());
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
                Key secretKey = keyFactory.generateSecret(dks);
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                AlgorithmParameterSpec paramSpec = new IvParameterSpec("12345678".getBytes());
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);
                byte[] bytes = cipher.doFinal(plaintext.getBytes());
                return byte2hex(bytes);
            } catch (InvalidKeyException e) {
                System.err.println("Wrong key size (" + key.length() + " < 8)");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @param encryptedText encrypted text
     * @param key           password
     * @return decoded text
     * @throws Exception
     */
    public static String decode(String key, String encryptedText) {
        if (key == null) {
            return null;
        }
        if (encryptedText != null) {
            try {
                DESKeySpec dks = new DESKeySpec(key.getBytes());
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
                Key secretKey = keyFactory.generateSecret(dks);
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                AlgorithmParameterSpec paramSpec = new IvParameterSpec("12345678".getBytes());
                cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
                return new String(cipher.doFinal(hex2byte(encryptedText.getBytes())));
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

}
