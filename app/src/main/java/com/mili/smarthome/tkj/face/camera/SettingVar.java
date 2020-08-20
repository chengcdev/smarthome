package com.mili.smarthome.tkj.face.camera;

import mcv.facepass.types.FacePassImageRotation;

/**
 * Created by wangzhiqiang on 2017/11/22.
 */

public class SettingVar {
    public static boolean cameraFacingFront = false;
    public static int faceRotation = FacePassImageRotation.DEG270;
    public static boolean isSettingAvailable = true;
    public static int cameraPreviewRotation = 0;
    public static boolean isCross = false;
    public static String SharedPrefrence = "user";
    public static int mHeight;
    public static int mWidth;
    public static boolean cameraSettingOk = false;
    public static boolean iscameraNeedConfig = false;
    public static boolean isButtonInvisible = false;
}
