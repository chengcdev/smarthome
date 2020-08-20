package com.mili.smarthome.tkj.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.CommStorePathDef;
import com.android.CommTypeDef;
import com.android.InterCommTypeDef;
import com.android.client.InterCommClient;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.call.CallManage;
import com.mili.smarthome.tkj.call.CallMonitorBean;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.inteface.IActCallBackListener;
import com.mili.smarthome.tkj.main.activity.MainActivity;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.utils.AppUtils;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.MediaPlayerUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;

import java.util.Locale;

public class CallCenterFragment extends BaseMainFragment implements View.OnClickListener,
        InterCommTypeDef.InterCallOutListener, IActCallBackListener {

    private TextView tvTitle;
    private TextView tvState;
    private LinearLayout btnCall;
    private LinearLayout btnCancel;

    private CountdownTask mCountdownTask = new CountdownTask();
    private InterCommClient interCommClient;
    private String TAG = "CallCenterFragment";
    //是否真正播放语音
    private boolean isPlaying;
    private LinearLayout mLinBtnCall;
    private TextView mTvOpenDoor;
    private ImageView mImaLeft;
    //当前是否没接通，处于呼叫中
    public static boolean isCalling;
    private int nums;
    private final int VIEW_STATE_1 = 0x001;
    private RefresHandle mRefrshHandle = new RefresHandle();
    public static boolean isTalking;
    //是否播过回铃声
    private boolean isHintCallRing = true;
    //是否呼叫结束
    private boolean isCallEnd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(getLayout(), container, false);
        initView(contentView);
        return contentView;
    }


    public void initView(View view) {
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvState = (TextView) view.findViewById(R.id.tv_state);
        btnCall = (LinearLayout) view.findViewById(R.id.btn_call);
        btnCancel = (LinearLayout) view.findViewById(R.id.btn_cancel);
        mLinBtnCall = (LinearLayout) view.findViewById(R.id.lin_btn_call);
        mTvOpenDoor = (TextView) view.findViewById(R.id.tv_open_door);
        mImaLeft = (ImageView) view.findViewById(R.id.img_left_icon);
        btnCall.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        //倒计时5秒
        startCount(6);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_call_center;
    }


    @Override
    public void onResume() {
        super.onResume();
        nums = 0;
        isCalling = false;
        isTalking = false;
        isHintCallRing = true;
        isCallEnd = false;
        //呼叫监听
        interCommClient = CallManage.getInstance().getInterCommClient();
        if (interCommClient != null) {
            interCommClient.setInterCallOutListener(this);
        }
        //管理中心监视呼叫通话回调
        callMonitor();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMainHandler.removeCallbacks(mCountdownTask);
        if (interCommClient != null && (isCalling || isTalking)) {
            interCommClient.InterHandDown();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //关闭红外补光灯
        SinglechipClientProxy.getInstance().ctrlCcdLed(0);
        mMainHandler.removeCallbacksAndMessages(null);
        mRefrshHandle.removeCallbacksAndMessages(null);
        isCalling = false;
        isTalking = false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        MainActivity activity = (MainActivity) context;
        activity.setActCallBackListener(this);
    }

    private void callMonitor() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            String param = bundle.getString(Const.CallAction.KEY_PARAM);
            if (param != null && param.equals(Const.CallAction.CALL_FROM_CENTER)) {
                hideOpenDoorView();
                //显示请通话
                setCallState(R.string.call_please, R.color.txt_green);
                isCalling = false;
                isTalking = true;
                //停止服务
                AppUtils.getInstance().stopScreenService();
                //主界面底部按钮不能点击
                setMainBtnEnable(false);
            }
        }
    }

    private void setOpenDoorState(final int textId, final int textColorId, final int resId, String ringPath, String roomNo) {
        if (isAdded() && interCommClient != null) {
            isPlaying = true;
            showOpenDoorView(textId, textColorId, resId);
            //播放当前开门状态
            PlaySoundUtils.playAssetsSound(ringPath, roomNo, true, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                @Override
                public void onMediaStatusCompletion(boolean flag) {
                    if (flag) {
                        hideOpenDoorView();
                        isPlaying = false;
                    }
                    if (isCallEnd && !isPlaying) {
                        startCount(-1);
                    }
                }
            });
        }
    }

    private void hideOpenDoorView() {
        mLinBtnCall.setVisibility(View.VISIBLE);
        mTvOpenDoor.setVisibility(View.GONE);
        mImaLeft.setVisibility(View.VISIBLE);
        mImaLeft.setImageResource(R.drawable.main_call_icon);
    }

    private void showOpenDoorView(int textId, int textColorId, int resId) {
        mLinBtnCall.setVisibility(View.GONE);
        mTvOpenDoor.setVisibility(View.VISIBLE);
        if (resId == -1) {
            mImaLeft.setVisibility(View.GONE);
        } else {
            mImaLeft.setVisibility(View.VISIBLE);
            mImaLeft.setImageResource(resId);
        }
        if (isAdded()) {
            mTvOpenDoor.setText(textId);
            mTvOpenDoor.setTextColor(getResources().getColor(textColorId));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_call:
                if (nums == Const.CallAction.CENTER_DEVNO) {
                    return;
                }

                nums = Const.CallAction.CENTER_DEVNO;
                setCallState(R.string.call_connecting, R.color.txt_green);
                mRefrshHandle.sendEmptyMessage(VIEW_STATE_1);
                break;
            case R.id.btn_cancel:
                isCallEnd = true;
                isPlaying = false;
                activeExitFragment();
                break;
        }
    }

    private void startCount(int i) {
        mCountdownTask.mTime = i;
        if (isPlaying) {
            if (i == 6) {
                mCountdownTask.run();
            }
        } else {
            if (i == 6) {
                mCountdownTask.run();
            } else {
                mMainHandler.postDelayed(mCountdownTask, 2000);
            }
        }
    }


    private void setCallState(int stateTextId, int stateColor) {
        if (isAdded()) {
            mMainHandler.removeCallbacks(mCountdownTask);
            tvTitle.setText(getString(R.string.manager_center));
            tvState.setText(getString(stateTextId));
            tvState.setTextColor(getResources().getColor(stateColor));
            btnCall.setVisibility(View.GONE);
        }
    }


    @Override
    public void InterCallOutNone(int param) {
        LogUtils.w(TAG + "InterCallOutNone=====" + param);
        if (isCalling || isTalking) {
            isCallEnd = true;
        }
        if (!isPlaying) {
            //退出界面
            startCount(-1);
        }
    }

    @Override
    public void InterCallOutCalling(int param) {
//        LogUtils.w(TAG + "InterCallOutCalling=====" + param);
        isCalling = true;
        //呼叫中
        if (!isPlaying) {
            setCallState(R.string.call_calling, R.color.txt_green);
            if (!isHintCallRing) {
                isHintCallRing = true;
                //回铃声循环播放
                PlaySoundUtils.playAssetsSoundLoop(CommStorePathDef.CALL_OUT_PATH, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                    @Override
                    public void onMediaStatusCompletion(boolean flag) {
                        isHintCallRing = false;
                    }
                });
            }
        }
    }

    @Override
    public void InterCallOutTalking(int param) {
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
            showOpenDoorView(R.string.call_open_door, R.color.txt_white, R.drawable.main_door_open);
        }
    }

    @Override
    public void InterCallOutEnd(int param) {
        LogUtils.w(TAG + "InterCallOutEnd=====" + param);
        isCalling = false;
        isTalking = false;
        isCallEnd = true;
        if (!isPlaying) {
            //退出界面
            startCount(-1);
        }
    }

    @Override
    public void InterCallOutRecording(int param) {
        //关闭声音播放
        PlaySoundUtils.stopPlayAssetsSound();
        isCalling = false;
        isPlaying = false;
        //请留言
        setCallState(R.string.call_please_ly, R.color.txt_green);
        isTalking = true;
    }

    @Override
    public void InterCallOutRecordHit(int param) {
        //关闭声音播放
        PlaySoundUtils.stopPlayAssetsSound();
        isCalling = false;
        isTalking = true;
        isPlaying = false;
        //留言提示中
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
//        LogUtils.w(TAG, "当前InterCallOutHitState>>>>>>>" + "wordhit>>>" + wordhit + ">>>>voicehit>>>>" + voicehit);
        //文字提示
        switch (wordhit) {
            //未知
            case CommTypeDef.CallConnectText.CALL_CONNECT_NONE:
                break;
            // 	连接超时
            case CommTypeDef.CallConnectText.CALL_CONNECT_TIMEOUT:
                setCallState(R.string.call_connect_fail, R.color.txt_red);
                break;
            // 	设备繁忙
            case CommTypeDef.CallConnectText.CALL_CONNECT_BUSY:
                setCallState(R.string.call_busy, R.color.txt_red);
                break;
            // 	无此房号
            case CommTypeDef.CallConnectText.CALL_CONNECT_NOROOMNO:
                setCallState(R.string.call_no_num, R.color.txt_red);
                break;
            // 	无人接听
            case CommTypeDef.CallConnectText.CALL_CONNECT_NOT_HANDDOWN:
                setCallState(R.string.call_no_respose, R.color.txt_red);
                break;
            // 	呼叫结束
            case CommTypeDef.CallConnectText.CALL_CONNECT_CALLING_END:
//                LogUtils.w(TAG + "InterCallOutHitState>>>>>>>" + "呼叫结束");
                if (isCalling) {
                    setCallState(R.string.call_end, R.color.txt_green);
                }
                break;
            // 	通话结束
            case CommTypeDef.CallConnectText.CALL_CONNECT_TALK_HANDDOWN:
                LogUtils.w(TAG + "InterCallOutHitState>>>>>>>" + "通话结束");
                if (isTalking) {
                    setCallState(R.string.call_talk_end, R.color.txt_green);
                }
                break;

        }
        //音频提示
        switch (voicehit) {
            //请稍后
            case CommTypeDef.VoiceHint.VOICE_HINT_CALL_PLSWAIT:
                isPlaying = true;
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1805_PATH, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                    @Override
                    public void onMediaStatusCompletion(boolean flag) {
                        isPlaying = false;
                    }
                });
                break;
            //您呼叫的住户暂时无人接听
            case CommTypeDef.VoiceHint.VOICE_HINT_CALL_TIMEOUT:
                isPlaying = true;
                hideOpenDoorView();
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1804_PATH, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                    @Override
                    public void onMediaStatusCompletion(boolean flag) {
                        if (flag) {
                            isPlaying = false;
                            //退出界面
                            startCount(-1);
                        }
                    }
                });
                break;
            //您呼叫的住户暂时无法接通
            case CommTypeDef.VoiceHint.VOICE_HINT_CALL_NOTCONNECT:
                isPlaying = true;
                hideOpenDoorView();
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1803_PATH, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                    @Override
                    public void onMediaStatusCompletion(boolean flag) {
                        if (flag) {
                            isPlaying = false;
                            //退出界面
                            startCount(-1);
                        }
                    }
                });
                break;
            //回铃声
            case CommTypeDef.VoiceHint.VOICE_HINT_CALL_RING:
                if (isPlaying) {
                    isHintCallRing = false;
                    return;
                }
                isHintCallRing = true;
                hideOpenDoorView();
