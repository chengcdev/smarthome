package com.mili.smarthome.tkj.entities.param;

/**
 * 拍照参数
 */
public class SnapParam {

    private int visitorSnap;
    private int errorPwdSnap;
    private int hijackPwdSnap;
    private int callCenterSnap;
    private int faceOpenSnap;
    private int fingerOpenSnap;
    private int cardOpenSnap;
    private int pwdOpenSnap;
    private int qrcodeOpenSnap;
    private int faceStrangerSnap;

    /**
     * 访客拍照
     * @return 0不启用，1启用
     */
    public int getVisitorSnap() {
        return visitorSnap;
    }

    /**
     * 错误密码开门拍照
     * @return 0不启用，1启用
     */
    public int getErrorPwdSnap() {
        return errorPwdSnap;
    }

    /**
     * 挟持密码开门拍照
     * @return 0不启用，1启用
     */
    public int getHijackPwdSnap() {
        return hijackPwdSnap;
    }

    /**
     * 呼叫中心拍照
     * @return 0不启用，1启用
     */
    public int getCallCenterSnap() {
        return callCenterSnap;
    }

    /**
     * 人脸开门拍照
     * @return 0不启用，1启用
     */
    public int getFaceOpenSnap() {
        return faceOpenSnap;
    }

    /**
     * 指纹开门拍照
     * @return 0不启用，1启用
     */
    public int getFingerOpenSnap() {
        return fingerOpenSnap;
    }

    /**
     * 刷卡开门拍照
     * @return 0不启用，1启用
     */
    public int getCardOpenSnap() {
        return cardOpenSnap;
    }

    /**
     * 密码开门拍照
     * @return 0不启用，1启用
     */
    public int getPwdOpenSnap() {
        return pwdOpenSnap;
    }

    /**
     * 扫码开门拍照
     * @return 0不启用，1启用
     */
    public int getQrcodeOpenSnap() {
        return qrcodeOpenSnap;
    }

    /**
     * 陌生人脸拍照
     * @return 0不启用，1启用
     */
    public int getFaceStrangerSnap() {
        return faceStrangerSnap;
    }


    /**
     * 访客拍照
     * @param value 0不启用，1启用
     */
    public SnapParam setVisitorSnap(int value) {
        this.visitorSnap = value;
        return this;
    }

    /**
     * 错误密码开门拍照
     * @param value 0不启用，1启用
     */
    public SnapParam setErrorPwdSnap(int value) {
        this.errorPwdSnap = value;
        return this;
    }

    /**
     * 挟持密码开门拍照
     * @param value 0不启用，1启用
     */
    public SnapParam setHijackPwdSnap(int value) {
        this.hijackPwdSnap = value;
        return this;
    }

    /**
     * 呼叫中心拍照
     * @param value 0不启用，1启用
     */
    public SnapParam setCallCenterSnap(int value) {
        this.callCenterSnap = value;
        return this;
    }

    /**
     * 人脸开门拍照
     * @param value 0不启用，1启用
     */
    public SnapParam setFaceOpenSnap(int value) {
        this.faceOpenSnap = value;
        return this;
    }

    /**
     * 指纹开门拍照
     * @param value 0不启用，1启用
     */
    public SnapParam setFingerOpenSnap(int value) {
        this.fingerOpenSnap = value;
        return this;
    }

    /**
     * 刷卡开门拍照
     * @param value 0不启用，1启用
     */
    public SnapParam setCardOpenSnap(int value) {
        this.cardOpenSnap = value;
        return this;
    }

    /**
     * 密码开门拍照
     * @param value 0不启用，1启用
     */
    public SnapParam setPwdOpenSnap(int value) {
        this.pwdOpenSnap = value;
        return this;
    }

    /**
     * 扫码开门拍照
     * @param value 0不启用，1启用
     */
    public SnapParam setQrcodeOpenSnap(int value) {
        this.qrcodeOpenSnap = value;
        return this;
    }

    /**
     * 陌生人人脸拍照
     * @param value 0不启用，1启用
     */
    public SnapParam setFaceStrangerSnap(int value) {
        this.faceStrangerSnap = value;
        return this;
    }
}
