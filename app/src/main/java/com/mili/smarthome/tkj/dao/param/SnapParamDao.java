package com.mili.smarthome.tkj.dao.param;

import com.mili.smarthome.tkj.app.CustomVersion;
import com.mili.smarthome.tkj.entities.param.ParamModel;
import com.mili.smarthome.tkj.entities.param.SnapParam;

import java.util.List;

/**
 * 拍照参数访问类
 */
public class SnapParamDao {

    public static final String SNAP_PARAM = "snap_param";

    public static final String KEY_VISITOR = "snap_visitor";
    public static final String KEY_ERROR_PWD = "snap_error_pwd";
    public static final String KEY_HIJACK_PWD = "snap_hijack_pwd";
    public static final String KEY_CALL_CENTER = "snap_call_center";
    public static final String KEY_FACE_OPEN = "snap_face_open";
    public static final String KEY_FINGER_OPEN = "snap_finger_open";
    public static final String KEY_CARD_OPEN = "snap_card_open";
    public static final String KEY_PWD_OPEN = "snap_pwd_open";
    public static final String KEY_QRCODE_OPEN = "snap_qrcode_open";
    public static final String KEY_FACE_STRANGER = "snap_face_stranger";

    /**
     * 访客拍照
     * @return 0不启用，1启用
     */
    public static int getVisitorSnap() {
        return ParamDao.queryParamValue(KEY_VISITOR, 0);
    }

    /**
     * 访客拍照
     * @param value 0不启用，1启用
     */
    public static void setVisitorSnap(int value) {
        ParamDao.saveParam(SNAP_PARAM, KEY_VISITOR, value);
    }

    /**
     * 错误密码开门拍照
     * @return 0不启用，1启用
     */
    public static int getErrorPwdSnap() {
        return ParamDao.queryParamValue(KEY_ERROR_PWD, 0);
    }

    /**
     * 错误密码开门拍照
     * @param value 0不启用，1启用
     */
    public static void setErrorPwdSnap(int value) {
        ParamDao.saveParam(SNAP_PARAM, KEY_ERROR_PWD, value);
    }

    /**
     * 挟持密码开门拍照
     * @return 0不启用，1启用
     */
    public static int getHijackPwdSnap() {
        return ParamDao.queryParamValue(KEY_HIJACK_PWD, 0);
    }

    /**
     * 挟持密码开门拍照
     * @param value 0不启用，1启用
     */
    public static void setHijackPwdSnap(int value) {
        ParamDao.saveParam(SNAP_PARAM, KEY_HIJACK_PWD, value);
    }

    /**
     * 呼叫中心拍照
     * @return 0不启用，1启用
     */
    public static int getCallCenterSnap() {
        return ParamDao.queryParamValue(KEY_CALL_CENTER, 0);
    }

    /**
     * 呼叫中心拍照
     * @param value 0不启用，1启用
     */
    public static void setCallCenterSnap(int value) {
        ParamDao.saveParam(SNAP_PARAM, KEY_CALL_CENTER, value);
    }

    /**
     * 人脸开门拍照
     * @return 0不启用，1启用
     */
    public static int getFaceOpenSnap() {
        return ParamDao.queryParamValue(KEY_FACE_OPEN, 0);
    }

    /**
     * 人脸开门拍照
     * @param value 0不启用，1启用
     */
    public static void setFaceOpenSnap(int value) {
        ParamDao.saveParam(SNAP_PARAM, KEY_FACE_OPEN, value);
    }

    /**
     * 指纹开门拍照
     * @return 0不启用，1启用
     */
    public static int getFingerOpenSnap() {
        return ParamDao.queryParamValue(KEY_FINGER_OPEN, 0);
    }

    /**
     * 指纹开门拍照
     * @param value 0不启用，1启用
     */
    public static void setFingerOpenSnap(int value) {
        ParamDao.saveParam(SNAP_PARAM, KEY_FINGER_OPEN, value);
    }

    /**
     * 刷卡开门拍照
     * @return 0不启用，1启用
     */
    public static int getCardOpenSnap() {
        return ParamDao.queryParamValue(KEY_CARD_OPEN, 0);
    }

    /**
     * 刷卡开门拍照
     * @param value 0不启用，1启用
     */
    public static void setCardOpenSnap(int value) {
        ParamDao.saveParam(SNAP_PARAM, KEY_CARD_OPEN, value);
    }

    /**
     * 密码开门拍照
     * @return 0不启用，1启用
     */
    public static int getPwdOpenSnap() {
        return ParamDao.queryParamValue(KEY_PWD_OPEN, 0);
    }

    /**
     * 密码开门拍照
     * @param value 0不启用，1启用
     */
    public static void setPwdOpenSnap(int value) {
        ParamDao.saveParam(SNAP_PARAM, KEY_PWD_OPEN, value);
    }

    /**
     * 扫码开门拍照
     * @return 0不启用，1启用
     */
    public static int getQrcodeOpenSnap() {
        return ParamDao.queryParamValue(KEY_QRCODE_OPEN, 0);
    }

