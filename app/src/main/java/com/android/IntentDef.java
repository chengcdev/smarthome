
package com.android;


/**
 * @author huangxf
 * @version 1.0.0
 */
public class IntentDef {

    public static final String MODULE_NAME			="ml.intent.action.MODULE_NAME";
	public static final String MODULE_MAIN			="ml.intent.action.MODULE_MAIN";
	public static final String MODULE_INFO			="ml.intent.action.MODULE_INFO";
	public static final String MODULE_INTERCOMM		="ml.intent.action.MODULE_INTERCOMM";
	public static final String MODULE_ELECTRICAL	="ml.intent.action.MODULE_ELECTRICAL";
	public static final String MODULE_MEDIA			="ml.intent.action.MODULE_MEDIA";
	public static final String MODULE_PASSWORD		="ml.intent.action.MODULE_PASSWORD";
	public static final String MODULE_SCANQR		="ml.intent.action.MODULE_SCANQR";
	public static final String MODULE_SINGLECHIP	="ml.intent.action.MODULE_SINGLECHIP";
	public static final String MODULE_FINGER		="ml.intent.action.MODULE_FINGER";
	public static final String MODULE_MULTIMEDIA	="ml.intent.action.MULTIMEDIA";

	public static final String INTENT_NETCOMM_TYPE	="ml.intent.netcomm.TYPE";
	public static final int    INTENT__TYPE_INVALID =-1;
	public static final String INTENT_NETCOMM_DATA	="ml.intent.netcomm.DATA";
	
	public static final String INTENT_MAIN_KEYEVENT	="Main_KeyEvent";

	public interface OnNetCommDataReportListener
	{
		/***
		 * 
		 * @param action
		 * @param type
		 * @param data
		 */
		public void OnDataReport(String action, int type, byte[] data);

	}

	public interface OnVideoDataReportListener
	{
		/***
		 * @param data
		 * @param datalen
		 * @param width
		 * @param height
		 */
		public void OnVideoDataReport(byte[] data, int datalen, int width, int height, int type);

	}

	
	public class PubIntentTypeE
	{
		public static final int Main_UserEvent 		=0x04;
		public static final int Main_KeyEvent 		=0x05;
		public static final int Main_PubCmdEvent    =0x06;
		
		public static final int INTENT_MAIN_SET_SYS_PASS		=0x01;
		public static final int INTENT_MAIN_SET_AREACODEAM		=0x07;
		public static final int	INTENT_MAIN_DEVNO_RULE_CMD  	=0x08;			// 上位机下发设置设备编号规则
		public static final int	INTENT_MAIN_SYN_TIME			=0x55;			// 时间同步
		public static final int INTENT_MAIN_CENTER_ISONLINE		=0x62;			// 中心机在线状态回调

		public static final int INTENT_CARD_ADD 				=0x09;			// 上位机下发卡操作回调
		public static final int INTENT_CARD_DEL 				=0x0A;			// 上位机删除卡操作回调
		public static final int INTENT_CARD_CLEAR 				=0x0B;			// 上位机清空卡操作回调
		public static final int INTENT_AREA_LOGO 				=0x0C;			// 上位机下发logo回调

		public static final int INTENT_FINGER_ADD 				=0x11;			//上位机下发指纹操作回调
		public static final int INTENT_FINGER_DEL 				=0x12;			//上位机删除指纹操作回调
		public static final int INTENT_FINGER_DEL_USER 			=0x13;			//上位机删除住户所有指纹操作回调
		public static final int INTENT_FINGER_CLEAR 			=0x14;			//上位机清空卡操作回调

		public static final int INTENT_MAIN_FACE_LICENSE		=0x21;			// 授权人脸识别功能
		public static final int INTENT_MAIN_FTP_SYSTEM_PARAM	=0x22;			// FTP服务器系统参数
		public static final int IINTENT_MAIN_FACE_REGISTER		=0x23;			// 人脸照片注册
		public static final int IINTENT_MAIN_FACE_DEL			=0x24;			// 人脸照片删除
		public static final int INTENT_MAIN_FACE_QUERY_INFO		=0x25;			// 查询人脸信息
		public static final int INTENT_MAIN_FACE_COLLECT  		=0x26;			// 人脸采集请求
		public static final int INTENT_MAIN_FACE_RECOGNIZE		=0x28;			// 人脸识别请求
		public static final int INTENT_MAIN_FACE_TEMPERATURE	=0x29;			// 人脸体温检测

		public static final int INTENT_MAIN_CLOUD_REBOOT		=0x30;			// 云端重启
		public static final int INTENT_MAIN_CAMERA_ERROR		=0x31;			// 摄像头错误

		public static final int INTENT_MAIN_MQTT_PASSWD_ADD		=0x40;			// Mqtt密码添加
		public static final int INTENT_MAIN_MQTT_PASSWD_DEL  	=0x41;			// Mqtt密码删除
		public static final int INTENT_MAIN_MQTT_FACE_REGISTER	=0x42;			// Mqtt人脸照片注册
		public static final int INTENT_MAIN_MQTT_FACE_DEL  		=0x43;			// Mqtt人脸照片删除
		public static final int INTENT_MAIN_MQTT_DOORREMINDER_ADD	=0x44;		// Mqtt添加开门提醒
		public static final int INTENT_MAIN_MQTT_DOORREMINDER_DEL  	=0x45;		// Mqtt删除开门提醒
		public static final int INTENT_MAIN_MQTT_SYN_TIME  			=0x46;		// Http鉴权时间同步
		public static final int INTENT_MAIN_MQTT_INFO_ADD  			=0x47;		// Mqtt添加信息

		public static final int CallOutStatusNofity				= 0x1001;
		public static final int MonitorStatusNotify 			= 0x1002;
		public static final int IntercomLock 					= 0x1003;
		public static final int InterComSnap 					= 0x1004;
		public static final int INTENT_CLEAR_ACCESS_PASS		= 0x1011;

		public static final int Singlecgip_AddCard				=0x2001;
		public static final int Singlecgip_Face					=0x2002;
		public static final int Singlecgip_CardOpenDoor			=0x2003;
		public static final int Singlecgip_DoorAlarm			=0x2004;
		public static final int Singlecgip_BodyInduction		=0x2005;
		public static final int Singlecgip_TouchKeyReport		=0x2006;		// 触摸键值上报

		public static final int Info_NewInfoNofity				=0x3001;		// 新信息
		public static final int Info_ClearInfoNofity			=0x3002;		// 清空信息

		public static final int PassWord_DealNofity				=0x4001;		// 密码处理回调

		public static final int Finger_AddState					=0x5001;		// 指纹添加回调
		public static final int Finger_OpenDoor					=0x5002;
		public static final int Finger_State					=0x5003;

		public static final int ScanQr_Recognize_CallBack		=0x6001;

		public static final int Multimedia_Set_Media_Type		=0x7001;
		public static final int Multimedia_DownLoad				=0x7002;
		public static final int Multimedia_Del					=0x7003;
	}

}
