package org.m2sec.core.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */

public class HashUtil {

    public static final String SHA_256 = "SHA256";
    public static final String SHA_1 = "SHA1";

    public static final String MD_5 = "MD5";

    public static byte[] calc(byte[] data, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            return md.digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String calcToHex(byte[] data, String algorithm) {
        return ByteUtil.toHexString(calc(data, algorithm));
    }

    public static String calcToBase64(byte[] data, String algorithm) {
        return Base64.getEncoder().encodeToString(calc(data, algorithm));
    }
}
