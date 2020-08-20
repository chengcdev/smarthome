package com.android;


public class CommSysDef {

	public static final String LibRKJniName = "jniandroidrktkj";
	public static final String LibDPXJniName = "jniandroiddpxtkj";
	public static final String LibMTKJniName = "jniandroidmtktkj";
	public static final String LibMedia = "androidmedia";
	
	public static final String SERVICE_NAME_MAIN ="com.android.main.MainService";
	public static final String SERVICE_NAME_INFO = "com.android.main.Info";
	/**
	 *  网络IP变更
	 */
	public static final String BROADCAST_NAME_IP = "android.app.main.action.NanmeIp";
	/**
	 *  设备编号变更
	 */
	public static final String BROADCAST_DEVICENUMBER	= "android.app.main.action.DEVICENUMBER";
	/**
	 * 设备编号规则变更
	 */
	public static final String BROADCAST_DEVICENORULE	= "android.app.main.action.DEVICENORULE";
	/**
	 * 门状态变更
	 */
	public static final String BROADCAST_DOOR_STATE	= "android.app.main.action.DOORSTATE";
	/**
	 * 锁属性设置
	 */
	public static final String BROADCAST_LOCK_STATE	= "android.app.main.action.LOCKSTATE";
	/**
	 * 强行开门报警设置
	 */
	public static final String BROADCAST_FORCEDOPENDOOR	= "android.app.main.action.FORCEDOPENDOOR";
	/**
	 * 拍照参数
	 */
	public static final String BROADCAST_CAMERAPARAM	= "android.app.main.action.CAMERAPARAM";
	/**
	 * 灵敏度参数
	 */
	public static final String BROADCAST_TOUCHSENS	= "android.app.main.action.TOUCHSENS";
	/**
	 * 卡位数
	 */
	public static final String BROADCAST_CARDNUMS	= "android.app.main.action.CARDNUMS";
	/**
	 * 添加卡
	 */
	public static final String BROADCAST_ADD_CARD	= "android.app.main.action.ADDCARDS";
	/**
	 * 是否启用了人脸
	 */
	public static final String BROADCAST_ENABLE_FACE	= "android.app.main.action.BROADCAST_ENABLE_FACE";
	/**
	 * 是否启用了指纹
	 */
	public static final String BROADCAST_ENABLE_FINGER	= "android.app.main.action.BROADCAST_ENABLE_FINGER";
	/**
	 * 设置按键音
	 */
	public static final String BROADCAST_KEY_VOLUME	= "android.app.main.action.KEYVOLUME";
	/**
	 * 信息的ACTION
	 */
	public static final String BROADCAST_NAME_INFO		= "android.app.main.action.INFO_BROADCAST";
	/**
	 * 按键使能的ACTION
	 */
	public static final String BROADCAST_NAME_KEYENABLE	= "android.app.main.action.KEY_ENABLE_BROADCAST";
	/**
	 * 对讲仲裁的ACTION
	 */
	public static final String BROADCAST_NAME_INTERCOMM	= "android.app.main.action.INTERCOMM_BROADCAST";
	/**
	 * 重启的ACTION
	 */
	public static final String BROADCAST_NAME_REBOOT	= "android.intent.action.REBOOT_BROADCAST";
	/**
	 * 通知主页更新信息广播
	 */
	public static final String BROADCAST_MAINACTIVITYBTNINFO = "android.app.main.action.MainBtninfo";

	public static final String BROADCAST_REFRESH_REGISTER = "android.app.main.action.Refresh_Register";
	/**
	 * 创建文件夹
	 */
	public static final String BROADCAST_MKDIR = "android.app.main.action.MKdir";
	/**
	 * 房号描述
	 */
	public static final String BROADCAST_ROOMSUB = "android.app.main.action.RoomSub";

	public static final String BROADCAST_SCANQR = "android.app.main.action.BROADCAST_SCANQR";
	public static final String BROADCAST_SINGLECHIP = "android.app.main.action.BROADCAST_SINGLECHIP";

	/**
	 * 事件上报平台
	 */
	public static final String BROADCAST_EVENT_PLATFORM = "android.app.main.action.EVENT_PLATFORM";

	/**
	 * TTS语音
	 */
	public final static String TTSAPP_STARTSPEAK = "android.app.voice.TTSservice_StartSpeak";
	public final static String TTSAPP_STARTSPEAKTEXT = "android.app.voice.TTSservice_StartSpeakText";
	public final static String TTSAPP_STOPSPEAK = "android.app.voice.TTSservice_StopSpeak";
	public final static String TTSAPP_UPDATETEXT = "android.app.voice.TTSservice_UpdateText";
	public final static String TTSAPP_SPEAKSTATE = "android.app.voice.TTSservice_SpeakState";

	public static final String ttsSpeed="45";
	public static final String ttsPitch="50";
	public static final String ttsVolume="80";
	public static final String ttsVolumeSign="8";
	public static final String ttsStreamType="3";

	public static final String TTS_SUBNOTICE ="android.app.main.MainActivity.tts.subnotice";
	public static final String TTS_SUBWORKPLAN ="android.app.advantage.WorkPlanService.tts.subworkplan";
	public static final String TTS_SUBPUBLIC ="android.app.main.MainActivity.tts.public";
	public static final String TTS_SUBDUTYPLAN ="android.app.duts.dutyplan";

	public static final int TTS_MAN	= 0;
	public static final int TTS_WOMAN = 1;
	public static final int TTS_READ_TIMES = 1;
	public static final String TTS_READ_TYPE ="TTS_READ_TYPE";
	public static final String TTS_READ_TEXT="TTS_READ_TEXT";
}



