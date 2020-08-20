package com.mili.smarthome.tkj.main.fragment;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.CommStorePathDef;
import com.android.CommTypeDef;
import com.android.InterCommTypeDef;
import com.android.client.InterCommClient;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.base.K4Config;
import com.mili.smarthome.tkj.base.KeyboardCtrl;
import com.mili.smarthome.tkj.call.CallManage;
import com.mili.smarthome.tkj.call.CallMonitorBean;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.MediaPlayerUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;

public class CenterFragment extends K4BaseFragment implements View.OnClickListener {

    private static final String TAG = "CenterFragment";
    private TextView mTvTimer;
    private RelativeLayout mContent1;
    private FrameLayout mContent2;
    private TextView mTvDoorHint, mTvMonitorHint;
    private TextView mTvCallHint;
    private TextView mTvManager;
    private TextView mTvDesc;

    private int mTime = 5;
    private InterCommClient mInterCommClient;
    private boolean mIsCalling = false;
    private boolean mIsTalking = false;
    private int mUiState = 0;   //0:默认模式 1:呼叫模式 2:监视模式

    /*本次进入界面是否呼叫过 false 未呼叫过*/
    private boolean mCalled = false;

    @Override
    public boolean onKeyCancel() {
        super.onKeyCancel();

        boolean callstate = K4Config.getInstance().getCallState();
        boolean monitorTalk = K4Config.getInstance().getMonitorTalk();
        LogUtils.d(TAG + " onKeyCancel: callstate is " + callstate + ", monitorTalk is " + monitorTalk);
        if (callstate || monitorTalk) {
            handDown();
            requestBack();
        }
        return true;
    }

    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case MSG_TIME_HINT:
                LogUtils.d(TAG + " mTime is " + mTime);
                mMainHandler.removeMessages(MSG_TIME_HINT);
                mTime--;
                if (mTime <= 0) {
                    requestBack();
                    break;
                }
                @SuppressLint("DefaultLocale")
                String desc = String.format("%dS", mTime);
                mTvTimer.setText(desc);
                mMainHandler.sendEmptyMessageDelayed(MSG_TIME_HINT, 1000);
                break;

            case MSG_CALL_HINT:
                showCallHint(R.string.call_connect_fail, R.color.txt_red, true);
                break;

