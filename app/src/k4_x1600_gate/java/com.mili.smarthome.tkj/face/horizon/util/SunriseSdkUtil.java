package com.mili.smarthome.tkj.face.horizon.util;

import android.util.Log;

import hobot.sunrise.sdk.jni.EnumClass;
import hobot.sunrise.sdk.jni.ExtAlogoCfg;
import hobot.sunrise.sdk.jni.FaceAlogoCfg;
import hobot.sunrise.sdk.jni.FaceCfg;
import hobot.sunrise.sdk.jni.FaceResultListener;
import hobot.sunrise.sdk.jni.FaceSnapConfig;
import hobot.sunrise.sdk.jni.FeaResult;
import hobot.sunrise.sdk.jni.HobotBtParam;
import hobot.sunrise.sdk.jni.HobotFaceModuleCap;
import hobot.sunrise.sdk.jni.HobotFaceModuleCfg;
import hobot.sunrise.sdk.jni.HobotSunriseSdkJni;
import hobot.sunrise.sdk.jni.HobotSysSifConfig;
import hobot.sunrise.sdk.jni.HobotSysVer;
import hobot.sunrise.sdk.jni.Rect;
import hobot.sunrise.sdk.jni.TrackCfg;

public class SunriseSdkUtil {
    private static final String TAG = SunriseSdkUtil.class.getName();
    public static HobotSunriseSdkJni hobotSunriseSdkJni = null;
    public static int sunriseHandle = 0;
    public static boolean modeEnable = false;

    public static int init() {
        if (sunriseHandle != 0 && sunriseHandle != -1) {
            Log.i(TAG, "SunriseSdkUtil already init");
            return -1;
        }
        /*HobotSunriseSdkJni TEST*/
        hobotSunriseSdkJni = new HobotSunriseSdkJni();
        short width = (short) CameraDisplayUtil.getCameraWidth();
        short height = (short) CameraDisplayUtil.getCameraHeight();
//        int ret = hobotSunriseSdkJni.sys_set_cp_img_size(width, height);
        int ret = hobotSunriseSdkJni.sys_module_init();
        Log.i(TAG, "sys_module_init ret = " + ret);
        HobotBtParam btParam = new HobotBtParam(
                EnumClass.HobotSysBtMode.SYS_BT_YUV,
                EnumClass.HobotSysClkInv.SYS_CLK_INVERT);
        HobotSysSifConfig hobotSysSifConfig = new HobotSysSifConfig(
                width, height,
                EnumClass.HobotSysFormat.SYS_YUV420SP,
                EnumClass.HobotSysPixLen.SYS_PIX_LEN_8,
                EnumClass.HobotSysBusType.SYS_BUS_TYPE_MIPI,
                btParam);
        ret = hobotSunriseSdkJni.sys_sif_config(hobotSysSifConfig);
        Log.i(TAG, "sys_sif_config ret = " + ret);
        sunriseHandle = hobotSunriseSdkJni.face_module_create();
        Log.i(TAG, "face_module_create handle = " + sunriseHandle);
        return ret;
    }

    public static void setJniLogLevel(int level) {
        if (hobotSunriseSdkJni != null) {
            hobotSunriseSdkJni.hobot_jni_log_level(level);
        }
    }

    public static void setJniFaceAE(boolean enable) {
        if (hobotSunriseSdkJni != null) {
            hobotSunriseSdkJni.hobot_jni_enable_camera_face_ae(enable);
        }
    }

    public static void setFaceResultListener(FaceResultListener listener) {
        if (hobotSunriseSdkJni != null) {
            hobotSunriseSdkJni.setFaceResultListener(listener);
        }
    }

