package com.mili.smarthome.tkj.face.megvii.utils;

import java.util.concurrent.Semaphore;

/**
 * Created by chenrh on 2019/4/23.
 */

public class SemaphoreUtils {

    private static SemaphoreUtils mInstance = null;
    /* 视频帧数据处理信号量状态 */
    private Semaphore sem = null;

    private SemaphoreUtils() {
        sem = new Semaphore(1, true);
    }

    /*
     * 得到类对象实例（因为只能有一个这样的类对象，所以用单例模式）
     */
    public static SemaphoreUtils getInstance() {
        if (mInstance == null) {
            mInstance = new SemaphoreUtils();
        }
        return mInstance;
    }

    public boolean tryAcquire() {
        if (sem != null) {
            return sem.tryAcquire();
        }
        return false;
    }

    public void release() {
        if (sem != null) {
            sem.tryAcquire();
            sem.release();
        }
    }
}
