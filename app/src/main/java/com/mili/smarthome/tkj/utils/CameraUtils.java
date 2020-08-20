package com.mili.smarthome.tkj.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;

public final class CameraUtils {

    public static boolean hasCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private Camera openCamera(int cameraFace) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();
        Camera camera = null;
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == cameraFace) {
                try {
                    camera = Camera.open(camIdx);
                    break;
                } catch (Exception e) {
                    LogUtils.printThrowable(e);
                }
            }
        }
        return camera;
    }

}
