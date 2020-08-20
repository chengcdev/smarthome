package com.mili.smarthome.tkj.constant;

public class Constant {

    /**
     * 广播action
     */
    public static final class Action {
        /**
         * 主机面按钮
         */
        public static final String MAIN_BOTTOM_BTN = "main_bottom_btn";
        /**
         * 刷新主界面
         */
        public static final String MAIN_REFRESH_ACTION = "main_refresh_action";
        /**
         * 人脸识别
         */
        public static final String BODY_FACE_RECOGNITION_ACTION = "body_face_recognition_action";
        /**
         * 人脸注册成功提示
         */
        public static final String BODY_FACE_ENROLL_ACTION = "body_face_enrollment_action";
        /**
         * 人脸删除
         */
        public static final String BODY_FACE_DEL_ACTION = "body_face_delete_action";
        /**
         * 扫码开门
         */
        public static final String BODY_SCAN_DOOR_ACTION = "body_scan_door_action";
        /**
         * 关闭屏保Action
         */
        public static final String CLOSE_SCREEN_PROTECT = "close_screen_protect";
        /**
         * 监视通话
         */
        public static final String CALL_MONITOR_TALK = "call_monitor_talk";
        /**
         * 刷新设置列表
         */
        public static final String SETTING_NOTIFYCHANGE = "setting_notifychange";
        /**
         * 跳转到屏保界面
         */
        public static final String ACTION_TO_SCREEN_PRO= "action_to_screen_pro";
        /**
         * 显示主界面底部功能按键
         */
        public static final String ACTION_SHOW_BOTTOM_BTN= "action_show_bottom_btn";
        /**
         * 隐藏主界面底部功能按键
         */
        public static final String ACTION_HIDE_BOTTOM_BTN= "action_hide_bottom_btn";
        /**
         * 提示界面
         */
        public static final String ACTION_HINT_DIALOG  = "action_hint_dialog";
    }

    /**
     * Intent传递
     */
    public static final class IntentId {
        /**
         * key
         */
        public static final String INTENT_KEY = "intent_key";
        /**
         * 密码错误
         */
        public static final String INTENT_PWD_ERROR = "intent_pwd_error";
        /**
         * 管理员密码错误无声音
         */
        public static final String INTENT_PWD_ERROR_NO_SOUND = "intent_pwd_error_no_sound";
        /**
         * 密码功能未使用
         */
        public static final String INTENT_PWD_NOT_USER = "intent_pwd_not_user";
        /**
         * 开门
         */
        public static final String INTENT_OPNE_DOOR = "intent_opne_door";
        /**
         * 无效卡
         */
        public static final String INTENT_INVALID_CARD = "intent_invalid_card";
        /**
         * 请关好门
         */
        public static final String INTENT_DOOR_NOT_CLOSE = "intent_door_not_close";
        /**
         * 无效指纹
         */
        public static final String INTENT_INVALID_FINGER = "intent_invalid_finger";
        /**
         * 请保持手指按下
         */
        public static final String INTENT_KEEP_PRESS = "intent_keep_press";
        /**
         * 正在对比指纹，请等候！
         */
        public static final String INTENT_VERIFYING_FINGER = "intent_verifying";
        /**
         * 请重按手指
         */
        public static final String INTENT_FINGER_PRESS_AGAIN = "intent_finger_press_again";
        /**
         * 报警提示请关好门
         */
        public static final String ALARM_CLOSE_DOOD = "alarm_close_dood";

        /**
         * 带房号做开门提醒用
         */
        public static final String INTENT_OPNE_DOOR_REMIND = "intent_open_door_remind";
    }

    /**
     * 屏幕
     */
    public static final class ScreenId {
        /**
         * key
         */
        public static final String SCREEN_KEY = "SCREEN_KEY";
        /**
         * 是否开启屏保
         */
        public static boolean  SCREEN_PROTECT = false;
        /**
         * 是否开启关屏
         */
        public static boolean SCREEN_CLOSE = false;
        /**
         * 屏幕无操作
         */
        public static boolean SCREEN_NO_TOUCH = false;
        /**
         * 是否进入了屏保界面
         */
        public static boolean IS_SCREEN_SAVE = false;
        /**
         * 人体感应 0触发开屏，1人脸识别，2扫码开门，3蓝牙开门器
         */
        public static int SCREEN_BODY_STATE = 0;
    }

    /**
     * 开门密码错误计数，每三次下发一次
     */
    public static int ERROR_COUNT;
    /**
     * 信息fragment退出，屏幕服务不重新计时
     */
    public static boolean IS_MSGDIALOG_EXIT;
    /**
     * 是否正在报警
     */
    public static boolean IS_ALARM;
    /**
     *开门状态带房号
     */
    public static String OPENDOOR_ROOMNO;
}
