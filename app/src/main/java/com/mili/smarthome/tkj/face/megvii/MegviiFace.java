package com.mili.smarthome.tkj.face.megvii;

import android.content.Context;

import com.android.Common;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.app.CustomVersion;
import com.mili.smarthome.tkj.dao.param.NetworkParamDao;

import mcv.facepass.FacePassHandler;
import mcv.facepass.types.FacePassConfig;
import mcv.facepass.types.FacePassImageRotation;
import mcv.facepass.types.FacePassModel;
import mcv.facepass.types.FacePassPose;

/**
 * 旷视人脸
 * 2018-07-20: Created by chenrh.
 */
public class MegviiFace {

    /* MegviiFace */
    private static MegviiFace instance;

    /**
     * MegviiFace ,单例模式
     */
    public static MegviiFace getInstance() {
        if (instance == null) {
            instance = new MegviiFace();
        }
        return instance;
    }

    /* 人脸模式(在线&离线) */
    public enum FacePassSDKMode {
        MODE_ONLINE,
        MODE_OFFLINE
    }

    /* 授权文件保存路径 */
    public static final String FILE_ROOT_PATH = Const.Directory.MEGVII;
    /* 图像旋转角度值，0/90/180/270 */
    public static final int IMAGE_ROTATION = FacePassImageRotation.DEG0;
    /* 人脸授权服务器地址 */
    public static final String AUTH_IP = "https://api-cn.faceplusplus.com";
    /* Face++官方正式apiKey&apiSecret,<注意：正式key要对应正式的aar包使用> */
    public static final String API_KEY = "L1msmuTa2GU3JysqKrJJBVjpWOLkvPGo";
    public static final String API_SECRET = "uFBPqsX_weqy7hJlNByrxxLR3MWbPlHH";
    /* Face++官方测试apiKey&apiSecret,<注意：测试key要对应测试的aar包使用> */
//    public static final String API_KEY = "KWMFm90Y9tNjXsvoxR9U-uzLz8OIKk7E";
//    public static final String API_SECRET = "go7-s38XI4_Xexr7QmhhGb_9fpx2xRf0";

    /* Face++SDK实例对象 */
    public FacePassHandler mFacePassHandler = null;
    public FacePassSDKMode SDK_MODE = FacePassSDKMode.MODE_OFFLINE;
    public String serverIP = "";
    /* 在线模式：人脸操作请求的URL(识别、注册、注销) */
    public String recognizeUrl;
    public String registerUrl;
    public String unregisterUrl;

