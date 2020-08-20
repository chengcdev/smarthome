package com.android.client;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.android.CommSysDef;
import com.android.Common;
import com.android.IntentDef;
import com.android.interf.IBodyInductionListener;
import com.android.interf.ICardNoListener;
import com.android.interf.ICardReaderListener;
import com.android.interf.ICardStateListener;
import com.android.interf.IDoorAlarmListener;
import com.android.interf.IKeyEventListener;
import com.android.main.MainCommDefind;
import com.mili.smarthome.tkj.BuildConfig;
import com.mili.smarthome.tkj.utils.LogUtils;

/**
 * Created by Administrator on 2019/1/2 0002.
 */

public class SetDriverSinglechipClient extends BaseClient implements IntentDef.OnNetCommDataReportListener {

    private final static String TAG = "DriverSinglechipClient";
    private Context mContext = null;

    private static final SetDriverSinglechipClient mSetDriverSinglechipClient = new SetDriverSinglechipClient();
    private ICardNoListener mCardNoListener;
    private ICardStateListener cardStateListener;
    private ICardReaderListener mCardReaderListener;
    private IKeyEventListener mKeyEventListener;
    private IDoorAlarmListener mDoorAlarmListener;
    private IBodyInductionListener mBodyInductionListener;

    private static boolean systemSleepState = true; //false:在关屏状态  true:在开屏状态

    public static SetDriverSinglechipClient getInstance() {
        return mSetDriverSinglechipClient;
    }

    public SetDriverSinglechipClient() {
    }

    public SetDriverSinglechipClient(Context context) {
        super(context);
        // 设置界面回调接口
        mContext = context;
        // 启动broadcast接收
        String[] list = new String[]{IntentDef.MODULE_SINGLECHIP};
        startReceiver(context, list);
        // 启动服务
        StartIPC_Main(context);
        // 设置回调接口
        setmDataReportListener(this);
    }

    public void stopSetDriverSinglechipClient() {
        if (mContext == null) {
            return;
        }
        stopReceiver(mContext, IntentDef.MODULE_SINGLECHIP);
        StopIPC(mContext, CommSysDef.SERVICE_NAME_MAIN, IntentDef.MODULE_SINGLECHIP);
    }

