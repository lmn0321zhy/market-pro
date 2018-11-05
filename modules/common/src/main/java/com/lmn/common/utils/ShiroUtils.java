package com.lmn.common.utils;

import org.apache.shiro.crypto.hash.SimpleHash;

/**
 * Created by lmn on 2018-10-26.
 */
public class ShiroUtils {
    private static final int SALT_SIZE = 50;
    public static final int HASH_INTERATIONS = 1024;
    public static final String HASH_ALGORITHM = "SHA-1";
    /**
     * 生成安全的密码，生成随机的16位salt并经过1024次 sha-1 hash
     */
    public static String entryptPassword(String plainPassword) {
        String plain = Encodes.unescapeHtml(plainPassword);
        byte[] salt = Digests.generateSalt(SALT_SIZE);
        byte[] hashPassword = Digests.sha1(plain.getBytes(), salt, HASH_INTERATIONS);
        return Encodes.encodeHex(salt) + Encodes.encodeHex(hashPassword);
    }

}
