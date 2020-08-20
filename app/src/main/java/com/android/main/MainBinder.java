package com.android.main;

import android.content.Context;
import android.os.RemoteException;

import com.android.provider.FullDeviceNo;
import com.android.provider.NetworkHelp;
import com.mili.smarthome.tkj.BuildConfig;
import com.mili.smarthome.tkj.auth.AuthManage;

public class MainBinder extends IMainService.Stub {

	private static final String tag = "MainBinder";
	private MainJni mMainJni;
	private Context mContext = null;

	public MainBinder(Context c) {
		mMainJni = new MainJni(c);
		mContext = c;
	}

	public MainJni getMainJni() {
		return mMainJni;
	}

	public void MainLogicJniInit(){
		mMainJni.logicInitJni(AuthManage.getAuthState());
		mMainJni.setCallback(mMainJni, "mainCallbackProc", BuildConfig.DevType);
		mMainJni.setVideoCallback(mMainJni, "mainVideoCallbackProc");
	}

	@Override
	public void Main_setFullDevNo(FullDeviceNo object, String clsname) throws RemoteException {
		mMainJni.setFullDevNo(object, clsname);
	}

	@Override
	public void Main_setSysNetParam(NetworkHelp object, String clsname) throws RemoteException {

	}

	@Override
	public void Main_setRoomDest(byte[] dest, int length) throws RemoteException {

	}

	@Override
	public void Main_setDoorStateParam(int doorState, int doorAlarmOutput, int doorReportCenter) throws RemoteException {

	}

	@Override
	public long Main_getRandomSeed() throws RemoteException {
		return mMainJni.getRandomSeed();
	}

	@Override
	public void Main_registerCenter(String softverString, String hardverString) throws RemoteException {
		mMainJni.registerCenter(softverString, hardverString);
	}

	@Override
	public void Main_refreshRegisterCenter() throws RemoteException {
		mMainJni.refreshRegisterCenter();
	}

	@Override
	public void Main_reqSynTime() throws RemoteException {
		mMainJni.reqSynTime();
	}

	@Override
	public int Main_formatExternalSdCard() throws RemoteException {
		return mMainJni.formatExternalSdCard();
	}

	@Override
	public int Main_intercomCallRoom(String roomNum) throws RemoteException {
		return mMainJni.intercomCallRoom(roomNum);
	}

	@Override
	public int Main_intercomCallCenter(int centerdevno, int exno) throws RemoteException {
		return  mMainJni.intercomCallCenter(centerdevno, exno);
	}

	@Override
	public void Main_intercomHandDown() throws RemoteException {
		mMainJni.intercomHandDown();
	}

	@Override
	public void Main_intercomMonitorStop() throws RemoteException {
		mMainJni.intercomMonitorStop();
	}

	@Override
	public int Main_intercomPreviewStart(int state) throws RemoteException {
		return mMainJni.intercomPreviewStart(state);
	}

	@Override
	public void Main_intercomPreviewStop(int state) throws RemoteException {
		mMainJni.intercomPreviewStop(state);
	}

	@Override
	public void Main_intercomSetAudioState(int state) throws RemoteException {
		mMainJni.intercomSetAudioState(state);
	}

	@Override
	public int Main_intercomRtspStart(String url) throws RemoteException {
		return mMainJni.intercomRtspStart(url);
	}

	@Override
	public void Main_intercomRtspStop() throws RemoteException {
		mMainJni.intercomRtspStop();
	}

	@Override
	public int Main_intercomUnLock() throws RemoteException {
		return mMainJni.intercomUnLock();
	}

	@Override
	public void Main_intercomCloudPhoneState(int state) throws RemoteException {
		mMainJni.intercomCloudPhoneState(state);
	}

	@Override
	public int Main_scanqrOpendoorDreal(String qrString, int len) throws RemoteException {
		return mMainJni.scanqrOpendoorDreal(qrString, len);
	}

	@Override
	public String Main_getQRencode(String devid) throws RemoteException {
		return mMainJni.getQRencode(devid);
	}

	@Override
	public String Main_getDeviceInfoQR(int device) throws RemoteException {
		return mMainJni.getDeviceInfoQR(device);
	}

