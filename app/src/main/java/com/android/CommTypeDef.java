package com.android;


public class CommTypeDef {

	public class LANGUAGE_E
	{
		public static final byte CHINESE = 0;
		public static final byte CHNBIG5 = 1;
		public static final byte ENGLISH = 2;
	}

	public class LANGUAGE_NAME
	{
		public static final String ZHONGWENG = "zh";
		public static final String ENGLISH = "en";
	}

	public class ZOME
	{
		public static final String CHINESE = "CN";
		public static final String TAIWENG = "TW";
		public static final String ENGLISH = "US";
	}

	public class READFLAG_R{
		public static final int UNREAD = 1;
		public static final int READ = 2;
	}

	public class INFODATA_I{
		public static final int READALL = 0;
		public static final int READNEW = 1;
	}

	public class FILE_TYPE_E{
		public static final int FILE_NONE = 0;
		public static final int FILE_TXT = 1;
		public static final int FILE_BMP = 2;
		public static final int FILE_JPG = 3;
		public static final int FILE_GIF = 4;
		public static final int FILE_PNG = 5;
		public static final int FILE_FLASH = 6;
		public static final int FILE_MP3 = 32;
		public static final int FILE_WAVE = 33;
		public static final int FILE_AAC = 34;
		public static final int FILE_MP4 = 35;
		public static final int FILE_ASF = 36;
		public static final int FILE_BUTT = 255;
	}

	public static final String SECU_DATABASE = "secuparam.db";

	public class SubSysCode
	{
		public static final int SSC_PUBLIC			=0x00;
		public static final int SSC_INTERPHONE		=0x10;
		public static final int SSC_SINGLECHIP		=0x20;
		public static final int SSC_INFO			=0x30;
		public static final int SSC_PASSWORD		=0x40;
		public static final int SSC_fINGER			=0x50;
		public static final int SSC_SCANQR			=0x60;
		public static final int SSC_MULTIMEDIA		=0x70;
	}

	public class EchoValue
	{
		public static final int ECHO_OK			=0x00;
		public static final int ECHO_ERROR		=0x01;
		public static final int ECHO_BUSY		=0x02;
		public static final int ECHO_NO_RECORD	=0x03;
		public static final int ECHO_UNALLOWDD	=0x04;
		public static final int ECHO_NO_APPLY	=0x05;
		public static final int ECHO_CARD_FULL	=0x06;
		public static final int ECHO_HAVED		=0x07;
		public static final int ECHO_OFFLINE	=0x08;
		public static final int ECHO_MAC		=0x09;
		public static final int ECHO_REPEAT		=0x0A;
	}

	public class DeviceType
	{
		public static final int DEVICE_TYPE_NONE						=0x00;
		public static final int DEVICE_TYPE_ML_SERVER					=0x10;
		public static final int DEVICE_TYPE_ML_SERVER_WEB				=0x11;
		public static final int DEVICE_TYPE_ML_SERVER_SERVICE			=0x12;
		public static final int DEVICE_TYPE_COMM_SERVER_STREAMINGSERVER =0x13;
		public static final int DEVICE_TYPE_AURINE_SERVER_SMSMMSSERVER  =0x14;
		public static final int DEVICE_TYPE_CENTER						=0x20;
		public static final int DEVICE_TYPE_MANAGER						=0x21;
		public static final int DEVICE_TYPE_UNIT_MANAGER				=0x22;
		public static final int DEVICE_TYPE_AREA						=0x30;
		public static final int DEVICE_TYPE_STAIR						=0x31;
		public static final int DEVICE_TYPE_ROOM						=0x32;
		public static final int DEVICE_TYPE_ROOMFJ						=0x33;
		public static final int DEVICE_TYPE_PHONE						=0x40;
		public static final int DEVICE_TYPE_DOOR_PHONE					=0x41;
		public static final int DEVICE_TYPE_DOOR_NET					=0x42;
		public static final int DEVICE_TYPE_GATEWAY						=0x50;

	}

