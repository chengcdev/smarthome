package com.mili.smarthome.tkj.app;

import android.os.Environment;

import static com.mili.smarthome.tkj.BuildConfig.FLAVOR_MODEL;

public class Const {

    public static final class SystemBootUpTouchEvent {
        /**
         * 开机2分钟内没收到系统触摸事件重启设备,解决系统上电起来后逻辑功能都正常UI显示不出来问题
         */
        public static boolean SYSTEM_BOOT_UP_HAVE_TOUCH_ENEVT = false;
    }

    public static final class Config {
        /**
         * 人脸注册超时时间
         */
        public static final int FACE_ENROLL_TIMEOUT = 10 * 1000;
        /**
         * 人脸识别超时时间
         */
        public static final int FACE_RECOGNIZE_TIMEOUT = 60 * 1000;
    }

    /**
     * 程序目录
     */
    public static final class Directory {
        /**
         * SD卡路径
         */
        public static final String SD = Environment.getExternalStorageDirectory().getPath();
        /**
         * 应用程序根目录
         */
        public static final String ROOT = SD + "/smarthome/tkj/" + FLAVOR_MODEL;
        /**
         * 应用程序日志保存路径
         */
        public static final String LOG = ROOT + "/logs";
        /**
         * 应用程序临时文件保存路径
         */
        public static final String TEMP = ROOT + "/temp";
        /**
         * 印度wffr授权文件保存路径
         */
        public static final String WFFR = ROOT + "/wffr";
        /**
         * 旷视megvii授权文件保存路径
         */
        public static final String MEGVII = ROOT + "/megvii";
        /**
         * 地平线文件路径
         */
        public static final String HORIZON = ROOT + "/horizon";
    }

    /**
     * 人脸算法类型
     */
    public static final class FaceType {
        /**
         * 无人脸算法
         */
        public static final int NONE = 0;
        /**
         * EI2.1（离线）
         */
        public static final int EI_2_1 = 1;
        /**
         * EI3.1（离线）
         */
        public static final int EI_3_1 = 2;
        /**
         * Face++离线
         */
        public static final int FACEPASS_OFFLINE = 3;
        /**
         * Face++在线
         */
        public static final int FACEPASS_ONLINE = 4;
    }

    public static final class Action {
        /**
         *
         */
        public static final String RESET = "mili.intent.action.RESET";
        /**
         *
         */
        public static final String MAIN = "mili.intent.action.MAIN";
        /**
         *
         */
        public static final String MAIN_DEFAULT = "mili.intent.action.MAIN.default";
        /**
         *
         */
        public static final String MAIN_FACE_PROMPT = "mili.intent.action.MAIN.face_prompt";
        /**
         *
         */
        public static final String SCREEN_SAVER = "mili.intent.action.SCREEN_SAVER";
        /**
         *
         */
        public static final String SETTING = "mili.intent.action.SETTING";
        /**
         *
         */
        public static final String MAIN_FACE = "mili.intent.action.MAIN.face";
    }

    public static class SetDeviceNoId {
        //设备编号
        public static final String DEVICE_NO_ID = "device_no_id";
        //住户设置
        public static final String RESIDENT_SETTING_ID = "resident_setting_id";
    }

    public static class SetNetWorkId {
        /**
         * IP
         */
        public static final String NET_IP = "192.168.1.10";
        /**
         * 子网掩码
         */
        public static final String NET_MASK = "255.255.255.0";
        /**
         * 网关
         */
        public static final String NET_GATEWAY = "192.168.1.1";
        /**
         * 管理员
         */
        public static final String NET_ADMIN = "0.0.0.0";
        /**
         * 中心
         */
        public static final String NET_CENTER = "0.0.0.0";
        /**
         * 流媒体
         */
        public static final String NET_MEDIA = "0.0.0.0";
        /**
         * 人脸识别服务器
         */
        public static final String NET_FACE = "0.0.0.0";
        /**
         * 电梯
         */
        public static final String NET_ELEVATOR = "0.0.0.0";
        /**
         * Dns1
         */
        public static final String NET_DNS_1 = "10.110.114.77";
        /**
         * Dns2
         */
        public static final String NET_DNS_2 = "10.110.114.77";
    }


    public static class KeyBoardId {
        public static final String KEY_LOCK = "key_lock";
        public static final String KEY_CALL = "key_call";
        public static final String KEY_CANCEL = "key_cancel";
        public static final String KEY_CONFIRM = "key_confirm";
    }

    public static class CallAction {
        /**
         * 呼叫住户
         */
        public static final String CALL_FROM_RESIDENT = "call_resident";
        /**
         * 呼叫中心
         */
        public static final String CALL_FROM_CENTER = "call_center";
        /**
         * 呼叫中心设备号
         */
        public static final int CENTER_DEVNO = 0xFF;
        /**
         * 是否在呼叫住户界面或者呼叫中心界面,并且是否正在呼叫
         */
        public static boolean IS_CALL_ACT = false;
        /**
         * 跳转fragment key
         */
        public static String KEY_PARAM = "KEY_PARAM";
    }


    public static class CardAction {
        /**
         * 开门
         */
        public static final String OPNE_DOOR = "intent_opne_door";
        /**
         * 无效卡
         */
        public static final String INVALID_CARD = "intent_invalid_card";
    }

    public static class AlarmAction {
        /**
         * 请关好门
         */
        public static final String ALARM_OPEN_DOOR = "alarm_open_door";
    }

    public static class ActionId {
        /**
         * 电源键
         */
        public static final String KEY_DOWN_UPDATETOUCH = "interceptPowerKeyDownUpdateTouch";
        /**
         * 系统OTA升级
         */
        public static final String SYSTEM_OTA_UPDATE_ACTION = "android.intent.action.SYSTEM_OTA_UPDATE";
        /**
         * 主动获取最新OTA升级
         */
        public static final String CHECK_OTA_UPDATE_VERSION_ACTION = "android.intent.action.CHECK_OTA_UPDATE_VERSION";
        /**
         * 多媒体播放状态
         */
        public static final String ACTION_MULTI_MEDIA = "action_multi_media";
        /**
         * 退出屏保
         */
        public static final String SCREEN_SAVER_EXIT = "screen_saver_exit";
    }

    /**
     * 编译配置
     */
    public static class CommonConfig {
        //是否授权
        public static boolean mIsAppAuth = false;
    }

    /**
     * 授权配置
     */
    public static class AuthConfig {
        /**
         * appKey
         */
        public static String AUTH_APP_KEY = "t9RD8tMW7a9GK4we";
        /**
         * appSecret
         */
        public static String AUTH_APP_SECRET = "VMHqv3lBJw4EHLYM";
        /**
         * Rsa
         */
        public static String AUTH_RSA = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCV+msaIBZ3YwpBzZ8YaXRZokp8FHDZuE7/Jexpr6" +
                "1iY+AIGDsSYtqcSbtDvL7dD6X1d2dHmb+k03IuSHyAcX6ePj2AKBISYUHAs50e/d3BqZzBfDbfrgxKJSiR+vuv/8JGhQZ1D" +
                "9wWUnbaqhTKLR0oqhow4xyXJRdCE/ZdVAmx1QIDAQAB";
        /**
         * 协议版本号
         */
        public static String AGREEMENT_VER = "1.0";
    }

}
