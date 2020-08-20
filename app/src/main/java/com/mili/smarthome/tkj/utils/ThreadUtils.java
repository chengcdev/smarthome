package com.mili.smarthome.tkj.utils;

import android.os.Looper;

/**
 * 2018-03-20: Created by zenghm.
 */

public final class ThreadUtils {

    /** 当前线程是否是主线程 */
    public static boolean isMainThread() {
        return Thread.currentThread().getId() == Looper.getMainLooper().getThread().getId();
    }

    /**
     * @return 线程被中断则返回false，否则返回true
     */
    public static boolean sleep(long millis) {
        try {
            Thread.sleep(millis);
            return true;
        } catch (InterruptedException ex) {
            LogUtils.printThrowable(ex);
            return false;
        }
    }
}
