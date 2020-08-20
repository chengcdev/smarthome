package com.mili.smarthome.tkj.set;

import com.android.CommTypeDef;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.main.entity.KeyBoardMoel;
import com.mili.smarthome.tkj.main.entity.SettingModel;

import java.util.ArrayList;
import java.util.List;

public class Constant {


    //上键
    public static final String KEY_UP = "key_up";
    //删除键
    public static final String KEY_DELETE = "key_delete";
    //下键
    public static final String KEY_NEXT = "key_next";
    //取消键
    public static final String KEY_CANCLE = "key_cancle";
    //锁键
    public static final String KEY_LOCK = "key_lock";
    //显示字母按键
    public static final String KEY_LIST = "key_list";
    //O键呼叫
    public static final String KEY_CALL = "key_call";
    //打勾键
    public static final String KEY_CONFIRM = "key_confirm";
    //字符键
    public static final String KEY_CHAR = "key_char";
    //字符键
    public static final String KEY_QR = "key_qr";
    //小写
    public static final String KEY_LOWER = "key_lower";
    //大写
    public static final String KEY_CAPITAL = "key_capital";
    //0 呼叫中心
    public static final int CALL_CENTER = 0;
    // 1呼叫住户
    public static final int CALL_HOUSE = 1;
    // 是否呼叫中心界面
    public static final int IS_CALL_CENTER = 10000;
    // 监视状态 中心呼叫
    public static final int MONITOR_CALL_CENTER = 2;
    // 监视状态 住户呼叫
    public static final int MONITOR_CALL_ZHUHU = 3;
    // 监视状态 直接开门
    public static final int MONITOR_OPEN_DOOR = 4;
    // 门未关
    public static final int MONITOR_NOT_CLOSE_DOOR = 5;
    // 无效卡
    public static final int MONITOR_INVALID_CARD = 6;
    //密码错误
    public static final int KEY_PWD_FAIL = 7;
    // 开门提醒
    public static final int OPEN_DOOR_REMIND = 8;
    // 呼叫状态
    public static final String KEY_CALL_TYPE = "key_call_type";
    //房号
    public static final int KEY_INPUT_DEVICE_NO = 0;
    //用户开门密码
    public static final int KEY_INPUT_OPEN_PWD = 1;
    //管理员密码
    public static final int KEY_INPUT_ADMIN_PWD = 2;
    //呼叫键
    public static final int VIEW_ID_KEY_CALL = 0;
    //键盘按钮
    public static final int VIEW_ID_KEY_BOARD = 1;
    //刷新呼叫键
    public static final String ACTION_KEY_CALL = "action_key_call";
    //刷新呼叫图标
    public static final String ACTION_KEY_CALL_ICON = "action_key_call_icon";
    //呼叫图标状态KEY
    public static final String KEY_CALL_ICON = "key_call_icon";

    public static final String KEY_PARAM = "key_param";
    //首次设置参数key
    public static final String SETTING_FIRST = "setting_first";
    //管理中心
    public static final String MANAGE_CENTER = "manage_center";
    //管理中心房号
    public static final String MANAGE_CENTER_ROOM_NO = "-1";
    //是否刷新设置列表 显示住户设置
    public static boolean IS_SET_REFRESH = false;
    //当前是否为设置界面
    public static boolean IS_SET_FRAGMENT = false;
    //当前是否进行人体感应
    public static boolean IS_BODY_FEELING = false;

    public static final String KEY_HINT = "key_hint";

    public static int ERROR_PWD_COUNT = 0; //简易密码错误计数
    public static String OPENDOOR_ROOMNO = null; //开门房号

    public static class ActionId {
        //初始化主界面
        public static final String ACTION_INIT_MAIN = "action_init_main";
        //更新主界面
        public static final String ACTION_REFRESH_MAIN = "action_refresh_main";
        //更新主界面二位码图标
        public static final String ACTION_MAIN_QR = "action_main_qr";
        //刷新主界面键盘
        public static final String ACTION_REFRESH_MAIN_KEYBOARD = "action_refresh_main_keyboard";
        //直按式设置住户设置可编辑主界面
        public static final String ACTION_DIRECT_EDIT_VIEW = "action_direct_edit_view";
        //关闭当前activity
        public static final String ACTION_ACTIVITY_CLOSE = "action_activity_close";
        //直按式初始化主界面列表
        public static final String ACTION_INIT_MAIN_DIRECT = "action_init_main_direct";
        /**
         * 人脸识别界面，跳转到显示输入开门密码界面
         */
        public static final String ACTION_FACE_TO_OPEN = "action_face_to_open";
        /**
         * MainFragment刷新
         */
        public static final String ACTION_MAIN_FRAGMENT_NOTIFY = "action_main_fragment_notify";
        /**
         * 关闭屏保Action
         */
        public static final String ACITON_CLOSE_SCREEN_PROTECT = "aciton_close_screen_protect";
        /**
         * 显示信息界面
         */
        public static final String ACTION_SHOW_MESSAGE = "action_show_message";
        /**
         * 区口的时候进入开门密码
         */
        public static final String ACTION_AREA_TO_OPEN_PWD= "action_area_to_open_pwd";
        /**
         * 区口的时候进入输入房号
         */
        public static final String ACTION_AREA_TO_ROOM_NO= "action_area_to_room_no";
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
         * 是否进入了屏保界面
         */
        public static boolean IS_SCREEN_SAVE = false;
        /**
         * 是否进入设置界面
         */
        public static boolean SCREEN_IS_SET = false;
    }


