package com.mili.smarthome.tkj.face.horizon.util;

import android.app.Activity;
import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

public class ScreenUtil {
    private final static String TAG = ScreenUtil.class.getName();
    static PowerManager pm;
    static PowerManager.WakeLock wakeLock = null;

    public static int init(Activity activity) {
        int ret = -1;
        pm = (PowerManager) activity.getApplication().getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.PARTIAL_WAKE_LOCK, TAG);

        return ret;
    }

    /**
     * 亮屏
     */
    public static void screenOn() {
        wakeLock.acquire();
        Log.d(TAG, "screenOn");
    }


    /**
     * 灭屏
     */
    public static void screenOff() {
        wakeLock.release();
        Log.d(TAG, "screenOff");
    }

    public static int close() {
        int ret = -1;

        return ret;
    }
}
