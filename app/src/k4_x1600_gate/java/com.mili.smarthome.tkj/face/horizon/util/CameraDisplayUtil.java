package com.mili.smarthome.tkj.face.horizon.util;

/**
 * Created by liangbin.yang on 2019/6/20.
 */

public class CameraDisplayUtil {
    private static int cameraWidth = 608;
    private static int cameraHeight = 1080;
    private static int cameraDisplayWidth = 720;
    private static int cameraDisplayHeight = 1232;

    public static void setCameraDisplay(int type) {
        switch (type) {
            case 0:
                cameraWidth = 608;
                cameraHeight = 1080;
                cameraDisplayWidth = 720;
                cameraDisplayHeight = 1232;
                /*适配608 * 1080的摄像头显示和画人脸框*/
                break;
            case 1:
                cameraWidth = 1280;
                cameraHeight = 720;
                cameraDisplayWidth = 720;
                cameraDisplayHeight = 408;
                /*适配720p摄像头显示和画人脸框，因竖屏，把1280*720的视频缩放等比例缩放到
                720*408显示，因全志平台是8字节图像对齐，所以不是高度不是405而是408*/
                break;
            case 2:
                cameraWidth = 1920;
                cameraHeight = 1080;
                cameraDisplayWidth = 720;
                cameraDisplayHeight = 408;
                /*适配1080p摄像头显示和画人脸框，因竖屏，把1280*720的视频缩放等比例缩放到
                720*408显示，因全志平台是8字节图像对齐，所以不是高度不是405而是408*/
                break;
        }
    }

    public static int getCameraWidth() {
        return cameraWidth;
    }

    public static int getCameraHeight() {
        return cameraHeight;
    }

    public static int getCameraDisplayWidth() {
        return cameraDisplayWidth;
    }

    public static int getCameraDisplayHeight() {
        return cameraDisplayHeight;
    }

    public static String getCameraDisplayRatio() {
        switch (HorizonPreferences.getResolutionRatio()) {
            case 0:
                return "608";
            case 1:
                return "720";
            case 2:
                return "1080";
            default:
                return "608";
        }
    }

    public static void setCameraDisplayRatio(String cameraRatio) {
        if (cameraRatio != null) {
            if (cameraRatio.equals("608")) {
                HorizonPreferences.saveResolutionRatio(0);
            } else if (cameraRatio.equals("720")) {
                HorizonPreferences.saveResolutionRatio(1);
            } else if (cameraRatio.equals("1080")) {
                HorizonPreferences.saveResolutionRatio(2);
            }
        }
        setCameraDisplay(HorizonPreferences.getResolutionRatio());
    }
}
