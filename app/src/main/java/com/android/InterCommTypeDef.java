package com.android;

public class InterCommTypeDef {

	public class CallState {
		public static final int CALL_STATE_NONE = 0x00;
		public static final int CALL_STATE_CALLING = 0x01;
		public static final int CALL_STATE_RECORDHINT = 0x02;
		public static final int CALL_STATE_RECORDING = 0x03;
		public static final int CALL_STATE_TALK = 0x04;
		public static final int CALL_STATE_END = 0x05;
		public static final int CALL_STATE_MOVETEMP = 0x06;
		public static final int CALL_STATE_TIME = 0x07;
		public static final int CALL_STATE_HITSTATE = 0x08;
	}

	public class CallEndType {
		public static final int END_BY_OTHER_SIDE = 0x00;
		public static final int END_BY_USER_HANDDOW = 0x01;
		public static final int END_BY_CALL_BUSY = 0x02;
		public static final int END_BY_CALL_TIMEOUT = 0x03;
		public static final int END_BY_COMM_TIMEOUT = 0x04;
		public static final int END_BY_TALK_TIMEOUT = 0x05;
		public static final int END_BY_SELF_ERR = 0x06;
	}

	public class CallEcho {
		public static final int ECHO_CALL_OK = 0x00;
		public static final int ECHO_CALL_ERROR = 0x01;
		public static final int ECHO_CALL_NUM = 0x02;
		public static final int ECHO_CALL_DEV_TYPE = 0x03;
		public static final int ECHO_CALL_NO_IP = 0x04;
	}

	public class MonitorState {
		public static final int MONITOR_TALKING = 0x04;
		public static final int MONITOR_END = 0x05;
		public static final int MONITOR_TALK_END = 0x09;
	}

	public class MonitorEcho {
		public static final int ECHO_MONITOR_OK = 0x00;
		public static final int ECHO_MONITOR_ERR = 0x01;
		public static final int ECHO_MONITOR_BUSY = 0x02;
		public static final int ECHO_MONITOR_ERR_ID = 0x03;
	}

	public class CallActive {
		public static final int CALLACTIVE_IN = 0x00;
		public static final int CALLACTIVE_OUT = 0x01;
		public static final int CALLACTIVE_MONITOR = 0x02;
	}

	public class VideoCallBKState {
		public static final int SEND_VIDEO_STATE_FACE = 0x03;		// 人脸识别
		public static final int SEND_VIDEO_STATE_QRCODE = 0x04;		// 扫二维码
	}

	public interface InterCallOutListener {
		public void InterCallOutNone(int param);

		public void InterCallOutCalling(int param);

		public void InterCallOutTalking(int param);

		public void InterCallOutEnd(int param);

		public void InterCallOutRecording(int param);

		public void InterCallOutRecordHit(int param);

		public void InterCallOutTimer(int maxtime, int exittime);

		public void InterCallOutMoveing(int param);

		public void InterCallOutHitState(int wordhit, int voicehit);
	}

	public interface InterMonitorListener {
		public void InterMonitorTalking(int param);
		public void InterMonitorEnd(int param);
		public void InterMonitorTalkEnd(int param);
	}

	public interface InterLockListener {
		public void InterLock(int param);
	}

	public interface PassWordCmddListener {
		public void PassWordCmdListener(int param, int param2, String roomNo);
	}

	public interface InterDefVideoDataListener {
		/**
		 * @param type 0-本地预览，1-RTSP
		 */
		public void InterVideoCallBK(byte[] data, int datalen, int width, int height, int type);
	}

	public interface InterSnapListener {
		public void InterSnap(int param);
	}

	public interface IFaceRecognizeListener {
		/**
		 * 人脸识别结果反馈
		 * @param state	 0 识别成功 1识别失败
		 */
		void FaceRecognizeResult(int state);
	}

	public interface IFaceTemperatureListener {
		/**
		 * 人脸体温检测结果反馈
		 * @param state	0 体温正常 1 体温偏高 2 体温偏低
		 */
		void FaceTemperatureResult(int state);
	}
}