    /**
     * 扫码开门拍照
     * @param value 0不启用，1启用
     */
    public static void setQrcodeOpenSnap(int value) {
        ParamDao.saveParam(SNAP_PARAM, KEY_QRCODE_OPEN, value);
    }

    /**
     * 陌生人人脸拍照
     * @return 0不启用，1启用
     */
    public static int getFaceStrangerSnap() {
        return ParamDao.queryParamValue(KEY_FACE_STRANGER, 0);
    }

    /**
     * 陌生人人脸拍照
     * @param value 0不启用，1启用
     */
    public static void setFaceStrangerSnap(int value) {
        ParamDao.saveParam(SNAP_PARAM, KEY_FACE_STRANGER, value);
    }

    /**
     * 获取拍照参数
     */
    public static SnapParam getSnapParam() {
        // 默认值
        int visitorSnap = 0;
        int errorPwdSnap = 0;
        int hijackPwdSnap = 0;
        int callCenterSnap = 0;
        int faceOpenSnap = 0;
        int fingerOpenSnap = 0;
        int cardOpenSnap = 0;
        int pwdOpenSnap = 0;
        int qrcodeOpenSnap = 0;
        int faceStrangerSnap = 0;

        // 查询数据库
        List<ParamModel> paramList = ParamDao.queryParamListByType(SNAP_PARAM);
        if (paramList != null) {
            for (ParamModel paramModel : paramList) {
                switch (paramModel.getKey()) {
                    case KEY_VISITOR:
                        visitorSnap = paramModel.getIntValue();
                        break;
                    case KEY_ERROR_PWD:
                        errorPwdSnap = paramModel.getIntValue();
                        break;
                    case KEY_HIJACK_PWD:
                        hijackPwdSnap = paramModel.getIntValue();
                        break;
                    case KEY_CALL_CENTER:
                        callCenterSnap = paramModel.getIntValue();
                        break;
                    case KEY_FACE_OPEN:
                        faceOpenSnap = paramModel.getIntValue();
                        break;
                    case KEY_FINGER_OPEN:
                        fingerOpenSnap = paramModel.getIntValue();
                        break;
                    case KEY_CARD_OPEN:
                        cardOpenSnap = paramModel.getIntValue();
                        break;
                    case KEY_PWD_OPEN:
                        pwdOpenSnap = paramModel.getIntValue();
                        break;
                    case KEY_QRCODE_OPEN:
                        qrcodeOpenSnap = paramModel.getIntValue();
                        break;
                    case KEY_FACE_STRANGER:
                        faceStrangerSnap = paramModel.getIntValue();
                        break;
                }
            }
        }
        // 返回拍照参数对象
        return new SnapParam()
                .setVisitorSnap(visitorSnap)
                .setErrorPwdSnap(errorPwdSnap)
                .setHijackPwdSnap(hijackPwdSnap)
                .setCallCenterSnap(callCenterSnap)
                .setFaceOpenSnap(faceOpenSnap)
                .setFingerOpenSnap(fingerOpenSnap)
                .setCardOpenSnap(cardOpenSnap)
                .setPwdOpenSnap(pwdOpenSnap)
                .setQrcodeOpenSnap(qrcodeOpenSnap)
                .setFaceStrangerSnap(faceStrangerSnap);
    }

    /**
     * 保存拍照参数
     */
    public static void setSnapParam(SnapParam snapParam) {
        ParamDao.saveParamArray(
                new ParamModel().setType(SNAP_PARAM).setKey(KEY_VISITOR).setValue(snapParam.getVisitorSnap()),
                new ParamModel().setType(SNAP_PARAM).setKey(KEY_ERROR_PWD).setValue(snapParam.getErrorPwdSnap()),
                new ParamModel().setType(SNAP_PARAM).setKey(KEY_HIJACK_PWD).setValue(snapParam.getHijackPwdSnap()),
                new ParamModel().setType(SNAP_PARAM).setKey(KEY_CALL_CENTER).setValue(snapParam.getCallCenterSnap()),
                new ParamModel().setType(SNAP_PARAM).setKey(KEY_FACE_OPEN).setValue(snapParam.getFaceOpenSnap()),
                new ParamModel().setType(SNAP_PARAM).setKey(KEY_FINGER_OPEN).setValue(snapParam.getFingerOpenSnap()),
                new ParamModel().setType(SNAP_PARAM).setKey(KEY_CARD_OPEN).setValue(snapParam.getCardOpenSnap()),
                new ParamModel().setType(SNAP_PARAM).setKey(KEY_PWD_OPEN).setValue(snapParam.getPwdOpenSnap()),
                new ParamModel().setType(SNAP_PARAM).setKey(KEY_QRCODE_OPEN).setValue(snapParam.getQrcodeOpenSnap()),
                new ParamModel().setType(SNAP_PARAM).setKey(KEY_FACE_STRANGER).setValue(snapParam.getFaceStrangerSnap())
        );
    }
}
