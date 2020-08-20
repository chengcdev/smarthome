package com.example.authrolibrary.utils;

import android.text.TextUtils;

import java.security.MessageDigest;

/**
 * 提供MD5加密接口
 * @author
 *      2017-08-10: Created by zenghm.
 */
public class MD5Utils {

    /**
     * MD5加密字符串
     * @param strSrc
     * @return
     */
    public static byte[] toMD5(String strSrc) {
        if (TextUtils.isEmpty(strSrc))
            return null;
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(strSrc.getBytes("UTF-8"));
        } catch (Exception e) {
            return null;
        }
        return hash;
    }

    /**
     * MD5加密字符串
     * @param strSrc
     * @return
     */
    public static String toMD5String(String strSrc) {
        return ByteUtils.toHexString(toMD5(strSrc));
    }

}