    public static final class KeyNumId {
        public static final int KEY_NUM_0 = 0;
        public static final int KEY_NUM_1 = 1;
        public static final int KEY_NUM_2 = 2;
        public static final int KEY_NUM_3 = 3;
        public static final int KEY_NUM_4 = 4;
        public static final int KEY_NUM_5 = 5;
        public static final int KEY_NUM_6 = 6;
        public static final int KEY_NUM_7 = 7;
        public static final int KEY_NUM_8 = 8;
        public static final int KEY_NUM_9 = 9;
        public static final int KEY_NUM_10 = 10;
        public static final int KEY_NUM_11 = 11;
        public static final int KEY_NUM_12 = 12;
        public static final int KEY_NUM_13 = 13;
        public static final int KEY_NUM_14 = 14;
    }


    public static class SettingMainId {
        //卡管理
        public static final String SETTING_CARD_MANAGE = "setting_card_manage";
        //密码管理
        public static final String SETTING_PWD_MANAGE = "setting_pwd_manage";
        //门禁设置
        public static final String SETTING_DOOR_BAN = "setting_door_ban";
        //住户设置
        public static final String SETTING_ZHUHU = "setting_zhuhu";
        //系统设置
        public static final String SETTING_SYSTERM = "setting_systerm";
        //设备信息
        public static final String SETTING_DEVICES = "setting_devices";
        //声音设置
        public static final String SETTING_SOUND = "setting_sound";
        //public
        public static final String SETTING_RESET = "setting_restart";
        //蓝牙开门器
        public static final String SETTING_DOOR_BLUE = "setting_door_blue";
        //梯口
        public static final String SETTING_DEVICES_TK = "setting_devices_tk";
        //区口
        public static final String SETTING_DEVICES_QK = "setting_devices_qk";
        //启动参数
        public static final String SETTING_ENABLE_PARAM = "setting_enable_param";
        //设备信息系统参数
        public static final String SETTING_SYSTEM_INFO_PARAM = "setting_system_info_param";
    }


    /**
     * <item >梯口号设置</item>0
     * <item >网络设置</item>1
     * <item >编号规则设置</item>2
     * <item >高级设置</item>3
     */
    public static class SettinSystermId {
        //梯口号设置
        public static final String SYSTERM_TKH = "systerm_tkh";
        //网络设置
        public static final String SYSTERM_NET = "systerm_net";
        //编号规则设置
        public static final String SYSTERM_DEVICES_RULE = "systerm_devices_rule";
        //高级设置
        public static final String SYSTERM_SENIOR = "systerm_senior";
    }

    /**
     * <item >密码进门模式</item>0
     * <item >报警参数</item>1
     * <item >拍照参数</item>2
     * <item >省电模式</item>3
     * <item >动态密码</item>4
     * <item >屏保设置</item>5
     * <item >灵敏度设置</item>6
     */
    public static class SettinSeniorId {
        //密码进门模式
        public static final String SENIOR_PWD_DOOR_MODEL = "senior_pwd_door_model";
        //报警参数
        public static final String SENIOR_ALARM_PARAM = "senior_alarm_param";
        //拍照参数
        public static final String SENIOR_CAMERA_PARAM = "senior_camera_param";
        //错误开门密码拍照
        public static final String SENIOR_CAMERA_ERROR = "senior_camera_error";
        //挟持开门密码拍照
        public static final String SENIOR_CAMERA_XC = "senior_camera_xc";
        //省电模式
        public static final String SENIOR_POWER_SAVE = "senior_power_save";
        //动态密码
        public static final String SENIOR_PWD_DYNAMIC = "senior_pwd_dynamic";
        //屏保设置
        public static final String SENIOR_SCREEN_SETING = "senior_screen_seting";
        //灵敏度设置
        public static final String SENIOR_SENSITIVITY_SETTING = "senior_sensitivity_setting";
        //呼叫方式
        public static final String SENIOR_CALLTYPE_SETTING = "senior_calltype_setting";
        //启用
        public static final String SENIOR_ENABLE = "senior_enable";
        //不启用
        public static final String SENIOR_ENABLE_NOT = "senior_enable_not";
        //关闭
        public static final String SENIOR_CLOSE = "senior_close";
        //高
        public static final String SENIOR_HIGE = "senior_hige";
        //中
        public static final String SENIOR_MID = "senior_mid";
        //低
        public static final String SENIOR_LOW = "senior_low";
        //简易模式
        public static final String SENIOR_MODEL_EASY = "senior_model_easy";
        //高级模式
        public static final String SENIOR_MODEL_SENIOR = "senior_model_senior";
        //编码式
        public static final String SENIOR_CALL_TYPE_BIANMA = "senior_call_type_bianma";
        //直按式
        public static final String SENIOR_CALL_TYPE_ZHIAN = "senior_call_type_zhian";
        //启用中心机
        public static final String SENIOR_ENABLE_CENTER = "senior_enable_center";
        //是
        public static final String SENIOR_ENABLE_CENTER_YES = "senior_enable_center_yes";
        //否
        public static final String SENIOR_ENABLE_CENTER_NO = "senior_enable_center_no";
    }

    public static class SetPhotoParamId {
        /**
         * 启用访客拍照
         */
        public static final String SET_PHOTO_VISITOR = "set_photo_visitor";
        /**
         * 错误密码开门拍照
         */
        public static final String SET_PHOTO_ERR_PWD = "set_photo_err_pwd";
        /**
         * 挟持密码开门拍照
         */
        public static final String SET_PHOTO_HOLD_PWD = "set_photo_hold_pwd";
        /**
         * 呼叫中心拍照
         */
        public static final String SET_PHOTO_CALL_CENTER = "set_photo_call_center";
        /**
         * 人脸开门拍照
         */
        public static final String SET_PHOTO_FACE_OPEN = "set_photo_face_open";
        /**
         * 指纹开门拍照
         */
        public static final String SET_PHOTO_FINGER_OPEN = "set_photo_finger_open";
        /**
         * 刷卡开门拍照
         */
        public static final String SET_PHOTO_CARD_OPEN = "set_photo_card_open";
        /**
         * 密码开门拍照
         */
        public static final String SET_PHOTO_PWD_OPEN = "set_photo_pwd_open";
        /**
         * 扫码开门拍照
         */
        public static final String SET_QR_CODE_OPEN = "set_qr_code_open";
    }