	//控制执行者 （报警远程控制、家电远程控制）
	public class Executor
	{
		public static final int EXECUTOR_LOCAL_HOST			=0x01;			// 本机控制
		public static final int EXECUTOR_REMOTE_DEVICE		=0x02;			// 遥控器
		public static final int EXECUTOR_ZIGBEE_YKQ			=0x03;			// Zigbee遥控器
		public static final int EXECUTOR_ZIGBEE_PANEL		=0x04;			// Zigbee控制面板
		public static final int EXECUTOR_LOCAL_FJ			=0x05;			// 本地网络分机(包含无线终端、家庭PC)
		public static final int EXECUTOR_LOCAL_MNFJ			=0x06;			// 本地模拟分机
		public static final int EXECUTOR_MANAGER			=0x10;			// 管理员机
		public static final int EXECUTOR_STAIR				=0x11;			// 梯口机
		public static final int EXECUTOR_AREA				=0x12; 			// 区口机
		public static final int EXECUTOR_IE_USER			=0x13;			// 远程IE平台
		public static final int EXECUTOR_SHORT_MSG			=0x20;			// 住户短信
		public static final int EXECUTOR_PHONE_USER			=0x21;			// 住户电话
		public static final int EXECUTOR_SERVER				=0x22;			// 中心服务器
	}

	/**
	 * 事件发送方
	 * @author EventSendto
	 */
	public class EventSendto
	{
		public static final int EST_PINGTAI					=0X01;			// 发送到平台
		public static final int EST_SERVER					=0X02;			// 发送到中心服务器
		public static final int EST_MANAGER					=0X03;			// 发送到管理员机
	}

	/**
	 * 发送包状态
	 * @author ThinkPad
	 *
	 */
	public class SendStart
	{
		public static final int SEND_STATE_SEND_READY		=0;				// 等待发送
		public static final int SEND_STATE_SENDING			=1; 			// 正在发送
		public static final int SEND_STATE_WAIT_ECHO		=2;				// 等待应答
		public static final int SEND_STATE_RECIVED_ECHO		=3;				// 收到应答包
		public static final int SEND_STATE_ECHO_TIMEOUT		=4;				// 应答超时
	}

	/**
	 *
	 * @author ThinkPad
	 */
	public class SnapVideoEventType
	{
		public static final int SVET_CALL				=0x01;			// 访客呼叫
		public static final int SVET_HOLD_BY_DURESS		=0x02;			// 挟持报警（公共终端或室内）
		public static final int SVET_ROOM_ALARM			=0x03;			// 住户报警
		public static final int SVET_ERROR_PASS			=0x04;			// 错误密码开门报警
	}

	/**
	 * @author ThinkPad
	 */
	public class SysSoundType
	{
		public static final int KEY_TYPE				=0x01;
		public static final int ALARM_TYPE			    =0x02;
		public static final int PREALARM_TYPE			=0x03;
		public static final int EXITALARM_TYPE			=0x04;
		public static final int RING_TYPE				=0x05;
		public static final int VOICEHINT_TYPE			=0x06;
		public static final int BUTT					=0xFF;
	}

	/**
	 *
	 * @author Think
	 */
	public class SetChangedType
	{
		public static final int DEVICENO_CHANGED						=1;
		public static final int LOCALHOST_CHANGED						=2;
		public static final int SERVERDEV_CHANGED						=3;
		public static final int SECU_HOSTPARAM_CHANGED					=4;
		public static final int SECU_AREAPARAM_CHANGED					=5;
	}

	/**
	 * @author Think
	 */
	public static class SysStatusType
	{
		private static final int[]	SysStatus = {0,0,0,0,0,0,0,0,0,0,0,0};

		public static final int TIP_MESSAGE_NEW                   = 0;
		public static final int TIP_AUDIO_MSG              		  = 1;
		public static final int TIP_VIDEO                         = 2;
		public static final int TIP_NO_ANSWER_TEL                 = 3;
		public static final int TIP_EVENT                      	  = 4;
		public static final int TIP_NO_DISTURB                    = 5;
		public static final int TIP_MESSAGE_NEWSPAPER     		  = 6;
		public static final int TIP_MESSAGE_VOTE                  = 7;
		public static final int TIP_SECU_HOST					  = 8;
		public static final int TIP_SECU_ALARM					  =9;
		public static final int TIP_CALLIN_STATE				  =10;
		public static final int TIP_CALLOUT_STATE				  =11;
		public static final int TIP_MONITOR_STATE				  =12;

		public static int getSysstatus(int tipType) {
			if (tipType>=SysStatus.length)
				return 0;

			return SysStatus[tipType];
		}

