package com.mili.smarthome.tkj.set.call;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.android.CommStorePathDef;
import com.android.CommTypeDef;
import com.android.InterCommTypeDef;
import com.android.client.InterCommClient;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.call.CallManage;
import com.mili.smarthome.tkj.call.CallMonitorBean;
import com.mili.smarthome.tkj.call.ICallMonitorListener;
import com.mili.smarthome.tkj.main.entity.HintBean;
import com.mili.smarthome.tkj.main.face.activity.BaseFaceActivity;
import com.mili.smarthome.tkj.main.manage.HintEventManage;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.set.activity.HintActivity;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.MediaPlayerUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CallHelper implements InterCommTypeDef.InterCallOutListener, ICallMonitorListener {

    @SuppressLint("StaticFieldLeak")
    private static volatile CallHelper callHelper;
    private InterCommClient interCommClient;
    private static HintBean mHintBean;
    private Context mContext;
    private String TAG = "CallHelper";
    private ExecutorService threadExecutor;
    private static Handler mHandle = new CallHandle();
    private static final int NO_RESPONSE_CALL_RESIDENT = 1;
    private static final int NO_RESPONSE_CALL_CENTER = 0;
    private Runnable runnableCallResident;
    private Runnable runnableCallCenter;

    public static CallHelper getInstance() {
        if (callHelper == null) {
            synchronized (CallHelper.class) {
                if (callHelper == null) {
                    callHelper = new CallHelper();
                }
            }
        }
        return callHelper;
    }

    public void initCallBack(Context context) {
        mContext = context;
        //呼叫监听
        interCommClient = CallManage.getInstance().getInterCommClient();
        if (interCommClient != null) {
            interCommClient.setInterCallOutListener(this);
        }
        //对讲接口
        CallManage.getInstance().initCallClient(context);
        CallManage.getInstance().setCallMonitorListener(this);
    }

    /**
     * 挂断
     */
    public void callHandDown() {
        if (interCommClient != null) {
            interCommClient.InterHandDown();
            interCommClient.InterMontorStop();
        }
        if (mHintBean != null) {
            mHintBean.setActiveCall(false);
        }
        stopCallThread();
    }

    /**
     * 呼叫
     */
    private void startCall(Context context, final HintBean hintBean) {
        mHintBean = hintBean;
        HintEventManage.getInstance().toHintAct(context, hintBean);
        String callFrome = mHintBean.getCallFrome();
        threadExecutor = Executors.newSingleThreadExecutor();
        if (Constant.SetHintId.HINT_CALL_FROME_RESIDENT.equals(callFrome)) {
            //住户
            runnableCallResident = new Runnable() {
                @Override
                public void run() {
                    //住户
                    int code = interCommClient.InterCallRoom(hintBean.getRoomNo());
                    if (code != 0) {
                        try {
                            Thread.sleep(1000);
                            //无此房号
                            mHandle.sendEmptyMessage(NO_RESPONSE_CALL_RESIDENT);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            threadExecutor.execute(runnableCallResident);
        } else if (Constant.SetHintId.HINT_CALL_FROME_CENTER.equals(callFrome)) {
            runnableCallCenter = new Runnable() {
                @Override
                public void run() {
                    //中心
                    int ret = interCommClient.InterCallCenter(Const.CallAction.CENTER_DEVNO, 0);
                    if (ret == 4) {
                        ret = interCommClient.InterCallCenter(CommTypeDef.DEVICE_MANAGER_NUMMIN, 0);
                    }
                    if (ret != 0) {
                        mHandle.sendEmptyMessage(NO_RESPONSE_CALL_CENTER);
                    }
                }
            };
            threadExecutor.execute(runnableCallCenter);
        }

    }

    static class CallHandle extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NO_RESPONSE_CALL_CENTER:
                    mHintBean.setType(Constant.SetHintId.HINT_CALL_STATUS);
                    mHintBean.setCallStatus(R.string.call_connect_fail);
                    mHintBean.setCallStatusColor(R.color.txt_red);
                    mHintBean.setCallStatusImgId(R.drawable.center_noresponse);
                    HintEventManage.getInstance().setHintEvent(mHintBean);
                    mHintBean.setType(Constant.SetHintId.HINT_CALL_END);
                    HintEventManage.getInstance().setHintEvent(mHintBean);
                    break;
                case NO_RESPONSE_CALL_RESIDENT:
                    mHintBean.setType(Constant.SetHintId.HINT_CALL_STATUS);
                    mHintBean.setCallStatus(R.string.call_no_num);
                    mHintBean.setCallStatusColor(R.color.txt_red);
                    mHintBean.setCallStatusImgId(R.drawable.zhuhu_noresponse);
                    HintEventManage.getInstance().setHintEvent(mHintBean);
                    PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1202_PATH, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                        @Override
                        public void onMediaStatusCompletion(boolean flag) {
                            mHintBean.setType(Constant.SetHintId.HINT_CALL_END);
                            HintEventManage.getInstance().setHintEvent(mHintBean);
                        }
                    });
                    break;
            }
            super.handleMessage(msg);
        }
    }


    @Override
    public void InterCallOutNone(int param) {
        //退出界面
        mHintBean.setType(Constant.SetHintId.HINT_CALL_END);
        HintEventManage.getInstance().setHintEvent(mHintBean);
        stopCallThread();
        LogUtils.w(TAG + " >>>>InterCallOutNone ");
    }

    @Override
    public void InterCallOutCalling(int param) {
        //呼叫中
        mHintBean.setType(Constant.SetHintId.HINT_CALLING);
        HintEventManage.getInstance().setHintEvent(mHintBean);
        LogUtils.w(TAG + " >>>>InterCallOutCalling ");
    }

    @Override
    public void InterCallOutTalking(int param) {
        //关闭声音播放
        PlaySoundUtils.stopPlayAssetsSound();
        //请通话
        mHintBean.setType(Constant.SetHintId.HINT_TALKING);
        HintEventManage.getInstance().setHintEvent(mHintBean);
        LogUtils.w(TAG + " >>>>InterCallOutTalking ");
    }

    @Override
    public void InterCallOutEnd(int param) {
        //呼叫结束
        mHintBean.setType(Constant.SetHintId.HINT_CALL_END);
        HintEventManage.getInstance().setHintEvent(mHintBean);
        stopCallThread();
        LogUtils.w(TAG + " >>>>InterCallOutEnd ");
    }

    @Override
    public void InterCallOutRecording(int param) {
        //关闭声音播放
        PlaySoundUtils.stopPlayAssetsSound();
        //请留言
        mHintBean.setType(Constant.SetHintId.HINT_RECORD_WAIT);
        HintEventManage.getInstance().setHintEvent(mHintBean);
        LogUtils.w(TAG + " >>>>InterCallOutRecording ");
    }

    @Override
    public void InterCallOutRecordHit(int param) {
        //关闭声音播放
        PlaySoundUtils.stopPlayAssetsSound();
        //留言提示中
        mHintBean.setType(Constant.SetHintId.HINT_RECORDING);
        HintEventManage.getInstance().setHintEvent(mHintBean);
        LogUtils.w(TAG + " >>>>InterCallOutRecordHit ");
    }

    @Override
    public void InterCallOutTimer(int maxtime, int exittime) {

    }

    @Override
    public void InterCallOutMoveing(int param) {

    }

    @Override
    public void InterCallOutHitState(int wordhit, int voicehit) {
        //主动挂断了，回调不做处理
        if (mHintBean != null && !mHintBean.isActiveCall()) {
            return;
        }

        Activity currentActivity = App.getInstance().getCurrentActivity();
        //界面在HintActivity再进行相关处理
        if (currentActivity instanceof HintActivity) {
            String callFrome = mHintBean.getCallFrome();
            LogUtils.w(TAG + " 当前InterCallOutHitState>>>>>>>" + "wordhit>>>" + wordhit + ">>>>voicehit>>>>" + voicehit);
            //文字提示
            switch (wordhit) {
                //未知
                case CommTypeDef.CallConnectText.CALL_CONNECT_NONE:
                    break;
                // 	连接超时
                case CommTypeDef.CallConnectText.CALL_CONNECT_TIMEOUT:
                    if (callFrome != null && callFrome.equals(Constant.SetHintId.HINT_CALL_FROME_CENTER)) {
                        setState(R.string.call_connect_fail, R.color.txt_red, R.drawable.center_noresponse);
                    } else {
                        setState(R.string.call_connect_fail, R.color.txt_red, R.drawable.zhuhu_noresponse);
                    }
                    break;
                // 	设备繁忙
                case CommTypeDef.CallConnectText.CALL_CONNECT_BUSY:
                    if (callFrome != null && callFrome.equals(Constant.SetHintId.HINT_CALL_FROME_CENTER)) {
                        setState(R.string.call_busy, R.color.txt_red, R.drawable.center_noresponse);
                    } else {
                        setState(R.string.call_busy, R.color.txt_red, R.drawable.zhuhu_noresponse);
                    }
                    break;
                // 	无此房号
                case CommTypeDef.CallConnectText.CALL_CONNECT_NOROOMNO:
                    if (callFrome != null && callFrome.equals(Constant.SetHintId.HINT_CALL_FROME_CENTER)) {
                        setState(R.string.call_no_num, R.color.txt_red, R.drawable.center_noresponse);
                    } else {
                        setState(R.string.call_no_num, R.color.txt_red, R.drawable.zhuhu_noresponse);
                    }
                    break;
                // 	无人接听
                case CommTypeDef.CallConnectText.CALL_CONNECT_NOT_HANDDOWN:
                    if (callFrome != null && callFrome.equals(Constant.SetHintId.HINT_CALL_FROME_CENTER)) {
                        setState(R.string.call_no_respose, R.color.txt_red, R.drawable.center_noresponse);
                    } else {
                        setState(R.string.call_no_respose, R.color.txt_red, R.drawable.zhuhu_noresponse);
                    }
                    break;
                // 	呼叫结束
                case CommTypeDef.CallConnectText.CALL_CONNECT_CALLING_END:
                    if (callFrome != null && callFrome.equals(Constant.SetHintId.HINT_CALL_FROME_CENTER)) {
                        setState(R.string.call_end, R.color.txt_green, R.drawable.call_center);
                    } else {
                        setState(R.string.call_end, R.color.txt_green, R.drawable.call_zhuhu);
                    }
                    break;
                // 	通话结束
                case CommTypeDef.CallConnectText.CALL_CONNECT_TALK_HANDDOWN:
                    if (callFrome != null && callFrome.equals(Constant.SetHintId.HINT_CALL_FROME_CENTER)) {
                        setState(R.string.call_talk_end, R.color.txt_green, R.drawable.call_center);
                    } else {
                        setState(R.string.call_talk_end, R.color.txt_green, R.drawable.call_zhuhu);
                    }
                    break;

            }
            //音频提示
            switch (voicehit) {
                //请稍后
                case CommTypeDef.VoiceHint.VOICE_HINT_CALL_PLSWAIT:
                    PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1802_PATH, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                        @Override
                        public void onMediaStatusCompletion(boolean flag) {

                        }
                    });
                    break;
                //您呼叫的住户暂时无人接听
                case CommTypeDef.VoiceHint.VOICE_HINT_CALL_TIMEOUT:
                    PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1804_PATH, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                        @Override
                        public void onMediaStatusCompletion(boolean flag) {
                            mHintBean.setType(Constant.SetHintId.HINT_CALL_END);
                            HintEventManage.getInstance().setHintEvent(mHintBean);
                        }
                    });
                    break;
                //您呼叫的住户暂时无法接通
                case CommTypeDef.VoiceHint.VOICE_HINT_CALL_NOTCONNECT:
                    PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1803_PATH, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                        @Override
                        public void onMediaStatusCompletion(boolean flag) {
                            mHintBean.setType(Constant.SetHintId.HINT_CALL_END);
                            HintEventManage.getInstance().setHintEvent(mHintBean);
                        }
                    });
                    break;
                //回铃声
                case CommTypeDef.VoiceHint.VOICE_HINT_CALL_RING:
//                LogUtils.w(TAG + " CommTypeDef.VoiceHint.VOICE_HINT_CALL_RING isHintCallRing: " + isHintCallRing);
                    mHintBean.setType(Constant.SetHintId.HINT_CALL_RING);
                    HintEventManage.getInstance().setHintEvent(mHintBean);
                    break;
            }
        }
    }

    private void setState(int strId, int strColor, int imgId) {
        if (mHintBean == null) {
            mHintBean = new HintBean();
        }
        mHintBean.setType(Constant.SetHintId.HINT_CALL_STATUS);
        mHintBean.setCallStatus(strId);
        mHintBean.setCallStatusColor(strColor);
        mHintBean.setCallStatusImgId(imgId);
        HintEventManage.getInstance().toHintAct(mContext, mHintBean);
    }

    @Override
    public void onCallMonitor(CallMonitorBean callMonitorBean) {
        if (mHintBean == null) {
            mHintBean = new HintBean();
        }
        String callFrom = callMonitorBean.getCallFrom();
        boolean callTalk = callMonitorBean.isCallTalk();
        boolean callEnd = callMonitorBean.isCallEnd();
        boolean openDoor = callMonitorBean.isOpenDoor();
        Activity currentActivity = App.getInstance().getCurrentActivity();
        if (openDoor) {
            //开门
            if (currentActivity instanceof BaseFaceActivity || Constant.ScreenId.SCREEN_IS_SET) {
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1501_PATH);
            } else {
                mHintBean.setType(Constant.MONITOR_OPEN_DOOR);
                HintEventManage.getInstance().toHintAct(mContext, mHintBean);
            }
        }
        if (callTalk) {
            if (callFrom.equals(Const.CallAction.CALL_FROM_RESIDENT)) {
                //住户
                mHintBean.setCallFrome(Constant.SetHintId.HINT_CALL_FROME_RESIDENT);
            } else {
                //中心
                mHintBean.setCallFrome(Constant.SetHintId.HINT_CALL_FROME_CENTER);
            }
            mHintBean.setType(Constant.SetHintId.HINT_TALKING);
            HintEventManage.getInstance().toHintAct(mContext, mHintBean);
        }
        if (callEnd) {
            if (currentActivity instanceof HintActivity) {
                if (callFrom.equals(Const.CallAction.CALL_FROM_RESIDENT)) {
                    //住户
                    setState(R.string.call_talk_end, R.color.txt_green, R.drawable.call_zhuhu);
                } else {
                    //中心
                    setState(R.string.call_talk_end, R.color.txt_green, R.drawable.call_center);
                }
                mHintBean.setType(Constant.SetHintId.HINT_CALL_END);
                HintEventManage.getInstance().setHintEvent(mHintBean);
            }
        }

    }

    /**
     * 呼叫中心
     */
    public void callCenter(Context context) {
        callCenter(context, context.getString(R.string.manage_center));
    }

    /**
     * 呼叫中心
     */
    public void callCenter(Context context, String roomName) {
        HintBean hintBean = new HintBean();
        hintBean.setCallFrome(Constant.SetHintId.HINT_CALL_FROME_CENTER);
        hintBean.setType(Constant.SetHintId.HINT_CONNECTING);
        hintBean.setRoomNo(context.getString(R.string.manage_center));
        hintBean.setRoomName(roomName);
        hintBean.setCallStatusColor(R.color.txt_white);
        hintBean.setActiveCall(true);
        CallHelper.getInstance().startCall(context, hintBean);
    }

    /**
     * 呼叫住户
     *
     * @param context 上下文
     * @param roomNo  房号
     */
    public void callResident(Context context, String roomNo) {
        callResident(context, roomNo, roomNo);
    }

    /**
     * 呼叫住户
     *
     * @param context 上下文
     * @param roomNo  房号
     */
    public void callResident(Context context, String roomNo, String roomName) {
        HintBean hintBean = new HintBean();
        hintBean.setCallFrome(Constant.SetHintId.HINT_CALL_FROME_RESIDENT);
        hintBean.setType(Constant.SetHintId.HINT_CONNECTING);
        hintBean.setRoomNo(roomNo);
        hintBean.setRoomName(roomName);
        hintBean.setActiveCall(true);
        CallHelper.getInstance().startCall(context, hintBean);
    }

    /**
     * 结束主动呼叫的线程
     */
    private void stopCallThread() {
        if (threadExecutor != null) {
            threadExecutor.shutdownNow();
        }
    }
}