    public static class SetCardManageId {
        //添加卡
        public static final String CARD_ADD = "card_add";
        //删除卡
        public static final String CARD_DELETE = "card_delete";
        //清空卡
        public static final String CARD_CLEAR = "card_clear";
        //卡号位数
        public static final String CARD_NUMS = "card_nums";
        //8位
        public static final String CARD_NUMS_8 = "card_nums_8";
        //6位
        public static final String CARD_NUMS_6 = "card_nums_6";
    }

    public static class SetPwdManageId {
        //添加密码
        public static final String PWD_ADD = "pwd_add";
        //删除密码
        public static final String PWD_DELETE = "pwd_delete";
        //清空密码
        public static final String PWD_CLEAR = "pwd_clear";
        //修改管理密码
        public static final String PWD_UPDATE = "pwd_update";
    }


    public static class SetEntranceGuardId {
        //锁属性设置
        public static final String GUARD_LOCK_NATURE = "guard_lock_nature";
        //门属性设置
        public static final String GUARD_DOOR_NATURE = "guard_door_nature";
        //人脸识别
        public static final String GUARD_FACE = "guard_face";
        //扫码开门
        public static final String GUARD_SAOMA = "guard_saoma";
        //蓝牙开门器
        public static final String GUARD_BLUETOOTH_OPEN = "guard_bluetooth_open";
        //人体感应
        public static final String GUARD_BODY_FEEL = "guard_body_feel";
        //触发开屏
        public static final String GUARD_TRIGGER_SCREEN = "guard_trigger_screen";
        //启用
        public static final String GUARD_ENABLE = "guard_enable";
        //否
        public static final String GUARD_NO = "guard_no";
        //是
        public static final String GUARD_YES = "guard_yes";
        //禁用
        public static final String GUARD_BAN = "guard_ban";
        //常开
        public static final String GUARD_OPEN = "guard_open";
        //常闭
        public static final String GUARD_CLOSE = "guard_close";
        //清空人脸记录
        public static final String GUARD_FACE_CLEAR = "guard_face_clear";
        //安全级别
        public static final String GUARD_SAFE_LEVEL = "guard_safe_level";
        //人脸活体检测
        public static final String GUARD_FACE_LIVING = "guard_face_living";
        //人脸识别阈值
        public static final String GUARD_FACE_THRESHOLD = "guard_face_threshold";
        //高
        public static final String GUARD_LEVLE_HIGH = "guard_levle_high";
        //正常
        public static final String GUARD_LEVLE_NORMAL = "guard_levle_normal";
        //普通
        public static final String GUARD_LEVLE_ORDINARY = "guard_levle_ordinary";
        //开锁类型
        public static final String GUARD_OPEN_LOCK_TYPE = "guard_open_lock_type";
        //开锁时间
        public static final String GUARD_OPEN_LOCK_TIME = "guard_open_lock_time";
        //门检测状态
        public static final String GUARD_DOOR_STATE_CHECK = "guard_door_state_check";
        //报警输出
        public static final String GUARD_ALARM_OUT = "guard_alarm_out";
        //上报中心
        public static final String GUARD_UPDATE_CENTER = "guard_update_center";
    }

    public static class SetVolumeId {
        //通话音量
        public static final String VOLUME_CALL = "volume_call";
        //提示音
        public static final String VOLUME_TIP = "volume_tip";
        //按键语音
        public static final String VOLUME_KEY = "volume_key";
        //否
        public static final String VOLUME_NO = "volume_no";
        //是
        public static final String VOLUME_YES = "volume_yes";
    }

    public static class SetLanguageId {
        //中文简体
        public static final String LANGUAGE_ZH_SIMPLE = "language_zh_simple";
        //中文繁体
        public static final String LANGUAGE_ZH_FANTI = "language_zh_fanti";
        //english
        public static final String LANGUAGE_EG = "language_eg";
    }

    public static class SetCallTypeId {
        //编码式
        public static final String CALL_TYPE_BIANMA = "call_type_bianma";
        //直按式
        public static final String CALL_TYPE_ZHIAN = "call_type_zhian";
        //启用中心机
        public static final String CALL_TYPE_ENABLE_CENTER = "call_type_enable_center";
        //是
        public static final String CALL_TYPE_ENABLE_CENTER_YES = "call_type_enable_center_yes";
        //否
        public static final String CALL_TYPE_ENABLE_CENTER_NO = "call_type_enable_center_no";

    }

    public static class SetHintId {
        //连接中
        public static final int HINT_CONNECTING = 0xFF0;
        //呼叫
        public static final int HINT_CALLING = 0xFF1;
        //通话
        public static final int HINT_TALKING = 0xFF2;
        //挂机
        public static final int HINT_CALL_END = 0xFF3;
        //请留言
        public static final int HINT_RECORD_WAIT = 0xFF4;
        //留言中
        public static final int HINT_RECORDING = 0xFF5;
        //通话界面文字状态变化
        public static final int HINT_CALL_STATUS = 0xFF6;
        //回铃声
        public static final int HINT_CALL_RING = 0xFF7;
        //监视
        public static final int HINT_CALL_MONITOR = 0xFF8;
        //住户呼叫
        public static final String HINT_CALL_FROME_RESIDENT = "hint_call_frome_resident";
        //中心呼叫
        public static final String HINT_CALL_FROME_CENTER = "hint_call_frome_center";
    }


    public static class ClearId {
        //清理人脸
        public static final String CLEAR_FACE = "clear_face";
    }