		public static void setSysStatus(int tipType,int state)
		{
			if (tipType>=SysStatus.length)
				return ;
			SysStatus[tipType] = state;
		}
	}

	/**
	 * @author Think
	 *
	 */
	public static class NetParam
	{
		public int	IP;
		public int	SubNet;
		public int	DefaultGateway;
		public int	DNS1;
		public int	DNS2;
		public int	CenterIP;
		public int	ManagerIP;
		public int	ManagerIP1;
		public int	ManagerIP2;
		public String	SipProxyServer;
		public String	PingtaiServer;
		public String	StunServer;
		public int  	IP1;
		public int 		SubNet1;
	}

	/**
	 * 按键状态
	 * @author Think
	 *
	 */
	public static class UserKeyState
	{
		public static final int KEY_STATE_UP				=0;	// 抬起
		public static final int KEY_STATE_DOWN				=1; // 按下
		public static final int KEY_STATE_PRESS				=2; // 长按
	}

	/**
	 * 快捷按键事件
	 */
	public static class UserKeyEvent
	{
		public static final	int	KEY_TALK			=35;	// 通话
		public static final	int	KEY_LOCK 			=59;	// 开锁
		public static final	int	KEY_TS_CALIBRATE	=50;	//
		public static final int KEY_MQJ1_CALLING	=30;	//
		public static final int KEY_MQJ2_CALLING	=48;
		public static final int KEY_SOS				=31;	// SOS
		public static final int KEY_ALARM			=215;	// 安防
		public static final int KEY_INFO			=60;	// 信息
	}

	/**
	 * 设置视频状态
	 * @author Think
	 *
	 */
	public static class SetVideo
	{
		public static final int VIDEO_OFF					=0;
		public static final int VIDEO_ON					=1;
		public static final int VIDEO_FULL_OFF				=0;
		public static final int VIDEO_FULL_ON				=1;
	}

	/**
	 * @author ThinkPad
	 *
	 */
	public static class SystemStateView
	{
		public static int NEW_NO 							=0;
		public static int NEW_ONE 							=1;
	}

	/**
	 * 快捷按键键值
	 * @author ThinkPad
	 *
	 */
	public static class KeyCodeValue
	{
		public static final int KEY_DOOR1					=71;
		public static final int KEY_DOOR2					=72;
		public static final int KEY_ALARM					=73;//119;
		public static final int KEY_INFO					=74;//60;
		public static final int KEY_LOCK					=60;//59;
		public static final int KEY_TALK					=59;//35;
		public static final int KEY_SOS						=70;//31;
		public static final int TS_CALIBRATE				=75;//50;
	}

	/**
	 * @author ThinkPad
	 *
	 */
	public static class CleanScreenActive
	{
		public static int UNABLE 							=0;
		public static int ENABLE 							=1;
	}

	/**
	 * 视频类型
	 * @author ThinkPad
	 *
	 */
	public class VideoType{
		public static final int LYLY_VIDEO					=0;
		public static final int BJ_VIDEO					=1;
	}

	/**
	 * @author ThinkPad
	 *
	 */
	public class playVideoState
	{
		public static final int PLAY 		= 0;
		public static final int PAUSE 		= 1;
		public static final int STOP 		= 2;
		public static final int PLAY_PRE	= 3;
		public static final int PLAY_NEXT	= 4;
	}

	public static final int MONITOR_HOME_CAMERA_NUM = 16;
	public static final int ELEVATOR_NUM = 2;


	public class TextSize{
		public static final int TEXT_SIZE_SMALL = 16;
		public static final int TEXT_SIZE_MID = 18;
		public static final int TEXT_SIZE_BIG = 30;
	}

	public class Rtsp_State{
		public static final int RTSP_STATE_NONE = 0;
		public static final int	RTSP_STATE_CONNECT = 1;
		public static final int	RTSP_STATE_OPER_PLAY = 2;
		public static final int	RTSP_STATE_PLAY = 3;
		public static final int	RTSP_STATE_PAUSE = 4;
		public static final int	RTSP_STATE_OPER_STOP =5;
		public static final int	RTSP_STATE_STOP = 6;
		public static final int	RTSP_STATE_HEART = 7;
	}

