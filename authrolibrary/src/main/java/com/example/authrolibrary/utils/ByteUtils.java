package com.example.authrolibrary.utils;

public final class ByteUtils {

    public static byte[] toBytes(final Number val, int len) {
        long temp = val.longValue();
        byte[] bytes = new byte[len];
        for (int i = 0 ; i < len; i ++) {
            bytes[i] = (byte)(temp & 0xFF);
            temp = temp >> 8;
        }
        return bytes;
    }

    public static String toHexString(byte[] bytes) {
        if (bytes == null)
            return "";
        char[] hexDigits = new char[] {
                '0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hex.append(hexDigits[(b >> 4) & 0xF]);
            hex.append(hexDigits[b & 0xF]);
        }
        return hex.toString();
    }

    public static byte[] fromHexString(String data) {
        int len = data.length() / 2;
        byte[] bytes = new byte[len];
        for (int i = 0; i < data.length(); i += 2) {
            bytes[i] = Byte.valueOf(data.substring(i, i + 2), 16);
        }
        return bytes;
    }
}
