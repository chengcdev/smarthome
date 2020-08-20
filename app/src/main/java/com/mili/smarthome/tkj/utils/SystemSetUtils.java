package com.mili.smarthome.tkj.utils;

import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.provider.Settings;
import android.widget.Toast;

import com.android.internal.app.LocalePicker;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.appfunc.BuildConfigHelper;
import com.mili.smarthome.tkj.appfunc.FreeObservable;
import com.mili.smarthome.tkj.receiver.ScreenOffAdminReceiver;

import java.util.Locale;

import static android.content.Context.POWER_SERVICE;

/**
 * 系统设置接口
 * Created by zhengxc on 2019/2/26 0026.
 */

public class SystemSetUtils {

    /**
     * 设置系统语言
     *
     * @param language 0：简体  1：繁体  2：英文
     * @return 成功失败
     */
    public static boolean setSystemLanguage(int language) {

        if (BuildConfigHelper.isPad()) {
            return false;
        }

        switch (language) {
            case 0:
                LocalePicker.updateLocale(Locale.SIMPLIFIED_CHINESE);
                break;

            case 1:
                LocalePicker.updateLocale(Locale.TRADITIONAL_CHINESE);
                break;

            case 2:
                LocalePicker.updateLocale(Locale.US);
                break;

            default:
                return false;
        }
        return true;
    }

    /**
     * 获取语言类型
     *
     * @return 0：简体  1：繁体  2：英文
     */
    private static int getSystemLanguage() {
        int language = 0;
        Locale curLocale = App.getInstance().getResources().getConfiguration().locale;

        if (curLocale.equals(Locale.SIMPLIFIED_CHINESE)) {
            language = 0;
        } else if (curLocale.equals(Locale.TRADITIONAL_CHINESE)) {
            language = 1;
        } else if (curLocale.equals(Locale.US)) {
            language = 2;
        }

        return language;
    }

    /**
     * 设置按键音
     *
     * @param enable true:启用     false:不启用
     */
    public static void setEnableKeyVoice(boolean enable) {

        if (BuildConfigHelper.isPad()) {
            return;
        }

        AudioManager mAudioManager = (AudioManager) App.getInstance().getSystemService(Context.AUDIO_SERVICE);
        if (enable) {
            //启用按键音
            assert mAudioManager != null;
            mAudioManager.loadSoundEffects();
            Settings.System.putInt(App.getInstance().getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 1);
        } else {
            //不启用按键音
            assert mAudioManager != null;
            mAudioManager.unloadSoundEffects();
            Settings.System.putInt(App.getInstance().getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 0);
        }
    }

    /**
     * 获取按键音
     * int 1启用 0不启用
     */
    public static int getEnableKeyVoice() {
        int enable = Settings.System.getInt(App.getInstance().getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 1);
        return enable;
    }

    /**
     * 播放按键音效
     * 只能在view中调用
     */
    public static void playSoundEffect() {
//        playSoundEffect(SoundEffectConstants.CLICK);
    }

    /**
     * 关屏
     */
    public static void screenOff(Context context) {

        if (BuildConfigHelper.isPad()) {
            return;
        }

        DevicePolicyManager policyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminReceiver = new ComponentName(context, ScreenOffAdminReceiver.class);
        boolean admin = policyManager.isAdminActive(adminReceiver);
        if (!admin) {
            // 没有权限设置权限
            AddDeviceAdmin addDeviceAdmin = new AddDeviceAdmin();
            addDeviceAdmin.addSystemAdmin(context);
            admin = policyManager.isAdminActive(adminReceiver);
        }
        if (admin) {
            policyManager.lockNow();
//            //启用wifi 解决接收不到UDP包
//            if (EthernetUtils.getNetWorkType() == EthernetUtils.NETWORK_TYPE_WIFI) {
//                WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
//                WifiManager.MulticastLock multicastLock=wifiManager.createMulticastLock("multicast.test");
//                multicastLock.acquire();
//            }
        } else {
            Toast.makeText(context, context.getString(R.string.no_device_permission), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 开屏
     * 亮屏
     */
    @SuppressLint("WakelockTimeout")
    public static void screenOn() {

        if (BuildConfigHelper.isPad()) {
            return;
        }

        if (!BuildConfigHelper.isK6() && !BuildConfigHelper.isK7()) {
            FreeObservable.getInstance().observeFree();
        }

        // turn on screen
        PowerManager mPowerManager = (PowerManager) App.getInstance().getSystemService(POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
        mWakeLock.acquire();
        mWakeLock.release();
//        if (multicastLock != null) {
//            multicastLock.release();
//            multicastLock = null;
//        }
    }

    /**
     * @return 屏幕是否亮屏
     */
    public static boolean isScreenOn(){

        if (BuildConfigHelper.isPad()) {
            return true;
        }

        PowerManager mPowerManager = (PowerManager) App.getInstance().getSystemService(POWER_SERVICE);
        assert mPowerManager != null;
        return mPowerManager.isInteractive();
    }


    /**
     * 获取当前的通话音量
     */
    public static int getCallVolume() {
        AudioManager mAudioManager = (AudioManager) App.getInstance().getSystemService(Context.AUDIO_SERVICE);
        //通话音量
        assert mAudioManager != null;
        int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        return volume;
    }

    /**
     * 获取通话音量最大值
     */
    public static int getCallMaxVolume() {
        AudioManager mAudioManager = (AudioManager) App.getInstance().getSystemService(Context.AUDIO_SERVICE);
        //通话音量
        assert mAudioManager != null;
        int volume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
        return volume;
    }


    /**
     * 设置通话音量
     */
    public static void setCallVolume(int volume) {

        if (BuildConfigHelper.isPad()) {
            return;
        }

        AudioManager mAudioManager = (AudioManager) App.getInstance().getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, volume, 0);
    }

    /**
     * 重启设备命令
     */
    public static void rebootDevice() {
        LogUtils.w(" SystemSetUtils rebootDevice");
        PowerManager pm = (PowerManager)App.getInstance().getSystemService(POWER_SERVICE);
        pm.reboot(null);
    }

    /**
     * 请求ota升级
     * @param mode 1：半夜重启；  2：手动点升级；    3：收到平台请求升级
     * 半夜重启后请求一次，手动点升级，收到平台请求升级
     */
    public static void systemUpgrade(int mode) {
        LogUtils.d(" ======= upgrade system ====== mode = " + mode);
        if(mode == 1 || mode == 2 || mode == 3) {
            Intent intent = new Intent(Const.ActionId.CHECK_OTA_UPDATE_VERSION_ACTION);
            intent.putExtra("updateMode", mode);
            App.getInstance().sendBroadcast(intent);
        }
    }

    /**
     * 获取当前连接的网络类型
     * @param context
     * @return
     */
    public static int getNetType(Context context) {
        int netType = ConnectivityManager.TYPE_NONE;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null) {
            LogUtils.d("SystemSetUtils" + " network type is " + activeNetwork.getType() + ", connect is " + activeNetwork.isConnected());
            LogUtils.d("SystemSetUtils" + " available is " + activeNetwork.isAvailable());
            switch (activeNetwork.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                case ConnectivityManager.TYPE_ETHERNET:
                case ConnectivityManager.TYPE_MOBILE:
                    if (activeNetwork.isConnected()) {
                        netType = activeNetwork.getType();
                    }
                    break;
            }
        } else {
            LogUtils.d("SystemSetUtils" + " activeNetwork is null.");
        }

        return netType;
    }

}