	// 电梯状态
	public class elevator_state
	{
		public static final int OPER_OK	= 0;				//操作成功
		public static final int ELEVATOR_UP = 1;			//电梯上行
		public static final int ELEVATOR_DOWN = 2;			//电梯下行
		public static final int ELEVATOR_STOP = 3;			//电梯停止ֹ
		public static final int ELEVATOR_BUSY = 4;			//电梯忙
		public static final int ELEVATOR_ARRIVED = 5;		//电梯到达
		public static final int OPER_FAIL = -1;				//操作失败
		public static final int ELEVATOR_NOEXIST = 0xFF;	//电梯不存在
	}

	public class timeType
	{
		public static final int everyday = 0;       //每天
		public static final int everyweek = 1;      //每周
		public static final int everymonth = 2;		//每月
		public static final int everyyear = 3;		//每年
		public static final int week1_5 = 4;		//周一到周五
		public static final int week1_6 = 5;		//周一到周六
	}

	/**
	 *  预约周
	 */
	public class BespokeWeek {
		public static final int BE_MON = 0;            // 周一
		public static final int BE_TUES = 1;           // 周二
		public static final int BE_WEDN = 2;           // 周三
		public static final int BE_THUR = 3;           // 周四
		public static final int BE_FRI = 4;            // 周五
		public static final int BE_STAU = 5;           // 周六
		public static final int BE_SUN = 6;            // 周日
	}

	public class YuyueType
	{
		public static final int elecyuyue = 0;       //家电预约
		public static final int secuyuyue = 1;       //安防预约
	}

	public class KeyCode
	{
		public static final int KEY_TALK = 183;
		public static final int KEY_LOCK = 184;
		public static final int KEY_MONITOR = 185;
		public static final int KEY_CENTER = 186;
		public static final int KEY_SOS = 187;
	}

	public class CallConnectText
	{
		public static final int CALL_CONNECT_NONE = 0x00; 				// 	无提示
		public static final int CALL_CONNECT_TIMEOUT = 0xE1; 			// 	连接超时
		public static final int CALL_CONNECT_BUSY = 0xE2; 				// 	设备繁忙
		public static final int CALL_CONNECT_NOROOMNO = 0xE3; 			// 	无此房号
		public static final int CALL_CONNECT_NOT_HANDDOWN = 0xE4; 		// 	无人接听
		public static final int CALL_CONNECT_TALK_HANDDOWN = 0xE5; 		// 	通话结束
		public static final int CALL_CONNECT_CALLING_END = 0xE6;		//	呼叫结束
	}

	public class TextHit {
		public static final int OpenDoor_Hit_None = 0; 						// 	空提示
		public static final int OpenDoor_Open_OK = 1; 						// 	门开了，请进入
		public static final int OpenDoor_QR_Invalid = 2; 					// 	无效二维码！
		public static final int OpenDoor_QR_Expired = 3; 					// 	过期二维码！
		public static final int OpenDoor_FPR_INVALID = 4; 					// 	无效指纹
		public static final int OpenDoor_FPR_Err = 5; 						// 	请重按指纹
		public static final int OpenDoor_FPR_Wait = 6; 						// 	正在比对指纹，请等候！
		public static final int OpenDoor_FPR_Hold = 7; 						// 	请保持手指按下!
		public static final int OpenDoor_FPR_Again = 8; 					// 	请重按指纹

		public static final int OpenDoor_Err_Pwd = 0xF1; 					// 	密码错误
		public static final int OpenDoor_Err_Card = 0xF2; 					// 	卡无效
		public static final int OpenDoor_Err_Pwd_NoUse = 0xF3; 				// 	密码功能未启用
		public static final int OpenDoor_Pwd_Wait = 0xF4; 					// 	请稍后

		public static final int Hit_Message_Property = 0xA1; 				// 	您已欠物业费，请及时缴纳!
		public static final int Hit_Message_Water = 0xA2; 					// 您已欠水费，请及时缴纳!
		public static final int Hit_Message_Electry = 0xA3; 				// 	您已欠电费，请计时缴纳!
		public static final int Hit_Message_Mail = 0xA4; 					// 	您有邮件，请及时认领!
		public static final int Hit_Message_Contact = 0xA5; 				// 	看到信息请联系物业!


		public static final int Alarm_ForceOpenDoor = 0xD0; 				// 	请关好门
		public static final int Alarm_NotCloseDoor = 0xD1; 					//  请关好门
		public static final int Alarm_Destory = 0xD2; 						// 	请关好门
		public static final int Card_Status_Xungen = 0xE0; 					// 	刷卡正确!
		public static final int Card_Status_PatrolEvent = 0xE1;
		public static final int Bule_Set_Status = 0xE9;
	}

