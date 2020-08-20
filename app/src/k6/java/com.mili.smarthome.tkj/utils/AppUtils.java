package com.mili.smarthome.tkj.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.app.AppPreferences;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.face.FaceMegviiOpenFragment;
import com.mili.smarthome.tkj.face.FaceWffrOpenFragment;
import com.mili.smarthome.tkj.fragment.CallCenterFragment;
import com.mili.smarthome.tkj.fragment.CallResidentFragment;
import com.mili.smarthome.tkj.fragment.MainFragment;
import com.mili.smarthome.tkj.fragment.MessageDialogFragment;
import com.mili.smarthome.tkj.fragment.ScreenProFragment;
import com.mili.smarthome.tkj.main.activity.MainActivity;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.service.ScreenService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

public class AppUtils {

    private static volatile AppUtils appUtils;
    private ArrayList<Activity> actLists;
    private static boolean isEnableLed;

    public static AppUtils getInstance() {
        if (appUtils == null) {
            synchronized (AppUtils.class) {
                if (appUtils == null) {
                    appUtils = new AppUtils();
                }
            }
        }
        return appUtils;
    }

    /**
     * 发送广播
     */
    public void sendReceiver(String action) {
        Intent intent = new Intent(action);
        App.getInstance().sendBroadcast(intent);
    }

    /**
     * 发送广播
     */
    public void sendReceiver(String action,String key,String value) {
        Intent intent = new Intent(action);
        intent.putExtra(key, value);
        App.getInstance().sendBroadcast(intent);
    }

    /**
     * 跳转到某个activity
     */
    public void toAct(Context context, Class c) {
        Intent intent = new Intent(context, c);
        context.startActivity(intent);
    }

    /**
     * 跳转到某个activity ,带String类型的参数
     */
    public void toAct(Context context, Class c, String key, String value) {
        Intent intent = new Intent(context, c);
        intent.putExtra(key, value);
        context.startActivity(intent);
    }

    /**
     * 跳转到某个activity，并关闭当前的activity
     */
    public void toActFinish(Context context, Class c) {
        Intent intent = new Intent(context, c);
        context.startActivity(intent);
        Activity activity = (Activity) context;
        activity.finish();
    }