    public static HobotSysVer getVersion() {
        HobotSysVer hobotSysVer = null;
        if (hobotSunriseSdkJni != null && sunriseHandle != 0 && sunriseHandle != -1) {
            hobotSysVer = hobotSunriseSdkJni.face_module_get_version(sunriseHandle);
            Log.d(TAG, "hobotSysVer.model = " + hobotSysVer.model);
            Log.d(TAG, "hobotSysVer.firmware = " + hobotSysVer.firmware);
            Log.d(TAG, "hobotSysVer.uboot = " + hobotSysVer.uboot);
            Log.d(TAG, "hobotSysVer.sdk = " + hobotSysVer.sdk);
        }
        return hobotSysVer;
    }

    public static int resetCp() {
        int ret = -1;
        if (hobotSunriseSdkJni != null) {
            ret = hobotSunriseSdkJni.sys_cp_reset();
        }
        return ret;
    }

    public static HobotFaceModuleCap getCap() {
        HobotFaceModuleCap hobotFaceModuleCap = null;
        if (hobotSunriseSdkJni != null && sunriseHandle != 0 && sunriseHandle != -1) {
            Log.d(TAG, "HobotFaceModuleCap sunriseHandle = " + sunriseHandle);
            hobotFaceModuleCap = hobotSunriseSdkJni.face_module_get_cap(sunriseHandle);
            if (hobotFaceModuleCap != null) {
                Log.d(TAG, "hobotFaceModuleCap.faceCap.faceBaseCap.track = " + hobotFaceModuleCap.faceCap.faceBaseCap
                        .track);
                Log.d(TAG, "hobotFaceModuleCap.faceCap.faceBaseCap.feature = " + hobotFaceModuleCap.faceCap.faceBaseCap
                        .feature);
                Log.d(TAG, "hobotFaceModuleCap.faceCap.faceBaseCap.snap = " + hobotFaceModuleCap.faceCap.faceBaseCap
                        .snap);
                Log.d(TAG, "hobotFaceModuleCap.faceCap.faceAttrCap.lmk = " + hobotFaceModuleCap.faceCap.faceAttrCap
                        .lmk);
                Log.d(TAG, "hobotFaceModuleCap.faceCap.faceQualityCap.pose = " + hobotFaceModuleCap.faceCap
                        .faceQualityCap.pose);
                Log.d(TAG, "hobotFaceModuleCap.faceCap.faceQualityCap.s3d_pose = " + hobotFaceModuleCap.faceCap
                        .faceQualityCap.s3d_pose);
                Log.d(TAG, "hobotFaceModuleCap.faceCap.faceQualityCap.clarity = " + hobotFaceModuleCap.faceCap
                        .faceQualityCap.clarity);
                Log.d(TAG, "hobotFaceModuleCap.faceCap.faceQualityCap.occlusion = " + hobotFaceModuleCap.faceCap
                        .faceQualityCap.occlusion);
                Log.d(TAG, "hobotFaceModuleCap.extCap.extBaseCap.head_track = " + hobotFaceModuleCap.extCap.extBaseCap
                        .head_track);
            }
        }
        return hobotFaceModuleCap;
    }

    public static int config(HobotFaceModuleCfg faceModuleCfg) {
        int ret = -1;
        if (hobotSunriseSdkJni != null && sunriseHandle != 0 && sunriseHandle != -1) {
            ret = hobotSunriseSdkJni.face_module_config(sunriseHandle, faceModuleCfg);
            Log.i(TAG, "face_module_config ret = " + ret);
        }
        return ret;
    }

