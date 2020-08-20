package com.mili.smarthome.tkj.app;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 任务执行类
 * <p>2020-01-07 13:55  create by zenghm
 */
public class AppExecutors {

    private static final Executor mainThread;
    private static final Executor diskIO;
    private static final Executor newThread;
    private static final ScheduledExecutorService scheduler;

    static {
        mainThread = new MainThreadExecutor();
        diskIO = Executors.newSingleThreadExecutor();
        newThread = Executors.newCachedThreadPool();
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * 主线程（UI线程）
     */
    public static Executor mainThread() {
        return mainThread;
    }

    /**
     * 磁盘读写线程，用于数据库、文件等异步读写操作
     */
    public static Executor diskIO() {
        return diskIO;
    }

    /**
     * 创建新线程用于执行异步任务
     */
    public static Executor newThread() {
        return newThread;
    }

    /**
     * 定时线程，用于执行延迟或定时任务
     */
    public static ScheduledExecutorService scheduler() {
        return scheduler;
    }


    private static class MainThreadExecutor implements Executor {

        private Handler mMainHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mMainHandler.post(command);
        }
    }
}