    /**
     * 重新回到主界面
     */
    public void restartLauncherAct() {
        Activity currentActivity = App.getInstance().getCurrentActivity();
        String className = currentActivity.getComponentName().getClassName();

        String setPkgName = "com.mili.smarthome.tkj.main.activity.MainActivity";
        if (className.equals(setPkgName)) {
//            //主activity
            MainActivity activity = (MainActivity) currentActivity;
            //如果是信息弹窗不做处理
            if (activity.currentFrag instanceof MessageDialogFragment) {
                return;
            }
            activity.initBottomBtn();
        } else {
            sendReceiver(Constant.Action.MAIN_REFRESH_ACTION);
            //跳转到主界面
            Intent intent = new Intent(currentActivity, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            App.getInstance().startActivity(intent);
            currentActivity.finish();
        }
    }


    /**
     * 如果当前在屏保界面或者关屏状态下，回到主界面
     */
    public void toLauncherAct() {
        //当前栈顶的activity若是屏保
        Activity currentActivity = App.getInstance().getCurrentActivity();
        String className = currentActivity.getComponentName().getClassName();

        if (className.equals("com.mili.smarthome.tkj.main.activity.ScreenSaverActivity") || !SystemSetUtils.isScreenOn()) {
            if (!SystemSetUtils.isScreenOn()) {
                //亮屏
                SystemSetUtils.screenOn();
            }
            //回到主界面
            Intent intent = new Intent(App.getInstance(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            App.getInstance().startActivity(intent);
        }
    }

    /**
     * 添加一个activity到集合
     */
    public void addAct(Activity activity) {
        if (actLists == null) {
            actLists = new ArrayList<>();
        }
        actLists.add(activity);
    }

    /**
     * 清空集合下的所有activity
     */
    public void clearAct() {
        if (actLists != null && actLists.size() > 0) {
            for (int i = 0; i < actLists.size(); i++) {
                actLists.get(i).finish();
            }
            actLists.clear();
        }
    }


    /**
     * 替换当前的fragment
     */
    public void replaceFragment(FragmentActivity activity, Fragment fragment, int layoutId, String flag) {
        LogUtils.w(" replaceFragment flag: " + flag);
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(layoutId, fragment);
        ft.addToBackStack(flag);
        ft.commitAllowingStateLoss();
    }

    /**
     * 替换当前的fragment
     */
    public void replaceFragment(FragmentActivity activity, Fragment fragment, int layoutId) {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(layoutId, fragment);
        ft.commitAllowingStateLoss();
    }

    /**
     * 替换当前的fragment
     *
     * @value String
     */
    public void replaceFragment(FragmentActivity activity, Fragment fragment, int layoutId, String flag, String key, String value) {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString(key, value);
        fragment.setArguments(bundle);
        ft.replace(layoutId, fragment);
        ft.addToBackStack(flag);
        ft.commitAllowingStateLoss();
    }

    /**
     * 替换当前的fragment
     *
     * @value Serializable
     */
    public void replaceFragment(FragmentActivity activity, Fragment fragment, int layoutId, String flag, String key, Serializable value) {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable(key, value);
        fragment.setArguments(bundle);
        ft.replace(layoutId, fragment);
        ft.addToBackStack(flag);
        ft.commitAllowingStateLoss();
    }

    /**
     * 添加当前的fragment
     */
    public void addFragment(FragmentActivity activity, Fragment fragment, int layoutId, String flag, String key, String value) {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString(key, value);
        fragment.setArguments(bundle);
        ft.replace(layoutId, fragment);
        ft.addToBackStack(flag);
        ft.commitAllowingStateLoss();
    }

    /**
     * 添加当前的fragment
     */
    public void addFragment(FragmentActivity activity, Fragment fragment, int layoutId, String flag) {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(layoutId, fragment);
        ft.addToBackStack(flag);
        ft.commitAllowingStateLoss();
    }

    /**
     * 删除当前的fragment
     */
    public void removeFragment(FragmentActivity activity, Fragment fragment) {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fragment);
        ft.commitAllowingStateLoss();
    }

    /**
     * 开启屏保和关屏服务
     */
    public void startScreenService() {
        //是否恢复出厂第一次启动 ，是否是关屏,是否在人脸界面,是否屏保界面
        if (!AppPreferences.isReset()
                && SystemSetUtils.isScreenOn()
                && !isCalling()
                && !isFaceWffrOpenFrg()
                && !isScreenProAct()) {
            Intent intent = new Intent(App.getInstance(), ScreenService.class);
            App.getInstance().startService(intent);
        }
    }

    /**
     * 马上开启屏保或者关屏
     */
    public void openScreenService() {
        //是否恢复出厂第一次启动 ，是否是关屏,是否在人脸界面,是否屏保界面
        Intent intent = new Intent(App.getInstance(), ScreenService.class);
        intent.putExtra(Constant.ScreenId.SCREEN_KEY, true);
        App.getInstance().startService(intent);
    }

    /**
     * 更新屏幕服务
     */
    public void refreshScreenService() {
        Intent intent = new Intent(App.getInstance(), ScreenService.class);
        App.getInstance().startService(intent);
    }

    /**
     * 关闭屏保和关屏服务
     */
    public void stopScreenService() {
        Intent intent = new Intent(App.getInstance(), ScreenService.class);
        App.getInstance().stopService(intent);
    }

    /**
     * 设置界面,人脸界面不做跳转，只播声音
     */
    public boolean isToTipAct() {
        Activity currentActivity = App.getInstance().getCurrentActivity();
        if (currentActivity instanceof MainActivity) {
            //人脸界面
            if (((MainActivity) currentActivity).currentFrag instanceof FaceWffrOpenFragment || ((MainActivity) currentActivity).currentFrag instanceof FaceMegviiOpenFragment) {
                return true;
            }
        }
        String className = currentActivity.getComponentName().getClassName();
        //设置界面
        String setPkgName1 = "com.mili.smarthome.tkj.setting.activity.SettingActivity";
        //恢复出厂后启动的设置界面
        String setPkgName2 = "com.mili.smarthome.tkj.main.activity.ResetActivity";
        if (className.equals(setPkgName1) || className.equals(setPkgName2)) {
            return true;
        }
        return false;
    }

    /**
     * 设置和屏保界面
     */
    public boolean isSetAndScreenProAct() {
        Activity currentActivity = App.getInstance().getCurrentActivity();
        String className = currentActivity.getComponentName().getClassName();
        //设置界面
        String setPkgName1 = "com.mili.smarthome.tkj.setting.activity.SettingActivity";
        //屏保界面
        String setPkgName2 = " com.mili.smarthome.tkj.main.activity.ScreenSaverActivity";
        if (className.equals(setPkgName1) || className.equals(setPkgName2)) {
            return true;
        }
        return false;
    }


    /**
     * 主界面Activity
     */
    public boolean isMainAct() {
        Activity currentActivity = App.getInstance().getCurrentActivity();
        String className = currentActivity.getComponentName().getClassName();
        //主界面
        String setPkgName = "com.mili.smarthome.tkj.main.activity.MainActivity";
        if (className.equals(setPkgName)) {
            return true;
        }
        return false;
    }

    /**
     * 主界面Fragment
     */
    public boolean isMainFragment() {
        Activity currentActivity = App.getInstance().getCurrentActivity();
        String className = currentActivity.getComponentName().getClassName();
        //主界面
        String setPkgName = "com.mili.smarthome.tkj.main.activity.MainActivity";
        if (className.equals(setPkgName)) {
            try {
                MainActivity activity = (MainActivity) currentActivity;
                if (activity.currentFrag instanceof MainFragment) {
                    return true;
                }
            } catch (Exception e) {
                LogUtils.e("AppUtils" + e.getMessage());
            }
        }
        return false;
    }

    /**
     * 屏保界面
     */
    public boolean isScreenProAct() {
        Activity currentActivity = App.getInstance().getCurrentActivity();
        if (currentActivity instanceof MainActivity && ((MainActivity) currentActivity).currentFrag instanceof ScreenProFragment) {
            return true;
        }
        return false;
    }

    /**
     * 人脸开门界面
     */
    public boolean isFaceWffrOpenFrg() {
        Activity currentActivity = App.getInstance().getCurrentActivity();
        if (currentActivity instanceof MainActivity && (((MainActivity) currentActivity).currentFrag instanceof FaceWffrOpenFragment ||
                ((MainActivity) currentActivity).currentFrag instanceof FaceMegviiOpenFragment)) {
            return true;
        }
        return false;
    }

    /**
     * 是否在对讲呼叫或者通话中
     */
    public boolean isCalling() {
        return CallResidentFragment.isCalling || CallResidentFragment.isTalking || CallCenterFragment.isCalling || CallCenterFragment.isTalking;
    }

    /**
     * 是否开启补关灯
     */
    public void setEnableLed(boolean isEnable) {
        SinglechipClientProxy.getInstance().ctrlOpenCCD(isEnable);
    }

    public boolean isEnableLed() {
        return SystemClock.elapsedRealtime() > 30 * 60 * 1000 || isEnableLed;
    }

    /**
     * 判断服务是否在后台运行
     * @param ServicePackageName 服务包名
     */
    public boolean isServiceRunning(String ServicePackageName) {
        ActivityManager manager = (ActivityManager) ContextProxy.getContext().getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo service : runningServices) {
            if (ServicePackageName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