    public static int config() {
        int ret = -1;
        if (hobotSunriseSdkJni != null && sunriseHandle != 0 && sunriseHandle != -1) {
            HobotFaceModuleCap hobotFaceModuleCap = SunriseSdkUtil.getCap();
            HobotFaceModuleCfg faceModuleCfg = new HobotFaceModuleCfg();
            faceModuleCfg.faceCfg = new FaceCfg();
            faceModuleCfg.faceAlogoCfg = new FaceAlogoCfg();
            faceModuleCfg.extAlogoCfg = new ExtAlogoCfg();
            faceModuleCfg.faceCfg.snap_cfg = new FaceSnapConfig();
            if (hobotFaceModuleCap != null) {
                if (hobotFaceModuleCap.faceCap.faceBaseCap.track) {
                    faceModuleCfg.faceAlogoCfg.track_en = true;
                    if (hobotFaceModuleCap.faceCap.faceAttrCap.lmk)
                        faceModuleCfg.faceAlogoCfg.attr_lmk_en = true;
                    if (hobotFaceModuleCap.faceCap.faceQualityCap.pose)
                        faceModuleCfg.faceAlogoCfg.attr_pose_en = true;
                    if (hobotFaceModuleCap.faceCap.faceQualityCap.s3d_pose)
                        faceModuleCfg.faceAlogoCfg.attr_3d_pose_en = true;
                    if (hobotFaceModuleCap.faceCap.faceQualityCap.clarity)
                        faceModuleCfg.faceAlogoCfg.attr_clarity_en = true;
                    faceModuleCfg.faceCfg.track_cfg = new TrackCfg();
                    faceModuleCfg.faceCfg.track_cfg.num_frame_after_vanish = 30;
                    faceModuleCfg.faceCfg.track_cfg.track_score_eliminate = HorizonPreferences.getSensitivity();
//                    faceModuleCfg.faceAlogoCfg.reserve = new boolean[1];
//                    faceModuleCfg.faceAlogoCfg.reserve[0] = true;
                }
                if (hobotFaceModuleCap.faceCap.faceBaseCap.feature)
                    faceModuleCfg.faceAlogoCfg.feature_en = true;
                if (hobotFaceModuleCap.faceCap.faceBaseCap.snap)
                    faceModuleCfg.faceAlogoCfg.snap_en = true;
                if (hobotFaceModuleCap.extCap.extBaseCap.head_track)
                    faceModuleCfg.extAlogoCfg.head_track_alo_en = true;
            }
            faceModuleCfg.reserve = new int[1];
            faceModuleCfg.reserve[0] = 0x5A;
            faceModuleCfg.extAlogoCfg.g_feature_queue_max_len = HorizonPreferences.getFeatureQueueMaxLen();
            faceModuleCfg.extAlogoCfg.snap_face_sel_mode = HorizonPreferences.getBigFaceOpen() ?
                    EnumClass.HobotSnapFaceSelMode.SNAP_BOX_LARGE_MODE : EnumClass.HobotSnapFaceSelMode.SNAP_DEFAULT_MODE;
            faceModuleCfg.extAlogoCfg.det_frame_rate_div = HorizonPreferences.getDetFrameLevel();
            faceModuleCfg.extAlogoCfg.sif_freq_down_grade = HorizonPreferences.getSifFreqLevel();
            if (HorizonPreferences.getCaptureSize() != 0) {
                faceModuleCfg.faceCfg.snap_cfg.snap_size_thr = HorizonPreferences.getCaptureSize();
            } else {
                faceModuleCfg.faceCfg.snap_cfg.snap_size_thr = 80;
            }
            if (HorizonPreferences.getFrontalThr() != 0) {
                faceModuleCfg.faceCfg.snap_cfg.frontal_thr = HorizonPreferences.getFrontalThr();
            } else {
                faceModuleCfg.faceCfg.snap_cfg.frontal_thr = 1000;
            }
            if (HorizonPreferences.getBeginPostFrameThr() != 0) {
                faceModuleCfg.faceCfg.snap_cfg.begin_post_frame_thr = HorizonPreferences.getBeginPostFrameThr();
            } else {
                faceModuleCfg.faceCfg.snap_cfg.begin_post_frame_thr = 1;
            }
            if (HorizonPreferences.getFirstNumAvailThr() != 0) {
                faceModuleCfg.faceCfg.snap_cfg.first_num_avail_thr = HorizonPreferences.getFirstNumAvailThr();
            } else {
                faceModuleCfg.faceCfg.snap_cfg.first_num_avail_thr = 1;
            }
            if (HorizonPreferences.getResnapThr() != 0) {
                faceModuleCfg.faceCfg.snap_cfg.resnap_thr = HorizonPreferences.getResnapThr();
            } else {
                faceModuleCfg.faceCfg.snap_cfg.resnap_thr = 1;
            }
            if (HorizonPreferences.getSnapScaleThr() != 0) {
                faceModuleCfg.faceCfg.snap_cfg.snap_scale = HorizonPreferences.getSnapScaleThr();
            } else {
                faceModuleCfg.faceCfg.snap_cfg.snap_scale = 1.0F;
            }
            faceModuleCfg.faceCfg.snap_cfg.bound_thr_w = 0;
            faceModuleCfg.faceCfg.snap_cfg.bound_thr_h = 0;
            faceModuleCfg.faceCfg.snap_cfg.snap_pose_step = 200;
            faceModuleCfg.faceCfg.snap_cfg.x1 = 0;
            faceModuleCfg.faceCfg.snap_cfg.y1 = 0;
            faceModuleCfg.faceCfg.snap_cfg.x2 = CameraDisplayUtil.getCameraWidth();
            faceModuleCfg.faceCfg.snap_cfg.y2 = CameraDisplayUtil.getCameraHeight();
            ret = config(faceModuleCfg);
        }
        return ret;
    }