	@Override
	public int Main_comSendSetCardNum(int cardNum) throws RemoteException {
		return mMainJni.comSendSetCardNum(cardNum);
	}

	@Override
	public int Main_comSendSetLockParam(int type, int time) throws RemoteException {
		return mMainJni.comSendSetLockParam(type, time);
	}

	@Override
	public int Main_comSendOpenDoor() throws RemoteException {
		return mMainJni.comSendOpenDoor();
	}

	@Override
	public int Main_comSendReboot() throws RemoteException {
		return mMainJni.comSendReboot();
	}

	@Override
	public int Main_comSendDelayReboot(int time) throws RemoteException {
		return mMainJni.comSendDelayReboot(time);
	}

	@Override
	public int Main_stopFeeddogHeat() throws RemoteException {
		return mMainJni.stopFeeddogHeat();
	}

	@Override
	public int Main_sendFeeddogHeat() throws RemoteException {
		return mMainJni.sendFeeddogHeat();
	}

	@Override
	public int Main_comSendCardPwdCtrlReset() throws RemoteException {
		return mMainJni.comSendCardPwdCtrlReset();
	}

	@Override
	public int Main_driverPhyPwrCtrlReset() throws RemoteException {
		return mMainJni.driverPhyPwrCtrlReset();
	}

	@Override
	public int Main_driverFingerPwrCtrlReset() throws RemoteException {
		return mMainJni.driverFingerPwrCtrlReset();
	}

	@Override
	public int Main_driverSetTouchSens(int flag) throws RemoteException {
		return mMainJni.driverSetTouchSens(flag);
	}

	@Override
	public int Main_driverCcdLed(int flag) throws RemoteException {
		return mMainJni.driverCcdLed(flag);
	}

	@Override
	public int Main_driverCamLamp(int flag, int index) throws RemoteException {
		return mMainJni.driverCamLamp(flag, index);
	}

	@Override
	public int Main_driverWhiteLampChange(int start, int end) throws RemoteException {
		return mMainJni.driverWhiteLampChange(start, end);
	}

	@Override
	public int Main_resTouchKey() throws RemoteException {
		return mMainJni.resTouchKey();
	}

	@Override
	public int Main_ctrlTouchKeyLamp(int flag) throws RemoteException {
		return mMainJni.ctrlTouchKeyLamp(flag);
	}

	@Override
	public void Main_enableOpenCCD(int enable) throws RemoteException {
		mMainJni.enableOpenCCD(enable);
	}

	@Override
	public int Main_setSystemSleepState(int enable) throws RemoteException {
		return mMainJni.setSystemSleepState(enable);
	}

	@Override
	public int Main_resetOtgPhy() throws RemoteException {
		return mMainJni.resetOtgPhy();
	}

	@Override
	public int Main_addCard(String roomString, String cardString, int cardType, int roomNoState, String keyID,
							int startTime, int endTime, int lifecycle) throws RemoteException {
		return mMainJni.addCard(roomString, cardString, cardType, roomNoState, keyID, startTime, endTime, lifecycle);
	}

	@Override
	public int Main_delCard(String roomString, String cardString) throws RemoteException {
		return mMainJni.delCard(roomString, cardString);
	}

	@Override
	public int Main_delUserCard(String roomString) throws RemoteException {
		return mMainJni.delUserCard(roomString);
	}

	@Override
	public int Main_clearCard() throws RemoteException {
		return mMainJni.clearCard();
	}

	@Override
	public int Main_getCardCount() throws RemoteException {
		return mMainJni.getCardCount();
	}

	@Override
	public int Main_getCardFreeCount() throws RemoteException {
		return mMainJni.getCardFreeCount();
	}

	@Override
	public void Mian_setCardState(int state) throws RemoteException {
		mMainJni.setCardState(state);
	}

	@Override
	public void Main_dealAdvPassWord(String devno, String password) throws RemoteException {
		mMainJni.dealAdvPassWord(devno, password);
	}

	@Override
	public void Main_dealPassWord(String keyID, String devno, int result) throws RemoteException {
		mMainJni.dealPassWord(keyID, devno, result);
	}

	@Override
	public void Main_dealOtherPassWord(String devno, String password, int type) throws RemoteException {
		mMainJni.dealOtherPassWord(devno, password, type);
	}

