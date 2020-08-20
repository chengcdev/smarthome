package com.mili.smarthome.tkj.appfunc.service;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.android.client.SetDriverSinglechipClient;
import com.mili.smarthome.tkj.utils.LogUtils;

/**
 * 喂狗线程
 * <p>2020-01-09 19:59  create by zenghm
 */
public final class FeedDog {

    private static final Object LOCK = new Object();
    private static FeedDogTask mTask;

    public static void start() {
        synchronized (LOCK) {
            if (mTask != null && mTask.isRunning()) {
                return;
            }
            mTask = new FeedDogTask();
            new Thread(mTask).start();
        }
    }

    public static void stop() {
        synchronized (LOCK) {
            if (mTask != null) {
                mTask.interrupt();
                mTask = null;
            }
        }
    }

    /**
     * 喂狗任务
     */
    private static class FeedDogTask implements Runnable {

        private volatile boolean running = true;

        private Handler feedDooghandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0x01:
                        LogUtils.d("FeedDoogRunnable handler heat dog");
                        SetDriverSinglechipClient.getInstance().sendFeedDogHeat();
                        break;
                }
            }
        };

        @Override
        public void run() {
            try {
                //解决APP一直处于闪退状态起不来情况下，每次APP起来都会喂一下狗，导致系统不会重启
                Thread.sleep(15000);
                while (isRunning()) {
                    feedDooghandler.sendEmptyMessage(0x01);
                    Thread.sleep(5000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public boolean isRunning() {
            return running;
        }

        public void interrupt() {
            running = false;
        }
    }

    private FeedDog() {

    }
}
