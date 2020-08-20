
package com.android.main;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.CommSysDef;
import com.android.CommTypeDef;
import com.android.Common;
import com.android.IntentDef;
import com.mili.smarthome.tkj.BuildConfig;
import com.mili.smarthome.tkj.auth.AuthManage;

import static com.android.IntentDef.PubIntentTypeE.Singlecgip_TouchKeyReport;

public class MainJni {

    private static final String tag = "MainJni";
    private static Context mContext;
    private static IntentDef.OnNetCommDataReportListener mMainListerner = null;
    private static IntentDef.OnNetCommDataReportListener mInterCommListener = null;
    private static IntentDef.OnNetCommDataReportListener mInfoCommListener = null;
    private static IntentDef.OnNetCommDataReportListener mSinglechipListener = null;
    private static IntentDef.OnVideoDataReportListener mInterVideoCallBKListener = null;
    private static IntentDef.OnNetCommDataReportListener mPassWordListener = null;
    private static IntentDef.OnNetCommDataReportListener mMultimediaListener = null;

    public static void setmMainListerner(
            IntentDef.OnNetCommDataReportListener mMainListerner) {
        MainJni.mMainListerner = mMainListerner;
    }

    public static void setmInterCommListener(
            IntentDef.OnNetCommDataReportListener mInterCommListener) {
        MainJni.mInterCommListener = mInterCommListener;
    }

    public static void setmInfoCommListener(
            IntentDef.OnNetCommDataReportListener mInfoCommListener) {
        MainJni.mInfoCommListener = mInfoCommListener;
    }

    public static void setmPassWordListener(
            IntentDef.OnNetCommDataReportListener mPassWordListener) {
        MainJni.mPassWordListener = mPassWordListener;
    }

    public static void setmSinglechipListener(
            IntentDef.OnNetCommDataReportListener mSinglechipListener) {
        MainJni.mSinglechipListener = mSinglechipListener;
    }

    public static void setmMultimediaListener(
            IntentDef.OnNetCommDataReportListener mMultimediaListener) {
        MainJni.mMultimediaListener = mMultimediaListener;
    }

    public static void setmInterVideoDataCallBKListener(
            IntentDef.OnVideoDataReportListener mInterVideoDataCallBKListener) {
        MainJni.mInterVideoCallBKListener = mInterVideoDataCallBKListener;
    }

    public MainJni(Context c) {
        mContext = c;
    }

    public static void mainCallbackProc(int infoType, byte[] data, int dataLen) {
        if (mContext == null)
            return;

        String moduleString = "";
        int sysType = (infoType >> 8) & 0xff;

        switch (sysType) {
            case CommTypeDef.SubSysCode.SSC_PUBLIC:
                moduleString = IntentDef.MODULE_MAIN;
                if (MainJni.mMainListerner != null) {
                    MainJni.mMainListerner.OnDataReport(moduleString, infoType, data);
                }
                break;

            case CommTypeDef.SubSysCode.SSC_INTERPHONE:
                moduleString = IntentDef.MODULE_INTERCOMM;
                if (MainJni.mInterCommListener != null) {
                    MainJni.mInterCommListener.OnDataReport(moduleString, infoType, data);
                }
                break;

            case CommTypeDef.SubSysCode.SSC_INFO:
                moduleString = IntentDef.MODULE_INFO;
                if (MainJni.mInfoCommListener != null) {
                    MainJni.mInfoCommListener.OnDataReport(moduleString, infoType, data);
                }
                break;

            case CommTypeDef.SubSysCode.SSC_PASSWORD:
                moduleString = IntentDef.MODULE_PASSWORD;
                if (MainJni.mPassWordListener != null) {
                    MainJni.mPassWordListener.OnDataReport(moduleString, infoType, data);
                }
                break;

            case CommTypeDef.SubSysCode.SSC_SINGLECHIP:
                if (!AuthManage.isAuth() && (infoType != Singlecgip_TouchKeyReport)) {
                    return;
                }

                moduleString = IntentDef.MODULE_SINGLECHIP;
                if (MainJni.mSinglechipListener != null) {
                    MainJni.mSinglechipListener.OnDataReport(moduleString, infoType, data);
                }
                break;

            case CommTypeDef.SubSysCode.SSC_fINGER:
                if (!AuthManage.isAuth()) {
                    return;
                }
                moduleString = IntentDef.MODULE_FINGER;
                break;

            case CommTypeDef.SubSysCode.SSC_SCANQR:
                moduleString = IntentDef.MODULE_SCANQR;
                break;

            case CommTypeDef.SubSysCode.SSC_MULTIMEDIA:
                moduleString = IntentDef.MODULE_MULTIMEDIA;
                if (MainJni.mMultimediaListener != null) {
                    MainJni.mMultimediaListener.OnDataReport(moduleString, infoType, data);
                }
                break;

            default:
                return;
        }

        if (sysType != CommTypeDef.SubSysCode.SSC_PASSWORD) {
            Intent intent = new Intent(moduleString);
            intent.putExtra(IntentDef.INTENT_NETCOMM_TYPE, infoType);
            if (dataLen > 0)
                intent.putExtra(IntentDef.INTENT_NETCOMM_DATA, data);
            Common.SendBroadCast(mContext, intent);
        }
    }

