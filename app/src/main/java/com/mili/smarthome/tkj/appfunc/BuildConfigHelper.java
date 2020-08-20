package com.mili.smarthome.tkj.appfunc;

import com.mili.smarthome.tkj.BuildConfig;
import com.mili.smarthome.tkj.app.CustomVersion;

public final class BuildConfigHelper {

    public static final String K3 = "k3";
    public static final String K4 = "k4";
    public static final String K4_X1600 = "k4_x1600";
    public static final String K4_X1600_GATE = "k4_x1600_gate";
    public static final String K6 = "k6";
    public static final String K7 = "k7";

    public static final String FULL = "_Full";
    public static final String PAD = "_Pad";

    public static final String RELEASE = "release";
    public static final String DEBUG = "debug";


    public static boolean isK3() {
        return K3.equals(BuildConfig.FLAVOR_MODEL);
    }

    public static boolean isK4() {
        return K4.equals(BuildConfig.FLAVOR_MODEL);
    }

    public static boolean isK4_X1600() {
        return K4_X1600.equals(BuildConfig.FLAVOR_MODEL);
    }

    public static boolean isK4_X1600_GATE() {
        return K4_X1600_GATE.equals(BuildConfig.FLAVOR_MODEL);
    }

    public static boolean isK6() {
        return K6.equals(BuildConfig.FLAVOR_MODEL);
    }

    public static boolean isK7() {
        return K7.equals(BuildConfig.FLAVOR_MODEL);
    }

    /** 地平线 */
    public static boolean isHorizon() {
        return K4_X1600.equals(BuildConfig.FLAVOR_MODEL)
                || K4_X1600_GATE.equals(BuildConfig.FLAVOR_MODEL);
    }

    /** 完整版 */
    public static boolean isFull() {
        return FULL.equals(BuildConfig.FLAVOR_FUNC);
    }

    /** 平板版 */
    public static boolean isPad() {
        return PAD.equals(BuildConfig.FLAVOR_FUNC);
    }

    /** 闸机版 */
    public static boolean isGate() {
        return K4_X1600_GATE.equals(BuildConfig.FLAVOR_MODEL);
    }

    /** debug */
    public static boolean isDebug() {
        return DEBUG.equals(BuildConfig.BUILD_TYPE);
    }

    /** release */
    public static boolean isRelease() {
        return RELEASE.equals(BuildConfig.BUILD_TYPE);
    }

    /** 是否启用IPC功能 */
    public static boolean isEnabledIPC() {
        return BuildConfig.isEnabledIPC;
    }

    /** 是否启用APN功能 */
    public static boolean isEnabledAPN() {
        return BuildConfig.isEnabledAPN;
    }

    /** 获取软件版本 */
    public static String getSoftWareVer() {
        return BuildConfig.softVersionType + "_" + BuildConfig.buildVersionTime;
    }

    /** 获取硬件版本 */
    public static String getHardWareVer() {
        return "TK" + (BuildConfig.FLAVOR_MODEL).toUpperCase() + "33-100101-REL";
    }

}
