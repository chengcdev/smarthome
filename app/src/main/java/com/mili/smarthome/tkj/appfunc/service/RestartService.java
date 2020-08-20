package com.mili.smarthome.tkj.appfunc.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import com.android.client.MainClient;
import com.android.client.SetDriverSinglechipClient;
import com.android.main.MainCommDefind;
import com.mili.smarthome.tkj.appfunc.facefunc.WffrFaceDbManager;
import com.mili.smarthome.tkj.utils.SystemSetUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

/**
 * 开机时间同步、半夜重启服务
 * Created by zhengxc on 2018/9/10 0010.
 */

public class RestartService extends Service {

    public final static String TAG = "RestartService";
    public final static String REQ_SYN_TIME = "android.app.main.REQ_SYN_TIME";
    public final static String NIGHT_RRBOOT_FILE_PATH = "/mnt/sdcard/isNightReboot.dat";

    private Context mContext;
    private Handler mHandler;
    private int mDelayTime;
    private boolean isMainActivityDestory = false;

    /** 是否将要重启 */
    public static boolean mRestartFlag = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        IntentFilter filter = new IntentFilter();
        filter.addAction(REQ_SYN_TIME);
        registerReceiver(mReceiver, filter);    // 上电请求时间同步

        // 改到子线程上处理消息，减轻主线程压力。 by zenghm
        HandlerThread handlerThread = new HandlerThread("RestartService#HandlerThread");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());

        new TimeoutThread().start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private class TimeoutThread extends Thread {

        private int restartFlag = 0;

        public TimeoutThread() {
            super("RestartService#TimeoutThread");
        }

        @Override
        public void run() {
            try {
                if (getNightRebootFlag()) {
                    //半夜重启发送请求OTA升级
                    mHandler.postDelayed(nightRebootOtaUpdate, 60 * 1000);
                }

                while (true) {
                    sleep(10000);

                    if (MainCommDefind.mainActivity == null && MainCommDefind.mainClient == null) {
                        debug("===========RebootSystem " + MainCommDefind.mainActivity + " " + MainCommDefind.mainClient);
                        isMainActivityDestory = true;
                        mHandler.postDelayed(RebootSystem, 1000 * 60);
                    } else {
                        isMainActivityDestory = false;
                    }

                    GregorianCalendar calendar = new GregorianCalendar();
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);

                    if (hour == 1) {
                        restartFlag = 1;
                    }
                    if (hour == 2 && restartFlag != 0) {
                        restartFlag = 0;
                        if (MainCommDefind.mainClient != null) {
                            Random random = new Random(MainCommDefind.mainClient.Main_GetRandomSeed());
                            mDelayTime = (random.nextInt(7200)) * 1000;  // 生成0-7200秒的随机数
    //                        mDelayTime = 1000;  // 测试时使用2点后1S
                        } else {
                            mDelayTime = 1000;
                        }
                        debug("delaytime is " + mDelayTime);
                        mHandler.postDelayed(restartPretreatTask, mDelayTime);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Runnable restartPretreatTask = new Runnable() {
        @Override
        public void run() {
            // 延迟30秒执行重启
            mRestartFlag = true;
            mHandler.postDelayed(restartDelay, 30 * 1000);

            // 通知底层正在进行人脸文件备份
            MainClient.getInstance().Main_SetFaceBackUpState(1);
            // 备份人脸数据库
            WffrFaceDbManager.uploadAsync(mContext);
            // 设置半夜重启标志
            try {
                setNightRebootFlag();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private Runnable restartDelay = new Runnable() {
        @Override
        public void run() {
            debug("MainCommDefind.mainClient.Reboot_SetLcd = 0");
            SetDriverSinglechipClient setDriverSinglechipClient = new SetDriverSinglechipClient();
            setDriverSinglechipClient.rebootSystem();
//            setDriverSinglechipClient.stopFeeddogReboot();

            mHandler.postDelayed(Reboot5Min, 600 * 1000);
        }
    };

    private Runnable Reboot5Min = new Runnable() {
        @Override
        public void run() {
            debug(">>>>>>> Reboot5Min >>>>>>");
            SystemSetUtils.rebootDevice();
        }
    };

    private Runnable RebootSystem = new Runnable() {
        @Override
        public void run() {
            if (isMainActivityDestory) {
                isMainActivityDestory = false;
                if (MainCommDefind.mainActivity == null && MainCommDefind.mainClient == null) {
                    debug("===========Reboot main==null || mainClient == null again");
                    SystemSetUtils.rebootDevice();
                }
            }
        }
    };

    // 上电请求上位机时间同步
    private Runnable ReqSynTimetDelay = new Runnable() {
        @Override
        public void run() {
            debug("ReqSynTimetDelay");
            if (MainCommDefind.mainClient != null) {
                MainCommDefind.mainClient.Main_SendReqSynTime();
            }
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (REQ_SYN_TIME.equals(action)) {
                int time = (int) (Math.random() * 30) * 1000;
                mHandler.postDelayed(ReqSynTimetDelay, time);
            }
        }
    };

    private void debug(String msg) {
        Log.d(TAG, msg);
    }

    private Runnable nightRebootOtaUpdate = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "nightRebootOtaUpdate Runnable");
            if (ConnectivityManager.TYPE_NONE != SystemSetUtils.getNetType(mContext)) {
                SystemSetUtils.systemUpgrade(1);
            } else {
                mHandler.postDelayed(this, 30 * 1000);
            }
        }
    };

    private void setNightRebootFlag() throws IOException {
        File file = new File(NIGHT_RRBOOT_FILE_PATH);
        String string = "1";
        if(!file.exists()) {
            FileOutputStream fos = new FileOutputStream(file);
            byte [] bytes = string.getBytes();
            fos.write(bytes);
            fos.flush();
            fos.getFD().sync();
            fos.close();
        }
        Log.d(TAG, NIGHT_RRBOOT_FILE_PATH + "=" + file.exists());
    }

    private boolean getNightRebootFlag() throws IOException{
        File file = new File(NIGHT_RRBOOT_FILE_PATH);
        if(file.exists()) {
            file.delete();
            Log.d(TAG, "getNightRebootFlag = true");
            return true;
        } else {
            Log.d(TAG, "getNightRebootFlag = false");
            return false;
        }
    }
}
