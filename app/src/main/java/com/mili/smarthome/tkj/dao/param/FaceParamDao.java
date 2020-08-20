package com.mili.smarthome.tkj.dao.param;

import com.mili.smarthome.tkj.appfunc.BuildConfigHelper;

/**
 * 人脸参数访问类
 */
public class FaceParamDao {

    public static final String FACE_PARAM = "face_param";

    public static final String KEY_FACE_MODE = "face_mode";
    public static final String KEY_FACE_MODULE = "face_module";
    public static final String KEY_FACE_RECOGNITION = "face_recognition";
    public static final String KEY_FACE_SECURITY = "face_security";
    public static final String KEY_FACE_LIVENESS = "face_liveness";
    public static final String KEY_FACE_BLACK_LIST = "face_black_list";
    public static final String KEY_RTSP_URL = "rtsp_url";

    /**
     * 人脸模式
     * @return 0印度离线，1旷视离线，2旷视在线
     */
    public static int getFaceMode() {
        return ParamDao.queryParamValue(KEY_FACE_MODE, 0);
    }

    /**
     * 人脸模式
     * @param value 0印度离线，1旷视离线，2旷视在线
     */
    public static void setFaceMode(int value) {
        ParamDao.saveParam(FACE_PARAM, KEY_FACE_MODE, value);
    }

    /**
     * 人脸模块
     * @return 0禁用，1启用
     */
    public static int getFaceModule() {
        return 1;//ParamDao.queryParamValue(KEY_FACE_MODULE, 1);
    }

    /**
     * 人脸模块
     * @param value 0禁用，1启用
     */
    public static void setFaceModule(int value) {
        ParamDao.saveParam(FACE_PARAM, KEY_FACE_MODULE, value);
    }

    /**
     * 人脸识别
     * @return 0禁用，1启用
     */
    public static int getFaceRecognition() {
//        int defaultValue = BuildConfigHelper.isGate() ? 1 : 0; // 闸机版默认启用人脸识别
        int defaultValue = 1;
        return ParamDao.queryParamValue(KEY_FACE_RECOGNITION, defaultValue);
    }

    /**
     * 人脸识别
     * @param value 0禁用，1启用
     */
    public static void setFaceRecognition(int value) {
        ParamDao.saveParam(FACE_PARAM, KEY_FACE_RECOGNITION, value);
    }

    /**
     * 安全级别
     * @return 0高，1正常，2普通
     */
    public static int getFaceSafeLevel() {
        return ParamDao.queryParamValue(KEY_FACE_SECURITY, 1);
    }

    /**
     * 安全级别
     * @param value 0高，1正常，2普通
     */
    public static void setFaceSafeLevel(int value) {
        ParamDao.saveParam(FACE_PARAM, KEY_FACE_SECURITY, value);
    }

    /**
     * 活体检测
     * @return 0禁用，1启用
     */
    public static int getFaceLiveCheck() {
        return ParamDao.queryParamValue(KEY_FACE_LIVENESS, 1);
    }

    /**
     * 活体检测
     * @param value 0禁用，1启用
     */
    public static void setFaceLiveCheck(int value) {
        ParamDao.saveParam(FACE_PARAM, KEY_FACE_LIVENESS, value);
    }

    /**
     * 布控管理
     * @return 0禁用，1启用
     */
    public static int getFaceBlackList() {
        return ParamDao.queryParamValue(KEY_FACE_BLACK_LIST, 0);
    }

    /**
     * 布控管理
     * @param value 0禁用，1启用
     */
    public static void setFaceBlackList(int value) {
        ParamDao.saveParam(FACE_PARAM, KEY_FACE_BLACK_LIST, value);
    }

    /**
     * 获取IPC地址
     */
    public static String getRtspUrl() {
        return ParamDao.queryParamValue(KEY_RTSP_URL, "");
    }

    /**
     * 设置IPC地址
     */
    public static void setRtspUrl(String value) {
        ParamDao.saveParam(FACE_PARAM, KEY_RTSP_URL, value);
    }
}