    /**
     * 主界面数字键盘
     */
    public static List<KeyBoardMoel> getNumLists() {
        List<KeyBoardMoel> list = new ArrayList<>();
        list.clear();
        list.add(new KeyBoardMoel(KEY_UP, KEY_UP, R.drawable.key_last));
        list.add(new KeyBoardMoel(KEY_DELETE, KEY_DELETE, R.drawable.key_del));
        list.add(new KeyBoardMoel(KEY_NEXT, KEY_NEXT, R.drawable.key_next));
        list.add(new KeyBoardMoel("1", "1", R.drawable.key_1));
        list.add(new KeyBoardMoel("2", "2", R.drawable.key_2));
        list.add(new KeyBoardMoel("3", "3", R.drawable.key_3));
        list.add(new KeyBoardMoel("4", "4", R.drawable.key_4));
        list.add(new KeyBoardMoel("5", "5", R.drawable.key_5));
        list.add(new KeyBoardMoel("6", "6", R.drawable.key_6));
        list.add(new KeyBoardMoel("7", "7", R.drawable.key_7));
        list.add(new KeyBoardMoel("8", "8", R.drawable.key_8));
        list.add(new KeyBoardMoel("9", "9", R.drawable.key_9));
        list.add(new KeyBoardMoel(KEY_CANCLE, KEY_CANCLE, R.drawable.key_cancle));
        list.add(new KeyBoardMoel("0", "0", R.drawable.key_0));
        list.add(new KeyBoardMoel(KEY_LOCK, KEY_LOCK, R.drawable.key_lock));
        return list;
    }

    /**
     * 直按式 数字键盘
     */
    public static List<KeyBoardMoel> getDirecNumLists() {
        List<KeyBoardMoel> list = new ArrayList<>();
        list.clear();
        list.add(new KeyBoardMoel("1", "1", R.drawable.key_1));
        list.add(new KeyBoardMoel("2", "2", R.drawable.key_2));
        list.add(new KeyBoardMoel("3", "3", R.drawable.key_3));
        list.add(new KeyBoardMoel("4", "4", R.drawable.key_4));
        list.add(new KeyBoardMoel("5", "5", R.drawable.key_5));
        list.add(new KeyBoardMoel("6", "6", R.drawable.key_6));
        list.add(new KeyBoardMoel("7", "7", R.drawable.key_7));
        list.add(new KeyBoardMoel("8", "8", R.drawable.key_8));
        list.add(new KeyBoardMoel("9", "9", R.drawable.key_9));
        list.add(new KeyBoardMoel(KEY_CANCLE, KEY_CANCLE, R.drawable.key_cancle));
        list.add(new KeyBoardMoel("0", "0", R.drawable.key_0));
        list.add(new KeyBoardMoel(KEY_LOCK, KEY_LOCK, R.drawable.key_lock));
        list.add(new KeyBoardMoel(KEY_UP, KEY_UP, R.drawable.key_last));
        list.add(new KeyBoardMoel(KEY_NEXT, KEY_NEXT, R.drawable.key_next));
        list.add(new KeyBoardMoel(KEY_LIST, KEY_LIST, R.drawable.key_list));
        return list;
    }

    /**
     * 直按式带字母数字键盘
     * 编辑状态
     */
    public static List<KeyBoardMoel> getDirecLetterLowerLists() {
        List<KeyBoardMoel> list = new ArrayList<>();
        list.clear();
        list.add(new KeyBoardMoel(KEY_LOWER, KEY_LOWER, R.drawable.key_lower));
        list.add(new KeyBoardMoel(KEY_CHAR, KEY_CHAR, R.drawable.key_z));
        list.add(new KeyBoardMoel(KEY_DELETE, KEY_DELETE, R.drawable.key_del));
        list.add(new KeyBoardMoel("1", "1", R.drawable.key_1));
        list.add(new KeyBoardMoel("2", "2", R.drawable.letter1_2));
        list.add(new KeyBoardMoel("3", "3", R.drawable.letter1_3));
        list.add(new KeyBoardMoel("4", "4", R.drawable.letter1_4));
        list.add(new KeyBoardMoel("5", "5", R.drawable.letter1_5));
        list.add(new KeyBoardMoel("6", "6", R.drawable.letter1_6));
        list.add(new KeyBoardMoel("7", "7", R.drawable.letter1_7));
        list.add(new KeyBoardMoel("8", "8", R.drawable.letter1_8));
        list.add(new KeyBoardMoel("9", "9", R.drawable.letter1_9));
        list.add(new KeyBoardMoel(KEY_CANCLE, KEY_CANCLE, R.drawable.key_cancle));
        list.add(new KeyBoardMoel("0", "0", R.drawable.letter_0));
        list.add(new KeyBoardMoel(KEY_CONFIRM, KEY_CONFIRM, R.drawable.key_ok));
        return list;
    }

    /**
     * 直按式带字母数字键盘
     * 大写
     * isUpdateName 是否是修改姓名界面
     */
    public static List<KeyBoardMoel> getDirecLetterCapitalLists(boolean isUpdateName) {
        List<KeyBoardMoel> list = new ArrayList<>();
        list.clear();
        if (isUpdateName) {
            list.add(new KeyBoardMoel(KEY_CAPITAL, KEY_CAPITAL, R.drawable.key_capital));
            list.add(new KeyBoardMoel(KEY_CHAR, KEY_CHAR, R.drawable.key_z));
            list.add(new KeyBoardMoel(KEY_DELETE, KEY_DELETE, R.drawable.key_del));
        }
        list.add(new KeyBoardMoel("1", "1", R.drawable.key_1));
        list.add(new KeyBoardMoel("2", "2", R.drawable.letter_2));
        list.add(new KeyBoardMoel("3", "3", R.drawable.letter_3));
        list.add(new KeyBoardMoel("4", "4", R.drawable.letter_4));
        list.add(new KeyBoardMoel("5", "5", R.drawable.letter_5));
        list.add(new KeyBoardMoel("6", "6", R.drawable.letter_6));
        list.add(new KeyBoardMoel("7", "7", R.drawable.letter_7));
        list.add(new KeyBoardMoel("8", "8", R.drawable.letter_8));
        list.add(new KeyBoardMoel("9", "9", R.drawable.letter_9));
        list.add(new KeyBoardMoel(KEY_CANCLE, KEY_CANCLE, R.drawable.key_cancle));
        list.add(new KeyBoardMoel("0", "0", R.drawable.letter_0));
        if (isUpdateName) {
            list.add(new KeyBoardMoel(KEY_CONFIRM, KEY_CONFIRM, R.drawable.key_ok));
        } else {
            list.add(new KeyBoardMoel(KEY_LOCK, KEY_LOCK, R.drawable.key_lock));
        }

        return list;
    }


