package com.mili.smarthome.tkj.proxy;

import android.content.Context;
import android.media.AudioManager;
import android.os.SystemClock;
import android.util.Log;

import com.android.client.CardClient;
import com.android.client.FingerClient;
import com.android.client.InterCommClient;
import com.android.client.MainClient;
import com.android.client.SetDriverSinglechipClient;
import com.android.interf.IBodyInductionListener;
import com.android.interf.ICardNoListener;
import com.android.interf.ICardReaderListener;
import com.android.interf.ICardStateListener;
import com.android.interf.IDoorAlarmListener;
import com.android.interf.IFingerEventListener;
import com.android.interf.IKeyEventListener;
import com.android.main.MainCommDefind;
import com.mili.smarthome.tkj.BuildConfig;
import com.mili.smarthome.tkj.app.AppPreferences;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.appfunc.BuildConfigHelper;
import com.mili.smarthome.tkj.appfunc.FreeObservable;
import com.mili.smarthome.tkj.dao.FingerDao;
import com.mili.smarthome.tkj.entities.FingerModel;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.SystemSetUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 */
public final class SinglechipClientProxy implements IKeyEventListener, ICardStateListener, ICardNoListener,
        ICardReaderListener, IFingerEventListener, IDoorAlarmListener, IBodyInductionListener {

    /**
     * 白光补光灯 - 关
     */
    public static final int TURN_OFF = 0xFF;
    /**
     * 白光补光灯 - 半亮
     */
    public static final int TURN_HALF = 0xC0;
    /**
     * 白光补光灯 - 全亮
     */
    public static final int TURN_ON = 0x00;
    /**
     * 白光补光灯 - 全亮（人脸界面）
     */
    public static final int TURN_ON_FOR_FACE = 0x80;

    private static final SinglechipClientProxy INSTANCE = new SinglechipClientProxy();

    public static SinglechipClientProxy getInstance() {
        return INSTANCE;
    }

    private SetDriverSinglechipClient mSinglechipClient;
    private CardClient mCardClient;
    private FingerClient mFingerClient;
    private InterCommClient mInterCommClient;

    private List<IKeyEventListener> mKeyEventListenerList = new CopyOnWriteArrayList<>();
    private List<ICardStateListener> mCardStateListenerList = new ArrayList<>();
    private List<IFingerEventListener> mFingerEventListenerList = new ArrayList<>();
    private ICardNoListener mCardNoListener;
    private ICardReaderListener mCardReaderListener;
    private IBodyInductionListener mBodyInductionListener;
    private IDoorAlarmListener mDoorAlarmListener;

    /**
     * 红外补光灯时限，开机时间小于该时限，则不能打开红外补光灯
     */
    private long mCcdLedTimeLimit = 30 * 60 * 1000;
    /**
     * 摄像头白光补光灯当前亮度值
     */
    private int mBrightness = 0xFF;

    private SinglechipClientProxy() {
        final Context context = ContextProxy.getContext();
        //
        mSinglechipClient = new SetDriverSinglechipClient(context);
        if (BuildConfigHelper.isK3()) {
            mSinglechipClient.setKeyEventListener(this);
        }
        mSinglechipClient.setCardStateListener(this);
        mSinglechipClient.setCardNoListener(this);
        mSinglechipClient.setBodyInductionListener(this);
        mSinglechipClient.setDoorAlarmListener(this);
        mSinglechipClient.setCardReaderListener(this);
        //
        mCardClient = new CardClient(context);
        //
        mFingerClient = new FingerClient(context);
        mFingerClient.setFingerEventListener(this);
        //
        mInterCommClient = new InterCommClient(context);
    }

    /**
     * 重启系统
     */
    public int rebootSystem() {
        return mSinglechipClient.rebootSystem();
    }

    /**
     * 发送延迟一段时间后没收到喂狗命令重启设备命令
     * 单位秒
     */
    public int delayTimeRebootSystem(int delayTime) {
        return mSinglechipClient.delayTimeRebootSystem(delayTime);
    }

    /**
     * 播放按键提示音
     */
    public void playKeyClick() {
        AudioManager audioManager = ContextProxy.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null)
            audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
    }


    // ======================================================================
    //  按键事件分发
    // ======================================================================

    public void addKeyEventListener(IKeyEventListener listener) {
        mKeyEventListenerList.remove(listener);
        mKeyEventListenerList.add(0, listener);
    }

    public void removeKeyEventListener(IKeyEventListener listener) {
        mKeyEventListenerList.remove(listener);
    }

    @Override
    public boolean onKeyEvent(int keyCode, int keyState) {
        LogUtils.d(this.getClass().getSimpleName() + ">>>>>onKeyEvent: code=" + keyCode + ", state=" + keyState);
        if (keyState == KEYSTATE_DOWN) {
            playKeyClick();
            SystemSetUtils.screenOn();
            ctrlTouchKeyLampState(true);
        }
        FreeObservable.getInstance().resetFreeTime();
        for (IKeyEventListener listener : mKeyEventListenerList) {
            if (listener.onKeyEvent(keyCode, keyState))
                return true;
        }
        return false;
    }


    // ======================================================================
    //  卡
    // ======================================================================

    public void addCardStateListener(ICardStateListener listener) {
        mCardStateListenerList.add(0, listener);
    }

    public void removeCardStateListener(ICardStateListener listener) {
        mCardStateListenerList.remove(listener);
    }

    public void setCardNoListener(ICardNoListener listener) {
        mCardNoListener = listener;
    }

    public void setCardReaderListener(ICardReaderListener listener) {
        mCardReaderListener = listener;
    }

    /**
     * 添加卡
     *
     * @param roomNo 房号
     * @param cardNo 卡号
     */
    public int addCard(String roomNo, String cardNo, int cardType, int roomNoState, String keyID, int startTime, int endTime, int lifecycle) {
        if (mCardClient != null) {
            return mCardClient.AddCard(roomNo, cardNo, cardType, roomNoState, keyID, startTime, endTime, lifecycle);
        }
        return 0xF0;
    }

    /**
     * 删除卡
     *
     * @param roomNo 房号
     * @param cardNo 卡号
     */
    public int delCard(String roomNo, String cardNo) {
        if (mCardClient != null) {
            return mCardClient.DelCard(roomNo, cardNo);
        }
        return 0xF0;
    }

    /**
     * 删除某住户的所有卡
     *
     * @param roomNo 房号
     */
    public int delUserCard(String roomNo) {
        if (mCardClient != null) {
            return mCardClient.DelUserCard(roomNo);
        }
        return 0xF0;
    }

    /**
     * 清空卡
     */
    public int clearCard() {
        if (mCardClient != null) {
            return mCardClient.ClearCard();
        }
        return 0xF0;
    }

    /**
     * 获取卡个数
     */
    public int getCardCount() {
        if (mCardClient != null) {
            return mCardClient.GetCardCount();
        }
        return 0;
    }

    /**
     * 获取剩余卡个数
     */
    public int getCardFreeCount() {
        if (mCardClient != null) {
            return mCardClient.GetCardFreeCount();
        }
        return 0;
    }

    /**
     * 设置卡处理状态
     *
     * @param state 00处于刷卡进门状态; 01处于设置卡状态; 02处于人脸刷卡编辑状态; 03处于添加指纹状态; 04处于修改密码状态
     */
    public int setCardState(int state) {
        if (mCardClient != null) {
            return mCardClient.SetCardState(state);
        }
        return 0;
    }

    @Override
    public void onCardState(int state, String roomNo) {
        LogUtils.d(this.getClass().getSimpleName() + ">>>>>onCardState: state=" + state);
        playKeyClick();
        SystemSetUtils.screenOn();
        if (BuildConfig.DevType == MainCommDefind.DEVICE_TYPE_K4_X1600_REL) {
            SetDriverSinglechipClient.getInstance().setSystemSleep(1);
        }
        ctrlTouchKeyLampState(true);
        FreeObservable.getInstance().resetFreeTime();
        for (ICardStateListener listener : mCardStateListenerList) {
            listener.onCardState(state, roomNo);
        }
    }

    @Override
    public void onCardNo(String cardNo) {
        LogUtils.d(this.getClass().getSimpleName() + ">>>>>onCardNo: cardNo=" + cardNo);
        playKeyClick();
        FreeObservable.getInstance().resetFreeTime();
        if (mCardNoListener != null) {
            mCardNoListener.onCardNo(cardNo);
        }
    }

    @Override
    public void onCardRead(int cardId, int result) {
        LogUtils.d(this.getClass().getSimpleName() + ">>>>>onCardRead: cardId=" + cardId + ", result=" + result);
        playKeyClick();
        FreeObservable.getInstance().resetFreeTime();
        if (mCardReaderListener != null) {
            mCardReaderListener.onCardRead(cardId, result);
        }
    }


    // ======================================================================
    //  指纹识别
    // ======================================================================


    /**
     * 添加指纹事件回调
     */
    public void addFingerEventListener(IFingerEventListener listener) {
        mFingerEventListenerList.add(0, listener);
    }

    /**
     * 移除指纹事件回调
     */
    public void removeFingerEventListener(IFingerEventListener listener) {
        mFingerEventListenerList.remove(listener);
    }

    /**
     * 判断指纹模块是否能正常工作
     */
    public boolean isFingerWork() {
        if (mFingerClient != null) {
            return mFingerClient.FingerGetWorkState() == 1;
        }
        return false;
    }

    /**
     * 设置指纹状态
     *
     * @param state 1-在人脸界面；0-不在人脸界面
     */
    public void setFingerState(int state) {
        if (mFingerClient != null) {
            mFingerClient.FingerSetFaceState(state);
        }
    }

    /**
     * 按照房号识别指纹
     *
     * @param roomNo 房号
     */
    public void setFingerDevNo(String roomNo) {
        if (mFingerClient != null) {
            mFingerClient.FingerSetDevnoDistinguish(roomNo);
        }
    }

    /**
     * 初始化指纹数据库
     */
    public void initFingerDatabase() {
        FingerDao fingerDao = new FingerDao();
        List<FingerModel> fingerList = fingerDao.queryAll();
        for (FingerModel fingerModel : fingerList) {
            if (mFingerClient != null) {
                mFingerClient.FingerStorageInitAdd(fingerModel.getFingerId(), fingerModel.getValid(), fingerModel.getRoomNo(), fingerModel.getFingerInfo());
            }
        }
    }

    /**
     * 获取剩余指纹数
     */
    public int getFingerSurplus() {
        if (mFingerClient != null) {
            return mFingerClient.FingerGetCount();
        }
        return 0;
    }

    /**
     * 添加指纹
     *
     * @param roomNo 房号
     * @return 成功：1    失败：0
     */
    public int addFinger(String roomNo) {
        LogUtils.d(this.getClass().getSimpleName() + ">>>>>start addFinger: roomNo=" + roomNo);
        if (mFingerClient != null) {
            return mFingerClient.FingerAdd(roomNo);
        }
        return 0;
    }

    /**
     * 停止指纹添加
     */
    public void stopAddFinger() {
        LogUtils.d(this.getClass().getSimpleName() + ">>>>>stop addFinger");
        if (mFingerClient != null) {
            mFingerClient.FingerOperStop();
        }
    }

    /**
     * 删除住户的所有指纹
     *
     * @param roomNo 房号
     */
    public void delFinger(String roomNo) {
        if (mFingerClient != null) {
            mFingerClient.FingerDelUser(roomNo);
        }
    }

    /**
     * 清空指纹
     */
    public void clearFinger() {
        if (mFingerClient != null) {
            mFingerClient.FingerClear();
        }
    }

    @Override
    public void onFingerCollect(int code, int press, int count) {
        LogUtils.d(this.getClass().getSimpleName() + ">>>>>onFingerCollect: code=" + code + ", press=" + press + ", count=" + count);
        if (press == 1) {
            playKeyClick();
        }
        FreeObservable.getInstance().resetFreeTime();
        for (IFingerEventListener listener : mFingerEventListenerList) {
            listener.onFingerCollect(code, press, count);
        }
    }

    @Override
    public void onFingerAdd(int code, int fingerId, int valid, byte[] fingerData) {
        LogUtils.d(this.getClass().getSimpleName() + ">>>>>onFingerAdd: code=" + code);
        for (IFingerEventListener listener : mFingerEventListenerList) {
            listener.onFingerAdd(code, fingerId, valid, fingerData);
        }
    }

    @Override
    public void onFingerOpen(int code, String roomNo) {
        LogUtils.d(this.getClass().getSimpleName() + ">>>>>onFingerOpen: code=" + code);
        if (code == 2) {
            playKeyClick();
            SystemSetUtils.screenOn();
            if (BuildConfig.DevType == MainCommDefind.DEVICE_TYPE_K4_X1600_REL) {
                SetDriverSinglechipClient.getInstance().setSystemSleep(1);
            }
            ctrlTouchKeyLampState(true);
        }
        FreeObservable.getInstance().resetFreeTime();
        for (IFingerEventListener listener : mFingerEventListenerList) {
            listener.onFingerOpen(code, roomNo);
        }
    }


    // ======================================================================
    //  人体感应
    // ======================================================================

    private long mDisableBodyInductionTimeout = 0L;

    /**
     * 禁用人体感应十秒钟
     *
     * @param funcId 1人脸识别，2扫码开门，3蓝牙开门器
     */
    public void disableBodyInductionForTenSecond(int funcId) {
        disableBodyInduction(funcId, 10 * 1000);
    }

    /**
     * 禁用人体感应
     *
     * @param funcId      1人脸识别，2扫码开门，3蓝牙开门器
     * @param disableTime 禁用的时间，单位毫秒
     */
    public void disableBodyInduction(int funcId, long disableTime) {
        int bodyInduction = AppConfig.getInstance().getBodyInduction();
        if (bodyInduction == funcId) {
            mDisableBodyInductionTimeout = SystemClock.elapsedRealtime() + disableTime;
        }
        LogUtils.d(this.getClass().getSimpleName() + ">>>>>disableBodyInduction: funcId=" + funcId + ", bodyInduction=" + bodyInduction + ", timeout=" + mDisableBodyInductionTimeout);
    }

    public void setBodyInductionListener(IBodyInductionListener listener) {
        mBodyInductionListener = listener;
    }

    @Override
    public void onBodyInduction() {
        LogUtils.d(this.getClass().getSimpleName() + ">>>>>onBodyInduction");
        SystemSetUtils.screenOn();
        ctrlTouchKeyLampState(true);
        if (!mSinglechipClient.getSystemSleep()) {
            mSinglechipClient.setSystemSleep(1);
        }
        FreeObservable.getInstance().resetFreeTime();
        if (mBodyInductionListener == null) {
            return;
        }
        if (SystemClock.elapsedRealtime() < mDisableBodyInductionTimeout) {
            LogUtils.d(this.getClass().getSimpleName() + ">>>>>onBodyInduction: in Disabled");
            return;
        }
        mBodyInductionListener.onBodyInduction();
    }


    // ======================================================================
    //  门报警
    // ======================================================================

    public void setDoorAlarmListener(IDoorAlarmListener listener) {
        mDoorAlarmListener = listener;
    }

    @Override
    public void onDoorAlarm(int doorAlarmType) {
        LogUtils.d(this.getClass().getSimpleName() + ">>>>>onDoorAlarm: " + doorAlarmType);
        if (mDoorAlarmListener != null) {
            mDoorAlarmListener.onDoorAlarm(doorAlarmType);
        }
    }


    // ======================================================================
    //
    // ======================================================================

    /**
     * 按键背光控制
     *
     * @param turnOn true打开，false关闭
     * @return 0成功，其他失败
     */
    public int ctrlTouchKeyLampState(boolean turnOn) {
        if (BuildConfigHelper.isK3()) {
            return mSinglechipClient.ctrlTouchKeyLampState(turnOn ? 1 : 0);
        }
        return 1;
    }

    public void ctrlOpenCCD(boolean enabled) {
        if (mSinglechipClient != null) {
            mSinglechipClient.ctrlOpenCCD(enabled ? 1 : 0);
        }
        setCcdLedOpen();
    }

    public void setCcdLedTimeLimit(long timeLimit) {
        mCcdLedTimeLimit = timeLimit;
        if (mSinglechipClient != null) {
            mSinglechipClient.ctrlOpenCCD(timeLimit == 0 ? 1 : 0);
        }
        setCcdLedOpen();
    }

    /**
     * 出厂设备未进行恢复出厂操作时，进入系统信息后打开红外补光灯，不进行时间段判断。
     */
    public void setCcdLedOpen() {
        if (!AppPreferences.getResumeFactory()) {
            if (mSinglechipClient != null) {
                mSinglechipClient.ctrlOpenCCD(2);
            }
            LogUtils.d(" ======== setCcdLedOpen ======== ");
        }
    }

    /**
     * 摄像头红外补光灯控制
     *
     * @param flag 1打开，0关闭
     * @return 0成功，其他失败
     */
    public int ctrlCcdLed(int flag) {
        if (flag == 1) {
            // 8:00 - 17:00 不开红外补光灯
            Calendar calendar = Calendar.getInstance();
            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            if (hourOfDay >= 8 && hourOfDay < 17) {
                return 1;
            }
            if (SystemClock.elapsedRealtime() < mCcdLedTimeLimit) {
                return 2;
            }
        }
        if (mSinglechipClient != null) {
            return mSinglechipClient.ctrlCcdLed(flag);
        }
        return 0;
    }

    /*
     * 红外补光灯控制
     */
    public int IRLedCtrl(boolean turnOn) {
        if (mSinglechipClient != null) {
//            Log.d("lfl", "IRLedCtrl:   " + turnOn);
            return mSinglechipClient.ctrlCcdLed(turnOn ? 1 : 0);
        } else {
//            Log.e("lfl", "IRLedCtrl: NULL");
        }
        return -2;
    }

    /**
     * 摄像头白光补光灯控制
     *
     * @param brightness 0xFF暗，0x0亮
     * @return 0成功，其他失败
     */
    public int ctrlCamLamp(int brightness) {
        if (mSinglechipClient == null) {
            return 0xFF;
        }
        int result = mSinglechipClient.ctrlCamLampChange(brightness, brightness);
        LogUtils.d("ctrlCamLamp: start=" + brightness + ", end=" + brightness + ", result=" + result);
        if (result == 0) {
            mBrightness = brightness;
        }
        return result;
    }

    /**
     * 摄像头白光补光灯渐变控制
     *
     * @param brightness 0xFF暗，0x0亮
     * @return 0成功，其他失败
     */
    public int ctrlCamLampChange(int brightness) {
        if (mBrightness == brightness) {
            return 0;
        }
        if (mSinglechipClient == null) {
            return 0xFF;
        }
        int result = mSinglechipClient.ctrlCamLampChange(mBrightness, brightness);
        LogUtils.d("ctrlCamLamp: start=" + mBrightness + ", end=" + brightness + ", result=" + result);
        if (result == 0) {
            mBrightness = brightness;
        }
        return result;
    }

    /**
     * 锁参数信息设置
     *
     * @param locktype 锁类型
     * @param locktime 锁时长，取值0/3/6/9，默认3，单位s
     * @return 0成功，其他失败
     */
    public int setLockParam(int locktype, int locktime) {
        if (mSinglechipClient != null) {
            return mSinglechipClient.setLockParam(locktype, locktime);
        }
        return 3;
    }

    /**
     * 设置灵敏度设置
     *
     * @param sens 灵敏度等级，0低，1中，2高
     * @return 0成功，其他失败
     */
    public int setTouchSens(int sens) {
        return mSinglechipClient.setTouchSens(sens);
    }


    // ======================================================================
    //
    // ======================================================================

    /**
     * @param faceName    人脸ID
     * @param confidence  相似度
     * @param keyID       用户ID
     * @param previewType 预览类型：0本地，1流媒体
     * @param previewData 预览数据
     * @return 0忽略；1开锁成功；-1无效人脸
     */
    public int faceRecognizeSucc(String faceName, String keyID, float confidence, int previewType, byte[] previewData) {
        if (mInterCommClient != null) {
            int faceLen = faceName == null ? 0 : faceName.length();
            int snapDataLen;
            int snapType;//0本地抓拍；1本地数据；2流媒体抓拍；3流媒体数据
            if (previewData == null) {
                snapDataLen = 0;
                snapType = (previewType == 1) ? 2 : 0;
            } else {
                snapDataLen = previewData.length;
                snapType = (previewType == 1) ? 3 : 1;
            }
            LogUtils.d("-- FACE: faceRecognizeSuccReport: faceName=%s, confidence=%f, snapType=%d", faceName, confidence, snapType);
            return mInterCommClient.InterFaceRecognizeOk(faceName, faceLen, keyID, confidence, snapType, previewData, snapDataLen);
        }
        return 0;
    }

    public void faceRecognizeSuccReport(String faceName, String snapPath, float confidence) {
        LogUtils.d("-- FACE: faceRecognizeSuccReport: faceName=%s, confidence=%f, snapPath=%s", faceName, confidence, snapPath);
        if (mInterCommClient != null) {
            mInterCommClient.InterFaceSnapAndReport(faceName, snapPath, confidence);
        }
    }

    /**
     * 云端库异常重启
     */
    public void cloudReboot() {
        int state = MainClient.getInstance().Main_getCloudReboot();
        LogUtils.d(" ========= cloud reboot state is " + state + " ========");
        if (state == 1) {
            LogUtils.d(" ========= cloud library error, reboot ========= ");
//            SetDriverSinglechipClient setDriverSinglechipClient = new SetDriverSinglechipClient();
//            setDriverSinglechipClient.rebootSystem();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    // ======================================================================
    //  4G网络标志位
    // ======================================================================
    private int mNetWorkOnlineType = -1;     // -1无网络 0-有线网络 1-4G网络
    private boolean mCloudReset = false;    // 是否重启程序以恢复云端网络

    public void setNetworkOnlineType(int type) {
        mNetWorkOnlineType = type;
    }

    public int getNetWorkOnlineType() {
        return mNetWorkOnlineType;
    }

    public void setCloudReset(boolean state) {
        mCloudReset = state;
    }

    public boolean getCloudReset() {
        return mCloudReset;
    }

    // =====================================================================
    //  网络状态
    // =====================================================================
    private int networkType = -1;
    private int networkState = -1;

    public void setNetworkInfo(int type, int state) {
        networkType = type;
        networkState = state;
    }

    public int getNetworkType() {
        return networkType;
    }

    public int getNetworkState() {
        return networkState;
    }
}
