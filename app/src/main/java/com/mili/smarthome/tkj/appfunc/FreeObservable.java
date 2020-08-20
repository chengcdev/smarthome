package com.mili.smarthome.tkj.appfunc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.android.client.SetDriverSinglechipClient;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.app.CustomVersion;
import com.mili.smarthome.tkj.main.activity.ScreenSaverActivity;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.SystemSetUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FreeObservable {

    public interface FreeObserver {
        boolean onFreeReport(long freeTime);
    }

    private static FreeObservable instance;
    public static FreeObservable getInstance() {
        if (instance == null) {
            synchronized (FreeObservable.class) {
                instance = new FreeObservable();
            }
        }
        return instance;
    }

    /** 超时进入屏保 */
    private static final int SCREEN_SAVER_TIMEOUT = 120;
    /** 超时关屏 */
    private static final int SCREEN_OFF_TIMEOUT = 300;

    /** 最后一次操作的时间 */
    private long mLastOperTime = SystemClock.uptimeMillis();
    private ScheduledExecutorService mScheduledExecutor;
    private List<FreeObserver> mFreeObservers = new ArrayList<>();

    private FreeObservable() {
    }

    public void addObserver(FreeObserver observer) {
        LogUtils.d("---FreeObservable>>>addObserver");
        mFreeObservers.remove(observer);
        mFreeObservers.add(0, observer);
    }

    public void removeObserver(FreeObserver observer) {
        LogUtils.d("---FreeObservable>>>removeObserver");
        mFreeObservers.remove(observer);
    }

    /** 观察空闲时间 */
    public final void observeFree() {
        LogUtils.d("---FreeObservable>>>observeFree");
        mLastOperTime = SystemClock.uptimeMillis();
        if (mScheduledExecutor == null || mScheduledExecutor.isShutdown()) {
            mScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
            mScheduledExecutor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    onFreeReport(getFreeTime());
                }
            }, 1, 1, TimeUnit.SECONDS);
        }
    }

    /** 取消观察空闲时间 */
    public final void cancelObserveFree() {
        LogUtils.d("---FreeObservable>>>cancelObserveFree");
        if (mScheduledExecutor != null) {
            mScheduledExecutor.shutdown();
            mScheduledExecutor = null;
        }
    }

    /** 重置空闲时间 */
    public final void resetFreeTime() {
        //LogUtils.d("--->>>resetFreeTime");
        mLastOperTime = SystemClock.uptimeMillis();
    }

    /** 获取空闲时间 */
    public final long getFreeTime() {
        return SystemClock.uptimeMillis() - mLastOperTime;
    }

    /**
     * 空闲时间上报
     * @param freeTime 空闲时间(单位毫秒)
     */
    private void onFreeReport(long freeTime) {
        //LogUtils.d("--->>>onFreeReport: " + freeTime);
        long freeSecond = freeTime / 1000;
        if (freeSecond == SCREEN_OFF_TIMEOUT) {
            if (AppConfig.getInstance().getPowerSaving() == 1) {
                LogUtils.d("---FreeObservable >>> onFreeReport: SCREEN_OFF_TIMEOUT");
                systemSleep();
            }
//            SinglechipClientProxy.getInstance().cloudReboot();
        } else if (freeSecond == SCREEN_SAVER_TIMEOUT) {
            LogUtils.d("---FreeObservable >>> onFreeReport: SCREEN_SAVER_TIMEOUT");
            if (AppConfig.getInstance().getScreenSaver() == 1) {
                startScreenSaver();
            }
        }
        for (FreeObserver observer : mFreeObservers) {
            if (observer.onFreeReport(freeTime)) {
                break;
            }
        }
    }

    public void startScreenSaver() {
        if (BuildConfigHelper.isK4_X1600_GATE()) {
            return;
        }
        // 进入屏保界面
        Activity activity = App.getInstance().getCurrentActivity();
        if (activity instanceof ScreenSaverActivity) {
            return;
        }
        activity.startActivity(new Intent(Const.Action.SCREEN_SAVER));
    }

    public void systemSleep() {
        // 取消计时
        cancelObserveFree();
        // 关闭屏保界面
        ContextProxy.sendBroadcast(Const.ActionId.SCREEN_SAVER_EXIT);
        // 关屏
        if (BuildConfigHelper.isHorizon()) {
            if (SetDriverSinglechipClient.getInstance().getSystemSleep()) {
                SetDriverSinglechipClient.getInstance().setSystemSleep(0);
            }
        } else {
            SystemSetUtils.screenOff(ContextProxy.getContext());
            SinglechipClientProxy.getInstance().ctrlTouchKeyLampState(false);
        }

        /* 关屏时开启人脸识别界面20200320 */
        if (CustomVersion.VERSION_K3_SCREENOFF_FACE_RECOGNIZE) {
            if (AppConfig.getInstance().getBodyInduction() == 1) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.d("=======FreeObservable>>> screen off, open face ui ====== ");
                        Bundle bundle = new Bundle();
                        bundle.putInt("screen_off_type", 1);
                        ContextProxy.sendBroadcast(Const.Action.MAIN_FACE, bundle);
                    }
                }, 2000);
            }
        }
    }
}