    /*************************************************
     Function			:    	setCardNum
     Description		:		设置卡号位数
     Input				:		cardNum 6位0x06  8位0x08
     Output			:		无
     Return			:		0 成功  -1 失败
     Others			:		无
     *************************************************/
    public int setCardNum(int cardNum) {
        if (null == mMainService) {
            Log.d(TAG, "setCardNum: mMainService is null....");
            return -1;
        }
        try {
            return mMainService.Main_comSendSetCardNum(cardNum);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*************************************************
     Function			:    	setLockParam
     Description		:		锁参数信息设置
     Input				:
     1.locktype		  	锁类型：0:常闭;1:常开	  默认: 常开
     2.locktime		  	锁时长：单位s;0/3/6/9s	  默认: 3s
     Output			:
     Return			:		0成功 -1失败
     Others			:		无
     *************************************************/
    public int setLockParam(int locktype, int locktime) {
        if (null == mMainService) {
            Log.d(TAG, "setLockParam: mMainService is null....");
            return -1;
        }
        try {
            return mMainService.Main_comSendSetLockParam(locktype, locktime);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*************************************************
     Function			:    	comSendOpenDoor
     Description		:		发送开锁命令
     Input				:		无
     Output			:		无
     Return			:		0 成功  -1 失败
     Others			:		无
     *************************************************/
    public int openDoor() {
        if (null == mMainService) {
            Log.d(TAG, "openDoor: mMainService is null....");
            return -1;
        }
        try {
            return mMainService.Main_comSendOpenDoor();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*************************************************
     Function			:    	rebootSystem
     Description		:		立即重启命令
     Input				:
     Output			:		无
     Return			:		无
     Others			:		无
     *************************************************/
    public int rebootSystem() {
        if (null == mMainService) {
            Log.d(TAG, "rebootSystem: mMainService is null....");
            return -1;
        }
        try {
            return mMainService.Main_comSendReboot();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*************************************************
     Function		:       delayTimeRebootSystem
     Description	:	    发送延迟一段时间后没收到喂狗命令重启设备命令
     Input			:       delayTime:延迟时长，单位秒
     Output			:		无
     Return			:		无
     Others			:		无
     *************************************************/
    public int delayTimeRebootSystem(int delayTime) {
        if (null == mMainService) {
            Log.d(TAG, "delayTimeRebootSystem: mMainService is null....");
            return -1;
        }
        try {
            return mMainService.Main_comSendDelayReboot(delayTime);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*************************************************
     Function:		stopFeeddogReboot
     Description:  停止喂狗重启
     Input: 		无
     Output:		无
     Return:		无
     Others:
     *************************************************/
    public int stopFeeddogReboot() {
        if (null == mMainService) {
            Log.d(TAG, "stopFeeddogReboot: mMainService is null....");
            return -1;
        }
        try {
            return mMainService.Main_stopFeeddogHeat();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*************************************************
     Function:		sendFeedDogHeat
     Description:  喂狗接口，每5秒喂一次
     Input: 		无
     Output:		无
     Return:		无
     Others:
     *************************************************/
    public int sendFeedDogHeat() {
        if (null == mMainService) {
            Log.d(TAG, "sendFeedDogHeat: mMainService is null....");
            return -1;
        }
        try {
//            Log.d(TAG, "sendFeedDogHeat: ...............");
            return mMainService.Main_sendFeeddogHeat();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*************************************************
     Function			:    	cardPwdCtrl
     Description		:		复位刷卡模块
     Input				:
     Output			:		无
     Return			:		0 成功  -1 失败
     Others			:		无
     *************************************************/
    public int cardPwdCtrlReset() {
        if (null == mMainService) {
            Log.d(TAG, "cardPwdCtrlReset: mMainService is null....");
            return -1;
        }
        try {
            return mMainService.Main_comSendCardPwdCtrlReset();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*************************************************
     Function:		phyPwrCtrlReset
     Description: 	复位以太网
     Input:
     Output:		无
     Return:		成功=0，其它值失败
     Others:
     *************************************************/
    public int phyPwrCtrlReset() {
        if (null == mMainService) {
            Log.d(TAG, "stopFeeddogReboot: mMainService is null....");
            return -1;
        }
        try {
            return mMainService.Main_driverPhyPwrCtrlReset();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*************************************************
     Function:		fingerPwrCtrlReset
     Description: 复位指纹模块
     Input:
     Output:		无
     Return:		0:成功 -1:失败
     Others:
     *************************************************/
    public int fingerPwrCtrlReset() {
        if (null == mMainService) {
            Log.d(TAG, "fingerPwrCtrlReset: mMainService is null....");
            return -1;
        }
        try {
            return mMainService.Main_driverFingerPwrCtrlReset();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /***********************************************
     Function Name:	setTouchSens
     Function:	    设置触摸按键的灵敏度
     Input:
     1.sens:灵敏度等级	0:低，1:中，2:高
     Output:	none
     Return:	none
     Discripton:	none
     ************************************************/
    public int setTouchSens(int flag) {
        if (null == mMainService) {
            Log.d(TAG, "setTouchSens: mMainService is null....");
            return -1;
        }
        try {
            return mMainService.Main_driverSetTouchSens(flag);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*************************************************
     Function: 	ctrlCcdLed
     Description:	摄像头红外补光灯控制
     Input:
     1.打开	0:关闭
     Output:		无
     Return:		0:成功 -1:失败
     Others:
     *************************************************/
    public int ctrlCcdLed(int flag) {
        if (null == mMainService) {
            Log.d(TAG, "setTouchSens: mMainService is null....");
            return -1;
        }
        try {
            return mMainService.Main_driverCcdLed(flag);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*************************************************
     Function:		ctrlCamLamp
     Description:  摄像头白光补光灯控制
     Input:
                    flag:0x01：暗到亮渐变	0x02：亮到暗渐变 0x03：固定亮度 0x04:灭
     Output:		无
     Return:		0:成功 -1:失败
     Others:
     *************************************************/
    public int ctrlCamLamp(int flag) {
        if (null == mMainService) {
            Log.d(TAG, "ctrlCamLamp: mMainService is null....");
            return -1;
        }
        int index = 0;
        int cmd = flag;
        try {
            if (flag == 0x03) {
                index = 0x0;
            } else if (flag == 0x04) {
                index = 0xff;
                cmd = 0x03;
            }

            if (cmd == 0x01 || cmd == 0x02 || cmd == 0x03) {
                return mMainService.Main_driverCamLamp(cmd, index);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*************************************************
     Function:		ctrlCamLampChange
     Description:  白光补光灯板渐变亮度控制
     Input:     start:亮度前值	end:亮度后值
     Output:		无
     Return:		0:成功 -1:失败
     Others:        亮度前值1B：0XFF~0X00对应0~100%的占空比
                     亮度后值1B：0XFF~0X00对应0~100%的占空比
                     接口说明：补光灯板将从亮度前值渐变到亮度后值
     *************************************************/
    public int ctrlCamLampChange(int start, int end) {
        if (null == mMainService) {
            Log.d(TAG, "ctrlCamLampChange: mMainService is null....");
            return -1;
        }
        try {
            if (start < 0 || start > 0xff || end < 0 || end > 0xff) {
                return 0;
            }

            return mMainService.Main_driverWhiteLampChange(start, end);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*************************************************
     Function:		ctrlCamLamp
     Description:  复位感应按键
     Input:
     Output:		无
     Return:		0:成功 -1:失败
     Others:
     *************************************************/
    public int resTouchKeyModular() {
        if (null == mMainService) {
            Log.d(TAG, "ctrlCamLamp: mMainService is null....");
            return -1;
        }
        try {
            return mMainService.Main_resTouchKey();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*************************************************
     Function:		ctrlCamLamp
     Description:  按键背光控制
     Input:         flag:关：0	开：1
     Output:		无
     Return:		0:成功 -1:失败
     Others:
     *************************************************/
    public int ctrlTouchKeyLampState(int flag) {
        if (null == mMainService) {
            Log.d(TAG, "ctrlCamLamp: mMainService is null....");
            return -1;
        }
        try {
            return mMainService.Main_ctrlTouchKeyLamp(flag);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*************************************************
     Function:		enableOpenCCD
     Description:   设置是否开启红外补光灯
     Input:         enable:关：0	开：1
     Output:		无
     Return:		无
     Others:        无
     *************************************************/
    public void ctrlOpenCCD(int enable) {
        if (null == mMainService) {
            return;
        }
        try {
            mMainService.Main_enableOpenCCD(enable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /*************************************************
     Function:		setSystemSleep
     Description:   设置系统是否休眠
     Input:         enable:关：0	开：1
     Output:		无
     Return:		无
     Others:        只在x1600方案上使用
     *************************************************/
    public int setSystemSleep(int enable) {
        if (BuildConfig.DevType == MainCommDefind.DEVICE_TYPE_K4_X1600_REL) {
            if (null == mMainService) {
                return -1;
            }
            try {
                if (enable == 0) {
                    systemSleepState = false;
                    return mMainService.Main_setSystemSleepState(0);
                } else {
                    systemSleepState = true;
                    return mMainService.Main_setSystemSleepState(1);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    /*************************************************
     Function:		getSystemSleep
     Description:   获取系统是否休眠
     Input:         enable:关：0	开：1
     Output:		无
     Return:		false:在关屏状态  true:在开屏状态
     Others:        只在x1600方案上使用
     *************************************************/
    public boolean getSystemSleep() {
        if (BuildConfig.DevType == MainCommDefind.DEVICE_TYPE_K4_X1600_REL) {
            return systemSleepState;
        }
        return true;
    }

    /*************************************************
     Function:		resetOtgPhy
     Description:   复位OTG
     Output:		无
     Return:
     Others:
     *************************************************/
    public int resetOtgPhy() {
        if (null == mMainService) {
            Log.d(TAG, "ctrlCamLamp: mMainService is null....");
            return -1;
        }
        try {
            return mMainService.Main_resetOtgPhy();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void OnDataReport(String action, int type, byte[] data) {
        Log.d(TAG, "OnDataReport: action=" + action + ", type=" + type+">>>>>data："+ data);
        if (!action.equals(IntentDef.MODULE_SINGLECHIP)) {
            return;
        }

        switch (type) {
            case IntentDef.PubIntentTypeE.Singlecgip_AddCard:   //处于设置卡状态回调
                // 返回卡号字符串
                String cardString = new String (data);
                LogUtils.w(TAG+ "   OnDataReport: cardString=" + cardString);
                if (mCardNoListener != null) {
                    mCardNoListener.onCardNo(cardString);
                }
                break;

            case IntentDef.PubIntentTypeE.Singlecgip_Face:  // 处于人脸刷卡编辑状态回调
                int isValid = Common.bytes2int(data,0);     // 0：有效卡     1：无效卡
                int cardId = Common.bytes2int(data,4);
                int cardType = Common.bytes2int(data,8);     // 1:管理卡 0:不是管理卡
                LogUtils.w(TAG+"   OnDataReport: cardId=" + cardId + ", isValid=" + isValid + ", cardType=" + cardType);
                if (mCardReaderListener != null) {
                    mCardReaderListener.onCardRead(cardId, isValid);
                }
                break;

            case IntentDef.PubIntentTypeE.Singlecgip_CardOpenDoor:  // 刷卡进门
                String roomNo = null;
                int openValid = Common.bytes2int(data,0);     // 0：无效卡刷卡  1: 有效卡刷卡进门  2、巡更卡刷卡  3、带事件的巡更卡刷卡 4: 管理员卡
                if (data.length > 4){
                    byte[] roomno = new byte[20];
                    System.arraycopy(data, 4, roomno, 0, 20);
                    roomNo = Common.byteToString(roomno);
                }
                if (cardStateListener != null) {
                    cardStateListener.onCardState(openValid, roomNo);
                }
                break;

            case IntentDef.PubIntentTypeE.Singlecgip_DoorAlarm:  //门磁报警
                int doorAlarmType = Common.bytes2int(data,0);   // 1:强行开门报警     2: 门未关超时报警  3:防拆破坏报警
                LogUtils.w(TAG + "   OnDataReport: doorAlarmType=" + doorAlarmType);
                if (mDoorAlarmListener != null) {
                    mDoorAlarmListener.onDoorAlarm(doorAlarmType);
                }
                break;

            case IntentDef.PubIntentTypeE.Singlecgip_BodyInduction: // 人体感应上报
                LogUtils.w(TAG+"   OnDataReport: Singlecgip_BodyInduction 人体感应上报");
                // 1.关背光灯1秒内人体感应无效
                // 2.收到人体感应计时10秒无操作，并且在主界面跟屏保界面
                if (mBodyInductionListener != null) {
                    mBodyInductionListener.onBodyInduction();
                }
                break;

            case IntentDef.PubIntentTypeE.Singlecgip_TouchKeyReport: //触摸键值上报
                int keyValue = Common.bytes2int(data,0);     //表示键值 数字1~9：0x01~0x09;  数字0：0x0A  取消键：0x0B  钥匙键：0x0C  向上：0x0D  向下：0x0E  呼叫键：0x0F
                int keyState = Common.bytes2int(data,4);    //按键状态  0x10按键按下    0x11按键抬起
                LogUtils.w(TAG+"   OnDataReport: keyValue=" + keyValue + ", keyState=" + keyState);
                if (mKeyEventListener != null) {
                    mKeyEventListener.onKeyEvent(keyValue, keyState);
                }
                break;

            default:
                break;
        }
    }

    public void setCardNoListener(ICardNoListener cardNoListener){
        this.mCardNoListener = cardNoListener;
    }

    public void setCardStateListener(ICardStateListener cardStateListener) {
        this.cardStateListener = cardStateListener;
    }

    public void setCardReaderListener(ICardReaderListener listener) {
        this.mCardReaderListener = listener;
    }

    public void setKeyEventListener(IKeyEventListener listener) {
        this.mKeyEventListener = listener;
    }

    public void setDoorAlarmListener(IDoorAlarmListener listener) {
        this.mDoorAlarmListener = listener;
    }

    public void setBodyInductionListener(IBodyInductionListener listener) {
        this.mBodyInductionListener = listener;
    }

}