    public static void mainVideoCallbackProc(byte[] data, int datalen, int width, int height, int type) {
        if (mContext == null)
            return;

        if (data.length <= 0 || datalen <= 0 || width <= 0 || height <= 0) {
            Log.e(tag, "mainVideoCallbackProc the data is error!!!");
            return;
        }

        if (MainJni.mInterVideoCallBKListener != null) {
            MainJni.mInterVideoCallBKListener.OnVideoDataReport(data, datalen, width, height, type);
        }
    }

    static {
        System.loadLibrary(CommSysDef.LibMedia);
        if (BuildConfig.DevType == 2004) {
            System.loadLibrary(CommSysDef.LibDPXJniName);
        } else if (BuildConfig.DevType == 2003) {
            System.loadLibrary(CommSysDef.LibMTKJniName);
        } else {
            System.loadLibrary(CommSysDef.LibRKJniName);
        }
    }

    public native void logicInitJni(int auth);

    public native void setCallback(Object object, String funcName, int devtype);

    public native void setVideoCallback(Object object, String funcName);

    public native void setFullDevNo(Object object, String clsname);

    public native int setSysNetParam(Object object, String clsname);

    public native void setRoomDest(byte[] dest, int length);

    public native void setDoorStateParam(int doorState, int doorAlarmOutput, int doorReportCenter);

    public native void forceOpenDoorAlarm(int flag);

    public native void isUseFingerModular(int flag);

    public native void isUseFaceModular(int flag);

    public native void setSnapParam(int flag);

    public native long getRandomSeed();

    public native void registerCenter(String softverString, String hardverString);

    public native void refreshRegisterCenter();

    public native void reqSynTime();

    public native int intercomCallRoom(String roomNum);

    public native int intercomCallCenter(int centerdevno, int exno);

    public native void intercomHandDown();

    public native void intercomMonitorStop();

    public native int intercomPreviewStart(int state);

    public native void intercomPreviewStop(int state);

    public native void intercomSetAudioState(int state);

    public native int intercomRtspStart(String url);

    public native void intercomRtspStop();

    public native int intercomUnLock();

    public native void intercomCloudPhoneState(int state);

    public native int scanqrOpendoorDreal(String qrString, int len);

    public native String getQRencode(String devid);

    public native String getDeviceInfoQR(int device);

    public native int comSendSetCardNum(int cardNum);

    public native int comSendSetLockParam(int type, int time);

    public native int comSendOpenDoor();

    public native int comSendReboot();

    public native int comSendDelayReboot(int time);

    public native int stopFeeddogHeat();

    public native int sendFeeddogHeat();

    public native int comSendCardPwdCtrlReset();

    public native int driverPhyPwrCtrlReset();

    public native int driverFingerPwrCtrlReset();

    public native int driverSetTouchSens(int flag);

    public native int driverCcdLed(int flag);

    public native int driverCamLamp(int flag, int index);

    public native int driverWhiteLampChange(int start, int end);

    public native int resTouchKey();

    public native int ctrlTouchKeyLamp(int flag);

    public native void enableOpenCCD(int enable);

    public native int setSystemSleepState(int enable);

    public native int resetOtgPhy();

    // 卡接口
    public native int addCard(String roomString, String cardString, int cardType, int roomNoState, String keyID,
                              int startTime, int endTime, int lifecycle);

    public native int delCard(String roomString, String cardString);

    public native int delUserCard(String roomString);

    public native int clearCard();

    public native int getCardCount();

    public native int getCardFreeCount();

    public native void setCardState(int state);

    public native void setCardValid(int enable);

    // 密码设置
    public native void dealAdvPassWord(String devno, String password);

    public native void dealPassWord(String keyID, String devno, int result);

    public native void dealOtherPassWord(String devno, String password, int type);

    public native void fingerStorageInitAdd(int fingerId, int valid, String roomString, byte[] fingerInfo);

    public native int fingerAdd(String roomString);

    public native void fingerOperStop();

    public native int fingerDelUser(String roomString);

    public native void fingerClear();

    public native int fingerGetCount();

    public native void fingerSetDevnoDistinguish(String roomString);

    public native int fingerGetWorkState();

    public native void fingerSetFaceState(int state);

    public native void multimediaDownloadResult(int result);

    public native void multimediaDelResult(int result);

    public native long getSystemFreeMemory(int flag);

    public native int formatExternalSdCard();

    public native int dealFaceEvent(String faceName, int faceLen, String keyID, float confidence, int snaptype, byte[] snapdata, int datalen);

    public native void snapFacePhoto(String snapFile);

    public native void setUnnifiedFaceParam(int param);

    public native int appSnapFaceAndReport(String faceName, String snapPath, float confidence);

    public native void facePicRegResult(int regCode, int regResult, String faceToken);

    public native void faceAuthRegister(int faceType, Object object, String faceToken);

    public native void faceInfoQuery(String faceToken);

    public native void facePicCollectReg(Object object, String faceToken, int retsult);

    public native void setFaceType(int faceType);

    public native void setFaceBackUpState(int state);

    public native void faceMqttPicRegResult(int regCode, int regResult, String regMsgid);

    public native int faceHttpRecognize(String photoName);

    public native void faceSnapStranger(String snapFile);

    public native void faceTempScanStart();

    public native String getCloudSn();

    public native int getCloudState();

    public native int getCloudReboot();

    public native int setEventPlatform(int platform);
	public native int getMlinkState();
}
