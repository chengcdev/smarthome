package com.mili.smarthome.tkj.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.internal.app.LocalePicker;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.app.AppPreferences;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.main.activity.MainActivity;
import com.mili.smarthome.tkj.main.activity.ScreenSaverActivity;
import com.mili.smarthome.tkj.main.activity.direct.DirectPressMainActivity;
import com.mili.smarthome.tkj.main.entity.CommonBean;
import com.mili.smarthome.tkj.main.service.ScreenService;
import com.mili.smarthome.tkj.set.Constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.ACTIVITY_SERVICE;

public class AppManage {

    private static volatile AppManage appUtils;
    private ArrayList<Activity> actLists;
    //是否更改了语言
    public static boolean isChangeLang;
    private static int langaUageType = 0;
    private Handler handler = new Handler();
    private String Tag = "AppManage";
    public Fragment frgCurrent;
    private final int DEFAULT_KEY_CODE = -1;
    private int mCurrentKeycode = DEFAULT_KEY_CODE;

    public static AppManage getInstance() {
        if (appUtils == null) {
            synchronized (AppManage.class) {
                if (appUtils == null) {
                    appUtils = new AppManage();
                }
            }
        }
        return appUtils;
    }

    public int getmCurrentKeycode() {
        return mCurrentKeycode;
    }

    public void setmCurrentKeycode(int mCurrentKeycode) {
        this.mCurrentKeycode = mCurrentKeycode;
    }

    public void setmDefaultKeycode() {
        this.mCurrentKeycode = DEFAULT_KEY_CODE;
    }

    /**
     * 跳转到某个activity
     */
    public void toAct(Context context, Class c) {
        Intent intent = new Intent(context, c);
        context.startActivity(intent);
    }