//                LogUtils.w(TAG + " CommTypeDef.VoiceHint.VOICE_HINT_CALL_RING isHintCallRing: " + isHintCallRing);
                //循环播放
                PlaySoundUtils.playAssetsSoundLoop(CommStorePathDef.CALL_OUT_PATH, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                    @Override
                    public void onMediaStatusCompletion(boolean flag) {
                        isHintCallRing = false;
                    }
                });
                break;
        }
    }

    @Override
    public void callBackValue(String param, String roomNo) {
        switch (param) {
            //无效卡
            case Constant.IntentId.INTENT_INVALID_CARD:
                setOpenDoorState(R.string.call_invalid_card, R.color.txt_red, R.drawable.main_error_hit, CommStorePathDef.VOICE_1503_PATH, roomNo);
                break;
            //门开了进入
            case Constant.IntentId.INTENT_OPNE_DOOR:
                setOpenDoorState(R.string.call_open_door, R.color.txt_white, R.drawable.main_door_open, CommStorePathDef.VOICE_1501_PATH, roomNo);
                break;
            //报警，请关好门
            case Constant.IntentId.ALARM_CLOSE_DOOD:
//                LogUtils.w(TAG+"  callBackValue alarm");
                showOpenDoorView(R.string.call_no_close_door, R.color.txt_red, -1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideOpenDoorView();
                    }
                }, 3000);
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
//                LogUtils.w(TAG + "  callBack: 请通话");
                isPlaying = false;
                isTalking = true;
                isCallEnd = false;
                //关闭声音播放
                PlaySoundUtils.stopPlayAssetsSound();
                hideOpenDoorView();
                setCallState(R.string.call_please, R.color.txt_green);
                //主界面底部按钮不能点击
                setMainBtnEnable(false);
                //停止服务
                AppUtils.getInstance().stopScreenService();
            }
            //门开了
            if (openDoor) {
//                LogUtils.w(TAG + "  callBack: 门开了");
                setOpenDoorState(R.string.call_open_door, R.color.txt_white, R.drawable.main_door_open, CommStorePathDef.VOICE_1501_PATH, null);
            }
            //通话结束
            if (callEnd) {
                if (isAdded()) {
//                    LogUtils.w(TAG + "  callBack: 通话结束");
                    isTalking = false;
                    isCallEnd = true;
                    setCallState(R.string.call_talk_end, R.color.txt_green);
                    //退出界面
                    startCount(-1);
                }

            }
        }
    }

    private class CountdownTask implements Runnable {
        private int mTime;

        @Override
        public void run() {
            if (mTime == -1) {
                exitFragment();
            } else {
                mTime--;
                if (mTime >= 0) {
                    tvState.setText(String.format(Locale.getDefault(), "%dS", mTime));
                    mMainHandler.postDelayed(CountdownTask.this, 1000);
                } else {
                    isCallEnd = true;
                    exitFragment();
                }
            }

        }
    }

    public void exitFragment() {
        LogUtils.w(TAG + " exitFragment isPlaying: " + isPlaying + " isCallEnd: " + isCallEnd + " isTalking: " + isTalking);
        if (!AppUtils.getInstance().isMainFragment() && isAdded() && !isPlaying && isCallEnd) {
            activeExitFragment();
        }
    }

    public void activeExitFragment() {
        LogUtils.w(TAG + " activeExitFragment ");
        if (isTalking) {
            //关闭监视对讲
            CallManage.getInstance().stopCallMonitor();
        }
        if (interCommClient != null) {
            interCommClient.setInterCallOutListener(null);
            interCommClient.InterHandDown();
        }
        if (PlaySoundUtils.isLoopSound()) {
            PlaySoundUtils.stopPlayAssetsSound();
        }
        backMainActivity();
    }

    @SuppressLint("HandlerLeak")
    class RefresHandle extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //拨号
                case VIEW_STATE_1:
                    //关闭屏幕服务
                    AppUtils.getInstance().stopScreenService();
                    //主界面底部按钮不能点击
                    setMainBtnEnable(false);
                    if (interCommClient != null) {
                        //呼叫管理中心
                        int ret = interCommClient.InterCallCenter(nums, 0);
                        if (ret == 4) {
                            ret = interCommClient.InterCallCenter(CommTypeDef.DEVICE_MANAGER_NUMMIN, 0);
                        }
                        if (ret != 0) {
                            setCallState(R.string.call_no_reply, R.color.txt_red);
                            if (!isPlaying) {
                                startCount(-1);
                            }
                            isCallEnd = true;
                        } else {
                            isCalling = true;
                        }
                    } else {
                        setCallState(R.string.call_no_reply, R.color.txt_red);
                        if (!isPlaying) {
                            startCount(-1);
                        }
                        isCallEnd = true;
                    }
                    break;
            }
        }
    }
}