    /**
     * 设置主界面
     */
    public static List<SettingModel> getSettingList() {

        FullDeviceNo fullDeviceNo = new FullDeviceNo(App.getInstance());
        int roomNoLen = fullDeviceNo.getRoomNoLen();

        List<SettingModel> list = new ArrayList<>();
        String[] stringArray = App.getInstance().getResources().getStringArray(R.array.setting_list);
        list.clear();
        list.add(new SettingModel(SettingMainId.SETTING_CARD_MANAGE, stringArray[0]));
        list.add(new SettingModel(SettingMainId.SETTING_PWD_MANAGE, stringArray[1]));
        list.add(new SettingModel(SettingMainId.SETTING_DOOR_BAN, stringArray[2]));
        //显示住户设置
        if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR && roomNoLen == 4) {
            list.add(new SettingModel(SettingMainId.SETTING_ZHUHU, stringArray[3]));
        }
        list.add(new SettingModel(SettingMainId.SETTING_SYSTERM, stringArray[4]));
        list.add(new SettingModel(SettingMainId.SETTING_DEVICES, stringArray[5]));
        list.add(new SettingModel(SettingMainId.SETTING_SOUND, stringArray[6]));
        list.add(new SettingModel(SettingMainId.SETTING_RESET, stringArray[7]));
        list.add(new SettingModel(SettingMainId.SETTING_DOOR_BLUE, stringArray[8]));
        return list;
    }


    /**
     * 设置系统设置
     */
    public static List<SettingModel> getSetSysList() {
        List<SettingModel> list = new ArrayList<>();
        String[] stringArray = App.getInstance().getResources().getStringArray(R.array.setting_systerm_list);
        list.clear();
        list.add(new SettingModel(SettinSystermId.SYSTERM_TKH, stringArray[0]));
        list.add(new SettingModel(SettinSystermId.SYSTERM_NET, stringArray[1]));
        list.add(new SettingModel(SettinSystermId.SYSTERM_DEVICES_RULE, stringArray[2]));
        list.add(new SettingModel(SettinSystermId.SYSTERM_SENIOR, stringArray[3]));
        return list;
    }

    /**
     * 设置系统设置
     * 高级设置
     */
    public static List<SettingModel> getSetSeniorList() {
        FullDeviceNo fullDeviceNo = new FullDeviceNo(App.getInstance());
        List<SettingModel> list = new ArrayList<>();
        String[] stringArray = App.getInstance().getResources().getStringArray(R.array.setting_senior_list);
        list.clear();
        list.add(new SettingModel(SettinSeniorId.SENIOR_PWD_DOOR_MODEL, stringArray[0]));
        list.add(new SettingModel(SettinSeniorId.SENIOR_ALARM_PARAM, stringArray[1]));
        list.add(new SettingModel(SettinSeniorId.SENIOR_CAMERA_PARAM, stringArray[2]));
        list.add(new SettingModel(SettinSeniorId.SENIOR_POWER_SAVE, stringArray[3]));
        list.add(new SettingModel(SettinSeniorId.SENIOR_PWD_DYNAMIC, stringArray[4]));
        list.add(new SettingModel(SettinSeniorId.SENIOR_SCREEN_SETING, stringArray[5]));
        list.add(new SettingModel(SettinSeniorId.SENIOR_SENSITIVITY_SETTING, stringArray[6]));
        if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            //当前是梯口机可以显示呼叫方式
            list.add(new SettingModel(SettinSeniorId.SENIOR_CALLTYPE_SETTING, stringArray[7]));
        }
        return list;
    }


    /**
     * 获取拍照参数列表
     */
    public static List<SettingModel> getSetPhotoParamList() {
        List<SettingModel> list = new ArrayList<>();
        list.clear();
        list.add(new SettingModel(SetPhotoParamId.SET_PHOTO_VISITOR, App.getInstance().getString(R.string.setting_senior_camera_param)));
        list.add(new SettingModel(SetPhotoParamId.SET_PHOTO_ERR_PWD, App.getInstance().getString(R.string.setting_senior_camera_error)));
        list.add(new SettingModel(SetPhotoParamId.SET_PHOTO_HOLD_PWD, App.getInstance().getString(R.string.setting_senior_camera_xc)));
        list.add(new SettingModel(SetPhotoParamId.SET_PHOTO_CALL_CENTER, App.getInstance().getString(R.string.setting_call_center_photo)));
        if (AppConfig.getInstance().getFaceRecognition() == 1) {
            list.add(new SettingModel(SetPhotoParamId.SET_PHOTO_FACE_OPEN, App.getInstance().getString(R.string.setting_face_photo)));
        }
        if (AppConfig.getInstance().getFingerprint() == 1) {
            list.add(new SettingModel(SetPhotoParamId.SET_PHOTO_FINGER_OPEN, App.getInstance().getString(R.string.setting_finger_photo)));
        }
        list.add(new SettingModel(SetPhotoParamId.SET_PHOTO_CARD_OPEN, App.getInstance().getString(R.string.setting_card_photo)));
        list.add(new SettingModel(SetPhotoParamId.SET_PHOTO_PWD_OPEN, App.getInstance().getString(R.string.setting_pwd_photo)));
        if (AppConfig.getInstance().getQrScanEnabled() == 1) {
            list.add(new SettingModel(SetPhotoParamId.SET_QR_CODE_OPEN, App.getInstance().getString(R.string.setting_qr_photo)));
        }
        return list;
    }

    /**
     * 设置系统设置
     * 高级设置
     * 密码进门模式
     */
    public static List<SettingModel> getPwdModelList() {
        List<SettingModel> list = new ArrayList<>();
        String[] stringArray = App.getInstance().getResources().getStringArray(R.array.setting_senior_pwd_model);
        list.clear();
        list.add(new SettingModel(SettinSeniorId.SENIOR_MODEL_EASY, stringArray[0]));
        list.add(new SettingModel(SettinSeniorId.SENIOR_MODEL_SENIOR, stringArray[1]));
        return list;
    }

    /**
     * 设置系统设置
     * 高级设置
     * 报警参数
     */
    public static List<SettingModel> getAlarmParamList() {
        List<SettingModel> list = new ArrayList<>();
        String[] stringArray = App.getInstance().getResources().getStringArray(R.array.setting_senior_alarm);
        list.clear();
        list.add(new SettingModel(SettinSeniorId.SENIOR_ENABLE_NOT, stringArray[0]));
        list.add(new SettingModel(SettinSeniorId.SENIOR_ENABLE, stringArray[1]));
        return list;
    }

    /**
     * 设置系统设置
     * 高级设置
     * 是否启用
     */
    public static List<SettingModel> getEnableList() {
        List<SettingModel> list = new ArrayList<>();
        String[] stringArray = App.getInstance().getResources().getStringArray(R.array.setting_senior_alarm);
        list.clear();
        list.add(new SettingModel(SettinSeniorId.SENIOR_ENABLE_NOT, stringArray[0]));
        list.add(new SettingModel(SettinSeniorId.SENIOR_ENABLE, stringArray[1]));
        return list;
    }

    /**
     * 设置系统设置
     * 高级设置
     * 拍照参数
     */
    public static List<SettingModel> getCameraParamList() {
        List<SettingModel> list = new ArrayList<>();
        String[] stringArray = App.getInstance().getResources().getStringArray(R.array.setting_senior_camera);
        list.clear();
        list.add(new SettingModel(SettinSeniorId.SENIOR_ENABLE_NOT, stringArray[0]));
        list.add(new SettingModel(SettinSeniorId.SENIOR_ENABLE, stringArray[1]));
        return list;
    }

    /**
     * 设置系统设置
     * 高级设置
     * 错误开门拍照
     */
    public static List<SettingModel> getCameraErrorList() {
        List<SettingModel> list = new ArrayList<>();
        String[] stringArray = App.getInstance().getResources().getStringArray(R.array.setting_senior_camera);
        list.clear();
        list.add(new SettingModel(SettinSeniorId.SENIOR_ENABLE_NOT, stringArray[0]));
        list.add(new SettingModel(SettinSeniorId.SENIOR_ENABLE, stringArray[1]));
        return list;
    }

    /**
     * 设置系统设置
     * 高级设置
     * 挟持开门拍照
     */
    public static List<SettingModel> getCameraXcList() {
        List<SettingModel> list = new ArrayList<>();
        String[] stringArray = App.getInstance().getResources().getStringArray(R.array.setting_senior_camera);
        list.clear();
        list.add(new SettingModel(SettinSeniorId.SENIOR_ENABLE_NOT, stringArray[0]));
        list.add(new SettingModel(SettinSeniorId.SENIOR_ENABLE, stringArray[1]));
        return list;
    }

    /**
     * 设置系统设置
     * 高级设置
     * 省电模式
     */
    public static List<SettingModel> getPowerSaveList() {
        List<SettingModel> list = new ArrayList<>();
        String[] stringArray = App.getInstance().getResources().getStringArray(R.array.setting_senior_power_save);
        list.clear();
        list.add(new SettingModel(SettinSeniorId.SENIOR_CLOSE, stringArray[0]));
        list.add(new SettingModel(SettinSeniorId.SENIOR_ENABLE, stringArray[1]));
        return list;
    }

    /**
     * 设置系统设置
     * 高级设置
     * 动态密码
     */
    public static List<SettingModel> getPwdDynamicList() {
        List<SettingModel> list = new ArrayList<>();
        String[] stringArray = App.getInstance().getResources().getStringArray(R.array.setting_senior_pwd_dynamic);
        list.clear();
        list.add(new SettingModel(SettinSeniorId.SENIOR_CLOSE, stringArray[0]));
        list.add(new SettingModel(SettinSeniorId.SENIOR_ENABLE, stringArray[1]));
        return list;
    }

    /**
     * 设置系统设置
     * 高级设置
     * 屏幕设置
     */
    public static List<SettingModel> getScreenProList() {
        List<SettingModel> list = new ArrayList<>();
        String[] stringArray = App.getInstance().getResources().getStringArray(R.array.setting_senior_screen_pro);
        list.clear();
        list.add(new SettingModel(SettinSeniorId.SENIOR_CLOSE, stringArray[0]));
        list.add(new SettingModel(SettinSeniorId.SENIOR_ENABLE, stringArray[1]));
        return list;
    }

    /**
     * 设置系统设置
     * 高级设置
     * 灵敏度设置
     */
    public static List<SettingModel> getSensitivityList() {
        List<SettingModel> list = new ArrayList<>();
        String[] stringArray = App.getInstance().getResources().getStringArray(R.array.setting_senior_sensitivity);
        list.clear();
        list.add(new SettingModel(SettinSeniorId.SENIOR_HIGE, stringArray[0]));
        list.add(new SettingModel(SettinSeniorId.SENIOR_MID, stringArray[1]));
        list.add(new SettingModel(SettinSeniorId.SENIOR_LOW, stringArray[2]));
        return list;
    }

    /**
     * 卡管理
     */
    public static List<SettingModel> getCardManageList() {
        List<SettingModel> list = new ArrayList<>();
        String[] stringArray = App.getInstance().getResources().getStringArray(R.array.setting_card_manage);
        list.clear();
        list.add(new SettingModel(SetCardManageId.CARD_ADD, stringArray[0]));
        list.add(new SettingModel(SetCardManageId.CARD_DELETE, stringArray[1]));
        list.add(new SettingModel(SetCardManageId.CARD_CLEAR, stringArray[2]));
        return list;
    }

    /**
     * 卡管理
     */
    public static List<SettingModel> getPwdManageList() {
        List<SettingModel> list = new ArrayList<>();
        String[] stringArray = App.getInstance().getResources().getStringArray(R.array.setting_pwd_manage);
        list.clear();
        list.add(new SettingModel(SetPwdManageId.PWD_ADD, stringArray[0]));
        list.add(new SettingModel(SetPwdManageId.PWD_DELETE, stringArray[1]));
        list.add(new SettingModel(SetPwdManageId.PWD_CLEAR, stringArray[2]));
        list.add(new SettingModel(SetPwdManageId.PWD_UPDATE, stringArray[3]));
        return list;
    }

    /**
     * 门禁设置
     */
    public static List<SettingModel> getEntranceGuardList() {
        List<SettingModel> list = new ArrayList<>();
        String[] stringArray = App.getInstance().getResources().getStringArray(R.array.setting_entrance_guard);
        list.clear();
        list.add(new SettingModel(SetEntranceGuardId.GUARD_LOCK_NATURE, stringArray[0]));
        list.add(new SettingModel(SetEntranceGuardId.GUARD_DOOR_NATURE, stringArray[1]));
        list.add(new SettingModel(SetEntranceGuardId.GUARD_FACE, stringArray[2]));
        list.add(new SettingModel(SetEntranceGuardId.GUARD_SAOMA, stringArray[3]));
        if (getBodyFeelList().size() > 1) {
            list.add(new SettingModel(SetEntranceGuardId.GUARD_BODY_FEEL, stringArray[4]));
        }
        return list;
    }


    /**
     * 门状态检测
     */
    public static List<SettingModel> getDoorTestList() {
        List<SettingModel> list = new ArrayList<>();
        String[] stringArray = App.getInstance().getResources().getStringArray(R.array.setting_door_state);
        list.clear();
        list.add(new SettingModel(SetEntranceGuardId.GUARD_NO, stringArray[0]));
        list.add(new SettingModel(SetEntranceGuardId.GUARD_YES, stringArray[1]));
        return list;
    }

    /**
     * 锁属性
     */
    public static List<SettingModel> getLockList() {
        List<SettingModel> list = new ArrayList<>();
        String[] stringArray = App.getInstance().getResources().getStringArray(R.array.setting_open_lock);
        list.clear();
        list.add(new SettingModel(SetEntranceGuardId.GUARD_CLOSE, stringArray[0]));
        list.add(new SettingModel(SetEntranceGuardId.GUARD_OPEN, stringArray[1]));
        return list;
    }

    /**
     * 人脸识别 启用
     */
    public static List<SettingModel> getFaceList1() {
        List<SettingModel> list = new ArrayList<>();
        String[] stringArray = App.getInstance().getResources().getStringArray(R.array.setting_face);
        list.clear();
        list.add(new SettingModel(SetEntranceGuardId.GUARD_BAN, stringArray[0]));
        list.add(new SettingModel(SetEntranceGuardId.GUARD_ENABLE, stringArray[1]));
        list.add(new SettingModel(SetEntranceGuardId.GUARD_FACE_CLEAR, stringArray[2]));
        list.add(new SettingModel(SetEntranceGuardId.GUARD_SAFE_LEVEL, stringArray[3]));
        list.add(new SettingModel(SetEntranceGuardId.GUARD_FACE_LIVING, stringArray[4]));
//        list.add(new SettingModel(SetEntranceGuardId.GUARD_FACE_THRESHOLD, stringArray[5]));
        return list;
    }

    /**
     * 人脸识别 禁用
     */
    public static List<SettingModel> getFaceList2() {
        List<SettingModel> list = new ArrayList<>();
        String[] stringArray = App.getInstance().getResources().getStringArray(R.array.setting_face);
        list.clear();
        list.add(new SettingModel(SetEntranceGuardId.GUARD_BAN, stringArray[0]));
        list.add(new SettingModel(SetEntranceGuardId.GUARD_ENABLE, stringArray[1]));
//        list.add(new SettingModel(SetEntranceGuardId.GUARD_FACE_THRESHOLD, stringArray[5]));
        return list;
    }


    /**
     * 扫码开门
     */
    public static List<SettingModel> getSaomaList() {
        List<SettingModel> list = new ArrayList<>();
        String[] stringArray = App.getInstance().getResources().getStringArray(R.array.setting_door_saoma);
        list.clear();
        list.add(new SettingModel(SetEntranceGuardId.GUARD_BAN, stringArray[0]));
        list.add(new SettingModel(SetEntranceGuardId.GUARD_ENABLE, stringArray[1]));
        return list;
    }

    /**
     * 人体感应
     */
    public static List<SettingModel> getBodyFeelList() {
        List<SettingModel> list = new ArrayList<>();
        String[] stringArray = App.getInstance().getResources().getStringArray(R.array.setting_body_feel);
        list.clear();
        list.add(new SettingModel(SetEntranceGuardId.GUARD_TRIGGER_SCREEN, stringArray[0]));
        if (AppConfig.getInstance().getFaceRecognition() == 1) {
            list.add(new SettingModel(SetEntranceGuardId.GUARD_FACE, stringArray[1]));
        }
        if (AppConfig.getInstance().getQrScanEnabled() == 1) {
            list.add(new SettingModel(SetEntranceGuardId.GUARD_SAOMA, stringArray[2]));
        }
        if (AppConfig.getInstance().getBluetoothDevId() != null && !AppConfig.getInstance().getBluetoothDevId().equals("")) {
            list.add(new SettingModel(SetEntranceGuardId.GUARD_BLUETOOTH_OPEN, stringArray[3]));
        }
        return list;
    }

    /**
     * 安全等级
     */
    public static List<SettingModel> getSecuryLevelList() {
        List<SettingModel> list = new ArrayList<>();
        String[] stringArray = App.getInstance().getResources().getStringArray(R.array.setting_secury_lever);
        list.clear();
        list.add(new SettingModel(SetEntranceGuardId.GUARD_LEVLE_HIGH, stringArray[0]));
        list.add(new SettingModel(SetEntranceGuardId.GUARD_LEVLE_NORMAL, stringArray[1]));
        list.add(new SettingModel(SetEntranceGuardId.GUARD_LEVLE_ORDINARY, stringArray[2]));
        return list;
    }

    /**
     * 声音设置
     */
    public static List<SettingModel> getVolumeList() {
        List<SettingModel> list = new ArrayList<>();
        String[] stringArray = App.getInstance().getResources().getStringArray(R.array.setting_volume);
        list.clear();
        list.add(new SettingModel(SetVolumeId.VOLUME_CALL, stringArray[0]));
        list.add(new SettingModel(SetVolumeId.VOLUME_TIP, stringArray[1]));
        list.add(new SettingModel(SetVolumeId.VOLUME_KEY, stringArray[2]));
        return list;
    }

    /**
     * 是否启用提示音
     */
    public static List<SettingModel> getTipList() {
        List<SettingModel> list = new ArrayList<>();
        String[] stringArray = App.getInstance().getResources().getStringArray(R.array.setting_volume_tip);
        list.clear();
        list.add(new SettingModel(SetVolumeId.VOLUME_NO, stringArray[0]));
        list.add(new SettingModel(SetVolumeId.VOLUME_YES, stringArray[1]));
        return list;
    }

    /**
     * 选择语言
     */
    public static List<SettingModel> getLanguageList() {
        List<SettingModel> list = new ArrayList<>();
        String[] stringArray = App.getInstance().getResources().getStringArray(R.array.setting_choice_language);
        list.clear();
        list.add(new SettingModel(SetLanguageId.LANGUAGE_ZH_SIMPLE, stringArray[0]));
        list.add(new SettingModel(SetLanguageId.LANGUAGE_ZH_FANTI, stringArray[1]));
        list.add(new SettingModel(SetLanguageId.LANGUAGE_EG, stringArray[2]));
        return list;
    }

    /**
     * 选择卡号位数
     */
    public static List<SettingModel> getCardNumsList() {
        List<SettingModel> list = new ArrayList<>();
        String[] stringArray = App.getInstance().getResources().getStringArray(R.array.setting_choice_card_num);
        list.clear();
        list.add(new SettingModel(SetCardManageId.CARD_NUMS_6, stringArray[0]));
        list.add(new SettingModel(SetCardManageId.CARD_NUMS_8, stringArray[1]));
        return list;
    }

    /**
     * 设备属性
     */
    public static List<SettingModel> getDevicesNature() {
        List<SettingModel> list = new ArrayList<>();
        String[] stringArray = App.getInstance().getResources().getStringArray(R.array.setting_choice_device_nature);
        list.clear();
        list.add(new SettingModel(SettingMainId.SETTING_DEVICES_TK, stringArray[0]));
        list.add(new SettingModel(SettingMainId.SETTING_DEVICES_QK, stringArray[1]));
        return list;
    }


    /**
     * 呼叫方式
     */
    public static List<SettingModel> getCallTypeList() {
        List<SettingModel> list = new ArrayList<>();
        String[] stringArray = App.getInstance().getResources().getStringArray(R.array.setting_choice_call_type);
        list.clear();
        list.add(new SettingModel(SettinSeniorId.SENIOR_CALL_TYPE_BIANMA, stringArray[0]));
        list.add(new SettingModel(SettinSeniorId.SENIOR_CALL_TYPE_ZHIAN, stringArray[1]));
        return list;
    }

    /**
     * 启用中心机
     */
    public static List<SettingModel> getEnableCenterList() {
        List<SettingModel> list = new ArrayList<>();
        String[] stringArray = App.getInstance().getResources().getStringArray(R.array.setting_enable_center);
        list.clear();
        list.add(new SettingModel(SettinSeniorId.SENIOR_ENABLE_CENTER_YES, stringArray[0]));
        list.add(new SettingModel(SettinSeniorId.SENIOR_ENABLE_CENTER_NO, stringArray[1]));
        return list;
    }

    public static String[] letter1 = {"1"};
    public static String[] letter2 = {"a", "b", "c", "2"};
    public static String[] letter3 = {"d", "e", "f", "3"};
    public static String[] letter4 = {"g", "h", "i", "4"};
    public static String[] letter5 = {"j", "k", "l", "5"};
    public static String[] letter6 = {"m", "n", "o", "6"};
    public static String[] letter7 = {"p", "q", "r", "s", "7"};
    public static String[] letter8 = {"t", "u", "v", "8"};
    public static String[] letter9 = {"w", "x", "y", "z", "9"};
    public static String[] letter0 = {"0", " "};
    public static String[] letterZ = {".", "_", "'"};
}
