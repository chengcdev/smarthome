package com.mili.smarthome.tkj.face.horizon.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.face.horizon.HorizonConst;

public class HorizonPreferences {

    // ================================================//
    //  缓存，增加读取速度

    private static Integer faceThr;// 人脸阈值参数

    // ================================================//

    /**
     * 保存抓拍参数
     */
    public static void saveCaptureSize(int size) {
        putInt("captureSize", size);
    }

    /**
     * 保存正侧脸阈值参数
     */
    public static void saveFrontalThr(int thr) {
        putInt("frontalThr", thr);
    }

    /**
     * 保存抓拍优选帧数参数
     */
    public static void saveBeginPostFrameThr(int thr) {
        putInt("beginPostFrameThr", thr);
    }

    /**
     * 保存抓拍人脸数参数
     */
    public static void saveFirstNumAvailThr(int thr) {
        putInt("firstNumAvailThr", thr);
    }

    /**
     * 保存再次抓取帧数差参数
     */
    public static void saveResnapThr(int thr) {
        putInt("resnapThr", thr);
    }

    /**
     * 保存人脸外扩系数参数
     */
    public static void saveSnapScaleThr(Float thr) {
        putFloat("napScaleThr", thr);
    }

    /**
     * 保存人脸阈值参数
     */
    public static void saveFaceThr(int thr) {
        faceThr = thr;
        putInt("faceThr", thr);
    }

    /**
     * 保存画矩形开关参数
     */
    public static void saveDrawRectOpen(Boolean enable) {
        putBoolean("drawRectOpen", enable);
    }

    /**
     * 保存自动灭屏开关参数
     */
    public static void saveAutoScreenShutOpen(Boolean enable) {
        putBoolean("autoScreenShutOpen", enable);
    }

    /**
     * 保存人脸AE功能开关参数
     */
    public static void saveFaceAeEnable(Boolean enable) {
        putBoolean("faceAeEnable", enable);
    }

    /**
     * 保存摄像头分辨率参数
     */
    public static void saveResolutionRatio(int type) {
        putInt("cameraResolutionRatio", type);
    }

    /**
     * 保存大脸开关参数
     */
    public static void saveBigFaceOpen(Boolean enable) {
        putBoolean("bigFaceOpen", enable);
    }

    /**
     * 保存cp提特征缓存队列长度参数
     */
    public static void saveFeatureQueueMaxLen(int len) {
        putInt("featureQueueMaxLen", len);
    }

    /**
     * 保存判活阈值参数
     */
    public static void saveJudgeLiveThr(int thr) {
        putInt("judgeLiveThr", thr);
    }

    /**
     * 保存同一个人再次识别间隔参数
     */
    public static void saveSamePeopleRecognitionInterval(long time) {
        putLong("samePeopleInterval", time);
    }

    /**
     * 保存摄像头类型
     */
    public static void saveCameraType(int type) {
        putInt("cameraType", type);
    }

    /**
     * 保存灵敏度阈值
     */
    public static void saveSensitivity(int thr) {
        putInt("sensitivity", thr);
    }

    /**
     * 保存复位芯片开关参数
     */
    public static void saveResetCpOpen(boolean reset) {
        putBoolean("resetCpOpen", reset);
    }

    /**
     * 保存自动校准参数
     */
    public static void saveAutoFixCoordinate(Boolean enable) {
        putBoolean("autoFixCoordinate", enable);
    }

    /**
     * 保存日志级别参数
     */
    public static void saveLogLevel(int level) {
        putInt("LogLevel", level);
    }

    /**
     * 保存x1降频级别参数
     */
    public static void saveSifFreqLevel(int level) {
        putInt("SifFreqLevel", level);
    }

    /**
     * 保存x1降帧级别参数
     */
    public static void saveDetFrameLevel(int level) {
        putInt("DetFrameLevel", level);
    }

    /**
     * 保存摄像头帧率参数
     */
    public static void saveCameraFrameRate(int rate) {
        putInt("CameraFrameRate", rate);
    }

    /**
     * 获取抓拍参数
     */
    public static int getCaptureSize() {
        return getInt("captureSize", 90);
    }

    /**
     * 获取正侧脸阈值参数
     */
    public static int getFrontalThr() {
        return getInt("frontalThr", 1000);
    }

    /**
     * 获取抓拍优选帧数参数
     */
    public static int getBeginPostFrameThr() {
        return getInt("beginPostFrameThr", 1);
    }

