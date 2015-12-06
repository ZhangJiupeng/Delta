package com.delta.depend.util;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code TokenManager} class given management for
 * create/update/valid tokens
 *
 * @author Jim Zhang
 * @since Delta1.0
 */
@SuppressWarnings("ALL")
public final class TokenManager {
    /**
     * this parameter should be given by user
     * (7200000 ms = 2 hours)
     */
    private static final long lifeCycle = 7200000; // ms
    private static HashMap<String, String> tokenBuffer = new HashMap<>();

    private TokenManager() {
    }

    /**
     * Return a token calculate by MD5.<br/>
     * <b>DO NOT</b> create Token for same identity,
     * try update method instead.
     *
     * @param identityStr seed for your token
     * @return token in new lifecycle
     * @see MD5Encryptor
     */
    public synchronized static String createToken(String identityStr) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String newToken = MD5Encryptor.encrypt(identityStr, String.valueOf(timestamp));
        tokenBuffer.put(newToken, timestamp);
        return newToken;
    }

    /**
     * if the old-token is invalid, null will be returned.
     *
     * @param oldToken the token which wait for update
     * @return token in new lifecycle
     */
    public synchronized static String updateToken(String oldToken) {
        String timestamp = tokenBuffer.get(oldToken);
        if (timestamp == null) {
//            System.err.println("Token not exists. (" + oldToken + ")");
            return null;
        }
        tokenBuffer.remove(oldToken);
        return createToken(oldToken + System.currentTimeMillis());
    }

    /**
     * get token state from buffer.
     *
     * @return -1 token not exists, 0 legal token, 1 outdated
     */
    public static int checkToken(String token) {
        String timestamp = tokenBuffer.get(token);
        if (timestamp == null) return -1;
        if (System.currentTimeMillis() - Long.valueOf(timestamp) > lifeCycle) return 1;
        return 0;
    }

    /**
     * check identity if it is a legal token.
     */
    public static boolean validToken(String token) {
        int state = checkToken(token);
        switch (state) {
            case -1:
                return false;
            case 0:
                return true;
            case 1:
                tokenBuffer.remove(token);
        }
        return false;
    }

    /**
     * call this method to auto-remove outdated tokens
     */
    public static void refreshTokenBuffer() {
        for (Object o : tokenBuffer.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            if (System.currentTimeMillis() - Long.valueOf(entry.getValue().toString()) > lifeCycle) {
                tokenBuffer.remove(entry.getKey().toString());
            }
        }
    }

}