	@Override
	public void Main_fingerStorageInitAdd(int fingerId, int valid, String roomString, byte[] fingerInfo) throws RemoteException {
		mMainJni.fingerStorageInitAdd(fingerId, valid, roomString, fingerInfo);
	}

	@Override
	public int Main_fingerAdd(String roomString) throws RemoteException {
		return mMainJni.fingerAdd(roomString);
	}

	@Override
	public void Main_fingerOperStop() throws RemoteException {
		mMainJni.fingerOperStop();
	}

	@Override
	public int Main_fingerDelUser(String roomString) throws RemoteException {
		return mMainJni.fingerDelUser(roomString);
	}

	@Override
	public void Main_fingerClear() throws RemoteException {
		mMainJni.fingerClear();
	}

	@Override
	public int Main_fingerGetCount() throws RemoteException {
		return mMainJni.fingerGetCount();
	}

	@Override
	public void Main_fingerSetDevnoDistinguish(String roomString) throws RemoteException {
		mMainJni.fingerSetDevnoDistinguish(roomString);
	}

	@Override
	public int Main_fingerGetWorkState() throws RemoteException {
		return mMainJni.fingerGetWorkState();
	}

	@Override
	public void Main_fingerSetFaceState(int state) throws RemoteException {
		mMainJni.fingerSetFaceState(state);
	}

	@Override
	public int Main_dealFaceEvent(String faceName, int faceLen, String keyID, float confidence, int snaptype, byte[] snapdata, int datalen) throws RemoteException {
		return mMainJni.dealFaceEvent(faceName, faceLen, keyID, confidence, snaptype, snapdata, datalen);
	}

	@Override
	public void Main_snapFacePhoto(String snapFile) throws RemoteException {
		mMainJni.snapFacePhoto(snapFile);
	}

	@Override
	public int Main_appSnapFaceAndReport(String faceName, String snapPath, float confidence) throws RemoteException {
		return mMainJni.appSnapFaceAndReport(faceName, snapPath, confidence);
	}

	@Override
	public void Main_facePicRegResult(int regCode, int regResult, String faceToken) throws RemoteException {
		mMainJni.facePicRegResult(regCode, regResult, faceToken);
	}

	@Override
	public void Main_faceAuthRegister(int faceType, FullDeviceNo object, String faceToken) throws RemoteException {
		mMainJni.faceAuthRegister(faceType, object, faceToken);
	}

	@Override
	public void Main_faceInfoQuery(String faceToken) throws RemoteException {
		mMainJni.faceInfoQuery(faceToken);
	}

	@Override
	public void Main_facePicCollectReg(FullDeviceNo object, String faceToken, int retsult) throws RemoteException {
		mMainJni.facePicCollectReg(object, faceToken, retsult);
	}

	@Override
	public void Main_faceMqttPicRegResult(int regCode, int regResult, String regMsgid) throws RemoteException {
		mMainJni.faceMqttPicRegResult(regCode, regResult, regMsgid);
	}

	@Override
	public int Main_faceHttpRecognize(String potoName) throws RemoteException {
		return mMainJni.faceHttpRecognize(potoName);
	}

	@Override
	public void Main_faceSnapStranger(String snapFile) throws RemoteException {
		mMainJni.faceSnapStranger(snapFile);
	}

	@Override
	public void Main_faceTempScanStart() throws RemoteException {
		mMainJni.faceTempScanStart();
	}

	@Override
	public void Main_setFaceType(int faceType) throws RemoteException {
		mMainJni.setFaceType(faceType);
	}

	@Override
	public void Main_setFaceBackUpState(int state) throws RemoteException {
		mMainJni.setFaceBackUpState(state);
	}

	@Override
	public String Main_getCloudSn() throws RemoteException {
		return mMainJni.getCloudSn();
	}

	@Override
	public int Main_getCloudState() throws RemoteException {
		return mMainJni.getCloudState();
	}

	@Override
	public int Main_getCloudReboot() throws RemoteException {
		return mMainJni.getCloudReboot();
	}

	@Override
	public int Main_getMlinkState() throws RemoteException {
		return mMainJni.getMlinkState();
	}
}
