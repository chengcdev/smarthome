package com.mili.smarthome.tkj.appfunc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.android.Common;
import com.android.client.MainClient;
import com.android.client.SetDriverSinglechipClient;
import com.android.main.MainCommDefind;
import com.mili.smarthome.tkj.BuildConfig;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.app.AppPreferences;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.appfunc.facefunc.FacePresenterProxy;
import com.mili.smarthome.tkj.appfunc.service.RestartService;
import com.mili.smarthome.tkj.base.BaseActivity;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.SystemSetUtils;
import com.mili.smarthome.tkj.utils.ThreadUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;

public class WelcomeActivity extends BaseActivity {
    private boolean mReboot = true;
    private boolean mIsFeedDog = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        new Thread(new InitializationTask()).start();
        new Thread(new FeedDoogRunnable()).start();
        if ((BuildConfig.DevType == MainCommDefind.DEVICE_TYPE_K3_REL)
                || (BuildConfig.DevType == MainCommDefind.DEVICE_TYPE_K4_REL)
                || (BuildConfig.DevType == MainCommDefind.DEVICE_TYPE_K6_REL)) {
//            new Thread(new SystemBootUpRunnable()).start();
        }
//        new Thread(new rebootRunnable()).start();
    }

    private class InitializationTask implements Runnable {
        @Override
        public void run() {
            // 初始化App
            mIsFeedDog = App.getInstance().initialize();

            if (MainCommDefind.mainClient == null) {
                MainCommDefind.mainClient = MainClient.getInstance();
                MainCommDefind.mainActivity = WelcomeActivity.this;
            }

            if (AppPreferences.isReset()) {
                // 恢复出厂后首次启动
                // 完成初始化配置后，请调用 {@link AppPreferences.setReset(boolean)}修改标志
                Intent intent = new Intent(Const.Action.RESET);
                startActivity(intent);
            } else {
                Intent intent = new Intent(Const.Action.MAIN);
                startActivity(intent);
            }
            finish();

            ThreadUtils.sleep(5000);
            syncTime();
        }
    }

    /**
     * 时间同步
     */
    private void syncTime() {
        if (MainCommDefind.mainClient == null) {
            MainCommDefind.mainClient = MainClient.getInstance();
            MainCommDefind.mainActivity = WelcomeActivity.this;
        }
        Common.SendBroadCast(App.getInstance(), new Intent(RestartService.REQ_SYN_TIME));

        FacePresenterProxy.registerFaceType();

        String softverString = BuildConfigHelper.getSoftWareVer();
        String hardverString = BuildConfigHelper.getHardWareVer();
        LogUtils.d("softverString = " + softverString + ", hardverString = " + hardverString);
        MainClient.getInstance().Register_Center(softverString, hardverString);
    }

    /**
     * 喂狗线程
     */
    private class FeedDoogRunnable implements Runnable {
        private boolean isFirst = true;

        @Override
        public void run() {
            while (mIsFeedDog) {
                if (isFirst) {
                    LogUtils.w("FeedDoogRunnable is first sleep 15 S");
                    //解决APP一直处于闪退状态起不来情况下，每次APP起来都会喂一下狗，导致系统不会重启
                    ThreadUtils.sleep(15000);
                    isFirst = false;
                }

                Message message = Message.obtain();
                message.what = 0x01;
                feedDooghandler.sendMessage(message);

//                LogUtils.w("FeedDoogRunnable sendMessage");

                ThreadUtils.sleep(5000);
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler feedDooghandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x01:
                    LogUtils.d("FeedDoogRunnable handler heat dog");
                    SetDriverSinglechipClient.getInstance().sendFeedDogHeat();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 开机2分钟内没收到系统触摸事件重启设备,解决系统上电起来后逻辑功能都正常UI显示不出来问题
     */
    private class SystemBootUpRunnable implements Runnable {
        private int bootUpCount = 0;

        @Override
        public void run() {
            long startTime = SystemClock.elapsedRealtime();
            LogUtils.w("SystemBootUpRunnable startTime = " + startTime/1000 + "S");
            if (startTime > 60*1000) {  // 系统开机后60S就不做app起来检测触摸屏重启设备，避免APP闪退后30s没收到触摸事件导致系统重启
                LogUtils.w("SystemBootUpRunnable system boot up pass 60s return");
                return;
            }

            while (true) {
                bootUpCount ++;
                LogUtils.w("SystemBootUpRunnable bootUpCount = " + bootUpCount);

                if (Const.SystemBootUpTouchEvent.SYSTEM_BOOT_UP_HAVE_TOUCH_ENEVT) {
                    LogUtils.w("SystemBootUpRunnable have touch event return");
                    return;
                }

                if (bootUpCount >= 120) {//检测2分钟
                    LogUtils.w("SystemBootUpRunnable bootUpCount > 30 reboot system");
                    // 系统开机30秒内没有收到触摸事件重启系统
                    SetDriverSinglechipClient setDriverSinglechipClient = new SetDriverSinglechipClient();
                    setDriverSinglechipClient.rebootSystem();

                    mMainHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            LogUtils.w("SystemBootUpRunnable pass 30s reboot");
                            SystemSetUtils.rebootDevice();
                        }
                    }, 30000);
                }
                ThreadUtils.sleep(1000);
            }
        }
    }

    private void writeRebootCount(){
        String path = "/mnt/sdcard/appRebootCount.txt";
        int count = readRebootCount(path);
        Writer writer = null;
        try{
            if (count <= 3000){
                File file = new File(path);
                writer = new FileWriter(file);
                String data=String.valueOf(count);
                writer.write(data);
                LogUtils.e("write rebootCount: "+count);
            }
            else {
                mReboot = false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                writer.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private int readRebootCount(String filePath){
        int count = 0;
        BufferedReader bufferedReader = null;
        try{
            File file = new File(filePath);
            if (!file.exists())
            {
                file.createNewFile();
            }

            bufferedReader = new BufferedReader(new FileReader(filePath));
            String strCount = bufferedReader.readLine();
            if (strCount != null && strCount.trim().length() != 0){
                count = Integer.parseInt(strCount);
            }
            LogUtils.e("Read rebootCount: "+count);
            count++;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                bufferedReader.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return count;
    }
    /**
     * 软重启线程
     */
    private class rebootRunnable implements Runnable {
        @Override
        public void run() {
            writeRebootCount();
            if (mReboot){
                ThreadUtils.sleep(30*1000);
                SetDriverSinglechipClient setDriverSinglechipClient = new SetDriverSinglechipClient();
                setDriverSinglechipClient.rebootSystem();
            }
        }
    }
}