    public static int start() {
        int ret = -1;
        if (hobotSunriseSdkJni != null && sunriseHandle != 0 && sunriseHandle != -1) {
            ret = hobotSunriseSdkJni.face_module_start(sunriseHandle);
            Log.i(TAG, "face_module_start sunriseHandle = " + sunriseHandle + " ret = " + ret);
        }
        return ret;
    }

    public static void setCb() {
        if (hobotSunriseSdkJni != null && sunriseHandle != 0 && sunriseHandle != -1) {
            hobotSunriseSdkJni.face_module_set_cb(sunriseHandle);
        }
    }

    public static int stop() {
        int ret = -1;
        if (hobotSunriseSdkJni != null && sunriseHandle != 0 && sunriseHandle != -1) {
            Log.d(TAG, "face_module_stop in");
            ret = hobotSunriseSdkJni.face_module_stop(sunriseHandle);
            Log.i(TAG, "face_module_stop sunriseHandle = " + sunriseHandle + " ret = " + ret);
        }
        return ret;
    }

    public static int changeMode(boolean enable) {
        int ret = -1;
        if (modeEnable == enable) {
            return 0;
        }
        if (hobotSunriseSdkJni != null && sunriseHandle != 0 && sunriseHandle != -1) {
            ret = hobotSunriseSdkJni.face_moduel_change_to_ddr(sunriseHandle, enable);
            modeEnable = enable;
        }
        return ret;
    }

    public static FeaResult getFeature(byte[] img, int width, int height, Rect rect) {
        if (img == null) {
            Log.e(TAG, "getFeature img is null");
            return null;
        }
        FeaResult feaResult = null;
        if (hobotSunriseSdkJni != null && sunriseHandle != 0 && sunriseHandle != -1) {
            feaResult = hobotSunriseSdkJni.picface_module_get_feature(sunriseHandle, img, width, height, rect);
//        if (feaResult != null) {
//            for (int i = 0; i < feaResult.featureArray.length; i++) {
//                featurearray[i] = (float) (feaResult.featureArray[i])
//                        / (float) (1 << feaResult.shift_l);
//                Log.d(TAG, "getFeature featurearray[" + i + "]=" + feaResult.featureArray[i]);
//            }
//        }
        }
        return feaResult;
    }

    public static int deinit() {
        int ret = -1;
        if (hobotSunriseSdkJni != null && sunriseHandle != 0 && sunriseHandle != -1) {
            ret = hobotSunriseSdkJni.face_module_destroy(sunriseHandle);
            sunriseHandle = 0;
            Log.i(TAG, "face_module_destroy sunriseHandle = " + sunriseHandle + " ret = " + ret);
            ret = hobotSunriseSdkJni.sys_module_deinit();
            Log.i(TAG, "sys_module_deinit ret = " + ret);
            hobotSunriseSdkJni = null;
        }
        return ret;
    }

}
