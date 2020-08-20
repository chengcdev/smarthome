package com.mili.smarthome.tkj.face.horizon.util;

import android.content.Context;
import android.util.Log;

import com.hobot.livenesssdk.AutoCoordinateMapping;
import com.hobot.livenesssdk.Fov;
import com.hobot.livenesssdk.Image;
import com.hobot.livenesssdk.LiveResult;
import com.hobot.livenesssdk.hobotLivenessIf;
import com.mili.smarthome.tkj.face.horizon.bean.TrackInfo;

import hobot.sunrise.sdk.jni.Quality;
import hobot.sunrise.sdk.jni.Rect;

public class LiveNessUtil {
    private static final String TAG = "HOBOT_LIVENESS_UTIL";
    private static hobotLivenessIf mHobotLivenessIf = null;
    private static boolean openLiveNess = false;
    private static boolean isStarted = false;
    private static AutoCoordinateMapping autoMapping = new AutoCoordinateMapping();
    private static Image image = new Image();

    private static long totalTime = 0;
    private static long totalCnt = 0;
    private static long minElapse = 10000;
    private static long maxElapse = 0;
    private final static long FREE_DISK = 300;  // MB

    //private final static String LOG_PATH = "/sdcard/hobot/liveness/";
    public static int initErrCode = 0;

    private static void initCalcAvgTime() {
        totalTime = 0;
        totalCnt = 0;
        minElapse = 0;
        maxElapse = 0;
    }

    private static long calcAvgTime(long elapse) {
        if (minElapse > elapse) {
            minElapse = elapse;
        }

        if (maxElapse < elapse) {
            maxElapse = elapse;
        }
        totalTime += elapse;
        totalCnt++;
        long avgTime = totalTime / totalCnt;

        Log.d(TAG, "------------totalCnt=" + totalCnt +
                ", elapse=" + elapse +
                ", avgTime=" + avgTime +
                ", minElapse=" + minElapse +
                ", maxElapse=" + maxElapse);

        return avgTime;
    }

    public static void init(Context context, String pModulePath, String cachePath) {
        Log.d(TAG, "init in mHobotLivenessIf = " + mHobotLivenessIf);
        if (mHobotLivenessIf == null) {
            initErrCode = 0;
            //String livenessPath = autoMapping.genSaveDir(null, LOG_PATH);
            mHobotLivenessIf = new hobotLivenessIf();
            initErrCode = mHobotLivenessIf.copyModules(context, pModulePath) ? 0 : -1;
            if (initErrCode != 0) {
                Log.d(TAG, "init mHobotLivenessIf copyModules failed pModulePath =" + pModulePath);
                return;
            }
            if (HorizonPreferences.getResolutionRatio() != 0) {
                mHobotLivenessIf.setDetectSizeEn(true);
                // 设置两个脸是否大于指定瞳距的倍数
                mHobotLivenessIf.setEyeRatioDistance(10.0f);
            } else {
                // 设置两个脸是否大于指定瞳距的倍数
                mHobotLivenessIf.setEyeRatioDistance(1.5f);
            }
            // log打印和保存相关
            mHobotLivenessIf.setLogLevel(2);
            //mHobotLivenessIf.setLogSave(2);
            //mHobotLivenessIf.setLogPath(LOG_PATH + "liveness.log");

            // bit0~bit3: 2：可以防电子屏，3：提升强光下的效果，但防电子屏减弱
            // bit4: 1：启用先试用xface检测人脸，如果检测到再做活检；0：不启用

            // 2: HobotLiveDetectFrame2
            // 3: xface-->HobotLiveDetectFrame3
            // 4: HobotLiveDetectFrame2-->xface-->HobotLiveDetectFrame3
            // 5: HobotLiveDetectFrame3
            mHobotLivenessIf.setLiveDetectIfType(2);

            Fov fov = autoMapping.loadCoordinateJson(HorizonPreferences.getCameraType());
            int retCoordinate = -2;
            if (null != fov) {
                retCoordinate = mHobotLivenessIf.setFixCoordinate(fov);
            }
            int tryInit = 3;
            do {
                initErrCode = mHobotLivenessIf.initLivenessSdk(pModulePath, cachePath);
                tryInit--;
            } while (initErrCode == 65535 && tryInit > 0)/*避免活体加密芯片通讯失败，多try几次*/;

            // yuv保存相关
            //mHobotLivenessIf.setSaveYuvPath(livenessPath);
            Log.d(TAG, "initLivenessSdk=" + initErrCode + ", ver=" + getVersion() + ", retCoordinate=" + retCoordinate);
        }
    }

    public static void setLiveNessOpen(boolean enable) {
        openLiveNess = enable;
    }

    public static boolean getLiveNessOpen() {
        return openLiveNess;
    }

