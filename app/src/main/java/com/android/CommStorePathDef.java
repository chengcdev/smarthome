package com.android;

public class CommStorePathDef {
    /**
     * 文件根目录
     */
    /*本地保存路径*/
    public static final String USERDATA_PATH = "/mnt/sdcard/DCIM";
    /*SD卡路径*/
    public static final String EXTERNAL_SD_PATH = "/mnt/external_sd";

    /**
     * 使用本地保存
     */
	/*放置APP一些参数文件*/
    public static final String PARAM_PATH = USERDATA_PATH + "/param";

    /*信息保存路径*/
    public static final String INFO_DIR_PATH = USERDATA_PATH + "/info";

    /*抓拍图片*/
    public static final String SNAP_DIR_PATH = USERDATA_PATH + "/snap";

    /*抓拍图片*/
    public static final String RECORD_DIR_PATH = USERDATA_PATH + "/record";

    /*下载的人脸图片*/
    public static final String FACE_DIR_PATH = USERDATA_PATH + "/face";

    /*小区LOGO的图片*/
    public static final String LOGO_DIR_PATH = USERDATA_PATH + "/logo";

    /*扫描二维码模块存储路径*/
    public static final String SCANQR_DIR_PATH = USERDATA_PATH + "/scanqr";

    /*旷视人脸路径*/
    public static final String FACEPASS_DIR_PATH = USERDATA_PATH + "/megvii";

    /*旷视人脸本地注册的图片路径*/
    public static final String FACEPASS_PERSON_DIR_PATH = FACEPASS_DIR_PATH + "/person";

    /*多媒体模块存储路径*/
    public static final String MULTIMEDIA_DIR_PATH = USERDATA_PATH + "/multimedia";

    /*多媒体视频存储路径*/
    public static final String MULTIMEDIA_VIDEO_DIR_PATH = MULTIMEDIA_DIR_PATH + "/video";

    /*多媒体屏保图片存储路径*/
    public static final String MULTIMEDIA_PHOTO_DIR_PATH = MULTIMEDIA_DIR_PATH + "/screenphoto";

    /*多媒体下载视频和屏保图片临时存储路径*/
    public static final String MULTIMEDIA_DIR_TMP_PATH = MULTIMEDIA_DIR_PATH + "/temp";

    /**
     * 使用外部SD卡保存
     */

    /*多媒体模块存储路径*/
    public static final String EX_MULTIMEDIA_DIR_PATH = EXTERNAL_SD_PATH + "/multimedia";

    /*多媒体视频存储路径*/
    public static final String EX_MULTIMEDIA_VIDEO_DIR_PATH = EX_MULTIMEDIA_DIR_PATH + "/video";

    /*多媒体下载视频临时存储路径*/
    public static final String EX_MULTIMEDIA_DIR_TMP_PATH = EX_MULTIMEDIA_DIR_PATH + "/temp";



    /**
     * 报警声
     */
    public static final String ALARM_TIPS_PATH = "ring/alarm/alarmout.wav";
    /**
     * 回铃声
     */
    public static final String CALL_OUT_PATH = "ring/callout/ringout.wav";
    /**
     * 提示音
     */
    public static final String RING_TIPS_PATH = "ring/tiprings";
    /**
     * 欢迎使用
     */
    public static final String WLECOME_PATH = RING_TIPS_PATH + "/wlecome.wav";
    /**
     * 操作成功
     */
    public static final String SET_OK_PATH = RING_TIPS_PATH + "/set_ok.wav";
    /**
     * 请重新操作
     */
    public static final String SET_ERR_PATH = RING_TIPS_PATH + "/set_err.wav";
    /**
     * 正在恢复出厂，请稍候
     */
    public static final String DEFAULT_PATH = RING_TIPS_PATH + "/default.wav";
    /**
     * 请稍候
     */
    public static final String WAIT_PATH = RING_TIPS_PATH + "/wait.wav";
    /**
     * 请通话
     */
    public static final String TALK_PATH = RING_TIPS_PATH + "/talk.wav";
    /**
     * 通话已结束
     */
    public static final String TALK_END_PATH = RING_TIPS_PATH + "/talk_end.wav";
    /**
     * 开始监视
     */
    public static final String MONITOR_PATH = RING_TIPS_PATH + "/monitor.wav";
    /**
     * 监视结束
     */
    public static final String MONITOR_END_PATH = RING_TIPS_PATH + "/monitor_end.wav";
    /**
     * 请输入房号
     */
    public static final String VOICE_1201_PATH = RING_TIPS_PATH + "/voice_1201.wav";

    /**
     * 您呼叫的号码是空号，请核对后再呼
     */
    public static final String VOICE_1202_PATH = RING_TIPS_PATH + "/voice_1202.wav";

    /**
     * 请您输入开门密码
     */
    public static final String VOICE_1301_PATH = RING_TIPS_PATH + "/voice_1301.wav";
    /**
     * 请您输入管理密码
     */
    public static final String VOICE_1305_PATH = RING_TIPS_PATH + "/voice_1305.wav";
    /**
     * 您输入的密码有误，请重新输入
     */
    public static final String VOICE_1302_PATH = RING_TIPS_PATH + "/voice_1302.wav";
    /**
     * 密码开门功能未启用
     */
    public static final String VOICE_1303PATH = RING_TIPS_PATH + "/voice_1303.wav";
    /**
     * 请选择您要呼叫的住户
     */
    public static final String VOICE_1401_PATH = RING_TIPS_PATH + "/voice_1401.wav";

