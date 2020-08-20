package com.mili.smarthome.tkj.dao.param;

import com.mili.smarthome.tkj.entities.param.ParamModel;
import com.mili.smarthome.tkj.utils.LogUtils;

/**
 * 门禁参数访问类
 */
public class EntranceGuardDao {

    public static final String ENTRANCE_GUARD = "entrance_guard";

    public static final String KEY_OPEN_LOCK_TYPE = "open_lock_type";
    public static final String KEY_OPEN_LOCK_TIME = "open_lock_time";
    public static final String KEY_DOOR_STATE_CHECK = "door_state_check";
    public static final String KEY_DOOR_ALARM_OUT = "door_alarm_out";
    public static final String KEY_DOOR_REPORT_CENTER = "door_alarm_report";
    public static final String KEY_QRCODE_ENABLE = "qrcode_enable";
    public static final String KEY_QRCODE_OPEN_TYPE = "qrcode_open_type";
    public static final String KEY_BLUETOOTH_DEVID = "bluetooth_devid";
    public static final String KEY_FINGERPRINT_ENABLE = "fingerprint_enable";
    public static final String KEY_BODY_DETECTION = "body_detection";
    public static final String KEY_CLOUD_TALK = "cloud_talk";

    /**
     * 获取开锁类型
     * @return 0常闭，1常开
     */
    public static int getOpenLockType() {
        return ParamDao.queryParamValue(KEY_OPEN_LOCK_TYPE, 1);
    }

    /**
     * 设置开锁类型
     * @param value 0常闭，1常开
     */
    public static void setOpenLockType(int value) {
        ParamDao.saveParam(ENTRANCE_GUARD, KEY_OPEN_LOCK_TYPE, value);
    }

    /**
     * 获取开锁时间
     * @return 0s 3s 6s 9s
     */
    public static int getOpenLockTime() {
        return ParamDao.queryParamValue(KEY_OPEN_LOCK_TIME, 3);
    }

    /**
     * 设置开锁时间
     * @param value 0s 3s 6s 9s
     */
    public static void setOpenLockTime(int value) {
        ParamDao.saveParam(ENTRANCE_GUARD, KEY_OPEN_LOCK_TIME, value);
    }

    /**
     * 设置锁属性
     * @param openLockType 开锁类型(0常闭，1常开)
     * @param openLockTime 开锁时间(0s, 3s, 6s, 9s)
     */
    public static void setOpenLockAttr(int openLockType, int openLockTime) {
        ParamDao.saveParamArray(
                new ParamModel().setType(ENTRANCE_GUARD).setKey(KEY_OPEN_LOCK_TYPE).setValue(openLockType),
                new ParamModel().setType(ENTRANCE_GUARD).setKey(KEY_OPEN_LOCK_TIME).setValue(openLockTime)
        );
    }

    /**
     * 获取门状态检测
     * @return 0否，1是
     */
    public static int getDoorStateCheck() {
        return ParamDao.queryParamValue(KEY_DOOR_STATE_CHECK, 0);
    }

    /**
     * 设置门状态检测
     * @param value 0否，1是
     */
    public static void setDoorStateCheck(int value) {
        ParamDao.saveParam(ENTRANCE_GUARD, KEY_DOOR_STATE_CHECK, value);
    }

    /**
     * 获取报警输出
     * @return 0否，1是
     */
    public static int getAlarmOut() {
        return ParamDao.queryParamValue(KEY_DOOR_ALARM_OUT, 0);
    }

    /**
     * 设置报警输出
     * @param value 0否，1是
     */
    public static void setAlarmOut(int value) {
        ParamDao.saveParam(ENTRANCE_GUARD, KEY_DOOR_ALARM_OUT, value);
    }

    /**
     * 获取上报中心
     * @return 0否，1是
     */
    public static int getUpdateCenter() {
        return ParamDao.queryParamValue(KEY_DOOR_REPORT_CENTER, 0);
    }