    public static void deinit() {
        Log.d(TAG, "deinit in mHobotLivenessIf = " + mHobotLivenessIf);
        if (mHobotLivenessIf != null) {
            if (isStarted) {
                stop();
            }
            mHobotLivenessIf.deinitLivenessSdk();
            mHobotLivenessIf = null;
        }
        Log.d(TAG, "deinit out mHobotLivenessIf = " + mHobotLivenessIf);
    }

    public static int start() {
        int ret = 0;
        Log.d(TAG, "start in mHobotLivenessIf = " + mHobotLivenessIf);
        if (mHobotLivenessIf != null && !isStarted) {
            String horizonPath = HorizonPreferences.getHorizonPath();
            mHobotLivenessIf.setXfacePath(horizonPath + "/model_conf.json");
            ret = mHobotLivenessIf.startLivenessSdk("/dev/video0");
            if (ret == 0) {
                initCalcAvgTime();
                isStarted = true;

                //bit0: 640x480, bit1: 320x480, bit2: 320x240，bit3：指示保存活检成功的图片，bit4：指示保存活检失败的图片
                //         1              2              4                  8                            16
//                int flag = 0;
//                switch (HorizonPreferences.getRecogOption()) {
//                    case 1: // 保存判假yuv
//                        flag = 2 + 4 + 16;
//                        break;
//                    case 2: // 保存判真yuv
//                        flag = 2 + 4 + 8;
//                        break;
//                    case 3: // 保存全部
//                        flag = 2 + 4 + 8 + 16;
//                        break;
//                    default: // 不保存
//                        flag = 0;
//                        break;
//                }
//                mHobotLivenessIf.setSaveYuvFlag(flag);
            }
            Log.d(TAG, "startLivenessSdk=" + ret);
        }
        return ret;
    }

    public static void stop() {
        Log.d(TAG, "stop in mHobotLivenessIf = " + mHobotLivenessIf);
        if (mHobotLivenessIf != null && isStarted) {
            mHobotLivenessIf.stopLivenessSdk();
            isStarted = false;
        }
        Log.d(TAG, "stop out mHobotLivenessIf = " + mHobotLivenessIf);
    }

    public static int updateParam(boolean force, int id, String name, int nLeftEyeX, int nLeftEyeY, int nRightEyeX, int
            nRightEyeY, long ts, boolean assign_pos_livedetect_en, boolean onePer, boolean crop, Quality quality,
                                  Rect rect) {
        if (mHobotLivenessIf == null || !isStarted) {
            return -11;
        }
        if (getSDAvailableSize() <= FREE_DISK) {
            return -99;
        }

        int ret = 0;
        int mirror_en = (1 == HorizonPreferences.getCameraType()) ? 0 : 1;

        //int id, int ts, int crop_en, int fov_en, int is_one_person, int mirror_en, String id_name, int
        // auto_coordiante_mapping_en
        image.frame.init(id,
                (int) ts,
                crop ? 1 : 0,
                HorizonPreferences.getResolutionRatio() == 0 ? 1 : 0,
                assign_pos_livedetect_en ? 1 : 0,
                0,/*传0*/
                mirror_en,
                name,
                HorizonPreferences.getAutoFixCoordinate() ? 1 : 0);

        image.pose.init(quality.pitch, quality.roll, quality.yaw);

        image.rect.init(rect.left, rect.top, rect.right, rect.bottom);

        image.eye.init(nLeftEyeX, nLeftEyeY, nRightEyeX, nRightEyeY);

        ret = mHobotLivenessIf.updateParam(image);
        //Log.d(TAG, "-------updateParam ret=" + ret);

        return ret;
    }

    public static boolean getResult(int[] id) {
        boolean ret = false;
        int err;
        LiveResult liveResult = new LiveResult();
        if (mHobotLivenessIf != null && isStarted) {
            err = mHobotLivenessIf.getResult(liveResult);
            ret = 1 == liveResult.live;
            id[0] = (err < 0) ? err : liveResult.id;
            id[1] = liveResult.score;
            id[2] = 15;
            id[3] = liveResult.eye_center_x;
            id[4] = liveResult.eye_center_y;

            if (HorizonPreferences.getAutoFixCoordinate()) {
                Fov fov = autoMapping.calcCoordinateJson(image.eye.center_x, image.eye.center_y,
                        liveResult.eye_center_x, liveResult.eye_center_y,
                        (image.rect.right - image.rect.left) / 2,
                        Integer.valueOf(CameraDisplayUtil.getCameraDisplayRatio()));
                if (fov == null) {
                    return false;
                }
                Log.d(TAG, "Info: fov.valid=" + fov.valid + ", fov.step=" + fov.step);
                id[2] = fov.step;
                if (1 == fov.valid) {
                    HorizonPreferences.saveAutoFixCoordinate(false);
                    fov = autoMapping.loadCoordinateJson(0);
                    if (null != fov) {
                        mHobotLivenessIf.setFixCoordinate(fov);
                    }
                }
            }
            Log.d(TAG, "getResult=" + liveResult.id_name +
                    ", err=" + err +
                    ", id=" + liveResult.id +
                    ", live=" + liveResult.live +
                    ", score=" + liveResult.score +
                    ", centerx=" + liveResult.eye_center_x +
                    ", centery=" + liveResult.eye_center_y +
                    ", elapse=" + liveResult.elapse +
                    ", autoFix=" + liveResult.auto_fix +
                    ", is_same_person=" + liveResult.is_same_person);


            if (1 == liveResult.live) {
                calcAvgTime(liveResult.elapse);
            }
        }

        if (!isStarted) {
            id[0] = 0;
        }

        return ret;
    }

