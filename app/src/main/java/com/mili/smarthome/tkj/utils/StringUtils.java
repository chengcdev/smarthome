package com.mili.smarthome.tkj.utils;

public final class StringUtils {

    /**
     * 输入的字符串是否为空
     */
    public static boolean isEmpty(String str) {
        return (null == str || str.isEmpty());
    }

    /**
     * 左边补齐
     * @param srcStr 原始字符串
     * @param length 目标长度
     * @param padText 补齐字符
     * @return 补齐长度的字符串
     */
    public static String padLeft(String srcStr, int length, char padText) {
        if (srcStr == null)
            srcStr = "";
        StringBuilder temp = new StringBuilder(srcStr);
        for (int i = srcStr.length(); i < length; i++) {
            temp.insert(0, padText);
        }
        return temp.toString();
    }

    /**
     * 右边补齐
     * @param srcStr 原始字符串
     * @param length 目标长度
     * @param padText 补齐字符
     * @return 补齐长度的字符串
     */
    public static String padRight(String srcStr, int length, char padText) {
        if (srcStr == null)
            srcStr = "";
        StringBuilder temp = new StringBuilder(srcStr);
        for (int i = srcStr.length(); i < length; i++) {
            temp.append(padText);
        }
        return temp.toString();
    }
}
