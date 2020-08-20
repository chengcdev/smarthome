package com.mili.smarthome.tkj.constant;

public class Constant {

    /**
     * 关屏时间 5分钟
     */
    public static final int SCREEN_CLOSE_TIME = 5*60000;

    /**
     * 屏保时间 2分钟
     */
    public static final int SCREEN_PRO_TIME = 2*60000;

    /**
     * 设置界面长时间不操作回主界面时间 30秒
     */
    public static final int SCREEN_BACKMAIN_SET_TIME = 30000;

    /**
     * 非设置界面长时间不操作回主界面时间 15秒
     */
    public static final int SCREEN_BACKMAIN_TIME = 15000;

    /**
     * 管理员密码长度
     */
    public static final int PASSWORD_ADMIN_LEN = 8;

    /**
     * 开门密码长度
     */
    public static final int PASSWORD_LEN = 6;

    /**
     * 高级密码长度
     */
    public static final int PASSWORD_SENIOR_LEN = 5;

    /**
     * 设置提示时间
     */
    public static final int SET_HINT_TIMEOUT = 2000;
    /**
     * 主界面提示时间
     */
    public static final int MAIN_HINT_TIMEOUT = 4000;

    public static final class HintAction {
        /**
         * 开门
         */
        public static final String OPNE_DOOR = "intent_opne_door";
        /**
         * 无效卡
         */
        public static final String INVALID_CARD = "intent_invalid_card";
        /**
         * 报警提示:请关好门
         */
        public static final String ALARM_HINT   = "intent_alarm";
    }

    public static final class CallAction{
        /**
         * 是否在呼叫住户界面 callresidentFragment
         */
        public static boolean IS_CALL_RESIDENT_FRAGMENT = false;
        /**
         * 是否在呼叫中心界面 callcenterFragment
         */
        public static boolean IS_CALL_CENTER_FRAGMENT = false;
        /**
         * 是否是监视对讲
         */
        public static boolean IS_CALL_MONITOR_TALK = false;
        /**
         * 住户监视
         */
        public static final String CALL_FROM_RESIDENT = "CALL_FROM_RESIDENT";
        /**
         * 中心监视
         */
        public static final String CALL_FROM_CENTER = "call_from_center";

    }
}