	public class VoiceHint
	{
		public static final int TTS_PUBLIC_NONE = 0x00; 						// 	空文件
		public static final int TTS_PUBLIC_WELCOME = 0xA001; 					// 	欢迎使用
		public static final int TTS_PUBLIC_SET_OK = 0xA002; 					// 	操作成功
		public static final int TTS_PUBLIC_SET_ERROR = 0xA003; 					// 	请重新操作
		public static final int TTS_PUBLIC_DEFAULT = 0xA004; 					// 	正在恢复出厂，请稍候
		public static final int TTS_PUBLIC_WAIT = 0xA005; 						// 	请稍候
		public static final int TTS_INTERCOMM_TALK = 0xA006; 					// 	请通话
		public static final int TTS_INTERCOMM_TALK_END = 0xA007; 				// 	通话已结束
		public static final int TTS_INTERCOMM_MONITOR = 0xA008; 				// 	开始监视
		public static final int TTS_INTERCOMM_MONITOR_END = 0xA009; 			// 	监视结束

		public static final int VOICE_HINT_INPUT_ROOMNO = 0xB001; 				// 	请输入房号
		public static final int VOICE_HINT_INPUT_ROOMNO_ERR = 0xB002; 			// 	您呼叫的号码是空号，请核对后再呼
		public static final int VOICE_HINT_INPUT_PWD = 0xB003; 					// 	请您输入开门密码
		public static final int VOICE_HINT_INPUT_ADMINPWD = 0xB004; 			// 	请您输入管理密码
		public static final int VOICE_HINT_INPUT_PWD_ERR = 0xB005; 				// 	您输入的密码有误，请重新输入
		public static final int VOICE_HINT_INPUT_PWD_ERR_UNSET = 0xB006; 		// 	密码开门功能未启用
		public static final int VOICE_HINT_INPUT_PWD_ERR_3 = 0xB007;
		public static final int VOICE_HINT_SELECT_ROOM = 0xB008; 				// 	请选择您要呼叫的住户
		public static final int VOICE_HINT_OPEN_LOCK_VISTER = 0xB009; 			// 	门开了，请进入
		public static final int VOICE_HINT_OPEN_LOCK_VISTER_EXT = 0xB00A; 		// 	门开了，请进入   对讲中播放开锁铃声时用
		public static final int VOICE_HINT_OPEN_LOCK_ROOM = 0xB00B; 			// 	您输入的住户不在使用中
		public static final int VOICE_HINT_CARD_ERR = 0xB00C; 					// 	此卡没有开门权限，请联系管理处
		public static final int VOICE_HINT_QR_T_ERR = 0xB00D; 					// 	过期二维码
		public static final int VOICE_HINT_QR_ERR = 0xB00E; 					// 	无效二维码
		public static final int VOICE_HINT_FPR_INVALID = 0xB00F;				//  无效指纹
		public static final int VOICE_HINT_CALL_BUSY = 0xB010; 					// 	您呼叫的线路正忙，请稍后再呼
		public static final int VOICE_HINT_CALL_PLSWAIT = 0xB011; 				// 	请稍后
		public static final int VOICE_HINT_CALL_NOTCONNECT = 0xB012; 			// 	您呼叫的住户暂时无法接通
		public static final int VOICE_HINT_CALL_TIMEOUT = 0xB013; 				// 	您呼叫的住户暂时无人接听
		public static final int VOICE_HINT_CALL_MANAGER = 0xB014; 				// 	正在呼叫管理处，请稍后
		public static final int VOICE_HINT_CALL_BY_HANDDOWN = 0xB015; 			// 	对方已挂机
		public static final int VOICE_HINT_CALL_TALK_END = 0xB016; 				//  通话已结束
		public static final int VOICE_HINT_RECORDHINT = 0xB017; 				// 	留影留言提示音
		public static final int VOICE_HINT_TONE_PROPERTY = 0xB018;				//  您已欠物业费，请及时缴纳
		public static final int VOICE_HINT_TONE_WATER = 0xB019; 				// 	您已欠水费，请及时缴纳
		public static final int VOICE_HINT_TONE_ELECTRY = 0xB01A; 				// 	您已欠电费，请及时缴纳
		public static final int VOICE_HINT_TONE_MAIL = 0xB01B; 					// 	您有新的信件，请及时认领
		public static final int VOICE_HINT_TONE_CONTACT = 0xB01C; 				// 	请联系物业
		public static final int VOICE_HINT_CALL_RING = 0xB01D; 					// 	回铃声

