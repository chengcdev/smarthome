package com.android.main;

import java.util.Arrays;

import com.android.Common;
import com.android.InterCommTypeDef;
import com.android.InterCommTypeDef.InterLockListener;
import com.android.InterCommTypeDef.PassWordCmddListener;
import com.android.InterCommTypeDef.CallState;
import com.android.InterCommTypeDef.InterCallOutListener;
import com.android.InterCommTypeDef.InterMonitorListener;
import com.android.InterCommTypeDef.InterDefVideoDataListener;
import com.android.InterCommTypeDef.InterSnapListener;
import com.android.InterCommTypeDef.MonitorState;
import com.mili.smarthome.tkj.utils.LogUtils;

import android.content.Context;
import android.util.Log;

public class InterCommDistribute {
	private InterCallOutListener mInterCallOutListener = null;
	private InterMonitorListener mInterMonitorListener = null;
	private InterLockListener mInterLockListener = null;
	private InterDefVideoDataListener mInterDefVideoDataListener = null;
	private PassWordCmddListener mPassWordCmddListener = null;
	private InterSnapListener mInterSnapListener = null;

	public void setInterCallOutListener(InterCallOutListener listener) {
		mInterCallOutListener = listener;
	}

	public void setInterMonitorListener(InterMonitorListener listener) {
		mInterMonitorListener = listener;
	}

	public void setInterLockListener(InterLockListener listener) {
		mInterLockListener = listener;
	}

	public void setInterVideoDataListener(InterDefVideoDataListener listener) {
		mInterDefVideoDataListener = listener;
	}

	public void setPassWordDataListener(PassWordCmddListener listener) {
		mPassWordCmddListener = listener;
	}

	public void setSnapDataListener(InterSnapListener listener) {
		mInterSnapListener = listener;
	}

	public void InterCommDistributeCallOut(Context context, byte[] data) {
		if (null == mInterCallOutListener) {
			return;
		}

		byte[] callno = new byte[30];
		int state = Common.bytes2int(data, 0);
		int param = Common.bytes2int(data, 4);
		int param2 = Common.bytes2int(data, 8);
		String devNo = "";

		LogUtils.d(" InterCommDistributeCallOut: state is " + state + ", param is " + param + ", param2 is " + param2);
		switch (state) {
		case CallState.CALL_STATE_NONE:
			mInterCallOutListener.InterCallOutNone(param);
			break;

		case CallState.CALL_STATE_CALLING:
			mInterCallOutListener.InterCallOutCalling(param);
			break;

		case CallState.CALL_STATE_RECORDHINT:
			mInterCallOutListener.InterCallOutRecordHit(param);
			break;

		case CallState.CALL_STATE_RECORDING:
			mInterCallOutListener.InterCallOutRecording(param);
			break;

		case CallState.CALL_STATE_TALK:
			mInterCallOutListener.InterCallOutTalking(param);
			break;

		case CallState.CALL_STATE_END:
			mInterCallOutListener.InterCallOutEnd(param);
			break;

		case CallState.CALL_STATE_MOVETEMP:
			mInterCallOutListener.InterCallOutMoveing(param);
			break;

		case CallState.CALL_STATE_TIME:
			int maxTimer = Common.bytes2short(data, 4);
			int exitTimer = Common.bytes2short(data, 6);
			mInterCallOutListener.InterCallOutTimer(maxTimer, exitTimer);
			break;

		case CallState.CALL_STATE_HITSTATE:
			mInterCallOutListener.InterCallOutHitState(param, param2);
			break;
		}
	}

	public void InterCommDistributeMonitor(Context context, byte[] data) {
		if (mInterMonitorListener == null) {
			return;
		}
		int state = 0;
		int param = 0;
		byte[] callno = new byte[30];
		String devNo = "";
		state = Common.bytes2int(data, 0);
		param = Common.bytes2int(data, 4);

		switch (state){
			case MonitorState.MONITOR_TALKING:
				mInterMonitorListener.InterMonitorTalking(param);
				break;


			case MonitorState.MONITOR_TALK_END:
				mInterMonitorListener.InterMonitorTalkEnd(param);
				break;


			case MonitorState.MONITOR_END:
				mInterMonitorListener.InterMonitorEnd(param);
				break;

			default:
				break;
		}
	}

	public void InterCommDistributeLock(Context context, byte[] data) {
		if (mInterLockListener == null) {
			return;
		}
		int param = Common.bytes2int(data, 0);

		mInterLockListener.InterLock(param);
	}

	public void InterCommDistributeSnap(Context context, byte[] data) {
		if (mInterSnapListener == null) {
			return;
		}
		int param = Common.bytes2int(data, 0);

		mInterSnapListener.InterSnap(param);
	}

	public void InterCommDistributePassWord(Context context, byte[] data) {
		if (mPassWordCmddListener == null) {
			return;
		}
		String roomNo = null;
		int param = Common.bytes2int(data, 0);
		int param2 = Common.bytes2int(data, 4);

		if (data.length > 8){
			byte[] roomno = new byte[20];
			System.arraycopy(data, 8, roomno, 0, 20);
			roomNo = Common.byteToString(roomno);
		}
		mPassWordCmddListener.PassWordCmdListener(param, param2, roomNo);
	}

	public void InterCommDistributeVideoDataCallBK(Context context, byte[] data, int datalen, int width, int height, int type) {
		if (mInterDefVideoDataListener == null) {
			return;
		}
		mInterDefVideoDataListener.InterVideoCallBK(data, datalen, width, height, type);
	}
}