    public static int[] getEyePos(TrackInfo trackInfo) {
        int[] pos = new int[trackInfo.lmk_len * 2 + 2];
        // 框的中心点坐标
        int centerPosX = (trackInfo.rect.left + trackInfo.rect.right) / 2;
        int centerPosY = (trackInfo.rect.top + trackInfo.rect.bottom) / 2;
        // pos[0-1]为左眼，pos[2-3]为右眼，pos[4-5]为鼻子，pos[6-7]为左唇，pos[8-9]为右唇
        if (trackInfo.xy == null) {
            Log.e(TAG, "getEyePos trackInfo.xy = null");
            return pos;
        } else {
            for (int i = 0; i < trackInfo.lmk_len; i++) {
                if (trackInfo.xy[i] == null) {
                    Log.e(TAG, "getEyePos trackInfo.xy[" + i + "] = null");
                    return pos;
                }
            }
        }
        if (0 == trackInfo.xy[0].x || 0 == trackInfo.xy[0].y ||
                0 == trackInfo.xy[1].x || 0 == trackInfo.xy[1].y) {
            trackInfo.xy[0].x = (short) (0 - (trackInfo.rect.right - trackInfo.rect.left) * 1 / 4);
            trackInfo.xy[0].y = (short) (trackInfo.rect.top + ((trackInfo.rect.bottom - trackInfo.rect.top) * 2 / 5)
                    - centerPosY);
            trackInfo.xy[1].x = (short) ((trackInfo.rect.right - trackInfo.rect.left) * 1 / 4);
            trackInfo.xy[1].y = (short) (trackInfo.rect.top + ((trackInfo.rect.bottom - trackInfo.rect.top) * 2 / 5)
                    - centerPosY);
            Log.d(TAG, "cos xy is zero, calculate it!");
        }

        for (int i = 0; i < trackInfo.lmk_len; i++) {
            pos[i * 2 + 0] = centerPosX + trackInfo.xy[i].x;
            pos[i * 2 + 1] = centerPosY + trackInfo.xy[i].y;
        }
        pos[trackInfo.lmk_len * 2] = centerPosX;
        pos[trackInfo.lmk_len * 2 + 1] = centerPosY;

//        Log.d(TAG, "rectCenter[" + centerPosX + ", " + centerPosY + "], " +
//                "eye:[" + trackInfo.xy[0].x + "_" + trackInfo.xy[0].y + "], [" + trackInfo.xy[1].x + "_" +
// trackInfo.xy[1].y + "]" +
//                "pos:[" + pos[0] + "_" + pos[1] + "], [" + pos[2] + "_" + pos[3] + "]");

        return pos;
    }

    public static String getVersion() {
        String version = null;
        if (mHobotLivenessIf != null) {
            version = mHobotLivenessIf.getAllVer();
        }
        return version;
    }

    public static void initCoordinateMapping() {
        autoMapping.initCoordinateMapping();
    }

    public static void setThreshold(int threshold) {
        if (mHobotLivenessIf != null) {
            mHobotLivenessIf.setLiveThreshold(threshold);
            Log.d(TAG, "set=" + threshold + ", LiveThreshold=" + getThreshold());
        }
    }

    public static int getThreshold() {
        int ret = -11;
        if (mHobotLivenessIf != null) {
            ret = mHobotLivenessIf.getLiveThreshold();
        }
        return ret;
    }

    private static long getSDAvailableSize() {
        long freeByte = (FlashSpaceCheck.getSDAvailableSize()) / (1024 * 1024);
        return freeByte;
    }

    public static int getPicNumber() {
        int ret = 0;
        if (mHobotLivenessIf != null) {
            ret = mHobotLivenessIf.getPanguTs();
        }
        return ret;
    }
}