    /**
     * 设置上报中心
     * @param value 0否，1是
     */
    public static void setUpdateCenter(int value) {
        ParamDao.saveParam(ENTRANCE_GUARD, KEY_DOOR_REPORT_CENTER, value);
    }

    /**
     * 门状态设置
     * @param doorStateCheck 门状态检测（0否，1是）
     * @param alarmOut 报警输出（0否，1是）
     * @param updateCenter 上报中心（0否，1是）
     */
    public static void setDoorState(int doorStateCheck, int alarmOut, int updateCenter) {
        ParamDao.saveParamArray(
                new ParamModel().setType(ENTRANCE_GUARD).setKey(KEY_DOOR_STATE_CHECK).setValue(doorStateCheck),
                new ParamModel().setType(ENTRANCE_GUARD).setKey(KEY_DOOR_ALARM_OUT).setValue(alarmOut),
                new ParamModel().setType(ENTRANCE_GUARD).setKey(KEY_DOOR_REPORT_CENTER).setValue(updateCenter)
        );
    }

    /**
     * 获取扫码开门
     * @return 0禁用，1启用
     */
    public static int getSweepCodeOpen() {
        return ParamDao.queryParamValue(KEY_QRCODE_ENABLE, 1);
    }

    /**
     * 设置扫码开门
     * @param value 0禁用，1启用
     */
    public static void setSweepCodeOpen(final int value) {
        ParamDao.saveParam(ENTRANCE_GUARD, KEY_QRCODE_ENABLE, value);
    }

    /**
     * 获取二维码开门方式
     * @return 0扫码开门，1蓝牙开门器开门
     */
    public static int getQrOpenDoorType(){
        return ParamDao.queryParamValue(KEY_QRCODE_OPEN_TYPE, 0);
    }


    /**
     * 设置二维码开门方式
     * @param value 0扫码开门，1蓝牙开门器开门
     */
    public static void setQrOpenDoorType(int value){
        ParamDao.saveParam(ENTRANCE_GUARD, KEY_QRCODE_OPEN_TYPE, value);
    }

    /**
     * 获取蓝牙开门器ID
     */
    public static String getBluetoothDevId(){
        return ParamDao.queryParamValue(KEY_BLUETOOTH_DEVID, "");
    }


    /**
     * 设置蓝牙开门器ID
     */
    public static void setBluetoothDevId(String value){
        ParamDao.saveParam(ENTRANCE_GUARD, KEY_BLUETOOTH_DEVID, value);
    }

    /**
     * 获取指纹识别
     * @return 0禁用，1启用
     */
    public static int getFingerprint() {
        return ParamDao.queryParamValue(KEY_FINGERPRINT_ENABLE, 0);
    }

    /**
     * 设置指纹识别
     * @param value 0禁用，1启用
     */
    public static void setFingerprint(int value) {
        ParamDao.saveParam(ENTRANCE_GUARD, KEY_FINGERPRINT_ENABLE, value);
    }

    /**
     * 获取人体感应
     * @return 0触发开屏，1人脸识别，2扫码开门，3蓝牙开门器
     */
    public static int getBodyFeeling() {
        return ParamDao.queryParamValue(KEY_BODY_DETECTION, 1);
    }

    /**
     * 设置人体感应
     * @param value 0触发开屏，1人脸识别，2扫码开门，3蓝牙开门器
     */
    public static void setBodyFeeling(int value) {
        ParamDao.saveParam(ENTRANCE_GUARD, KEY_BODY_DETECTION, value);
    }

    /**
     * 获取云对讲状态
     * @return 0禁用 1启用
     */
    public static int getCloudTalk() {
        int value = ParamDao.queryParamValue(KEY_CLOUD_TALK, 1);
        LogUtils.d(" == getCloudTalk: value = " + value);
        return value;
    }

    /**
     * 设置云对讲
     * @param value 0禁用 1启用
     */
    public static void setCloudTalk(int value) {
        LogUtils.d(" == setCloudTalk: value = " + value);
        ParamDao.saveParam(ENTRANCE_GUARD, KEY_CLOUD_TALK, value);
    }
}