            case MSG_REQUEST_EXIT:
                requestBack();
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_center;
    }

    @Override
    protected void bindView() {
        super.bindView();

        Button mBtCall = findView(R.id.bt_call);
        Button mBtHandup = findView(R.id.bt_handup);
        assert mBtCall != null;
        mBtCall.setOnClickListener(this);
        assert mBtHandup != null;
        mBtHandup.setOnClickListener(this);
        mTvTimer = findView(R.id.tv_time);

        setRbDrawbleSize(mBtCall,R.drawable.call_calling);
        setRbDrawbleSize(mBtHandup,R.drawable.call_hangup);

        mContent1 = findView(R.id.content1);
        mContent2 = findView(R.id.content2);
        mTvDoorHint = findView(R.id.tv_doorHint);
        mTvMonitorHint = findView(R.id.tv_monitorHint);
        mTvCallHint = findView(R.id.tv_callstate);
        mTvManager = findView(R.id.tv_manager);
        mTvDesc = findView(R.id.tv_desc);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        setKeyboardMode(KeyboardCtrl.KEYMODE_CALL);
        setDevDesc();
        if (!showMonitorTalk()) {
            showView(0);
            showTime();
        }
        mCalled = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mInterCommClient != null) {
            mInterCommClient.setInterCallOutListener(null);
            handDown();
        }
        if (PlaySoundUtils.isLoopSound()) {
            PlaySoundUtils.stopPlayAssetsSound();
        }

        K4Config.getInstance().setCallState(false);
        K4Config.getInstance().setMonitorTalk(false);
        mIsCalling = false;
        mIsTalking = false;
        mMainHandler.removeCallbacksAndMessages(0);
    }

    private void initListener() {
        mInterCommClient = CallManage.getInstance().getInterCommClient();
        mInterCommClient.setInterCallOutListener(mCallStateListener);
    }

    private void showTime() {
        mTime = 5;
        @SuppressLint("DefaultLocale")
        String desc = String.format("%dS", mTime);
        mTvTimer.setText(desc);
        mMainHandler.sendEmptyMessageDelayed(MSG_TIME_HINT, 1000);
    }

    /**
     * 设备描述，根据内容长度调整字体大小
     */
    private void setDevDesc() {
        if (mTvDesc != null) {
            String devDesc = K4Config.getInstance().getDeviceDesc(true);
            mTvDesc.setText(devDesc);
        }
    }

    /** 显示监视通话界面 */
    private boolean showMonitorTalk() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            //显示请通话
            String param = bundle.getString(Const.CallAction.KEY_PARAM);
            LogUtils.d(TAG + " showMonitorTalk: param = " + param);
            if (param != null && param.equals(Const.CallAction.CALL_FROM_CENTER)) {
                K4Config.getInstance().setMonitorTalk(true);
                setMainClickable(1, false);
                showMonitorHint(R.string.call_please, false);
                return true;
            }
        }
        return false;
    }

    private void showView(int state) {
        mUiState = state;
        switch (state) {
            case 0:
                mContent1.setVisibility(View.VISIBLE);
                mContent2.setVisibility(View.GONE);
                break;
            case 1:
                mContent1.setVisibility(View.GONE);
                mContent2.setVisibility(View.VISIBLE);
                mTvManager.setVisibility(View.VISIBLE);
                mTvCallHint.setVisibility(View.VISIBLE);
                mTvDoorHint.setVisibility(View.GONE);
                mTvMonitorHint.setVisibility(View.GONE);
                break;
            case 2:
                mContent1.setVisibility(View.GONE);
                mContent2.setVisibility(View.VISIBLE);
                mTvManager.setVisibility(View.GONE);
                mTvCallHint.setVisibility(View.GONE);
                mTvDoorHint.setVisibility(View.GONE);
                mTvMonitorHint.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 显示呼叫界面
     * @param resId     文本资源ID
     * @param colorId   颜色资源ID
     * @param isBack    是否退出
     */
    private void showCallHint(int resId, int colorId, boolean isBack) {
        showView(1);
        mTvCallHint.setText(resId);
        mTvCallHint.setTextColor(getResources().getColor(colorId));
        if (isBack) {
            mMainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestBack();
                }
            }, Constant.MAIN_HINT_TIMEOUT);
        }
    }

    /**
     * 显示监视通话界面
     * @param resId     文本ID
     * @param isBack    是否退出
     */
    private void showMonitorHint(int resId, boolean isBack) {
        showView(2);
        mTvMonitorHint.setText(resId);
        if (isBack) {
            mMainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestBack();
                }
            }, Constant.MAIN_HINT_TIMEOUT);
        }
    }

    /**
     * 显示开门提示
     */
    private void showDoorHint(boolean show, int textId, int colorId) {
        if (mUiState != 1 && mUiState != 2) {
            LogUtils.d(" uistate is not in calling or monitor.");
            return;
        }
        switch (mUiState) {
            case 1:     // 呼叫状态开门
                if (show) {
                    mTvDoorHint.setText(textId);
                    mTvDoorHint.setTextColor(getResources().getColor(colorId));
                    mTvDoorHint.setVisibility(View.VISIBLE);
                    mTvManager.setVisibility(View.GONE);
                    mTvCallHint.setVisibility(View.GONE);
                } else {
                    showView(mUiState);
                }
                break;
            case 2:     // 监视状态开门
                if (show) {
                    mTvDoorHint.setText(textId);
                    mTvDoorHint.setTextColor(getResources().getColor(colorId));
                    mTvDoorHint.setVisibility(View.VISIBLE);
                    mTvMonitorHint.setVisibility(View.GONE);
                } else {
                    showView(mUiState);
                }
                break;
        }
    }


    /**
     * 主动挂断
     */
    private void handDown() {
        if (mInterCommClient != null) {
            LogUtils.d(TAG + " handDown: isCalling = " + mIsCalling + ", isTalking = " + mIsTalking);
            if (mIsCalling || mIsTalking) {
                mInterCommClient.InterHandDown();
            }
            if (K4Config.getInstance().getMonitorTalk()) {
                mInterCommClient.InterMontorStop();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_call:
                mMainHandler.removeMessages(MSG_TIME_HINT);
                showCallHint(R.string.call_connect_loading, R.color.txt_green, false);
                K4Config.getInstance().setCallState(true);
                new Thread(new CallCenterTask()).start();
                mCalled = true;
                break;

            case R.id.bt_handup:
                mMainHandler.removeMessages(MSG_TIME_HINT);
                requestBack();
                break;
        }
    }

    private class CallCenterTask implements Runnable {
        @Override
        public void run() {
            if (mInterCommClient != null) {
                int ret = mInterCommClient.InterCallCenter(Const.CallAction.CENTER_DEVNO, 0);
                if (ret == 4) {
                    ret = mInterCommClient.InterCallCenter(CommTypeDef.DEVICE_MANAGER_NUMMIN, 0);
                }
                if (ret != 0) {
                    mMainHandler.sendEmptyMessage(MSG_CALL_HINT);
                    K4Config.getInstance().setCallState(false);
                }
            }
        }
    }

    private InterCommTypeDef.InterCallOutListener mCallStateListener = new InterCommTypeDef.InterCallOutListener() {
        @Override
        public void InterCallOutNone(int param) {
            mIsCalling = false;
            mIsTalking = false;
            if (!mCalled) {
                LogUtils.e(TAG + "[InterCallOutNone] mCalled is false.");
                return;
            }
            mMainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestBack();
                }
            }, Constant.MAIN_HINT_TIMEOUT);
        }

        @Override
        public void InterCallOutCalling(int param) {
            K4Config.getInstance().setCallState(true);
            showCallHint(R.string.call_calling, R.color.txt_green, false);
            mIsCalling = true;
            mIsTalking = false;
        }

        @Override
        public void InterCallOutTalking(int param) {
            //关闭声音播放
            PlaySoundUtils.stopPlayAssetsSound();
            showCallHint(R.string.call_please, R.color.txt_green, false);
            mIsCalling = false;
            mIsTalking = true;
            setMainClickable(1, false);
        }

        @Override
        public void InterCallOutEnd(int param) {
            mIsCalling = false;
            mIsTalking = false;
            setMainClickable(1, true);
        }

        @Override
        public void InterCallOutRecording(int param) {
            PlaySoundUtils.stopPlayAssetsSound();
            showCallHint(R.string.call_please_ly, R.color.txt_green, false);
            mIsCalling = false;
            mIsTalking = true;
            setMainClickable(1, false);
        }

        @Override
        public void InterCallOutRecordHit(int param) {
            PlaySoundUtils.stopPlayAssetsSound();
            showCallHint(R.string.call_recordhit, R.color.txt_green, false);
            mIsCalling = false;
            mIsTalking = true;
            setMainClickable(1, false);
        }

        @Override
        public void InterCallOutTimer(int maxtime, int exittime) {

        }

        @Override
        public void InterCallOutMoveing(int param) {

        }

        @Override
        public void InterCallOutHitState(int wordhit, int voicehit) {
            LogUtils.e(TAG + " InterCallOutHitState>>>>>>>" + "wordhit>>>" + wordhit + ">>>>voicehit>>>>" + voicehit);
            if (!mCalled) {
                LogUtils.e(TAG + "[InterCallOutHitState] mCalled is false.");
                return;
            }
            //文字提示
            switch (wordhit) {
                //未知
                case CommTypeDef.CallConnectText.CALL_CONNECT_NONE:
                    break;

                // 	连接超时
                case CommTypeDef.CallConnectText.CALL_CONNECT_TIMEOUT:
                    LogUtils.e(TAG + " InterCallOutHitState>>>>>>>" + "连接超时");
                    showCallHint(R.string.call_connect_fail, R.color.txt_red, true);
                    break;

                // 	设备繁忙
                case CommTypeDef.CallConnectText.CALL_CONNECT_BUSY:
                    LogUtils.e(TAG + " InterCallOutHitState>>>>>>>" + "设备繁忙");
                    showCallHint(R.string.call_busy, R.color.txt_red, true);
                    break;

                // 	无此房号
                case CommTypeDef.CallConnectText.CALL_CONNECT_NOROOMNO:
                    LogUtils.e(TAG + " InterCallOutHitState>>>>>>>" + "无此房号");
                    showCallHint(R.string.call_no_num, R.color.txt_red, true);
                    break;

                // 	无人接听
                case CommTypeDef.CallConnectText.CALL_CONNECT_NOT_HANDDOWN:
                    LogUtils.e(TAG + " InterCallOutHitState>>>>>>>" + "无人接听");
                    showCallHint(R.string.call_no_respose, R.color.txt_red, true);
                    break;

                // 	通话结束
                case CommTypeDef.CallConnectText.CALL_CONNECT_TALK_HANDDOWN:
                    LogUtils.e(TAG + " InterCallOutHitState>>>>>>>" + "通话结束");
                    showCallHint(R.string.call_talk_end, R.color.txt_green, true);
                    break;

                // 	呼叫结束
                case CommTypeDef.CallConnectText.CALL_CONNECT_CALLING_END:
                    LogUtils.e(TAG + " InterCallOutHitState>>>>>>>" + "呼叫结束");
                    showCallHint(R.string.call_end, R.color.txt_green, true);
                    if (PlaySoundUtils.isLoopSound()) {
                        PlaySoundUtils.stopPlayAssetsSound();
                    }
                    break;
            }

            //音频提示
            switch (voicehit) {
                //请稍后
                case CommTypeDef.VoiceHint.VOICE_HINT_CALL_PLSWAIT:
                    PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1802_PATH, null);
                    break;
                //您呼叫的住户暂时无人接听
                case CommTypeDef.VoiceHint.VOICE_HINT_CALL_TIMEOUT:
                    PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1804_PATH, null);
                    break;
                //您呼叫的住户暂时无法接通
                case CommTypeDef.VoiceHint.VOICE_HINT_CALL_NOTCONNECT:
                    PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1803_PATH, null);
                    break;
                //回铃声
                case CommTypeDef.VoiceHint.VOICE_HINT_CALL_RING:
                    //循环播放
                    PlaySoundUtils.playAssetsSoundLoop(CommStorePathDef.CALL_OUT_PATH, null);
                    break;
            }
        }
    };

    @Override
    public void actionCallback(String action, String roomNo) {
        LogUtils.d(TAG + " actionCallback: action is " + action);
        String ringPath;
        switch (action) {
            case Const.CardAction.INVALID_CARD:
                showDoorHint(true, R.string.call_invalid_card, R.color.txt_red);
                ringPath = CommStorePathDef.VOICE_1503_PATH;
                break;
            case Const.CardAction.OPNE_DOOR:
                showDoorHint(true, R.string.call_open_door, R.color.txt_white);
                ringPath = CommStorePathDef.VOICE_1501_PATH;
                break;
            case Constant.HintAction.ALARM_HINT:
                ringPath = null;
                showDoorHint(true, R.string.comm_text_d0, R.color.txt_red);
                break;
            default:
                return;
        }

        // 呼叫或通话时发生报警
        if (action.equals(Constant.HintAction.ALARM_HINT)) {
            mMainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showDoorHint(false, 0, 0);
                }
            }, 4000);
            return;
        }

        //播放刷卡提示音，若当前在呼叫中则恢复回铃声的播放
        PlaySoundUtils.playAssetsSound(ringPath, roomNo, false, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
            @Override
            public void onMediaStatusCompletion(boolean flag) {
                LogUtils.d(TAG + " actionCallback: flag is " + flag + ", isCalling is " + mIsCalling);
                if (flag) {
                    showDoorHint(false, 0, 0);
                    if (mIsCalling) {
                        //循环播放回铃声
                        PlaySoundUtils.playAssetsSoundLoop(CommStorePathDef.CALL_OUT_PATH, null);
                    }
                }
            }
        });
    }

    @Override
    public void onMonitor(CallMonitorBean callMonitorBean) {
        super.onMonitor(callMonitorBean);
        if (callMonitorBean != null) {
            boolean openDoor = callMonitorBean.isOpenDoor();
            boolean callEnd = callMonitorBean.isCallEnd();
            boolean callTalk = callMonitorBean.isCallTalk();
            LogUtils.d(TAG + " openDoor=" + openDoor + ", callEnd=" + callEnd + ", calltalk=" + callTalk);
            //请通话
            if (callTalk) {
                mMainHandler.removeMessages(MSG_TIME_HINT);
                K4Config.getInstance().setMonitorTalk(true);
                setMainClickable(1, false);
                showMonitorHint(R.string.call_please, false);
                //关闭声音播放
                PlaySoundUtils.stopPlayAssetsSound();
            }
            //门开了
            if (openDoor) {
                LogUtils.d(TAG + " =========InterLock");
                actionCallback(Const.CardAction.OPNE_DOOR, null);
            }
            //通话结束
            if (callEnd) {
                LogUtils.d(TAG + " ==========monitor end");
                K4Config.getInstance().setMonitorTalk(false);
                showMonitorHint(R.string.call_talk_end, true);
            }
        }
    }

    private void setRbDrawbleSize(Button button, int drawbleId) {
        Drawable drawable = getResources().getDrawable(drawbleId, null);
        drawable.setBounds(0,0,getResources().getDimensionPixelSize(R.dimen.dp_30),getResources().getDimensionPixelSize(R.dimen.dp_30));
        button.setCompoundDrawables(drawable,null,null,null);
    }
}
