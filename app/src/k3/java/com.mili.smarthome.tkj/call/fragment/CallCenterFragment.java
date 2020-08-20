package com.mili.smarthome.tkj.call.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.CommStorePathDef;
import com.android.CommTypeDef;
import com.android.InterCommTypeDef;
import com.android.client.InterCommClient;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.appfunc.FreeObservable;
import com.mili.smarthome.tkj.base.K3BaseFragment;
import com.mili.smarthome.tkj.call.CallManage;
import com.mili.smarthome.tkj.call.CallMonitorBean;
import com.mili.smarthome.tkj.call.IActCallBackListener;
import com.mili.smarthome.tkj.main.activity.MainActivity;
import com.mili.smarthome.tkj.main.widget.GotoMainDefaultTask;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.MediaPlayerUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;

import java.util.Locale;

public class CallCenterFragment extends K3BaseFragment implements View.OnClickListener, InterCommTypeDef.InterCallOutListener, IActCallBackListener {

    public static Bundle createArguments(String action) {
        Bundle args = new Bundle();
        args.putString(Const.CallAction.KEY_PARAM, action);
        return args;
    }

    private String TAG = "CallCenterFragment";
    private TextView tvTitle;
    private TextView tvState;
    private Button btnCall;
    private Button btnCancel;
    private boolean isPlaying;
    private CountdownTask mCountdownTask = new CountdownTask();
    private InterCommClient interCommClient;
    private byte roomNoLen;
    //是否梯口机
    private boolean isStair;
    //是否正在呼叫
    public static boolean isCalling;
    //是否通话
    public static boolean isTalking;
    //是否播过回铃声
    private boolean isBackRing = true;
    //是否呼叫结束
    private boolean isCallEnd;
    private FrameLayout mFlCall;
    private TextView mTvOpenDoor;
    private int nums;
    private final int VIEW_STATE_1 = 0x001;
    private RefresHandle mRefrshHandle = new RefresHandle();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_call_center;
    }

    @Override
    protected void bindView() {
        tvTitle = findView(R.id.tv_title);
        tvState = findView(R.id.tv_state);
        btnCall = findView(R.id.btn_call);
        btnCancel = findView(R.id.btn_cancel);
        mFlCall = findView(R.id.fl_call);
        mTvOpenDoor = findView(R.id.tv_open_door);
        btnCall.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //SinglechipClientProxy.getInstance().ctrlCcdLed(1);
        nums = 0;
        isCalling = false;
        isTalking = false;
        isBackRing = true;
        isCallEnd = false;
        mFlCall.setVisibility(View.VISIBLE);
        mTvOpenDoor.setVisibility(View.GONE);
        tvTitle.setText(R.string.call_center_confirm);
        tvState.setText("");
        tvState.setTextColor(getResources().getColor(R.color.txt_white));
        btnCall.setVisibility(View.VISIBLE);
        mCountdownTask.mTime = 6;
        mCountdownTask.run();

        initListener();
        initData();
        //监视通话跳转
        toShowMonitorTalk();
    }


    @Override
    public void onDestroyView() {
        //SinglechipClientProxy.getInstance().ctrlCcdLed(0);
        mMainHandler.removeCallbacks(mCountdownTask);
        FreeObservable.getInstance().observeFree();
        super.onDestroyView();
        if (interCommClient != null && (isCalling || isTalking)) {
            interCommClient.InterHandDown();
        }
        isTalking = false;
        isCalling = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_call:
                if (nums == 0) {
                    setCallState(R.string.call_connect_loading, R.color.txt_green);
                    //开始呼叫
                    startCall();
                }
                break;
            case R.id.btn_cancel:
                backMain(false);
                break;
        }
    }

    @Override
    public boolean onKeyEvent(int keyCode, int keyState) {
        if (keyState == KEYSTATE_DOWN || keyCode == KEYCODE_UNLOCK)
            return false;
        switch (keyCode) {
            case KEYCODE_BACK:
                backMain(false);
                break;
            case KEYCODE_CALL:
                if (nums == 0) {
                    setCallState(R.string.call_connect_loading, R.color.txt_green);
                    //开始呼叫
                    startCall();
                }
                break;
        }
        return true;
    }

    //回到主界面
    private void backMain(boolean isDelay) {
        LogUtils.e("====CallCenterFragment  backMain : " + isDelay);
        if (isDelay) {
            mMainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LogUtils.d(" isPlaying: " + isPlaying + ", isCallEnd: " + isCallEnd);
                    if (!isPlaying && isCallEnd) {
                        if (PlaySoundUtils.isLoopSound()) {
                            PlaySoundUtils.stopPlayAssetsSound();
                        }
                        MainActivity activity = (MainActivity) getActivity();
                        assert activity != null;
                        if (isAdded() && activity.fmCurrent instanceof CallCenterFragment) {
                            if (interCommClient != null) {
                                interCommClient.setInterCallOutListener(null);
                            }
                            GotoMainDefaultTask.getInstance().run();
                        }
                    }
                }
            }, 2000);
        } else {
            LogUtils.e("====CallCenterFragment  backMain isPlaying: " + isPlaying + " isCallEnd: " + isCallEnd);
            if (isAdded()) {
                if (isTalking) {
                    //关闭监视对讲
                    CallManage.getInstance().stopCallMonitor();
                }
                if (interCommClient != null) {
                    interCommClient.setInterCallOutListener(null);
                }
                //关闭声音播放
                if (PlaySoundUtils.isLoopSound()) {
                    PlaySoundUtils.stopPlayAssetsSound();
                }
                GotoMainDefaultTask.getInstance().run();
            }
        }
    }

    private void initData() {
        FullDeviceNo fullDeviceNo = new FullDeviceNo(getContext());
        roomNoLen = fullDeviceNo.getRoomNoLen();
        int deviceType = fullDeviceNo.getDeviceType();
        if (deviceType == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            isStair = true;
        } else {
            isStair = false;
        }
    }

    private void initListener() {
        interCommClient = CallManage.getInstance().getInterCommClient();
        if (interCommClient != null) {
            interCommClient.setInterCallOutListener(this);
        }
        MainActivity activity = (MainActivity) mContext;
        activity.setActCallBackListener(this);
    }

    private void startCall() {
        FreeObservable.getInstance().cancelObserveFree();
        //底部按钮不能点击
        setMainBtnEnable();
        nums = Const.CallAction.CENTER_DEVNO;
        mRefrshHandle.sendEmptyMessage(VIEW_STATE_1);
    }

    private void setMainBtnEnable() {
        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        activity.setTabEnabled(false);
    }


    private void toShowMonitorTalk() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            String param = bundle.getString(Const.CallAction.KEY_PARAM);
            if (param != null && param.equals(Const.CallAction.CALL_FROM_CENTER)) {
                mMainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FreeObservable.getInstance().cancelObserveFree();
                    }
                }, 500);
                //显示请通话
                setCallState(R.string.call_please, R.color.txt_green);
                isCalling = false;
                isTalking = true;
                //底部按钮不能点击
                setMainBtnEnable();
            }
        }
    }


    private void setCallState(int stateId, int colorId) {
        mMainHandler.removeCallbacks(mCountdownTask);
        if (isAdded()) {
            tvTitle.setText(R.string.manager_center);
            tvState.setText(stateId);
            tvState.setTextColor(getResources().getColor(colorId));
            btnCall.setVisibility(View.GONE);
        }
    }

    /**
     * 拨号最后结束回调
     */
    @Override
    public void InterCallOutNone(int param) {
//        Log.e(TAG, "InterCallOutNone>>>>>>>" + "结束.... isPlaying" + isPlaying);
        if (isCalling || isTalking) {
            isCallEnd = true;
        }
        if (!isPlaying) {
            //退出界面
            backMain(true);
        }
    }

    /**
     * 呼叫中
     */
    @Override
    public void InterCallOutCalling(int param) {
        isCalling = true;
        isTalking = false;
//        LogUtils.w(TAG + " InterCallOutCalling isPlaying: " + isPlaying + " isBackRing: " + isBackRing);
        if (!isPlaying) {
            //呼叫中
            setCallState(R.string.call_calling, R.color.txt_green);
            if (!isBackRing) {
                isBackRing = true;
                PlaySoundUtils.playAssetsSoundLoop(CommStorePathDef.CALL_OUT_PATH, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                    @Override
                    public void onMediaStatusCompletion(boolean flag) {
                        isBackRing = false;
                    }
                });
            }
        }
    }

    /**
     * 请通话
     */
    @Override
    public void InterCallOutTalking(int param) {
        isPlaying = false;
        isCalling = false;
        isTalking = true;
        //关闭声音播放
        if (PlaySoundUtils.isLoopSound()) {
            PlaySoundUtils.stopPlayAssetsSound();
        }
        //请通话
        setCallState(R.string.call_please, R.color.txt_green);
        if (isPlaying) {
            isPlaying = false;
            showOpenDoorState(R.string.call_open_door, R.color.txt_white);
        }
    }


    @Override
    public void InterCallOutEnd(int param) {
        isCalling = false;
        isTalking = false;
        isCallEnd = true;
        if (!isPlaying) {
            //退出界面
            backMain(true);
        }
    }

    /**
     * 请留言
     */
    @Override
    public void InterCallOutRecording(int param) {
        //关闭声音播放
        PlaySoundUtils.stopPlayAssetsSound();
        isCalling = false;
        isTalking = true;
        isPlaying = false;
        //请留言
        setCallState(R.string.call_please_ly, R.color.txt_green);
    }

    /**
     * 留言提示中
     */
    @Override
    public void InterCallOutRecordHit(int param) {
        //关闭声音播放
        PlaySoundUtils.stopPlayAssetsSound();
        isCalling = false;
        isTalking = true;
        isPlaying = false;
        setCallState(R.string.call_recordhit, R.color.txt_green);
    }

    @Override
    public void InterCallOutTimer(int maxtime, int exittime) {

    }

    @Override
    public void InterCallOutMoveing(int param) {

    }

    @Override
    public void InterCallOutHitState(int wordhit, int voicehit) {
//        Log.e(TAG, "当前InterCallOutHitState>>>>>>>" + "wordhit>>>" + wordhit + ">>>>voicehit>>>>" + voicehit);
        //文字提示
        switch (wordhit) {
            //未知
            case CommTypeDef.CallConnectText.CALL_CONNECT_NONE:
                break;
            // 	连接超时
            case CommTypeDef.CallConnectText.CALL_CONNECT_TIMEOUT:
//                Log.e(TAG, "InterCallOutHitState>>>>>>>" + "连接超时");
                setCallState(R.string.call_connect_fail, R.color.txt_red);
                break;
            // 	设备繁忙
            case CommTypeDef.CallConnectText.CALL_CONNECT_BUSY:
//                Log.e(TAG, "InterCallOutHitState>>>>>>>" + "设备繁忙");
                setCallState(R.string.call_busy, R.color.txt_red);
                break;
            // 	无此房号
            case CommTypeDef.CallConnectText.CALL_CONNECT_NOROOMNO:
//                Log.e(TAG, "InterCallOutHitState>>>>>>>" + "无此房号");
                setCallState(R.string.call_no_num, R.color.txt_red);
                break;
            // 	无人接听
            case CommTypeDef.CallConnectText.CALL_CONNECT_NOT_HANDDOWN:
//                Log.e(TAG, "InterCallOutHitState>>>>>>>" + "无人接听");
                setCallState(R.string.call_no_respose, R.color.txt_red);
                break;
            // 	通话结束
            case CommTypeDef.CallConnectText.CALL_CONNECT_TALK_HANDDOWN:
//                Log.e(TAG, "====InterCallOutHitState>>>>>>>" + "通话结束");
                if (isTalking) {
                    setCallState(R.string.call_talk_end, R.color.txt_green);
                }
                break;
            // 	呼叫结束
            case CommTypeDef.CallConnectText.CALL_CONNECT_CALLING_END:
//                Log.e(TAG, "InterCallOutHitState>>>>>>>" + "呼叫结束");
                if (isCalling) {
                    setCallState(R.string.call_end, R.color.txt_green);
                }
                break;

        }
        //音频提示
        switch (voicehit) {
            //请稍后
            case CommTypeDef.VoiceHint.VOICE_HINT_CALL_PLSWAIT:
                isPlaying = true;
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1802_PATH, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                    @Override
                    public void onMediaStatusCompletion(boolean flag) {
                        isPlaying = false;
                    }
                });
                break;
            //您呼叫的住户暂时无人接听
            case CommTypeDef.VoiceHint.VOICE_HINT_CALL_TIMEOUT:
                isPlaying = true;
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1804_PATH, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                    @Override
                    public void onMediaStatusCompletion(boolean flag) {
                        if (flag) {
                            isPlaying = false;
//                            LogUtils.e("====CallCenterFragment  您呼叫的住户暂时无人接听" );
                            //退出界面
                            backMain(true);
                        }
                    }
                });
                break;
            //您呼叫的住户暂时无法接通
            case CommTypeDef.VoiceHint.VOICE_HINT_CALL_NOTCONNECT:
                isPlaying = true;
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1803_PATH, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                    @Override
                    public void onMediaStatusCompletion(boolean flag) {
                        if (flag) {
                            isPlaying = false;
//                            LogUtils.e("====CallCenterFragment  您呼叫的住户暂时无法接通" );
                            //退出界面
                            backMain(true);
                        }
                    }
                });
                break;
            //回铃声
            case CommTypeDef.VoiceHint.VOICE_HINT_CALL_RING:
                if (isPlaying) {
                    isBackRing = false;
                    return;
                }
                isBackRing = true;
                mFlCall.setVisibility(View.VISIBLE);
                mTvOpenDoor.setVisibility(View.GONE);
                //循环播放
                PlaySoundUtils.playAssetsSoundLoop(CommStorePathDef.CALL_OUT_PATH, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                    @Override
                    public void onMediaStatusCompletion(boolean flag) {
                        isBackRing = false;
                    }
                });
                break;
        }
    }

    @Override
    public void callBackValue(String param, String roomNo) {
        //刷卡状态
        switch (param) {
            case Const.CardAction.INVALID_CARD:
                setOpenDoorState(R.string.call_invalid_card, CommStorePathDef.VOICE_1503_PATH, roomNo);
                break;
            case Const.CardAction.OPNE_DOOR:
                setOpenDoorState(R.string.call_open_door, CommStorePathDef.VOICE_1501_PATH, roomNo);
                break;
            case Const.AlarmAction.ALARM_OPEN_DOOR:
                showOpenDoorState(R.string.call_no_close_door, R.color.txt_red);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideOpenDoorState();
                    }
                }, 3000);
                break;
            default:
                break;
        }
    }

    @Override
    public void callBack(CallMonitorBean callMonitorBean) {
        if (callMonitorBean != null) {
            boolean openDoor = callMonitorBean.isOpenDoor();
            boolean callEnd = callMonitorBean.isCallEnd();
            boolean callTalk = callMonitorBean.isCallTalk();
            //请通话
            if (callTalk) {
                isPlaying = false;
                isTalking = true;
                isCallEnd = false;
                //关闭声音播放
                PlaySoundUtils.stopPlayAssetsSound();
                setCallState(R.string.call_please, R.color.txt_green);
                //底部按钮不能点击
                setMainBtnEnable();
            }
            //门开了
            if (openDoor) {
//                LogUtils.e(TAG + "=========InterLock");
                setOpenDoorState(R.string.call_open_door, CommStorePathDef.VOICE_1501_PATH, null);
            }
            //通话结束
            if (callEnd) {
                if (isAdded()) {
//                    LogUtils.e(TAG + "==========callEnd");
                    setCallState(R.string.call_talk_end, R.color.txt_green);
                    isTalking = false;
                    isCallEnd = true;
                    //退出界面
                    backMain(true);
                }
            }
        }
    }

    private void setOpenDoorState(int textId, String ringPath, String roomNo) {
        if (isAdded() && interCommClient != null) {
            isPlaying = true;
            mFlCall.setVisibility(View.GONE);
            mTvOpenDoor.setVisibility(View.VISIBLE);
            mTvOpenDoor.setText(textId);
            //播放当前开门状态
            PlaySoundUtils.playAssetsSound(ringPath, roomNo, true, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                @Override
                public void onMediaStatusCompletion(boolean flag) {
                    if (flag) {
                        mFlCall.setVisibility(View.VISIBLE);
                        mTvOpenDoor.setVisibility(View.GONE);
                        isPlaying = false;
                    }

                    if (isCallEnd && !isPlaying) {
                        backMain(true);
                    }

                }
            });
        }
    }

    private class CountdownTask implements Runnable {

        private int mTime;

        @Override
        public void run() {
            mTime--;
            if (mTime >= 0) {
                tvState.setText(String.format(Locale.getDefault(), "%dS", mTime));
                mMainHandler.postDelayed(CountdownTask.this, 1000);
            } else {
                isCallEnd = true;
                if (isAdded()) {
                    if (!isPlaying && isCallEnd) {
                        GotoMainDefaultTask.getInstance().run();
                    }
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    class RefresHandle extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //拨号
                case VIEW_STATE_1:
                    //呼叫管理中心
                    int ret = interCommClient.InterCallCenter(nums, 0);
                    if (ret == 4) {
                        ret = interCommClient.InterCallCenter(CommTypeDef.DEVICE_MANAGER_NUMMIN, 0);
                    }
                    if (ret != 0) {
                        setCallState(R.string.call_no_reply, R.color.txt_red);
//                        LogUtils.e("====CallCenterFragment  无回答" );
                        if (!isPlaying) {
                            backMain(true);
                        }
                        isCallEnd = true;
                    } else {
                        isCalling = true;
                    }
                    break;
            }
        }
    }

    public void showOpenDoorState(int textId, int colorId) {
        mFlCall.setVisibility(View.GONE);
        mTvOpenDoor.setVisibility(View.VISIBLE);
        mTvOpenDoor.setText(textId);
        mTvOpenDoor.setTextColor(getResources().getColor(colorId));
    }

    public void hideOpenDoorState() {
        mFlCall.setVisibility(View.VISIBLE);
        mTvOpenDoor.setVisibility(View.GONE);
        mTvOpenDoor.setTextColor(getResources().getColor(R.color.txt_white));
    }
}
