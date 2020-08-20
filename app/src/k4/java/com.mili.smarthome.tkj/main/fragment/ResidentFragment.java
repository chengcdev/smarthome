package com.mili.smarthome.tkj.main.fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.CommStorePathDef;
import com.android.CommTypeDef;
import com.android.InterCommTypeDef;
import com.android.client.InterCommClient;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.base.K4Config;
import com.mili.smarthome.tkj.base.KeyboardCtrl;
import com.mili.smarthome.tkj.call.CallManage;
import com.mili.smarthome.tkj.call.CallMonitorBean;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.main.adapter.NumBitmapAdapter;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.MediaPlayerUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;
import com.mili.smarthome.tkj.widget.MultiImageView;
import com.mili.smarthome.tkj.widget.NumInputView;


public class ResidentFragment extends K4BaseFragment {

    public static final String KEY_TEXT = "text";

    private static final String TAG = "ResidentFragment";
    private TextView mTvDesc;
//    private NumberView mNvDevno;
    private MultiImageView mNvDevno;
    private NumBitmapAdapter mAdapter;
    private TextView mTvInputHint, mTvCallState;
    private LinearLayout mLlArea, mTvHint1;
    private NumInputView mNvBuilding, mNvUnit, mNvRoomno;
    private TextView mTvHint2;
    private TextView mTvAreaCallno;
    private TextView mTvMonitorHint, mTvDoorHint;

    private int mRoomLen = 4;
    private int mDevType;

    private InterCommClient mInterCommClient;
    private FullDeviceNo mFullDeviceNo;
    private boolean mIsCalling = false;
    private boolean mIsTalking = false;

    /**0-提示输入模式 1-房号模式 2-呼叫模式 3-监视模式*/
    private int mUiState = 0;

    /*本次进入界面是否呼叫过 false 未呼叫过*/
    private boolean mCalled = false;

    @Override
    public boolean onTextChanged(String text) {
        super.onTextChanged(text);

        // 区口机时不处理
        if (mDevType != CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            return true;
        }

        if (!K4Config.getInstance().getCallState() && !K4Config.getInstance().getMonitorTalk()) {
            if (mNvDevno != null) {
                mAdapter.setText(text);
            }
            if (text.length() > 0) {
                showView(1);
            }
            setFingerDevNo();
        }
        return true;
    }

    @Override
    public boolean onKey(int code) {
        // 梯口机时不处理
        if (mDevType != CommTypeDef.DeviceType.DEVICE_TYPE_AREA) {
            return true;
        }

        if (!K4Config.getInstance().getCallState() && !K4Config.getInstance().getMonitorTalk()) {
            LogUtils.d(TAG + " onKey code is " + code);
            inputNum(code);
            showView(1);
            setFingerDevNo();
        }
        return true;
    }

    @Override
    public boolean onKeyConfirm() {
        super.onKeyConfirm();
        if (!K4Config.getInstance().getCallState() && !K4Config.getInstance().getMonitorTalk()) {
            callResident();
            mCalled = true;
        }
        return true;
    }

