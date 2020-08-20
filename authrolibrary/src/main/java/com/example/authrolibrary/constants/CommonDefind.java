package com.example.authrolibrary.constants;

public class CommonDefind {
    /**
     * 授权配置
     */
    public static class AuthConfig {
        /**
         * appKey
         */
        public static String AUTH_APP_KEY = "t9RD8tMW7a9GK4we";
        /**
         * appSecret
         */
        public static String AUTH_APP_SECRET = "VMHqv3lBJw4EHLYM";
        /**
         * Rsa
         */
        public static String AUTH_RSA = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCV+msaIBZ3YwpBzZ8YaXRZokp8FHDZuE7/Jexpr6"+
                "1iY+AIGDsSYtqcSbtDvL7dD6X1d2dHmb+k03IuSHyAcX6ePj2AKBISYUHAs50e/d3BqZzBfDbfrgxKJSiR+vuv/8JGhQZ1D"+
                "9wWUnbaqhTKLR0oqhow4xyXJRdCE/ZdVAmx1QIDAQAB";
        /**
         * 设备类型
         */
        public static String DEVICE_TYPE = "tkj";
        /**
         * 协议版本号
         */
        public static String AGREEMENT_VERSION = "1.0";
    }

    public static final class RetrunCode {
        /**
         * 成功
         */
        public static final int CODE_SUCCESS = 0;
        /**
         * 设备已被激活
         */
        public static final int CODE_ACTIVATION = 1;
        /**
         * 无该产品（appKey不存在）
         */
        public static final int CODE_NO_DEVICE = 1000;
        /**
         * 签名错误
         */
        public static final int CODE_SIGN_ERROR = 1001;
        /**
         * 产品无订单
         */
        public static final int CODE_NO_ORDER = 2000;
        /**
         * 激活时间已过期
         */
        public static final int CODE_ACTIVATION_OVERDUE = 2001;
        /**
         * 未知错误
         */
        public static final int CODE_UNKNOWN = 2003;
    }
}
