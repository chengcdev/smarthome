package com.mili.smarthome.tkj.setting.entities;

import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.utils.ResUtils;

import java.util.List;

public class SettingFunc implements Comparable<SettingFunc>{

    /** 添加卡 */
    public static final String CARD_ADD = "0101";
    /** 删除卡 */
    public static final String CARD_DEL = "0102";
    /** 清空卡 */
    public static final String CARD_CLEAR = "0103";
    /** 密码管理 */
    public static final String PWD_MANAGE = "02";
    /** 添加密码 */
    public static final String PASSWORD_ADD = "0201";
    /** 删除密码 */
    public static final String PASSWORD_DEL = "0202";
    /** 清空密码 */
    public static final String PASSWORD_CLEAR = "0203";
    /** 修改管理密码 */
    public static final String ADMIN_PWD_CHANGE = "0204";
    /** 门禁设置 */
    public static final String SET_DOOR_GUARD = "03";
    /** 锁属性设置 */
    public static final String SET_LOCK_ATTR = "0301";
    /** 门状态设置 */
    public static final String SET_DOOR_STATUS = "0302";
    /** 人脸识别 */
    public static final String SET_FACE_RECOGNITION = "0303";
    /** 清空人脸识别 */
    public static final String SET_FACE_CLEAR= "030301";
    /** 二维码开门 */
    public static final String SET_QR_OPEN_TYPE ="0304";
    /** 扫码开门 */
    public static final String SET_OPEN_BY_SCAN = "030401";
    /** 蓝牙开门器 */
    public static final String SET_OPEN_BY_BLUETOOTH = "030402";
    /** 指纹识别 */
    public static final String SET_FINGERPRINT = "0305";
    /** 清空人指纹识别 */
    public static final String SET_FINGER_PRINT_CLEAR= "030501";
    /**添加指纹*/
    public static final String SET_FINGER_PRINT_ADD= "030502";
    /**删除指纹*/
    public static final String SET_FINGER_PRINT_DELETE= "030503";
    /** 人体感应 */
    public static final String SET_BODY_DETECTION = "0306";
    /** 梯口号设置 */
    public static final String SET_DEVNO = "0401";
    /** 区口号设置 */
    public static final String SET_AREA_NO = "0400";
    /** 网络设置 */
    public static final String SET_NETWORK = "0402";
    /** 编码规则设置 */
    public static final String SET_NO_RULE = "0403";
    /** 住户设置 */
    public static final String SET_ROOM = "0404";
    /** 时间设置 */
    public static final String SET_TIME = "0405";
    /** 通话音量 */
    public static final String SET_CALL_VOLUME = "040601";
    /** 提示音 */
    public static final String SET_PROMPT_TONE = "040602";
    /** 按键音 */
    public static final String SET_KEY_TONE = "040603";
    /** 媒体静音 */
    public static final String SET_MEDIA_MUTE = "040604";
    /** 存储卡管理 */
    public static final String SET_MEMORY_MANAGE = "0407";
    /** 本地容量 */
    public static final String SET_MEMORY_CAPACITY = "040701";
    /** 格式化存储卡 */
    public static final String SET_MEMORY_FORMAT = "040702";
    /** 媒体信息 */
    public static final String SET_MEMORY_MEDIA = "040703";
    /** 外置sd卡容量 */
    public static final String SET_EXTERNAL_MEMORY = "040704";
    /** 恢复出厂 */
    public static final String SET_FACTORY = "0408";
    /** 高级设置 */
    public static final String SET_SENIOR = "05";
    /** 密码进门模式 */
    public static final String SET_OPEN_PWD_MODE = "0501";
    /** 报警参数 */
    public static final String SET_ALARM_PARAM = "0502";
    /** 拍照参数 */
    public static final String SET_PHOTO_PARAM = "0503";
    /** 启用访客拍照 */
    public static final String SET_PHOTO_VISITOR = "050301";
    /** 错误密码开门拍照 */
    public static final String SET_PHOTO_ERR_PWD = "050302";
    /** 挟持密码开门拍照 */
    public static final String SET_PHOTO_HOLD_PWD = "050303";
    /** 呼叫中心拍照 */
    public static final String SET_PHOTO_CALL_CENTER = "050304";
    /** 人脸开门拍照 */
    public static final String SET_PHOTO_FACE_OPEN = "050305";
    /** 指纹开门拍照 */
    public static final String SET_PHOTO_FINGER_OPEN = "050306";
    /** 刷卡开门拍照 */
    public static final String SET_PHOTO_CARD_OPEN = "050307";
    /** 密码开门拍照 */
    public static final String SET_PHOTO_PWD_OPEN = "050308";
    /** 扫码开门拍照 */
    public static final String SET_QR_CODE_OPEN = "050309";
    /** 陌生人脸拍照 */
    public static final String SET_PHOTO_FACE_STRANGER = "050310";

    /** 省电模式 */
    public static final String SET_POWER_SAVING = "0504";
    /** 屏保设置 */
    public static final String SET_SCREEN_SAVER = "0505";
    /** 动态密码 */
    public static final String SET_PWD_DYNAMIC = "0506";
    /** 事件上报平台 */
    public static final String SET_EVENT_PLATFORM = "0507";
//    /** 灵敏度设置 */
//    public static final String SET_SENSITIVITY = "0506";
//    /** 界面风格 */
//    public static final String SET_THEME = "0507";
    /** 设备信息 */
    public static final String SET_DEV_INFO = "06";
    /** 设置语言 */
    public static final String SET_LANGUAGE = "07";
    /** 设置卡号位数 */
    public static final String SET_CARD_NUM = "08";
    /** 设置设备属性 */
    public static final String SET_DEVICE_TYPE = "09";
    private String code;
    private String name;
    private List<SettingFunc> child;

    public String getCode() {
        return code;
    }

    public String getName() {
        String resName = "setting_" + code;
        int resId = ResUtils.getStringId(App.getInstance(), resName);
        if (resId == 0)
            return name;
        else
            return App.getInstance().getString(resId);
    }

    public boolean hasChild() {
        return child != null && child.size() > 0;
    }

    public List<SettingFunc> getChild() {
        return child;
    }

    public SettingFunc(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(SettingFunc settingFunc) {
        return Integer.valueOf(this.getCode()) - Integer.valueOf(settingFunc.getCode());
    }
}