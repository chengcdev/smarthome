package com.mili.smarthome.tkj.app;

/**
 * 定制版本
 */
public class CustomVersion {

    /**
     * 定制版本，k3旷世人脸增加陌生人脸抓拍功能以及扩大人脸容量定制（北京通州周转房項目）
     */
    public static boolean VERSION_K3_MEGVII_SNAP_STRANGER = false;

    /**
     * 演示版本，k3关屏时检测到人脸时可识别进门
     */
    public static boolean VERSION_K3_SCREENOFF_FACE_RECOGNIZE = true;

    /**
     * 演示版本，k6旷世人脸检测体温，体温正常方可进门
     */
    public static boolean VERSION_K6_MEGVII_TEMPERATURE = false;

    /**
     * 楷唯物业项目，现场临时版本，增加系统信息face++人脸授权，增加云端呼中心失败后发mqtt呼叫命令功能
     * jni层中宏ENABLE_MQTT_UNLOCK需要启用
     */
    public static boolean VERSION_KAIWEI_WUYE = false;
}