    @Override
    public boolean onKeyCancel() {
        super.onKeyCancel();

        boolean callstate = K4Config.getInstance().getCallState();
        boolean monitorTalk = K4Config.getInstance().getMonitorTalk();
        LogUtils.d(TAG + " onKeyCancel: callstate is " + callstate + ", monitorTalk is " + monitorTalk);
        LogUtils.d(TAG + " onKeyCancel: isCalling = " + mIsCalling + ", isTalking = " + mIsTalking);
        if (!callstate && !monitorTalk) {
            if (mDevType == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
                String text = mAdapter.getText();
                if (text == null || text.length() <= 0) {
                    requestBack();
                } else if (text.length() == 1) {
                    showView(0);
                }
            } else {
                backspace();
            }
            setFingerDevNo();
        } else {
            handDown();
            requestBack();
        }
        return true;
    }

    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case MSG_CALL_HINT:
                showCallHint(R.string.call_no_num, R.color.txt_red, true);
                break;
            case MSG_REQUEST_EXIT:
                requestBack();
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_resident;
    }

    @Override
    protected void bindView() {
        super.bindView();

        mTvDesc = findView(R.id.tv_desc);
        mNvDevno = findView(R.id.nv_devno);
        mTvInputHint = findView(R.id.tv_inputhint);

        mLlArea = findView(R.id.ll_area);
        mNvBuilding = findView(R.id.nv_building);
        mNvUnit = findView(R.id.nv_unit);
        mNvRoomno = findView(R.id.nv_roomno);
        mTvAreaCallno = findView(R.id.tv_areaCallno);

        mTvCallState = findView(R.id.tv_callstate);
        mTvHint1 = findView(R.id.tv_hint1);
        mTvHint2 = findView(R.id.tv_hint2);

        mTvMonitorHint = findView(R.id.tv_monitorHint);
        mTvDoorHint = findView(R.id.tv_doorHint);

        mNvDevno.setAdapter(mAdapter = new NumBitmapAdapter(mContext));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setKeyboardMode(KeyboardCtrl.KEYMODE_CALL);
        setKeyboardText("");
        setKeyboardMaxlen(mRoomLen);
        setDevDesc();
        mCalled = false;

        if (mFullDeviceNo == null) {
            mFullDeviceNo = new FullDeviceNo(getContext());
        }
        mDevType = mFullDeviceNo.getDeviceType();
        if (mDevType == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            mRoomLen = mFullDeviceNo.getRoomNoLen();
            setKeyboardMaxlen(mRoomLen);
//            mNvDevno.setMode(NumberView.MODE_CALL);
//            mNvDevno.setLen(0, mRoomLen);
        } else {
            // 根据编号规则设置相应控件的长度
            int stairlen = mFullDeviceNo.getStairNoLen();
            int roomLen = mFullDeviceNo.getRoomNoLen();
            int unitlen = mFullDeviceNo.getCellNoLen();
            int buildlen = stairlen - unitlen;
            mRoomLen = stairlen + roomLen;
            LogUtils.d(TAG + " stairlen=" + stairlen + ", roomlen=" + roomLen + ", unitlen=" + unitlen + ", buildlen=" + buildlen);
            setKeyboardMaxlen(mRoomLen);
            mNvBuilding.setMaxLength(buildlen);
            mNvUnit.setMaxLength(unitlen);
            mNvRoomno.setMaxLength(roomLen);
            mNvBuilding.requestFocus();

            mNvBuilding.setText("");
            mNvUnit.setText("");
            mNvRoomno.setText("");
        }
        showView(0);

        //预输入房号
        String arguText;
        Bundle bundle = getArguments();
        if (bundle != null) {
            arguText = bundle.getString(KEY_TEXT);
            LogUtils.d(TAG + " argument text is " + arguText);

            if (arguText != null && arguText.length() > 0) {
                switch (mDevType) {
                    case CommTypeDef.DeviceType.DEVICE_TYPE_STAIR:
                        mAdapter.setText(arguText);
                        mNvDevno.setVisibility(View.VISIBLE);
                        setKeyboardText(arguText);
                        break;
                    case CommTypeDef.DeviceType.DEVICE_TYPE_AREA:
                        inputNum(Integer.parseInt(arguText));
                        setKeyboardText(arguText);
                        break;
                }
                setFingerDevNo();
                showView(1);
            }
        }

        initListener();
        boolean ret = showMonitorTalk();
        if (!ret) {
            PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1201_PATH);
        }
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
        LogUtils.d(TAG + " ====== onDestroy ====== ");
    }

    private void showView(int state) {
        mUiState = state;
        switch (state) {
            case 0:     // 默认状态
                if (mDevType == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
                    mTvInputHint.setVisibility(View.VISIBLE);
                    mNvDevno.setVisibility(View.GONE);
                    mLlArea.setVisibility(View.GONE);
                    mTvAreaCallno.setVisibility(View.GONE);
                } else {
                    mTvInputHint.setVisibility(View.GONE);
                    mNvDevno.setVisibility(View.GONE);
                    mLlArea.setVisibility(View.VISIBLE);
                    mTvAreaCallno.setVisibility(View.GONE);
                }
                mTvCallState.setVisibility(View.GONE);
                mTvHint1.setVisibility(View.VISIBLE);
                mTvHint2.setVisibility(View.GONE);
                mTvDoorHint.setVisibility(View.GONE);
                mTvMonitorHint.setVisibility(View.GONE);
                break;
            case 1:     // 输入房号状态
                if (mDevType == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
                    mTvInputHint.setVisibility(View.GONE);
                    mNvDevno.setVisibility(View.VISIBLE);
                    mLlArea.setVisibility(View.GONE);
                    mTvAreaCallno.setVisibility(View.GONE);
                } else {
                    mTvInputHint.setVisibility(View.GONE);
                    mNvDevno.setVisibility(View.GONE);
                    mLlArea.setVisibility(View.VISIBLE);
                    mTvAreaCallno.setVisibility(View.GONE);
                }
                mTvCallState.setVisibility(View.GONE);
                mTvHint1.setVisibility(View.VISIBLE);
                mTvHint2.setVisibility(View.GONE);
                mTvDoorHint.setVisibility(View.GONE);
                mTvMonitorHint.setVisibility(View.GONE);
                break;
            case 2:     // 呼叫状态
                if (mDevType == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
                    mTvInputHint.setVisibility(View.GONE);
                    mNvDevno.setVisibility(View.VISIBLE);
                    mLlArea.setVisibility(View.GONE);
                    mTvAreaCallno.setVisibility(View.GONE);
                } else {
                    mTvInputHint.setVisibility(View.GONE);
                    mNvDevno.setVisibility(View.GONE);
                    mLlArea.setVisibility(View.GONE);
                    mTvAreaCallno.setVisibility(View.VISIBLE);
                }
                mTvCallState.setVisibility(View.VISIBLE);
                mTvHint1.setVisibility(View.GONE);
                mTvHint2.setVisibility(View.VISIBLE);
                mTvDoorHint.setVisibility(View.GONE);
                mTvMonitorHint.setVisibility(View.GONE);
                break;
            case 3:     //监视状态
                mTvInputHint.setVisibility(View.GONE);
                mNvDevno.setVisibility(View.GONE);
                mLlArea.setVisibility(View.GONE);
                mTvAreaCallno.setVisibility(View.GONE);
                mTvCallState.setVisibility(View.GONE);
                mTvHint1.setVisibility(View.GONE);
                mTvHint2.setVisibility(View.VISIBLE);
                mTvDoorHint.setVisibility(View.GONE);
                mTvMonitorHint.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 显示开门提示
     */
    private void showDoorHint(boolean show, int textId, int colorId) {
        if (mUiState != 2 && mUiState != 3) {
            LogUtils.d(" uistate is not in calling or monitor.");
            return;
        }
        LogUtils.d(TAG + " showDoorHint: show is " + show + ", mUiState is " + mUiState);
        switch (mUiState) {
            case 2:
                if (show) {
                    mTvDoorHint.setText(textId);
                    mTvDoorHint.setTextColor(getResources().getColor(colorId));
                    mTvDoorHint.setVisibility(View.VISIBLE);
                    mNvDevno.setVisibility(View.GONE);
                    mTvAreaCallno.setVisibility(View.GONE);
                    mTvCallState.setVisibility(View.GONE);
                } else {
                    showView(mUiState);
                }
                break;
            case 3:
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
     * 设备描述，根据内容长度调整字体大小
     */
    private void setDevDesc() {
        if (mTvDesc != null) {
            String devDesc = K4Config.getInstance().getDeviceDesc(true);
            mTvDesc.setText(devDesc);
        }
    }

    private void initListener() {
        mInterCommClient = CallManage.getInstance().getInterCommClient();
        mInterCommClient.setInterCallOutListener(mCallStateListener);
    }

    private boolean showMonitorTalk() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            String param = bundle.getString(Const.CallAction.KEY_PARAM);
            if (param != null && param.equals(Const.CallAction.CALL_FROM_RESIDENT)) {
                //显示请通话
                showView(3);
                mTvMonitorHint.setText(R.string.call_please);
                K4Config.getInstance().setMonitorTalk(true);
                setMainClickable(1, false);
                return true;
            }
        }
        return false;
    }

    private void showCallHint(int resId, int colorId, boolean isBack) {
        showView(2);
        mTvCallState.setText(resId);
        mTvCallState.setTextColor(getResources().getColor(colorId));
        if (isBack) {
            mMainHandler.removeMessages(MSG_REQUEST_EXIT);
            mMainHandler.sendEmptyMessageDelayed(MSG_REQUEST_EXIT, Constant.MAIN_HINT_TIMEOUT);
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

    private void callResident() {
        StringBuilder buffer = new StringBuilder();
        if (mDevType == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            String devno = mAdapter.getText();
            if (devno.length() < mRoomLen - 1) {
                LogUtils.d(TAG + " devlen < roomlen - 1");
                return;
            }

            if (devno.length() == mRoomLen - 1) {
                buffer.append('0').append(devno);
            } else {
                buffer.append(devno);
            }
        } else {
            String building = mNvBuilding.getText().toString();
            String unitText = mNvUnit.getText().toString();
            String roomNo = mNvRoomno.getText().toString();
            if (building.length() + unitText.length() + roomNo.length() != mRoomLen) {
                LogUtils.d(TAG + " roomlen is " + mRoomLen);
                return;
            }

            buffer.append(building).append(unitText).append(roomNo);
        }
        String callNo = buffer.toString();
        LogUtils.d(TAG + " callResident: roomNo = " + callNo);

        K4Config.getInstance().setCallState(true);
        mIsCalling = true;
        if (mDevType == CommTypeDef.DeviceType.DEVICE_TYPE_AREA) {
            mTvAreaCallno.setText(callNo);
        }
        showView(2);
        showCallHint(R.string.call_connect_loading, R.color.txt_green, false);
        new Thread(new CallResidentTask(callNo)).start();
    }

    private class CallResidentTask implements Runnable {
        private String mRoomNo;

        private CallResidentTask(String roomNo) {
            mRoomNo = roomNo;
        }

        @Override
        public void run() {
            if (mInterCommClient != null) {
                int code = mInterCommClient.InterCallRoom(mRoomNo);
                if (code != 0) {
                    mMainHandler.sendEmptyMessage(MSG_CALL_HINT);
                    K4Config.getInstance().setCallState(false);
                    mIsCalling = false;
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
                LogUtils.d(TAG + "[InterCallOutHitState] mCalled is false");
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
                    PlaySoundUtils.playAssetsSoundLoop(CommStorePathDef.CALL_OUT_PATH, null);
                    showDoorHint(false, 0, 0);
                    break;
            }
        }
    };

    @Override
    public void actionCallback(String action, String roomNo) {
        LogUtils.d(TAG + " actionCallback: action is " + action);
        // 呼叫过程刷卡不进行界面提示，只提示声音
        String ringPath;
        int textId, colorId;
        switch (action) {
            case Const.CardAction.INVALID_CARD:
                ringPath = CommStorePathDef.VOICE_1503_PATH;
                textId = R.string.call_invalid_card;
                colorId = R.color.txt_red;
                break;
            case Const.CardAction.OPNE_DOOR:
                ringPath = CommStorePathDef.VOICE_1501_PATH;
                textId = R.string.call_open_door;
                colorId = R.color.txt_white;
                break;
            case Constant.HintAction.ALARM_HINT:
                ringPath = null;
                textId = R.string.comm_text_d0;
                colorId = R.color.txt_red;
                break;
            default:
                return;
        }

        //提示文字
        showDoorHint(true, textId, colorId);

        //播放刷卡提示音，若当前在呼叫中则恢复回铃声的播放
        if (action.equals(Constant.HintAction.ALARM_HINT)) {
            mMainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showDoorHint(false, 0, 0);
                }
            }, 4000);
            return;
        }

        PlaySoundUtils.playAssetsSound(ringPath, roomNo, false, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
            @Override
            public void onMediaStatusCompletion(boolean flag) {
                LogUtils.d(TAG + " actionCallback flag is " + flag + ", isCalling is " + mIsCalling);
                if (flag) {
                    if (mIsCalling) {
                        //循环播放回铃声
                        PlaySoundUtils.playAssetsSoundLoop(CommStorePathDef.CALL_OUT_PATH, null);
                    }
                }
                showDoorHint(false, 0, 0);
            }
        });
    }

    @Override
    public void onMonitor(CallMonitorBean callMonitorBean) {
        //解决通话时Client连续分发两次，导致第二次分发时控件未创建的异常问题
        if (mTvCallState == null) {
            LogUtils.d(TAG + " onMonitor: mHvHint is null.");
            return;
        }
        super.onMonitor(callMonitorBean);
        if (callMonitorBean != null) {
            boolean openDoor = callMonitorBean.isOpenDoor();
            boolean callEnd = callMonitorBean.isCallEnd();
            boolean callTalk = callMonitorBean.isCallTalk();
            LogUtils.d(TAG + " openDoor=" + openDoor + ", callEnd=" + callEnd + ", calltalk=" + callTalk);
            //请通话
            if (callTalk) {
                K4Config.getInstance().setMonitorTalk(true);
                setMainClickable(1, false);
                mMainHandler.removeMessages(MSG_REQUEST_EXIT);
                //关闭声音播放
                PlaySoundUtils.stopPlayAssetsSound();
                showView(3);
                mTvMonitorHint.setText(R.string.call_please);
                mAdapter.clear();
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
                showView(3);
                mTvMonitorHint.setText(R.string.call_talk_end);
                //退出界面
                mMainHandler.sendEmptyMessageDelayed(MSG_REQUEST_EXIT, Constant.MAIN_HINT_TIMEOUT);
            }
        }
    }

    /**
     * 按照房号识别指纹
     */
    private void setFingerDevNo() {
        String fingerDevno;
        if (mDevType == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            fingerDevno = mAdapter.getText();
        } else {
            StringBuilder devNo = new StringBuilder();
            devNo.append(mNvBuilding.getText());
            if (mNvBuilding.getText().length() == mNvBuilding.getMaxLength()) {
                devNo.append(mNvUnit.getText());
                if (mNvUnit.getText().length() == mNvUnit.getMaxLength()) {
                    devNo.append(mNvRoomno.getText());
                }
            }
            fingerDevno = devNo.toString();
        }
        SinglechipClientProxy.getInstance().setFingerDevNo(fingerDevno);
        LogUtils.d(TAG + " setFingerDevNo: devno is " + fingerDevno);
    }
}
