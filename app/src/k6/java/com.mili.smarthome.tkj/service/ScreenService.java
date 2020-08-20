package com.mili.smarthome.tkj.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;

import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.face.FaceMegviiOpenFragment;
import com.mili.smarthome.tkj.face.FaceWffrOpenFragment;
import com.mili.smarthome.tkj.fragment.MainFragment;
import com.mili.smarthome.tkj.fragment.MessageDialogFragment;
import com.mili.smarthome.tkj.fragment.ScreenProFragment;
import com.mili.smarthome.tkj.main.activity.MainActivity;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.utils.AppUtils;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.SystemSetUtils;


public class ScreenService extends Service {


    private String TAG = "ScreenService";
    private Handler handler;
    private ScreenProRun screenProRun;
    //屏保状态
    private CloseScreenRun closeScreenRun;
    private BackMainScreenRun backMainRun;

    //回退主界面 30
    private int backMainCount = 30;//设置界面30 其他15
    //屏保 120
    private int screenProCount = 120;
    //关屏 300
    private int sccreenCloseCount = 300;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return new MyBind();
    }

    private class MyBind extends Binder {

    }

    @Override
    public void onCreate() {
        handler = new Handler();
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.w(TAG + " onStartCommand ");
        if (intent != null) {
            //是否启用人脸
            getFaceState(intent);
            //屏保
            setScreenPro();
            //关屏
            setCloseScreen();
            //回到主界面
            setBackMainAct();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void setScreenPro() {
//        LogUtils.w(TAG + "setScreenPro Constant.ScreenId.SCREEN_PROTECT: " + Constant.ScreenId.SCREEN_PROTECT);
        if (Constant.ScreenId.SCREEN_PROTECT) {
            if (screenProRun != null) {
                handler.removeCallbacks(screenProRun);
            }
            screenProRun = new ScreenProRun(0);
            //开启屏保
            handler.postDelayed(screenProRun, 1000);
        } else {
            if (screenProRun != null) {
                handler.removeCallbacks(screenProRun);
            }
        }
    }

    private void setCloseScreen() {
        if (Constant.ScreenId.SCREEN_CLOSE) {
            if (closeScreenRun != null) {
                handler.removeCallbacks(closeScreenRun);
            }
            closeScreenRun = new CloseScreenRun(0);
            //开启关屏
            handler.postDelayed(closeScreenRun, 1000);
        } else {
            //关闭
            if (closeScreenRun != null) {
                handler.removeCallbacks(closeScreenRun);
            }
        }
    }

    class ScreenProRun implements Runnable {

        private int count;

        private ScreenProRun(int count) {
            this.count = count;
        }

        @Override
        public void run() {
//            LogUtils.w(TAG + " ScreenProRun run======" + count);
            Activity currentActivity = App.getInstance().getCurrentActivity();

            if (currentActivity instanceof MainActivity && ((MainActivity) currentActivity).currentFrag instanceof ScreenProFragment) {
                if (((MainActivity) currentActivity).mLinBottom.getVisibility() == View.VISIBLE) {
                    if (count == screenProCount && Constant.ScreenId.IS_SCREEN_SAVE) {
                        AppUtils.getInstance().sendReceiver(Constant.Action.ACTION_HIDE_BOTTOM_BTN);
                    }
                }
                handler.removeCallbacks(this);
                screenProCount = 0;
                return;
            }

            if (currentActivity instanceof MainActivity) {
                if (Constant.ScreenId.SCREEN_PROTECT &&
                        (((MainActivity) currentActivity).currentFrag instanceof MainFragment || ((MainActivity) currentActivity).currentFrag instanceof MessageDialogFragment) &&
                        screenProCount <= backMainCount) {
                    if (count == screenProCount) {
                        AppUtils.getInstance().sendReceiver(Constant.Action.ACTION_TO_SCREEN_PRO);
                        handler.removeCallbacks(this);
                        return;
                    }
                }
            }

            if (screenProCount > backMainCount && count == screenProCount && Constant.ScreenId.SCREEN_PROTECT) {
//                LogUtils.w(TAG+" ScreenProEnd======");
                //跳转到屏保界面
                AppUtils.getInstance().sendReceiver(Constant.Action.ACTION_TO_SCREEN_PRO);
                handler.removeCallbacks(this);
                return;
            }
            count++;
            handler.postDelayed(this, 1000);
        }
    }


    class CloseScreenRun implements Runnable {

        private int count;

        private CloseScreenRun(int count) {
            this.count = count;
        }

        @Override
        public void run() {
//            LogUtils.w(TAG + " CloseScreenRun======" + count);
            Activity currentActivity = App.getInstance().getCurrentActivity();
            if (currentActivity instanceof MainActivity) {
                if (Constant.ScreenId.SCREEN_CLOSE &&
                        (((MainActivity) currentActivity).currentFrag instanceof MainFragment || ((MainActivity) currentActivity).currentFrag instanceof MessageDialogFragment) &&
                        sccreenCloseCount <= backMainCount) {
                    if (count == sccreenCloseCount) {
                        //去关屏
                        toScreenOff();
                        handler.removeCallbacks(this);
                        return;
                    }
                }
            }
            if (count < screenProCount && Constant.ScreenId.IS_SCREEN_SAVE) {
                count = screenProCount;
            }
            if (sccreenCloseCount > backMainCount && count == sccreenCloseCount && Constant.ScreenId.SCREEN_CLOSE) {
//                LogUtils.w(TAG+" CloseScreenEnd======");
                toScreenOff();
                handler.removeCallbacks(this);
                return;
            }
            count++;
            handler.postDelayed(this, 1000);
        }
    }

    private void toScreenOff() {
        //去关屏
        SystemSetUtils.screenOff(getApplicationContext());
        SinglechipClientProxy.getInstance().cloudReboot();
        //关闭服务
        AppUtils.getInstance().stopScreenService();
        //如果在播放视频，下发图片，关闭屏保
        AppUtils.getInstance().sendReceiver(Const.ActionId.ACTION_MULTI_MEDIA);
    }

    private void setBackMainAct() {
        //回到主界面
        if (backMainRun != null) {
            handler.removeCallbacks(backMainRun);
            backMainRun = null;
        }
        backMainRun = new BackMainScreenRun(0);
        handler.postDelayed(backMainRun, 1000);
    }

    class BackMainScreenRun implements Runnable {

        private int count;

        private BackMainScreenRun(int count) {
            this.count = count;
        }

        @Override
        public void run() {
//            LogUtils.w(TAG + " BackMainScreenRun run======" + count + "=====isMainFragment：" + AppUtils.getInstance().isMainFragment());
            Activity currentActivity = App.getInstance().getCurrentActivity();
            if (currentActivity instanceof MainActivity && count == 15) {
                handler.removeCallbacks(this);
                if (((MainActivity) currentActivity).currentFrag instanceof FaceWffrOpenFragment
                        || ((MainActivity) currentActivity).currentFrag instanceof FaceMegviiOpenFragment) {
//                    LogUtils.w(TAG + " currentFrag: " + ((MainActivity) currentActivity).currentFrag.toString());
                    return;
                }
                //在mainactivity的底部点击切换界面， 每15秒无操作重新回到主界面
                if (!AppUtils.getInstance().isMainFragment() && !AppUtils.getInstance().isScreenProAct()) {
//                    LogUtils.w(TAG + " restartLauncherAct======");
                    //重新启动app
                    AppUtils.getInstance().restartLauncherAct();
                }
                return;
            }
            //在设置界面每30秒无操作重新回到主界面
            if (count == backMainCount) {
                handler.removeCallbacks(this);
                if (!AppUtils.getInstance().isMainFragment() && !AppUtils.getInstance().isScreenProAct()) {
//                    LogUtils.w(TAG + " restartLauncherAct======");
                    //重新启动app
                    AppUtils.getInstance().restartLauncherAct();
                }
                return;
            }
            count++;
            handler.postDelayed(this, 1000);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(backMainRun);
        handler.removeCallbacks(screenProRun);
        handler.removeCallbacks(closeScreenRun);
    }


    /**
     * 1.如果启用了人脸，并且屏保和关屏都启用时，进入主界面倒计时10秒进入屏保，5分钟后进入关屏（包括10秒）
     * 2.如果启用了人脸，屏保不启用，关屏启用时，进入主界面倒计时10秒进入关屏
     * 2.如果不启用了人脸，屏保启用和关屏启用时，进入主界面倒计时2分钟进入屏保，5分钟后进入关屏（包括2分钟）
     */
    public void getFaceState(Intent intent) {
        boolean extra = intent.getBooleanExtra(Constant.ScreenId.SCREEN_KEY,false);
        if (extra) {
            //马上进入关屏或屏保界面
            if (Constant.ScreenId.SCREEN_PROTECT) {
                //跳转到屏保界面
                AppUtils.getInstance().sendReceiver(Constant.Action.ACTION_TO_SCREEN_PRO);
                sccreenCloseCount = 300;
//                LogUtils.w(" getFaceState to screenProFrag...");
            } else if(Constant.ScreenId.SCREEN_CLOSE){
                //回到主界面
                AppUtils.getInstance().sendReceiver(Constant.Action.MAIN_REFRESH_ACTION);
                toScreenOff();
            }
        }else {
            if (Constant.ScreenId.SCREEN_BODY_STATE == 1) {
                if (Constant.ScreenId.SCREEN_PROTECT) {
                    screenProCount = 10;
                    sccreenCloseCount = 300;
                } else {
                    sccreenCloseCount = 10;
                }
            } else if(Constant.ScreenId.SCREEN_CLOSE){
                screenProCount = 120;
                sccreenCloseCount = 300;
            }
        }
    }
}
