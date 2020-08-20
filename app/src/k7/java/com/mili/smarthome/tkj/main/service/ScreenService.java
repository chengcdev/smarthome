package com.mili.smarthome.tkj.main.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.android.client.SetDriverSinglechipClient;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.main.activity.MainActivity;
import com.mili.smarthome.tkj.main.activity.ScreenSaverActivity;
import com.mili.smarthome.tkj.main.activity.direct.DirectPressMainActivity;
import com.mili.smarthome.tkj.main.face.activity.BaseFaceActivity;
import com.mili.smarthome.tkj.main.fragment.MainFragment;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.set.fragment.MessageDialogFragment;
import com.mili.smarthome.tkj.utils.AppManage;
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
    private int backMainCount = 30; //设置界面30 其他15
    //屏保 120
    private int screenProCount = 120;
    //关屏 300
    private int sccreenCloseCount = 300;
    private Context mContext;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return new MyBind();
    }

    private class MyBind extends Binder {

    }

    @Override
    public void onCreate() {
        mContext = App.getInstance();
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
        if (AppConfig.getInstance().getScreenSaver() == 1) {
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
        if (AppConfig.getInstance().getPowerSaving() == 1) {
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
//            LogUtils.w(TAG + " >>ScreenPro count: " + count);
            Activity currentActivity = App.getInstance().getCurrentActivity();
            Fragment fragment = AppManage.getInstance().frgCurrent;
            if (currentActivity instanceof ScreenSaverActivity) {
                return;
            }
            if (AppConfig.getInstance().getCallType() == 0) {
                if (currentActivity instanceof MainActivity && !Constant.ScreenId.SCREEN_IS_SET) {
                    if (AppConfig.getInstance().getScreenSaver() == 1 && count == screenProCount) {
                        if (fragment instanceof MainFragment) {
                            if ((((MainFragment) fragment).numview != null &&
                                    !((MainFragment) fragment).numview.getNum().equals("")) ||
                                    ((MainFragment) fragment).adminCount > 0) {
                                return;
                            }
                        }
                        handler.removeCallbacks(this);
                        //跳转到屏保
                        AppManage.getInstance().toAct(ScreenSaverActivity.class);
                        return;
                    }
                }
            } else {
                if (currentActivity instanceof DirectPressMainActivity && !DirectPressMainActivity.isEdit) {
                    if (AppConfig.getInstance().getScreenSaver() == 1 && count == screenProCount) {
                        handler.removeCallbacks(this);
                        //跳转到屏保
                        AppManage.getInstance().toAct(ScreenSaverActivity.class);
                        return;
                    }
                }
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
//            LogUtils.w(TAG + " >>CloseScreen count: " + count);
            Activity currentActivity = App.getInstance().getCurrentActivity();
            Fragment fragment = AppManage.getInstance().frgCurrent;
            if (Constant.ScreenId.IS_SCREEN_SAVE && count < screenProCount) {
                count = screenProCount;
            }

            if (AppConfig.getInstance().getCallType() == 0) {
                if ((currentActivity instanceof MainActivity && !Constant.ScreenId.SCREEN_IS_SET)
                        || currentActivity instanceof ScreenSaverActivity) {
                    if (AppConfig.getInstance().getPowerSaving() == 1 && count == sccreenCloseCount) {
                        if (fragment instanceof MainFragment) {
                            if ((((MainFragment) fragment).numview != null &&
                                    !((MainFragment) fragment).numview.getNum().equals("")) ||
                                    ((MainFragment) fragment).adminCount > 0) {
                                return;
                            }
                        }
                        handler.removeCallbacks(this);
                        //去关屏
                        toScreenOff();
                        return;
                    }
                }
            } else {
                if (currentActivity instanceof DirectPressMainActivity && !DirectPressMainActivity.isEdit) {
                    if (AppConfig.getInstance().getPowerSaving() == 1 && count == sccreenCloseCount) {
                        handler.removeCallbacks(this);
                        //去关屏
                        toScreenOff();
                        return;
                    }
                }
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
        AppManage.getInstance().stopScreenService();
        //复位以太网
        SetDriverSinglechipClient.getInstance().phyPwrCtrlReset();
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
            LogUtils.w(TAG + " >>BackMainScreen count: " + count);
            Activity currentActivity = App.getInstance().getCurrentActivity();
            Fragment fragment = AppManage.getInstance().frgCurrent;

            if (currentActivity instanceof ScreenSaverActivity || currentActivity instanceof BaseFaceActivity) {
                handler.removeCallbacks(this);
                return;
            }

            if (Constant.ScreenId.SCREEN_IS_SET) {
                backMainCount = 30;
            } else {
                backMainCount = 15;
            }

            if (count == backMainCount) {
                handler.removeCallbacks(this);
                if (AppConfig.getInstance().getCallType() == 0) {
                    if (currentActivity instanceof MainActivity) {
                        if (fragment instanceof MainFragment) {
                            if ((((MainFragment) fragment).numview != null &&
                                    !((MainFragment) fragment).numview.getNum().equals("")) ||
                                    ((MainFragment) fragment).adminCount > 0) {
                                //主界面刷新
                                AppManage.getInstance().sendReceiver(Constant.ActionId.ACTION_MAIN_FRAGMENT_NOTIFY);
                            }
                        } else {
                            if (fragment instanceof MessageDialogFragment) {
                                return;
                            }
                            //回到主界面
                            AppManage.getInstance().restartLauncherAct();
                        }
                    } else {
                        //回到主界面
                        AppManage.getInstance().restartLauncherAct();
                    }
                } else {
                    if (currentActivity instanceof DirectPressMainActivity && !DirectPressMainActivity.isEdit) {
                        return;
                    } else {
                        //回到主界面
                        AppManage.getInstance().restartLauncherAct();
                    }
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
        boolean extra = intent.getBooleanExtra(Constant.ScreenId.SCREEN_KEY, false);
        if (extra) {
            //马上进入关屏或屏保界面
            if (AppConfig.getInstance().getScreenSaver() == 1) {
                //跳转到屏保界面
                AppManage.getInstance().toAct(ScreenSaverActivity.class);
                sccreenCloseCount = 300;
            } else if (AppConfig.getInstance().getPowerSaving() == 1) {
                //回到主界面
                AppManage.getInstance().restartLauncherAct();
                toScreenOff();
            }
        } else {
            if (AppConfig.getInstance().getBodyInduction() == 1) {
                if (AppConfig.getInstance().getScreenSaver() == 1) {
                    screenProCount = 10;
                    sccreenCloseCount = 300;
                } else {
                    sccreenCloseCount = 10;
                }
            } else if (AppConfig.getInstance().getPowerSaving() == 1) {
                screenProCount = 120;
                sccreenCloseCount = 300;
            }
        }
    }


}
