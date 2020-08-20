package com.mili.smarthome.tkj.setting.entities;

public interface SettingCode {

    /** 添加卡 */
    String CARD_ADD = "0101";
    /** 删除卡 */
    String CARD_DEL = "0102";
    /** 清空卡 */
    String CARD_CLEAR = "0103";

    /** 密码管理 */
    String SET_PASSWORD = "02";
    /** 添加密码 */
    String PASSWORD_ADD = "0201";
    /** 删除密码 */
    String PASSWORD_DEL = "0202";
    /** 清空密码 */
    String PASSWORD_CLEAR = "0203";
    /** 修改管理密码 */
    String ADMIN_PWD_CHANGE = "0204";

    /** 门禁设置 */
    String SET_ENTRANCE_GUARD = "03";
    /** 锁属性设置 */
    String SET_LOCK_ATTR = "0301";
    /** 开锁类型 */
    String SET_LOCK_TYPE = "030101";
    /** 开锁时间 */
    String SET_LOCK_TIME = "030102";
    /** 门状态设置 */
    String SET_DOOR_STATUS = "0302";
    /** 门状态检测 */
    String SET_DOOR_DETECTION = "030201";
    /** 报警输出 */
    String SET_DOOR_ALARM = "030202";
    /** 上报中心 */
    String SET_DOOR_UPLOAD = "030203";
    /** 人脸识别 */
    String SET_FACE_RECOGNITION = "0303";
    /** 清空人脸记录 */
    String SET_FACE_CLEAR = "030301";
    /** 人脸安全级别 */
    String SET_FACE_SECU_LEVEL = "030302";
    /** 人脸活体检测 */
    String SET_FACE_LIVENESS = "030303";
    /** IPCamera地址 */
    String SET_IPC_URL = "030304";
    /** 二维码开门 */
    String SET_QRCODE_OPEN = "0304";
    /** 扫码开门 */
    String SET_OPEN_BY_SCAN = "030401";
    /** 蓝牙开门器 */
    String SET_OPEN_BY_BLUETOOTH = "030402";
    /** 指纹识别 */
    String SET_FINGER = "0305";
    /** 指纹识别 - 添加 */
    String SET_FINGER_ADD = "030501";
    /** 指纹识别 - 删除 */
    String SET_FINGER_DEL = "030502";
    /** 指纹识别 - 清空 */
    String SET_FINGER_CLEAR = "030503";
    /** 人体感应 */
    String SET_BODY_DETECTION = "0306";
    /** APN设置 */
    String SET_APN = "0307";

    /** 梯口号设置 */
    String SET_STAIR_NO = "0401";
    /** 区口号设置 */
    String SET_AREA_NO = "0411";
    /** 网络设置 */
    String SET_NETWORK = "0402";
    /** 编码规则设置 */
    String SET_NO_RULE = "0403";
    /** 住户设置 */
    String SET_ROOM = "0404";
    /** 时间设置 */
    String SET_TIME = "0405";
    /** 通话音量 */
    String SET_CALL_VOLUME = "040601";
    /** 提示音 */
    String SET_PROMPT_TONE = "040602";
    /** 按键音 */
    String SET_KEY_TONE = "040603";
    /** 媒体静音 */
    String SET_MEDIA_MUTE = "040604";
    /** 存储卡管理 */
    String SET_MEMORY_MANAGE = "0407";
    /** 机身容量 */
    String SET_MEMORY_CAPACITY = "040701";
    /** 格式化存储卡 */
    String SET_MEMORY_FORMAT = "040702";
    /** 媒体信息 */
    String SET_MEMORY_MEDIA = "040703";
    /** 外置SD卡容量 */
    String SET_MEMORY_EXT_CAPACITY = "040704";
    /** 恢复出厂 */
    String SET_FACTORY = "0408";

    /** 高级设置 */
    String SET_ADVANCED = "05";
    /** 密码进门模式 */
    String SET_OPEN_PWD_MODE = "0501";
    /** 报警参数 */
    String SET_ALARM_PARAM = "0502";
    /** 强行开门报警 */
    String SET_FORCED_OPEN_ALARM = "050201";
    /** 拍照参数 */
    String SET_PHOTO_FUNC = "0503";
    /** 启用访客拍照 */
    String SET_PHOTO_VISITOR = "050301";
    /** 错误密码开门拍照 */
    String SET_PHOTO_ERR_PWD = "050302";
    /** 挟持密码开门拍照 */
    String SET_PHOTO_HOLD_PWD = "050303";
    /** 呼叫中心拍照 */
    String SET_PHOTO_CALL_CENTER = "050304";
    /** 人脸开门拍照 */
    String SET_PHOTO_FACE_OPEN = "050305";
    /** 指纹开门拍照 */
    String SET_PHOTO_FINGER_OPEN = "050306";
    /** 刷卡开门拍照 */
    String SET_PHOTO_CARD_OPEN = "050307";
    /** 密码开门拍照 */
    String SET_PHOTO_PWD_OPEN = "050308";
    /** 扫码开门抓拍 */
    String SET_PHOTO_QRCODE_OPEN = "050309";
    /** 陌生人人脸抓拍 */
    String SET_PHOTO_FACE_STRANGER = "050310";
    /** 省电模式 */
    String SET_POWER_SAVING = "0504";
    /** 屏保设置 */
    String SET_SCREEN_SAVER = "0505";
    /** 灵敏度设置 */
    String SET_SENSITIVITY = "0506";
    /** 界面风格 */
    String SET_THEME = "0507";
    /** 事件上报平台 */
    String SET_EVENT_PLATFORM = "0508";

    /** 设备信息 */
    String SET_DEV_INFO = "06";
}