		public static final int TTS_FACE_OPERATE = 0xC001; 						// 	请正对着设备，轻微抬头或左右摇头
		public static final int TTS_FACE_OPERATE_SHORT = 0xC002; 				//  请正对着设备
		public static final int TTS_FACE_DEL = 0xC003; 							// 	正在删除人脸
		public static final int TTS_FACE_OPER_OK = 0xC004;						//  请开门
		public static final int TTS_FACE_OPER_ERROR = 0xC005; 					// 	对不起，识别不成功！
		public static final int TTS_FACE_OPER_FULL = 0xC006; 					// 	人脸库已满
		public static final int TTS_FACE_LICENSE_NOT = 0xC007; 					//  人脸未授权
		public static final int TTS_FACE_LICENSE_YES = 0xC008; 					// 	人脸已授权
		public static final int TTS_FACE_LICENSE_OK = 0xC009;					//  人脸授权成功
		public static final int TTS_FACE_LICENSE_FAIL = 0xC00A; 				// 	人脸授权失败
		public static final int TTS_FACE_LICENSE_NOW = 0xC00B; 					// 	人脸授权中
	}

	public class CenterSysParam{
		public static final int UM_VILLAGEID 		= 0x00000000; 				 //社区第三方ID
		public static final int UM_USECLOUD 		= 0x00000001;				 //云对讲开关
		public static final int UM_USEFACE 			= 0x00000002;				 //人脸识别功能开关
		public static final int UM_COMMUNITYID		= 0x00000003; 				 //云社区ID

		public static final int UM_FTPADDR 			= 0x00000100;				 //FTP地址
		public static final int UM_FTPUSENAME 		= 0x00000101;				 //FTP用户名
		public static final int UM_FTPUSEPWD 		= 0x00000102;				 //FTP密码
		public static final int UM_FTPPORT			= 0x00000103;				 //FTP端口

		public static final int UM_FTPPIC_BLACKLIST = 0x00000110;				 //FTP黑名单人员底库照片
		public static final int UM_FTPPIC_WHITELIST	= 0x00000111;				 //FTP白名单人员底库照片
		public static final int UM_FTPPIC_ROOM		= 0x00000112;				 //FTP住户底库照片
		public static final int UM_FTPPIC_VISITOR	= 0x00000113;				 //FTP访客人员底库照片
		public static final int UM_FTPPIC_WORKER	= 0x00000114;				 //FTP工作人员底库照片

		public static final int UM_FTPSNAP_BLACKLIST= 0x00000120;				 //FTP黑名单人员抓拍照片
		public static final int UM_FTPSNAP_STFANGER	= 0x00000121;				 //FTP陌生人抓拍照片
		public static final int UM_FTPSNAP			= 0x00000122;				 //FTP事件抓拍照片
	}

	public class JudgeStatus {
		public static final int SUCCESS_STATE 		 = 0;						//成功状态
		public static final int FAIL_STATE 			 = 1;						//错误状态
		public static final int VAILD_FAIL_STATE 	 = 2;						//失效错误状态
		public static final int OTHER_FAIL_STATE 	 = 3;						//其他错误状态
	}

	public class LifecycleMode {
		public static final int VALID_LIFECYCLE_MODE  = 0;						//大于0判断次数有效性
		public static final int VALID_TIME_MODE 	  = -1;						//判断时间有效性
		public static final int VALID_NULL_MODE 	  = -2;						//不判断有效性
	}

	public class FaceAddType {
		public static final int FACETYPE_NONE 		 = 0;						//无此类型
		public static final int FACETYPE_DEV 		 = 1;						//本地添加
		public static final int FACETYPE_PC 		 = 2;						//上位机或云平台添加
	}

	public static final int DEVICE_MANAGER_NUMMIN = 101;						// 最小管理员机编号
	public static final int CARD_MAX_LEN =  59;									// 卡回调最大长度
	public static final int PWD_MAX_LEN =  62;									// 密码回调最大长度
	public static final int DOOR_REMINDER_MAX_LEN =  172;						// 开门提示最大长度
}