    /**
     * 获取抓拍人脸数参数
     */
    public static int getFirstNumAvailThr() {
        return getInt("firstNumAvailThr", 1);
    }

    /**
     * 获取再次抓取帧数差参数
     */
    public static int getResnapThr() {
        return getInt("resnapThr", 1);
    }

    /**
     * 获取人脸外扩系数参数
     */
    public static float getSnapScaleThr() {
        return getFloat("snapScaleThr", 1.6F);
    }

    /**
     * 获取人脸阈值参数
     */
    public static int getFaceThr() {
        if (faceThr == null) {
            faceThr = getInt("faceThr", 80);
        }
        return faceThr;
    }

    /**
     * 获取画矩形开关参数
     */
    public static boolean getDrawRectOpen() {
        return getBoolean("drawRectOpen", true);
    }

    /**
     * 获取复位芯片开关参数
     */
    public static boolean getResetCpOpen() {
        return getBoolean("resetCpOpen", false);
    }

    /**
     * 获取自动灭屏开关参数
     */
    public static boolean getAutoScreenShut() {
        return getBoolean("autoScreenShutOpen", false);
    }

    /**
     * 获取人脸AE功能开关参数
     */
    public static boolean getFaceAeEnable() {
        return getBoolean("faceAeEnable", true);
    }

    /**
     * 获取摄像头分辨率参数
     */
    public static int getResolutionRatio() {
        return getInt("cameraResolutionRatio", 0);
    }

    /**
     * 获取判活阈值参数
     */
    public static int getJudgeLiveThr() {
        return getInt("judgeLiveThr", 50);
    }

    /**
     * 获取灵敏度阈值参数
     */
    public static int getSensitivity() {
        return getInt("sensitivity", 57);
    }

    /**
     * 获取同一个人再次识别间隔参数
     */
    public static long getSamePeopleRecognitionInterval() {
        return getLong("samePeopleInterval", 1000);
    }

    /**
     * 获取摄像头类型参数
     */
    public static int getCameraType() {
        return getInt("cameraType", 4);
    }

    /**
     * 获取大脸开关参数
     */
    public static boolean getBigFaceOpen() {
        return getBoolean("bigFaceOpen", true);
    }

    /**
     * 获取提特征缓存队列长度参数
     */
    public static int getFeatureQueueMaxLen() {
        return getInt("featureQueueMaxLen", 1);
    }

    /**
     * 获取自动校准
     */
    public static boolean getAutoFixCoordinate() {
        return getBoolean("autoFixCoordinate", false);
    }

    /**
     * 获取日志级别参数
     */
    public static int getLogLevel() {
        return getInt("LogLevel", 5);
    }

    /**
     * 获取x1降频级别参数
     */
    public static int getSifFreqLevel() {
        return getInt("SifFreqLevel", 0);
    }

    /**
     * 获取x1降帧级别参数
     */
    public static int getDetFrameLevel() {
        return getInt("DetFrameLevel", 0);
    }

    /**
     * 获取摄像头帧率参数
     */
    public static int getCameraFrameRate() {
        return getInt("CameraFrameRate", 25);
    }

    // =================================================================== //
    // =================================================================== //
    // =================================================================== //

    public static String getHorizonPath() {
        return App.getInstance().getDir(HorizonConst.HORIZON_DIR, Context.MODE_PRIVATE).getAbsolutePath();
    }

    public static SharedPreferences getPreferences() {
        return App.getInstance().getSharedPreferences("HorizonPreferences", Context.MODE_PRIVATE);
    }

    public static boolean getBoolean(String key, boolean defValue) {
        SharedPreferences sp = getPreferences();
        return sp.getBoolean(key, defValue);
    }

    public static int getInt(String key, int defValue) {
        SharedPreferences sp = getPreferences();
        return sp.getInt(key, defValue);
    }

    public static long getLong(String key, long defValue) {
        SharedPreferences sp = getPreferences();
        return sp.getLong(key, defValue);
    }

    public static float getFloat(String key, float defValue) {
        SharedPreferences sp = getPreferences();
        return sp.getFloat(key, defValue);
    }

    public static String getString(String key, String defValue) {
        SharedPreferences sp = getPreferences();
        return sp.getString(key, defValue);
    }

    public static boolean putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public static boolean putInt(String key, int value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    public static boolean putLong(String key, long value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    public static boolean putFloat(String key, float value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putFloat(key, value);
        return editor.commit();
    }

    public static boolean putString(String key, String value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public static boolean put(String key, Object value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else {
            editor.putString(key, (String) value);
        }
        return editor.commit();
    }
}