    /**
     * 跳转到某个activity
     */
    public void toAct(Class c) {
        Intent intent = new Intent(App.getInstance(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.getInstance().startActivity(intent);
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
     * 跳转到某个activity,并带有参数String
     */
    public void toActExtra(Context context, Class c, String key, String param) {
        Intent intent = new Intent(context, c);
        intent.putExtra(key, param);
        context.startActivity(intent);
    }

    /**
     * 跳转到某个activity,并带有参数int
     */
    public void toActExtra(Context context, Class c, String key, int param) {
        Intent intent = new Intent(context, c);
        intent.putExtra(key, param);
        context.startActivity(intent);
    }

    /**
     * 跳转到某个activity,并带有参数CommonBean
     */
    public void toActExtra(Context context, Class c, String key, CommonBean commonBean) {
        Intent intent = new Intent(context, c);
        intent.putExtra(key, commonBean);
        context.startActivity(intent);
    }


    /**
     * 跳转到某个activity,带有参数CommonBean，并关闭当前的activity
     */
    public void toActExtraFinish(Context context, Class c, String key, CommonBean commonBean) {
        Intent intent = new Intent(context, c);
        intent.putExtra(key, commonBean);
        context.startActivity(intent);
        Activity activity = (Activity) context;
        activity.finish();
    }

    /**
     * 替换当前的fragment
     */
    public void replaceFragment(FragmentActivity activity, Fragment fragment) {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fl_container, fragment);
        ft.addToBackStack(fragment.getClass().getSimpleName());
        ft.commitAllowingStateLoss();
        frgCurrent = fragment;
    }

    /**
     * 替换当前的fragment
     */
    public void replaceFragment(FragmentActivity activity, Fragment fragment, Bundle bundle) {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        fragment.setArguments(bundle);
        ft.replace(R.id.fl_container, fragment);
        ft.addToBackStack(fragment.getClass().getSimpleName());
        ft.commitAllowingStateLoss();
        frgCurrent = fragment;
    }


    /**
     * 替换当前的fragment
     */
    public void addFragment(FragmentActivity activity, Fragment fragment) {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fl_container, fragment);
        ft.addToBackStack(fragment.getClass().getSimpleName());
        ft.commitAllowingStateLoss();
        frgCurrent = fragment;
    }

    /**
     * 添加当前的fragment
     */
    public void addFragment(FragmentActivity activity, Fragment fragment, Bundle bundle) {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        fragment.setArguments(bundle);
        ft.add(R.id.fl_container, fragment);
        ft.addToBackStack(fragment.getClass().getSimpleName());
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
     * 发送无参数广播
     */
    public void sendReceiver(String action) {
        Intent intent = new Intent(action);
        App.getInstance().sendBroadcast(intent);
    }

    /**
     * 替换当前的fragment,并传递参数
     */
    public void replaceFragment(FragmentActivity activity, Fragment fragment, int layoutId,
                                String key, CommonBean commonBean) {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable(key, commonBean);
        fragment.setArguments(bundle);
        ft.replace(layoutId, fragment);
        ft.commitAllowingStateLoss();
        frgCurrent = fragment;
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
     * 获取对应的按键点击位置
     *
     * @param keyCode 系统的kecode
     */
    public int getPosition(int keyCode) {
        switch (keyCode) {
            case 29:
                return 0;
            case 30:
                return 1;
            case 31:
                return 2;
            case 8:
                return 3;
            case 9:
                return 4;
            case 10:
                return 5;
            case 11:
                return 6;
            case 12:
                return 7;
            case 13:
                return 8;
            case 14:
                return 9;
            case 15:
                return 10;
            case 16:
                return 11;
            case 32:
                return 12;
            case 7:
                return 13;
            case 33:
                return 14;
        }
        return 0;
    }

    public void playKeySound(View view) {
        if (view != null) {
            //启动屏幕服务
            AppManage.getInstance().startScreenService();
            //设置按键音
            AppManage.getInstance().setKeyVolume();
            //按键音效
            view.playSoundEffect(SoundEffectConstants.CLICK);
        }
    }

    /**
     * view按下背景
     */
    public void keyBoardDown(View view) {
        LinearLayout linRoot = (LinearLayout) view.findViewById(R.id.lin_root);
        linRoot.setBackgroundResource(R.drawable.key_back_1);
        playKeySound(linRoot);
    }


    /**
     * view抬起背景
     */
    public void keyBoardUp(View view) {
        LinearLayout linRoot = (LinearLayout) view.findViewById(R.id.lin_root);
        linRoot.setBackgroundResource(R.drawable.key_back);
    }

    /**
     * 直按式图片按下背景
     */
    public void imgDown(View view) {
        playKeySound(view);
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.rl_direct);
        ImageView mIma = (ImageView) relativeLayout.findViewById(R.id.img_item);
        mIma.setScaleType(ImageView.ScaleType.FIT_XY);
        mIma.setImageResource(R.drawable.list_btn_1);
    }

    /**
     * 直按式图片抬起背景
     */
    public void imgUp(View view) {
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.rl_direct);
        ImageView mIma = (ImageView) relativeLayout.findViewById(R.id.img_item);
        mIma.setImageResource(R.drawable.list_btn);
    }

    /**
     * 根据不同的语言显示不同的图片
     *
     * @param imageView view
     * @param znImg     中文简体图片
     * @param twImg     中文繁体图片
     * @param enImg     英语图片
     */
    public void setTopImgBg(ImageView imageView, int znImg, int twImg, int enImg) {

        Locale locale = App.getInstance().getResources().getConfiguration().locale;
        String lang = locale.getLanguage() + "-" + locale.getCountry();

        switch (lang) {
            //中文简体
            case "zh-CN":
                imageView.setImageResource(znImg);
                break;
            //中文繁体
            case "zh-TW":
                imageView.setImageResource(twImg);
                break;
            //英语
            case "en-US":
                imageView.setImageResource(enImg);
                break;
        }
    }

    /**
     * 设置系统语言
     */
    public static boolean changeSystemLanguage(final int langType) {

        getLangaUageType();

        if (langaUageType == langType) {
            return false;
        }

        switch (langType) {
            case 0:
                LocalePicker.updateLocale(Locale.SIMPLIFIED_CHINESE);
                langaUageType = 0;
                return true;
            case 1:
                LocalePicker.updateLocale(Locale.TRADITIONAL_CHINESE);
                langaUageType = 1;
                return true;
            case 2:
                LocalePicker.updateLocale(Locale.US);
                langaUageType = 2;
                return true;
        }
        return true;
    }

    /**
     * 获取语言类型
     */
    private static void getLangaUageType() {
        Locale locale = App.getInstance().getResources().getConfiguration().locale;
        String lang = locale.getLanguage() + "-" + locale.getCountry();

        switch (lang) {
            //中文简体
            case "zh-CN":
                langaUageType = 0;
                break;
            //中文繁体
            case "zh-TW":
                langaUageType = 1;
                break;
            //英语
            case "en-US":
                langaUageType = 2;
                break;
        }
    }

    /**
     * 设置网络状态图片
     */
    private void setNetStateListener(final View view) {
        if (EthernetUtils.getNetWorkType() == EthernetUtils.NETWORK_TYPE_ETHERNET) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setNetStateListener(view);
                handler.removeCallbacks(this);
            }
        }, 2000);
    }

    /**
     * 设置按键音
     */
    public void setKeyVolume() {
        AudioManager mAudioManager = (AudioManager) App.getInstance().getSystemService(Context.AUDIO_SERVICE);
        if (AppConfig.getInstance().getKeyVolume() == 0) {
            //不启用按键音
            assert mAudioManager != null;
            mAudioManager.unloadSoundEffects();
            Settings.System.putInt(App.getInstance().getContentResolver(),
                    Settings.System.SOUND_EFFECTS_ENABLED, 0);
        } else {
            //启用按键音
            assert mAudioManager != null;
            mAudioManager.loadSoundEffects();
            Settings.System.putInt(App.getInstance().getContentResolver(),
                    Settings.System.SOUND_EFFECTS_ENABLED, 1);
        }
    }

    public void playKeySound() {
        AudioManager mAudioMgr = (AudioManager) App.getInstance().getSystemService(App.getInstance().AUDIO_SERVICE);
        mAudioMgr.playSoundEffect(AudioManager.FX_KEY_CLICK);
    }

    /**
     * 屏保界面
     */
    public boolean isScreenProAct() {
        Activity currentActivity = App.getInstance().getCurrentActivity();
        return currentActivity instanceof ScreenSaverActivity;
    }

    /**
     * 开启屏保和关屏服务
     */
    public void startScreenService() {
        //是否恢复出厂第一次启动
        if (!AppPreferences.isReset()) {
            Intent intent = new Intent(App.getInstance(), ScreenService.class);
            App.getInstance().startService(intent);
        }
    }

    /**
     * 停止屏保和关屏服务
     */
    public void stopScreenService() {
        Intent intent = new Intent(App.getInstance(), ScreenService.class);
        App.getInstance().stopService(intent);
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
     * 重新回到主界面
     */
    public void restartLauncherAct() {
        Activity currentActivity = App.getInstance().getCurrentActivity();
        LogUtils.w(" >>restartLauncherAct ");
        if (AppConfig.getInstance().getCallType() == 0) {
            //初始化主界面
            sendReceiver(Constant.ActionId.ACTION_INIT_MAIN);
            Intent intent = new Intent(currentActivity, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            App.getInstance().startActivity(intent);
            LogUtils.w(" >>restartLauncherAct MainActivity");
        } else {
            //直按式初始化主界面
            sendReceiver(Constant.ActionId.ACTION_INIT_MAIN);
            Intent intent = new Intent(currentActivity, DirectPressMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            App.getInstance().startActivity(intent);
            LogUtils.w(" >>restartLauncherAct DirectPressMainActivity");
        }
    }


    /**
     * 关闭屏保界面
     */
    public void closeScreenAct() {
        //亮屏
        SystemSetUtils.screenOn();
        Activity currentActivity = App.getInstance().getCurrentActivity();
        if (currentActivity instanceof ScreenSaverActivity) {
            currentActivity.finish();
        }
    }

    /**
     * 判断服务是否在后台运行
     *
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

    /**
     * 将list集合写入到文件中
     */
    public void writeFileList(String filePath,List list) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fos);
            objectOutputStream.writeObject(list);
            fos.getFD().sync();
            fos.close();
            objectOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 将list从文件中读取出来
     */
    public List readFlieList(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fis);
            List list = (List) objectInputStream.readObject();
            fis.getFD().sync();
            fis.close();
            objectInputStream.close();
            return list;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
