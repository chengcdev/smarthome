package com.android.main;
import com.android.provider.NetworkHelp;
import com.android.provider.FullDeviceNo;

interface IMainService
{
	void Main_setFullDevNo(in FullDeviceNo object, String clsname);
	void Main_setSysNetParam(in  NetworkHelp object, String clsname);
	void Main_setRoomDest(in byte[] dest, int length);
	void Main_setDoorStateParam(int doorState, int doorAlarmOutput, int doorReportCenter);
	long Main_getRandomSeed();
    void Main_registerCenter(String softverString, String hardverString);
    void Main_refreshRegisterCenter();
    void Main_reqSynTime();
    int Main_formatExternalSdCard();

	int Main_intercomCallRoom(String roomNum);
	int Main_intercomCallCenter(int centerdevno, int exno);
	void Main_intercomHandDown();
	void Main_intercomMonitorStop();
	int Main_intercomPreviewStart(int state);
	void Main_intercomPreviewStop(int state);
	void Main_intercomSetAudioState(int state);
	int Main_intercomRtspStart(String url);
    void Main_intercomRtspStop();
    int Main_intercomUnLock();
    void Main_intercomCloudPhoneState(int state);

	int Main_scanqrOpendoorDreal(String qrString, int len);
	String Main_getQRencode(String devid);
	String Main_getDeviceInfoQR(int device);

	int Main_comSendSetCardNum(int cardNum);
    int Main_comSendSetLockParam(int type, int time);
    int Main_comSendOpenDoor();
    int Main_comSendReboot();
    int Main_comSendDelayReboot(int time);
    int Main_stopFeeddogHeat();
    int Main_sendFeeddogHeat();
    int Main_comSendCardPwdCtrlReset();
    int Main_driverPhyPwrCtrlReset();
    int Main_driverFingerPwrCtrlReset();
    int Main_driverSetTouchSens(int flag);
    int Main_driverCcdLed(int flag);
    int Main_driverCamLamp(int flag, int index);
    int Main_driverWhiteLampChange(int start, int end);
    int Main_resTouchKey();
    int Main_ctrlTouchKeyLamp(int flag);
    void Main_enableOpenCCD(int enable);
    int Main_setSystemSleepState(int enable);
    int Main_resetOtgPhy();

    int Main_addCard(String roomString, String cardString, int cardType,
                       int roomNoState, String keyID, int startTime, int endTime, int lifecycle);
    int Main_delCard(String roomString, String cardString);
    int Main_delUserCard(String roomString);
    int Main_clearCard();
    int Main_getCardCount();
    int Main_getCardFreeCount();
    void Mian_setCardState(int state);

    // 密码设置
    void Main_dealAdvPassWord(String devno, String password);
    void Main_dealPassWord(String keyID, String devno, int result);
    void Main_dealOtherPassWord(String devno, String password, int type);

    void Main_fingerStorageInitAdd(int fingerId, int valid, String roomString, in byte[] fingerInfo);
    int Main_fingerAdd(String roomString);
    void Main_fingerOperStop();
    int Main_fingerDelUser(String roomString);
    void Main_fingerClear();
    int Main_fingerGetCount();
    void Main_fingerSetDevnoDistinguish(String roomString);
    int Main_fingerGetWorkState();
    void Main_fingerSetFaceState(int state);
    int Main_dealFaceEvent(String faceName, int faceLen, String keyID, float confidence, int snaptype, in byte[] snapdata, int datalen);
    void Main_snapFacePhoto(String snapFile);
    int Main_appSnapFaceAndReport(String faceName, String snapPath, float confidence);
    void Main_setFaceType(int faceType);
    void Main_setFaceBackUpState(int state);

    void Main_facePicRegResult(int regCode, int regResult, String faceToken);
    void Main_faceAuthRegister(int faceType, in FullDeviceNo object, String faceToken);
    void Main_faceInfoQuery(String faceToken);
    void Main_facePicCollectReg(in FullDeviceNo object, String faceToken, int retsult);
    void Main_faceMqttPicRegResult(int regCode, int regResult, String regMsgid);
    int Main_faceHttpRecognize(String potoName);
    void Main_faceSnapStranger(String snapFile);
    void Main_faceTempScanStart();


    String Main_getCloudSn();
    int Main_getCloudState();
    int Main_getCloudReboot();
    int Main_getMlinkState();
}