package com.android.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.android.CommSysDef;
import com.android.Common;
import com.android.provider.FullDeviceNo;
import com.android.provider.NetworkHelp;
import com.android.provider.RoomSubDest;
import com.mili.smarthome.tkj.BuildConfig;
import com.mili.smarthome.tkj.appfunc.BuildConfigHelper;
import com.mili.smarthome.tkj.dao.FingerDao;
import com.mili.smarthome.tkj.dao.UserInfoDao;
import com.mili.smarthome.tkj.dao.param.AlarmParamDao;
import com.mili.smarthome.tkj.dao.param.EntranceGuardDao;
import com.mili.smarthome.tkj.dao.param.FaceParamDao;
import com.mili.smarthome.tkj.dao.param.ParamDao;
import com.mili.smarthome.tkj.dao.param.SnapParamDao;
import com.mili.smarthome.tkj.dao.param.VolumeParamDao;
import com.mili.smarthome.tkj.entities.FingerModel;
import com.mili.smarthome.tkj.entities.param.SnapParam;
import com.mili.smarthome.tkj.entities.userInfo.UserCardInfoModels;
import com.mili.smarthome.tkj.utils.EthernetUtils;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.SysTimeSetUtils;
import com.mili.smarthome.tkj.utils.SystemSetUtils;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class MainData {

    private static final String tag = "MainData";
    private FullDeviceNo mFullDeviceNo = null;
    private NetworkHelp mNetworkHelp = null;
    public Context mContext;
    public static MainJni mMainJni = null;
    private Handler mHandler = null;
    //    private boolean isfristStart = false;
    private UserInfoDao userInfoDao;


    public MainData(Context c, MainJni Jni) {
        mContext = c;
        mMainJni = Jni;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public boolean Start() {

        LogUtils.d(" ============= MainData start =============");
        mFullDeviceNo = new FullDeviceNo(mContext);
//        isfristStart = Common.isfriststart();
        startReceiver(mContext);

        //默认时间
        SysSetTime();
        //默认声音
        SysSetVolume();
        //编号规则设置
        SysSetDevNo();
        //网络参数设置
        SysSetNetParam();
        //锁属性设置
        SysLockStateParam();
        //门状态设置
        SysDoorStateParam();
        //强行开门报警
        SysForcedOpenDoor();
        //拍照参数
        SysCameraParam();
        //设置人脸
        SysSetUnnifiedFaceParam();
        //灵敏度参数
        SysTouchSens();
        //卡位数
        SysCardNums();
        //是否启用了指纹
        SysFinger();
        //是否启用了人脸
        SysFace();
        //设置事件上报平台
        SysEventPlatform();
        //设置卡有效性判断
        SysCardValid(BuildConfig.isEnabledCardValid);
        //初始化添加卡存储
        SysAddCard();
        //初始化指纹存储
        SysAddFinger();

//        if (isfristStart) {
//            try {
//                Common.setisfriststart();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        return true;
    }

    private void SysFace() {
        mMainJni.isUseFaceModular(FaceParamDao.getFaceRecognition());
    }

    private void SysFinger() {
        mMainJni.isUseFingerModular(EntranceGuardDao.getFingerprint());
    }

    private void SysSetVolume() {
        //音量设置
        SystemSetUtils.setEnableKeyVoice(VolumeParamDao.getKeyVolume() != 0);
        SystemSetUtils.setCallVolume(VolumeParamDao.getCallVolume());
    }

    private void SysAddCard() {
        if (userInfoDao == null) {
            userInfoDao = new UserInfoDao();
        }
        List<UserCardInfoModels> allCard = userInfoDao.queryAllCards();
        if (allCard != null) {
            for (UserCardInfoModels userCardInfoModels : allCard) {
                String roomNo = userCardInfoModels.getRoomNo();
                String cardNo = userCardInfoModels.getCardNo();
                int cardType = userCardInfoModels.getCardType();
                int roomNoState = userCardInfoModels.getRoomNoState();
                String keyID = userCardInfoModels.getKeyID();
                int startTime = userCardInfoModels.getStartTime();
                int endTime = userCardInfoModels.getEndTime();
                int lifecycle = userCardInfoModels.getLifecycle();
                mMainJni.addCard(roomNo, cardNo, cardType, roomNoState, keyID, startTime, endTime, lifecycle);
            }
        }
    }

    private void SysAddFinger() {
        FingerDao fingerDao = new FingerDao();
        List<FingerModel> fingerList = fingerDao.queryAll();
        for (FingerModel fingerModel : fingerList) {
            mMainJni.fingerStorageInitAdd(fingerModel.getFingerId(), fingerModel.getValid(), fingerModel.getRoomNo(), fingerModel.getFingerInfo());
        }
    }

    private void SysCardNums() {
        mMainJni.comSendSetCardNum(ParamDao.getCardNoLen());
    }

    private void SysCardValid(int enable) {
        mMainJni.setCardValid(enable);
    }
    private void SysTouchSens() {
        if (!BuildConfigHelper.isK3()) {
            return;
        }
        mMainJni.driverSetTouchSens(2 - ParamDao.getTouchSensitivity());
    }

    private void SysCameraParam() {

        SnapParam snapParam = SnapParamDao.getSnapParam();
        int cameraParam = snapParam.getVisitorSnap();
        int errorCamera = snapParam.getErrorPwdSnap();
        int hijackingCamera = snapParam.getHijackPwdSnap();
        int callCenterPhoto = snapParam.getCallCenterSnap();
        int faceOpenPhoto = snapParam.getFaceOpenSnap();
        int fingerOpenPhoto = snapParam.getFingerOpenSnap();
        int cardOpenPhoto = snapParam.getCardOpenSnap();
        int pwdOpenPhoto = snapParam.getPwdOpenSnap();
        int qrCodeOpenPhoto = snapParam.getQrcodeOpenSnap();
        int faceStrangerPhoto = snapParam.getFaceStrangerSnap();

        Log.e("SysCameraParam", cameraParam + " " + errorCamera + " " + hijackingCamera + " " +
                callCenterPhoto + " " + faceOpenPhoto + " " + fingerOpenPhoto + " " + cardOpenPhoto + " " +
                pwdOpenPhoto + " " + qrCodeOpenPhoto + " " + faceStrangerPhoto);

        int allParam = 0;
        allParam = allParam | cameraParam;
        allParam = allParam | (errorCamera << 1);
        allParam = allParam | (hijackingCamera << 2);
        allParam = allParam | (callCenterPhoto << 3);
        allParam = allParam | (faceOpenPhoto << 4);
        allParam = allParam | (fingerOpenPhoto << 5);
        allParam = allParam | (cardOpenPhoto << 6);
        allParam = allParam | (pwdOpenPhoto << 7);
        allParam = allParam | (qrCodeOpenPhoto << 8);
        allParam = allParam | (faceStrangerPhoto << 9);

        Log.e("SysCameraParam", "allParam = " + allParam);

        mMainJni.setSnapParam(allParam);
    }

    private  void SysSetUnnifiedFaceParam(){
        int cloudTalk = EntranceGuardDao.getCloudTalk();
        int faceRecognition = FaceParamDao.getFaceModule();
        int param = 0;
        param = param | faceRecognition;
        param = param | (cloudTalk << 1);

        LogUtils.d(" cloudTalk = " + cloudTalk + ", faceRecognition = " + faceRecognition + ", param = " + param);
        mMainJni.setUnnifiedFaceParam(param);
    }

    private void SysForcedOpenDoor() {
        mMainJni.forceOpenDoorAlarm(AlarmParamDao.getForceOpen());
    }

    private void SysLockStateParam() {
        //设置锁类型
        mMainJni.comSendSetLockParam(EntranceGuardDao.getOpenLockType(), EntranceGuardDao.getOpenLockTime());
    }

    private void SysDoorStateParam() {
        mMainJni.setDoorStateParam(EntranceGuardDao.getDoorStateCheck(), EntranceGuardDao.getAlarmOut(), EntranceGuardDao.getUpdateCenter());
    }

    private void SysSetTime() {
        GregorianCalendar calendar = new GregorianCalendar();
        int year = calendar.get(Calendar.YEAR);
        if (year < 2018) {
            SysTimeSetUtils.setTimeZone(mContext, "Asia/Shanghai");
            SysTimeSetUtils.setSysDate(mContext, 2018, 8, 25);
            SysTimeSetUtils.setSysTime(mContext, 5, 0, 0);
        }
    }

//    public void SysCreateDir() {
//        Log.d(tag, "SysCreateDir");
//        Common.isExist(CommStorePathDef.USERDATA_PATH);
//        Common.isExist(CommStorePathDef.PARAM_PATH);
//        Common.isExist(CommStorePathDef.MULTIMEDIA_DIR_PATH);
//        Common.isExist(CommStorePathDef.INFO_DIR_PATH);
//        Common.isExist(CommStorePathDef.LOGO_DIR_PATH);
//    }

    public void SysSetRoomDest() {
        RoomSubDest mRoomSubDest = new RoomSubDest(mContext);
        String dest = mRoomSubDest.getSubDestDevNumber();
        byte[] destbyte = null;
        try {
            destbyte = dest.getBytes("GB2312");
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        mMainJni.setRoomDest(destbyte, destbyte.length);
    }

    public void SysSetDevNo() {
        if (mFullDeviceNo != null) {
            mFullDeviceNo.getvalue();
            mMainJni.setFullDevNo(mFullDeviceNo, "FullDeviceNo");
            SysSetRoomDest();
        }
    }

    public void SysSetNetParam() {
        mNetworkHelp = new NetworkHelp();
        if (mNetworkHelp != null) {
            mMainJni.setSysNetParam(mNetworkHelp, "NetworkHelp");
        }

        if (BuildConfigHelper.isPad()) {
            return;
        }

        EthernetUtils.NetParam netParam = new EthernetUtils.NetParam();
        netParam.setIpAddr(Common.intToIP(mNetworkHelp.getIp()));
        netParam.setGateway(Common.intToIP(mNetworkHelp.getDefaultGateway()));
        netParam.setNetMask(Common.intToIP(mNetworkHelp.getSubNet()));
        netParam.setDns1(Common.intToIP(mNetworkHelp.getDNS1()));
        netParam.setDns2(Common.intToIP(mNetworkHelp.getDNS2()));
        //设置系统网络参数
        EthernetUtils.setStaticIpConfiguration(mContext, netParam);
    }

    private void SysEventPlatform() {
        mMainJni.setEventPlatform(ParamDao.getEventPlatform());
    }

    public class SysReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            if (mHandler != null) {
                Message m = mHandler.obtainMessage(1, 1, 1, intent);
                mHandler.sendMessage(m);
            }
        }

    }

    class SysHandler extends Handler {
        public SysHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Intent intent = (Intent) msg.obj;
            if (intent == null) {
                super.handleMessage(msg);
                return;
            }

            String action = intent.getAction();
            Log.d(tag, action);
            switch (action) {
//                case CommSysDef.BROADCAST_MKDIR:
//                    SysCreateDir();
//                    break;
                case CommSysDef.BROADCAST_NAME_IP:
                    SysSetNetParam();
                    mMainJni.refreshRegisterCenter();
                    break;
                case CommSysDef.BROADCAST_DEVICENUMBER:
                    SysSetDevNo();
                    mMainJni.refreshRegisterCenter();
                    break;
                case CommSysDef.BROADCAST_DEVICENORULE:
                    SysSetDevNo();
                    break;
                case CommSysDef.BROADCAST_DOOR_STATE:
                    SysDoorStateParam();
                    break;
                case CommSysDef.BROADCAST_LOCK_STATE:
                    SysLockStateParam();
                    break;
                case CommSysDef.BROADCAST_FORCEDOPENDOOR:
                    SysForcedOpenDoor();
                    break;
                case CommSysDef.BROADCAST_CAMERAPARAM:
                    SysCameraParam();
                    break;
                case CommSysDef.BROADCAST_TOUCHSENS:
                    SysTouchSens();
                    break;
                case CommSysDef.BROADCAST_CARDNUMS:
                    SysCardNums();
                    break;
                case CommSysDef.BROADCAST_ADD_CARD:
                    SysAddCard();
                    break;
                case CommSysDef.BROADCAST_ENABLE_FACE:
                    SysFace();
                    break;
                case CommSysDef.BROADCAST_ENABLE_FINGER:
                    SysFinger();
                    break;
                case CommSysDef.BROADCAST_EVENT_PLATFORM:
                    SysEventPlatform();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }

        }

    }

    private void startReceiver(Context context) {
        String[] broadcastList = {CommSysDef.BROADCAST_MKDIR, CommSysDef.BROADCAST_NAME_IP, CommSysDef.BROADCAST_DEVICENUMBER,
                CommSysDef.BROADCAST_DEVICENORULE, CommSysDef.BROADCAST_DOOR_STATE, CommSysDef.BROADCAST_LOCK_STATE,
                CommSysDef.BROADCAST_FORCEDOPENDOOR, CommSysDef.BROADCAST_CAMERAPARAM, CommSysDef.BROADCAST_TOUCHSENS, CommSysDef.BROADCAST_CARDNUMS,
                CommSysDef.BROADCAST_ADD_CARD, CommSysDef.BROADCAST_ENABLE_FACE, CommSysDef.BROADCAST_ENABLE_FINGER,
                CommSysDef.BROADCAST_EVENT_PLATFORM};
        mHandler = new SysHandler(Looper.getMainLooper());
        SysReceiver mSysLinkReceiver = new SysReceiver();
        IntentFilter filter = new IntentFilter();
        for (String s : broadcastList) {
            filter.addAction(s);
        }
        context.registerReceiver(mSysLinkReceiver, filter);
    }

}

