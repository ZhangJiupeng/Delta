package com.delta.depend.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * The {@code MD5Encryptor} class provides encryption
 * for {@code String} base on MD5/MD5[SALT] and function
 * for matching the encrypted password.
 *
 * @author Jim Zhang
 * @since Delta1.0
 */
@SuppressWarnings("ALL")
public final class MD5Encryptor {
    private static final String[] strDigits = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    private MD5Encryptor() {
    }

    private static String byteToString(byte[] bByte) {
        StringBuilder sBuffer = new StringBuilder();
        for (byte aBByte : bByte) {
            sBuffer.append(byteToArrayString(aBByte));
        }
        return sBuffer.toString();
    }

    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return strDigits[iD1] + strDigits[iD2];
    }

    /**
     * Get MD5 password without salt [not recommended].
     */
    public static String encrypt(String plaintext) {
        if (plaintext == null) {
            return null;
        }
        String resultString = plaintext;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteToString(md.digest(plaintext.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return resultString.toUpperCase();
    }

    /**
     * Get MD5 password with salt [recommended].<br/>
     * You should use {@code SecureRandom} to generate
     * random salt and save separate from the password
     * string.
     */
    public static String encrypt(String plaintext, String salt) {
        if (plaintext == null || salt == null) {
            return MD5Encryptor.encrypt(plaintext);
        }
        String resultString = plaintext;
        try {
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(salt.getBytes());
            String hash = String.valueOf(Math.abs(secureRandom.nextLong()));
            resultString = MD5Encryptor.encrypt(plaintext + hash);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return resultString.toUpperCase();
    }

    /**
     * Match the result of plaintext and MD5(password)
     */
    public static boolean compare(String plaintext, String password) {
        return encrypt(plaintext).equals(password);
    }

    /**
     * Match the result of plaintext and MD5(password[with salt])
     */
    public static boolean compare(String plaintext, String password, String salt) {
        return encrypt(plaintext, salt).equals(password);
    }

}