    /**
     * 门开了，请进入!
     */
    public static final String VOICE_1501_PATH = RING_TIPS_PATH + "/voice_1501.wav";
    /**
     * 门未锁!
     */
    public static final String VOICE_UNLOCK_PATH = RING_TIPS_PATH + "/voice_unlock.wav";
    /**
     * 您输入的住户不在使用中
     */
    public static final String VOICE_1502_PATH = RING_TIPS_PATH + "/voice_1502.wav";
    /**
     * 此卡没有开门权限，请联系管理处
     */
    public static final String VOICE_1503_PATH = RING_TIPS_PATH + "/voice_1503.wav";
    /**
     * 无效二维码
     */
    public static final String VOICE_1504_PATH = RING_TIPS_PATH + "/voice_1504.wav";
    /**
     * 过期二维码
     */
    public static final String VOICE_1505_PATH = RING_TIPS_PATH + "/voice_1505.wav";
    /**
     * 无效指纹
     */
    public static final String VOICE_1506_PATH = RING_TIPS_PATH + "/voice_1506.wav";
    /**
     * 您呼叫的住户线路正忙，请稍后再呼
     */
    public static final String VOICE_1801_PATH = RING_TIPS_PATH + "/voice_1801.wav";
    /**
     * 请稍后
     */
    public static final String VOICE_1802_PATH = RING_TIPS_PATH + "/voice_1802.wav";
    /**
     * 您呼叫的住户暂时无法接通
     */
    public static final String VOICE_1803_PATH = RING_TIPS_PATH + "/voice_1803.wav";
    /**
     * 您呼叫的住户暂时无人接听
     */
    public static final String VOICE_1804_PATH = RING_TIPS_PATH + "/voice_1804.wav";
    /**
     * 正在呼叫管理处，请稍后
     */
    public static final String VOICE_1805_PATH = RING_TIPS_PATH + "/voice_1805.wav";
    /**
     * 对方已挂机
     */
    public static final String VOICE_1806_PATH = RING_TIPS_PATH + "/voice_1806.wav";

    /**
     * 通话已结束
     */
    public static final String VOICE_1807_PATH = RING_TIPS_PATH + "/voice_1807.wav";
    /**
     * 请留言
     */
    public static final String VOICE_1808_PATH = RING_TIPS_PATH + "/voice_1808.wav";
    /**
     * 您已欠物业费，请及时缴纳
     */
    public static final String VOICE_1901_PATH = RING_TIPS_PATH + "/voice_1901.wav";
    /**
     * 您已欠水费，请及时缴纳
     */
    public static final String VOICE_1902_PATH = RING_TIPS_PATH + "/voice_1902.wav";
    /**
     * 您已欠电费，请及时缴纳
     */
    public static final String VOICE_1903_PATH = RING_TIPS_PATH + "/voice_1903.wav";
    /**
     * 您有新的信件，请及时认领
     */
    public static final String VOICE_1904_PATH = RING_TIPS_PATH + "/voice_1904.wav";
    /**
     * 请联系物业
     */
    public static final String VOICE_1905_PATH = RING_TIPS_PATH + "/voice_1905.wav";
    /**
     * 请正对着设备，轻微抬头或左右摇头
     */
    public static final String FACE_OPERATE_PATH = RING_TIPS_PATH + "/face_operate.wav";
    /**
     * 请正对着设备
     */
    public static final String FACE_OPERATE_SHORT_PATH = RING_TIPS_PATH + "/face_operate_short.wav";
    /**
     * 正在删除人脸
     */
    public static final String FACE_DEL_PATH = RING_TIPS_PATH + "/face_del.wav";
    /**
     * 请开门
     */
    public static final String FACE_OPER_OK_PATH = RING_TIPS_PATH + "/face_oper_ok.wav";
    /**
     * 对不起，识别不成功！
     */
    public static final String FACE_OPER_ERROR_PATH = RING_TIPS_PATH + "/face_oper_error.wav";
    /**
     * 人脸库已满
     */
    public static final String FACE_OPER_FULL_PATH = RING_TIPS_PATH + "/face_oper_full.wav";
    /**
     * 人脸未授权
     */
    public static final String FACE_LICENSE_NOT_PATH = RING_TIPS_PATH + "/face_license_not.wav";
    /**
     * 人脸已授权
     */
    public static final String FACE_LICENSE_YES_PATH = RING_TIPS_PATH + "/face_license_yes.wav";
    /**
     * 人脸授权成功
     */
    public static final String FACE_LICENSE_OK_PATH = RING_TIPS_PATH + "/face_license_ok.wav";
    /**
     * 人脸授权失败
     */
    public static final String FACE_LICENSE_FAIL_PATH = RING_TIPS_PATH + "/face_license_fail.wav";
    /**
     * 人脸授权中
     */
    public static final String FACE_LICENSE_NOW_PATH = RING_TIPS_PATH + "/face_license_now.wav";
}
