package com.mili.smarthome.tkj.app;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.BuildConfigHelper;

import java.io.File;
import java.io.FileOutputStream;

public final class AppPreferences {

    private static final String APP_THEME = "app_theme";
    private static final String RESET_FLAG = "first_run";// 是否首次打开App
    private static final String WFFR_INITIALIZED = "wffr_initialized";// 初始化wffr文件
    private static final String WFFR_DB_SIZE = "wffr_db_size";// 人脸数据库大小
    private static final String MULTI_MEDIA_TYPE = "multi_media_type";
    private static final String RESUME_FACTORY = "resume_factory";    // 是否进行过恢复出厂操作
    private static final String FACE_MANUFACTURER = "face_manufacturer";

    //是否首次安装app
    private static final String RESET_FLAG_PATH = "/data/data/" + App.getInstance().getPackageName() + "/reset.dat";
    /**
     * 获取应用主题
     */
    public static int getAppTheme() {
        return getInt(APP_THEME, R.style.AppTheme);
    }

    /**
     * 保存应用主题
     */
    public static boolean setAppTheme(int themeId) {
        return putInt(APP_THEME, themeId);
    }

    /**
     * 获取恢复出厂标志
     *
     * @return true表示恢复出厂
     */
    public static boolean isReset() {
//        return getBoolean(RESET_FLAG, true);
        File file = new File(RESET_FLAG_PATH);
        if (!getBoolean(RESET_FLAG, true)) {
            return false;
        }else if (file.exists()) {
            setReset(false);
            return false;
        }
        return true;
    }

    /**
     * 设置恢复出厂标志
     */
    public static boolean setReset(boolean reset) {
        File file = new File(RESET_FLAG_PATH);
        if (reset) {
            //删除文件
            if (file.exists()) {
                file.delete();
            }
        } else {
            //创建文件
            try {
                if (!file.exists()) {
                    FileOutputStream fos = new FileOutputStream(file);
                    String info = BuildConfigHelper.getHardWareVer() + "/" + BuildConfigHelper.getSoftWareVer();
                    fos.write(info.getBytes());
                    fos.getFD().sync();
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /* 恢复出厂后红外补光灯测试版本关闭 */
        if (reset) {
            resumeFactory();
        }

        SharedPreferences.Editor editor = getDefault().edit();
        if (reset) {
            editor.remove(APP_THEME);
        }
        editor.putBoolean(RESET_FLAG, reset);
        return editor.commit();
    }

    /**
     * 是否已初始化wffr文件
     */
    public static boolean isWffrInitialized() {
        return getBoolean(WFFR_INITIALIZED, false);
    }

    /**
     * 保存wffr文件是否已初始化
     */
    public static boolean setWffrInitialized(boolean initialized) {
        return putBoolean(WFFR_INITIALIZED, initialized);
    }

    /**
     * 获取EI人脸数据库（成功上传FTP）的大小
     */
    public static long getWffrDbSize() {
        return getLong(WFFR_DB_SIZE, 0);
    }

    /**
     * 保存EI人脸数据库（成功上传FTP）的大小
     */
    public static boolean setWffrDbSize(long wffrDbSize) {
        return putLong(WFFR_DB_SIZE, wffrDbSize);
    }

    /**
     * 是否启用播放多媒体
     */
    public static boolean isMediaPlay() {
        return getBoolean(MULTI_MEDIA_TYPE, true);
    }

    /**
     * 保存播放媒体类型
     */
    public static void setMediaPlay(int type) {
        /*1、启用播放 0、禁止播放*/
        if (type == 1) {
            putBoolean(MULTI_MEDIA_TYPE, true);
        } else {
            putBoolean(MULTI_MEDIA_TYPE, false);
        }
    }

    /**
     * 设置恢复出厂操作标志
     */
    public static void resumeFactory() {
        putBoolean(RESUME_FACTORY, true);
    }

    /**
     * 获取是否恢复过出厂
     * @return  true/false
     */
    public static boolean getResumeFactory() {
        return getBoolean(RESUME_FACTORY, false);
    }

    /**
     * 设置人脸厂商
     * @param type  人脸厂商 0：EI人脸 1：Face++人脸
     * @return      true/false
     */
    public static boolean setFaceManufacturer(int type) {
        return putInt(FACE_MANUFACTURER, type);
    }

    /**
     * 获取人脸厂商
     * @return      人脸厂商 0：EI人脸 1：Face++人脸
     */
    public static int getFaceManufacturer() {
        return getInt(FACE_MANUFACTURER, 0);
    }


    // =================================================================== //
    // =================================================================== //
    // =================================================================== //

    private static SharedPreferences getDefault() {
        return PreferenceManager.getDefaultSharedPreferences(ContextProxy.getContext());
    }

    private static boolean getBoolean(String key, boolean defValue) {
        SharedPreferences sp = getDefault();
        return sp.getBoolean(key, defValue);
    }

    private static boolean putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getDefault().edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    private static int getInt(String key, int defValue) {
        SharedPreferences sp = getDefault();
        return sp.getInt(key, defValue);
    }

    private static boolean putInt(String key, int value) {
        SharedPreferences.Editor editor = getDefault().edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    private static long getLong(String key, long defValue) {
        SharedPreferences sp = getDefault();
        return sp.getLong(key, defValue);
    }

    private static boolean putLong(String key, long value) {
        SharedPreferences.Editor editor = getDefault().edit();
        editor.putLong(key, value);
        return editor.commit();
    }
}