    /* FacePass SDK 所需模型， 算法模型在assets目录下 */
    public FacePassConfig getFacePassConfig(Context context, int safeLevel, int liveCheck) {
        FacePassConfig config = new FacePassConfig();

        config.poseBlurModel = FacePassModel.initModel(context.getAssets(), "megvii/attr.pose_blur.align.av200.190630.bin");

//        config.livenessModel = FacePassModel.initModel(context.getAssets(), "megvii/liveness.3288CPU.rgb.int8.C.bin");
        //也可以使用GPU活体模型，GPU活体模型分两个，用于GPU加速的模型和CACHE，当使用CPU活体模型时，请传null，当使用GPU活体模型时，必须传入加速cache模型
        //config.livenessModel = FacePassModel.initModel(context.getAssets(), "megvii/liveness.3288GPU.rgb.C.bin");
        //config.livenessGPUCache = FacePassModel.initModel(context.getAssets(), "megvii/liveness.GPU.AlgoPolicy.C.cache");

        config.rgbIrLivenessModel = FacePassModel.initModel(context.getAssets(), "megvii/liveness.3288CPU.rgbir.int8.C.bin");

        config.searchModel = FacePassModel.initModel(context.getAssets(), "megvii/feat2.arm.F.v1.0.1core.bin");
        config.detectModel = FacePassModel.initModel(context.getAssets(), "megvii/detector.arm.C.bin");
        config.detectRectModel = FacePassModel.initModel(context.getAssets(), "megvii/detector_rect.arm.C.bin");
        config.landmarkModel = FacePassModel.initModel(context.getAssets(), "megvii/pf.lmk.float32.1015.bin");
        //config.smileModel = FacePassModel.initModel(context.getAssets(), "megvii/attr.smile.mgf29.0.1.1.181229.bin");
        //config.ageGenderModel = FacePassModel.initModel(context.getAssets(), "megvii/attr.age_gender.surveillance.nnie.av200.0.1.0.190630.bin");
        //config.occlusionFilterModel = FacePassModel.initModel(context.getAssets(), "megvii/occlusion.all_attr_configurable.occ.190816.bin");
        config.mouthOccAttributeModel = FacePassModel.initModel(context.getAssets(), "megvii/attribute.mouth.occ.gray.12M.190930.bin");

        //config.occlusionFilterEnabled = true;
        config.mouthOccAttributeEnabled = true;

        /* 授权文件保存路径 */
        config.fileRootPath = FILE_ROOT_PATH;
        /* 图像旋转角度值，0/90/180/270 */
        config.rotation = IMAGE_ROTATION;
        /* 活体检测开关*/
//        config.livenessEnabled = (liveCheck != 0);
        config.livenessEnabled = false;
        config.rgbIrLivenessEnabled = (liveCheck != 0);

        if (safeLevel == 0) {
            /* 识别阈值 */
            config.searchThreshold = FaceSafeLevel.High._search;//建议值：76f;
            /* 活体阈值 */
            config.livenessThreshold = FaceSafeLevel.High._liveness;//建议值：50f;
            /* 笑脸检测开关*/
            config.smileEnabled = FaceSafeLevel.High._isSmile;
            /* 最小人脸尺寸,取值范围[0,512] */
            config.faceMinThreshold = FaceSafeLevel.High._faceMin;//建议值：100
            /* 人脸角度阈值（旋转角度30f，垂直角度30f，水平角度30f */
            config.poseThreshold = new FacePassPose(FaceSafeLevel.High._pose1, FaceSafeLevel.High._pose2, FaceSafeLevel.High._pose3);//建议值：30
            /* 人脸模糊度阈值,取值范围[0, 1] */
            config.blurThreshold = FaceSafeLevel.High._blur;//建议值：0.2f;
            /* 人脸平均照度阈值范围,取值范围[0,255]，建议[70,210] */
            config.lowBrightnessThreshold = FaceSafeLevel.High._lowBrightness;//建议值：70f;
            config.highBrightnessThreshold = FaceSafeLevel.High._highBrightness;//建议值：210f;
            /* 人脸照度标准差阈值,取值范围[0,255]，越大对别越强烈 */
            config.brightnessSTDThreshold = FaceSafeLevel.High._brightnessSTD;//建议值：80f;
            /* 识别失败时的重试次数 */
            config.retryCount = FaceSafeLevel.High._retryTime;//建议值：2
            /* 最近脸检测开关 */
            config.maxFaceEnabled = FaceSafeLevel.High._isMaxFaceEnabled;
        } else if (safeLevel == 1) {
            /* 识别阈值 */
            config.searchThreshold = FaceSafeLevel.Medium._search;//建议值：76f;
            /* 活体阈值 */
            config.livenessThreshold = FaceSafeLevel.Medium._liveness;//建议值：50f;
            /* 笑脸检测开关*/
            config.smileEnabled = FaceSafeLevel.Medium._isSmile;
            /* 最小人脸尺寸,取值范围[0,512] */
            config.faceMinThreshold = FaceSafeLevel.Medium._faceMin;//建议值：100
            /* 人脸角度阈值（旋转角度30f，垂直角度30f，水平角度30f */
            config.poseThreshold = new FacePassPose(FaceSafeLevel.Medium._pose1, FaceSafeLevel.Medium._pose2, FaceSafeLevel.Medium._pose3);//建议值：30
            /* 人脸模糊度阈值,取值范围[0, 1] */
            config.blurThreshold = FaceSafeLevel.Medium._blur;//建议值：0.2f;
            /* 人脸平均照度阈值范围,取值范围[0,255]，建议[70,210] */
            config.lowBrightnessThreshold = FaceSafeLevel.Medium._lowBrightness;//建议值：70f;
            config.highBrightnessThreshold = FaceSafeLevel.Medium._highBrightness;//建议值：210f;
            /* 人脸照度标准差阈值,取值范围[0,255]，越大对别越强烈 */
            config.brightnessSTDThreshold = FaceSafeLevel.Medium._brightnessSTD;//建议值：80f;
            /* 识别失败时的重试次数 */
            config.retryCount = FaceSafeLevel.Medium._retryTime;//建议值：2
            /* 最近脸检测开关 */
            config.maxFaceEnabled = FaceSafeLevel.Medium._isMaxFaceEnabled;
        } else if (safeLevel == 2) {
            /* 识别阈值 */
            config.searchThreshold = FaceSafeLevel.Low._search;//建议值：76f;
            /* 活体阈值 */
            config.livenessThreshold = FaceSafeLevel.Low._liveness;//建议值：50f;
            /* 笑脸检测开关*/
            config.smileEnabled = FaceSafeLevel.Low._isSmile;
            /* 最小人脸尺寸,取值范围[0,512] */
            config.faceMinThreshold = FaceSafeLevel.Low._faceMin;//建议值：100
            /* 人脸角度阈值（旋转角度30f，垂直角度30f，水平角度30f */
            config.poseThreshold = new FacePassPose(FaceSafeLevel.Low._pose1, FaceSafeLevel.Low._pose2, FaceSafeLevel.Low._pose3);//建议值：30
            /* 人脸模糊度阈值,取值范围[0, 1] */
            config.blurThreshold = FaceSafeLevel.Low._blur;//建议值：0.2f;
            /* 人脸平均照度阈值范围,取值范围[0,255]，建议[70,210] */
            config.lowBrightnessThreshold = FaceSafeLevel.Low._lowBrightness;//建议值：70f;
            config.highBrightnessThreshold = FaceSafeLevel.Low._highBrightness;//建议值：210f;
            /* 人脸照度标准差阈值,取值范围[0,255]，越大对别越强烈 */
            config.brightnessSTDThreshold = FaceSafeLevel.Low._brightnessSTD;//建议值：80f;
            /* 识别失败时的重试次数 */
            config.retryCount = FaceSafeLevel.Low._retryTime;//建议值：2
            /* 最近脸检测开关 */
            config.maxFaceEnabled = FaceSafeLevel.Low._isMaxFaceEnabled;
        }
        setServerIP(NetworkParamDao.getFaceIp());
        return config;
    }

    public void setServerIP(String faceIp) {
        /* 人脸模式(在线&离线)请求地址 */
        SDK_MODE = (Common.ipToint(faceIp) == 0) ? FacePassSDKMode.MODE_OFFLINE : FacePassSDKMode.MODE_ONLINE;
        if (SDK_MODE == FacePassSDKMode.MODE_ONLINE) {
            serverIP = faceIp;
            recognizeUrl = "http://" + serverIP + ":8080/api/service/recognize/v1";
            registerUrl = "http://" + serverIP + ":8080/api/face/v1/add";
            unregisterUrl = "http://" + serverIP + ":8080/api/face/v1/delete";
        } else {
            serverIP = "";
        }
    }
}
