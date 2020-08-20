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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.CommStorePathDef;
import com.android.CommTypeDef;
import com.android.InterCommTypeDef;
import com.android.client.InterCommClient;
import com.android.provider.FullDeviceNo;
import com.android.provider.RoomSubDest;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.adapter.KeyBoardAdapter;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.call.CallManage;
import com.mili.smarthome.tkj.call.CallMonitorBean;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.entity.KeyBoardBean;
import com.mili.smarthome.tkj.inteface.IActCallBackListener;
import com.mili.smarthome.tkj.main.activity.MainActivity;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.utils.AppUtils;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.MediaPlayerUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;
import com.mili.smarthome.tkj.view.KeyBoardView;
import com.mili.smarthome.tkj.view.NumberView;
import com.mili.smarthome.tkj.widget.InputView;
import com.mili.smarthome.tkj.widget.NumInputView;

public class CallResidentFragment extends BaseMainFragment implements KeyBoardAdapter.IKeyBoardListener,
        NumberView.INumViewListener, InterCommTypeDef.InterCallOutListener, IActCallBackListener {

    private KeyBoardView keyBoardView;
    private NumberView mNumView;
    private LinearLayout mLinCallState;
    private TextView mTvCallRoomNo;
    private TextView mTvCallState;
    private TextView mTvDelete;
    private RelativeLayout mRlCallLeft;
    private TextView mTvDoorOpen;
    private String roomNo = "";
    private String TAG = "CallResidentFragment";
    private CountTimeRun countTimeRun = new CountTimeRun();
    //是否在呼叫
    public static boolean isCalling;
    //是否在通话
    public static boolean isTalking;
    private RefresHandle mRefrshHandle = new RefresHandle();
    //是否正在播放语音
    private boolean isPlaying;
    //拨号
    private final int VIEW_STATE_1 = 0x00;
    //显示门开了view
    private final int VIEW_STATE_2 = 0x01;
    //无效卡
    private final int VIEW_STATE_3 = 0x02;
    //报警，请关好门
    private final int VIEW_STATE_4 = 0x03;
    //刷卡显示门开了view
    private final int VIEW_STATE_5 = 0x04;
    private InterCommClient interCommClient;
    private View mLinCallArea;
    private NumInputView mTvBuildNo;
    private NumInputView mTvUnitNo;
    private NumInputView mTvRoomNo;
    private LinearLayout mLinBtnCall;
    private FullDeviceNo fullDeviceNo;
    //是否是梯口机
    private boolean isStair = false;
    private int stairNoLen;
    private int cellNoLen;
    private int roomNoLen;
    private String currentBuildNo = "";
    private String currentUnitNo = "";
    private String currentRoomNo = "";
    private int buildNoLen;
    private boolean isMonitor;
    private RelativeLayout mRlConfirm;
    private TextView mTvLocation;
    private TextView mTvDeviceNo;
    private RoomSubDest roomSubDest;
    //是否播过回铃声
    private boolean isHintCallRing = true;
    //是否呼叫结束
    private boolean isCallEnd;
    private LinearLayout mLlUnitNo;

    public void initView(View view) {
        keyBoardView = (KeyBoardView) view.findViewById(R.id.keyboardview);
        mNumView = (NumberView) view.findViewById(R.id.num_view);
        mLinCallState = (LinearLayout) view.findViewById(R.id.lin_call);
        mTvCallRoomNo = (TextView) view.findViewById(R.id.tv_call_room_no);
        mTvCallState = (TextView) view.findViewById(R.id.tv_call_state);
        mTvDelete = (TextView) view.findViewById(R.id.tv_delete);
        mRlConfirm = (RelativeLayout) view.findViewById(R.id.rl_confirm);
        mRlCallLeft = (RelativeLayout) view.findViewById(R.id.rl_call_left);
        mTvDoorOpen = (TextView) view.findViewById(R.id.tv_door_oper);
        mLinCallArea = view.findViewById(R.id.lin_call_area);
        mTvBuildNo = (NumInputView) view.findViewById(R.id.tv_build_no);
        mTvUnitNo = (NumInputView) view.findViewById(R.id.tv_unit_no);
        mTvRoomNo = (NumInputView) view.findViewById(R.id.tv_room_no);
        mLinBtnCall = (LinearLayout) view.findViewById(R.id.lin_btn_call);
        mTvLocation = (TextView) view.findViewById(R.id.tv_location);
        mTvDeviceNo = (TextView) view.findViewById(R.id.tv_deviceno);
        mLlUnitNo = (LinearLayout) view.findViewById(R.id.ll_unit_no);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(getLayout(), container, false);
        initView(contentView);
        return contentView;
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_call_resident;
    }

    @Override
    public void onResume() {
        super.onResume();
        initTopView();
        isCalling = false;
        isPlaying = false;
        isTalking = false;
        isHintCallRing = true;
        isCallEnd = false;
        //在呼叫住户界面
        mRlCallLeft.setVisibility(View.VISIBLE);
        mTvDoorOpen.setVisibility(View.GONE);
        keyBoardView.init(KeyBoardView.KEY_BOARD_CALL);
        keyBoardView.setKeyBoardListener(this);

        if (fullDeviceNo == null) {
            fullDeviceNo = new FullDeviceNo(getContext());
        }
        roomNoLen = fullDeviceNo.getRoomNoLen();
        int deviceType = fullDeviceNo.getDeviceType();
        if (deviceType == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            isStair = true;
        } else {
            isStair = false;
        }
        if (isStair) {
            mNumView.setVisibility(View.VISIBLE);
            mLinCallArea.setVisibility(View.GONE);
            mLinBtnCall.setVisibility(View.VISIBLE);
            mNumView.setNumListener(this);
            mNumView.init(getString(R.string.set_input_room_no), roomNoLen);
        } else {
            mNumView.setVisibility(View.GONE);
            mLinCallArea.setVisibility(View.VISIBLE);
            mLinBtnCall.setVisibility(View.GONE);
            //是否启动单元号
            if (fullDeviceNo.getUseCellNo() == 1) {
                mLlUnitNo.setVisibility(View.VISIBLE);
            } else {
                mLlUnitNo.setVisibility(View.GONE);
            }
            //区口拨号设置
            setAreaCallInput();
        }

        //呼叫监听
        initListener();
        //监视呼叫通话回调
        callMonitorBack();
    }

    private void initTopView() {
        mTvLocation.setText(getString(R.string.main_location));
        //显示编号规则
        if (roomSubDest == null) {
            roomSubDest = new RoomSubDest(getContext());
        }
        String subDestDevNumber = roomSubDest.getSubDestDevNumber();
        mTvDeviceNo.setText(subDestDevNumber);
    }

    private void setAreaCallInput() {
        //梯号长度
        stairNoLen = fullDeviceNo.getStairNoLen();
        //单元号长度
        cellNoLen = fullDeviceNo.getCellNoLen();
        //楼栋号长度
        buildNoLen = stairNoLen - cellNoLen;

        mTvBuildNo.setMaxLength(buildNoLen);
        mTvRoomNo.setMaxLength(roomNoLen);
        mTvUnitNo.setMaxLength(cellNoLen);
        if (buildNoLen <= 0) {
            //是否启用单元号
            if (fullDeviceNo.getUseCellNo() == 1 && cellNoLen > 0) {
                mTvUnitNo.requestFocus();
            } else {
                mTvRoomNo.requestFocus();
            }
        } else {
            mTvBuildNo.requestFocus();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        MainActivity activity = (MainActivity) context;
        activity.setActCallBackListener(this);
    }

    private void callMonitorBack() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            String param = bundle.getString(Const.CallAction.KEY_PARAM);
            if (param != null && param.equals(Const.CallAction.CALL_FROM_RESIDENT)) {
                //显示请通话
                setCallState(getString(R.string.call_please), R.color.txt_green);
                mNumView.setVisibility(View.GONE);
                mTvCallRoomNo.setVisibility(View.GONE);
                isCalling = false;
                isTalking = true;
                //停止服务
                AppUtils.getInstance().stopScreenService();
                //主界面底部按钮不能点击
                setMainBtnEnable(false);
            }
        }
    }

    private void initListener() {
        interCommClient = CallManage.getInstance().getInterCommClient();
        if (interCommClient != null) {
            interCommClient.setInterCallOutListener(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (interCommClient != null && (isTalking || isCalling)) {
            interCommClient.InterHandDown();
        }
        mMainHandler.removeCallbacks(countTimeRun);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //关闭启红外补光灯
        SinglechipClientProxy.getInstance().ctrlCcdLed(0);
        isCalling = false;
        isTalking = false;
    }

    @Override
    public void onKeyBoardListener(View view, int potion, KeyBoardBean keyBoardBean) {
        switch (keyBoardBean.getkId()) {
            case Const.KeyBoardId.KEY_CANCEL:
                if (isCalling || isTalking || isPlaying) {
                    exitFragment(false);
                    return;
                }
                if (isStair) {
                    //梯口机
                    boolean last = mNumView.removeNum();
                    if (last) {
                        //退出界面
                        exitFragment(false);
                    }
                } else {
                    //区口机
                    backspace();
                }
                break;
            case Const.KeyBoardId.KEY_CALL:
                //呼叫
                startCall();
                break;
            default:
                if (isTalking || isCalling) {
                    return;
                }
                if (isStair) {
                    mNumView.addNum(keyBoardBean.getkId(), NumberView.NUM_TYPE_CALL);
                } else {
                    String id = keyBoardBean.getkId();
                    inputNum(Integer.valueOf(id));
                }
                setFingerDevNo();
                break;
        }
    }


    @Override
    public void getNum(String num) {
        roomNo = num;
    }

    private void startCall() {
        if (isStair) {
            //梯口机呼叫
            if (roomNo.length() >= 3 && !isCalling && !isPlaying && !isTalking) {
                toCall();
            }
        } else {
            currentBuildNo = mTvBuildNo.getText().toString();
            currentUnitNo = mTvUnitNo.getText().toString();
            currentRoomNo = mTvRoomNo.getText().toString();

            //区口机呼叫
            if (currentBuildNo.length() < buildNoLen || currentUnitNo.length() < cellNoLen
                    || currentRoomNo.length() < roomNoLen || isPlaying || isCalling || isTalking) {
                return;
            }
            roomNo = currentBuildNo + currentUnitNo + currentRoomNo;
            toCall();
        }
    }

    private void toCall() {
        isCalling = true;
        setCallState(getString(R.string.call_connecting), R.color.txt_green);
        //停止屏幕服务
        AppUtils.getInstance().stopScreenService();
        //主界面底部按钮不能点击
        setMainBtnEnable(false);
        mRefrshHandle.sendEmptyMessage(VIEW_STATE_1);
    }

    private void setCallState(String stateText, int stateColor) {
        if (isAdded()) {
            mRlCallLeft.setVisibility(View.VISIBLE);
            mTvDoorOpen.setVisibility(View.GONE);
            if (roomNo.equals("")) {
                mNumView.setVisibility(View.GONE);
            } else {
                mNumView.setVisibility(View.VISIBLE);
            }
            if (isStair) {
                mTvCallRoomNo.setVisibility(View.INVISIBLE);
            } else {
                mTvCallRoomNo.setVisibility(View.VISIBLE);
            }
            mRlConfirm.setVisibility(View.GONE);
            mLinCallArea.setVisibility(View.GONE);
            mLinCallState.setVisibility(View.VISIBLE);
            mLinBtnCall.setVisibility(View.VISIBLE);
            mTvCallRoomNo.setText(roomNo);
            mTvCallState.setText(stateText);
            mTvCallState.setTextColor(getResources().getColor(stateColor));
            mTvDelete.setText(getString(R.string.contrl_return));
        }
    }

    @Override
    public void InterCallOutNone(int param) {
        if (isCalling || isTalking) {
            isCallEnd = true;
        }
        LogUtils.w(TAG + "  InterCallOutNone>>>>>>>" + "结束：" + isPlaying);
        if (!isPlaying) {
            //退出界面
            exitFragment(true);
        }
    }

    @Override
    public void InterCallOutCalling(int param) {
        LogUtils.w(TAG + "  当前InterCallOutCalling>>>>>>>" + param + " isPlaying: " + isPlaying);
        isCalling = true;
        //呼叫中
        if (!isPlaying) {
            setCallState(getString(R.string.call_calling), R.color.txt_green);
            if (!isHintCallRing) {
                isHintCallRing = true;
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
//        LogUtils.w(TAG + "  InterCallOutTalking>>>>>>>" + "请通话" + isPlaying);
        setCallState(getString(R.string.call_please), R.color.txt_green);
        if (isPlaying) {
            isPlaying = false;
            showOpenDoorState(R.string.call_open_door, R.color.txt_white);
        }
    }

    @Override
    public void InterCallOutEnd(int param) {
        isCallEnd = true;
        LogUtils.w(TAG + "  InterCallOutEnd>>>>>>>" + "结束：" + isPlaying);
        if (!isPlaying) {
            //退出界面
            exitFragment(true);
        }
    }

    @Override
    public void InterCallOutRecording(int param) {
        //关闭声音播放
        PlaySoundUtils.stopPlayAssetsSound();
        //请留言
        isCalling = false;
        isTalking = true;
        isPlaying = false;
        setCallState(getString(R.string.call_please_ly), R.color.txt_green);
//        LogUtils.w(TAG + "  InterCallOutRecording>>>>>>>" + "请留言");

    }

    @Override
    public void InterCallOutRecordHit(int param) {
        //关闭声音播放
        PlaySoundUtils.stopPlayAssetsSound();
        //请留言
        isCalling = false;
        isTalking = true;
        isPlaying = false;
        //留言提示中
        setCallState(getString(R.string.call_recordhit), R.color.txt_green);

//        LogUtils.w(TAG + "  InterCallOutRecordHit>>>>>>>" + "留言提示中");
    }

    @Override
    public void InterCallOutTimer(int maxtime, int exittime) {

    }

    @Override
    public void InterCallOutMoveing(int param) {

    }

    @Override
    public void InterCallOutHitState(int wordhit, int voicehit) {
//        LogUtils.w(TAG + "  当前InterCallOutHitState>>>>>>>" + "wordhit>>>" + wordhit + ">>>>voicehit>>>>" + voicehit);
        //文字提示
        switch (wordhit) {
            //未知
            case CommTypeDef.CallConnectText.CALL_CONNECT_NONE:
                break;
            // 	连接超时
            case CommTypeDef.CallConnectText.CALL_CONNECT_TIMEOUT:
//                LogUtils.w(TAG + "  InterCallOutHitState>>>>>>>" + "连接超时");
                setCallState(getString(R.string.call_connect_fail), R.color.txt_red);
                break;
            // 	设备繁忙
            case CommTypeDef.CallConnectText.CALL_CONNECT_BUSY:
//                LogUtils.w(TAG + "  InterCallOutHitState>>>>>>>" + "设备繁忙");
                setCallState(getString(R.string.call_busy), R.color.txt_red);
                break;
            // 	无此房号
            case CommTypeDef.CallConnectText.CALL_CONNECT_NOROOMNO:
//                LogUtils.w(TAG + "  InterCallOutHitState>>>>>>>" + "无此房号");
                setCallState(getString(R.string.call_no_num), R.color.txt_red);
                break;
            // 	无人接听
            case CommTypeDef.CallConnectText.CALL_CONNECT_NOT_HANDDOWN:
//                LogUtils.w(TAG + "  InterCallOutHitState>>>>>>>" + "无人接听");
                setCallState(getString(R.string.call_no_respose), R.color.txt_red);
                break;
            // 	通话结束
            case CommTypeDef.CallConnectText.CALL_CONNECT_TALK_HANDDOWN:
                LogUtils.w(TAG + "  InterCallOutHitState>>>>>>>" + "通话结束");
                if (isCalling || isTalking) {
                    setCallState(getString(R.string.call_talk_end), R.color.txt_green);
                }
                break;
            // 	呼叫结束
            case CommTypeDef.CallConnectText.CALL_CONNECT_CALLING_END:
                LogUtils.w(TAG + "  InterCallOutHitState>>>>>>>" + "呼叫结束");
                if (isCalling) {
                    setCallState(getString(R.string.call_end), R.color.txt_green);
                }
                break;

        }
        //音频提示
        switch (voicehit) {
            //请稍后
            case CommTypeDef.VoiceHint.VOICE_HINT_CALL_PLSWAIT:
                LogUtils.w(TAG + " 请稍后 ");
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
                mRlCallLeft.setVisibility(View.VISIBLE);
                mTvDoorOpen.setVisibility(View.GONE);
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1804_PATH, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                    @Override
                    public void onMediaStatusCompletion(boolean flag) {
                        if (flag) {
//                            LogUtils.w(TAG + "========您呼叫的住户暂时无人接听");
                            isPlaying = false;
                            //退出界面
                            exitFragment(true);
                        }
                    }
                });
                break;
            //您呼叫的住户暂时无法接通
            case CommTypeDef.VoiceHint.VOICE_HINT_CALL_NOTCONNECT:
                isPlaying = true;
                mRlCallLeft.setVisibility(View.VISIBLE);
                mTvDoorOpen.setVisibility(View.GONE);
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1803_PATH, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                    @Override
                    public void onMediaStatusCompletion(boolean flag) {
                        if (flag) {
//                            LogUtils.w(TAG + "========您呼叫的住户暂时无人接听");
                            isPlaying = false;
                            //退出界面
                            exitFragment(true);
                        }
                    }
                });
                break;
            //回铃声
            case CommTypeDef.VoiceHint.VOICE_HINT_CALL_RING:
                LogUtils.w(TAG + "========回铃声");
                if (isPlaying) {
                    isHintCallRing = false;
                    return;
                }
                isHintCallRing = true;
                setCallState(getString(R.string.call_calling), R.color.txt_green);
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

    private void exitFragment(boolean isDelay) {
        LogUtils.w(TAG + "========exitFragment");
        if (isDelay) {
            mMainHandler.postDelayed(countTimeRun, 2000);
        } else {
            if (isTalking) {
                //关闭监视对讲
                CallManage.getInstance().stopCallMonitor();
            }
            if (interCommClient != null) {
                interCommClient.setInterCallOutListener(null);
            }
            //关闭声音
            if (PlaySoundUtils.isLoopSound()) {
                PlaySoundUtils.stopPlayAssetsSound();
            }
            if (isAdded()) {
                backMainActivity();
            }
        }
    }


    @Override
    public void callBackValue(String param, String roomNo) {
        switch (param) {
            //无效卡
            case Constant.IntentId.INTENT_INVALID_CARD:
                mRefrshHandle.sendEmptyMessage(VIEW_STATE_3);
                break;
            //门开了进入
            case Constant.IntentId.INTENT_OPNE_DOOR:
                Message message = new Message();
                message.what = VIEW_STATE_5;
                message.obj = roomNo;
                mRefrshHandle.sendMessage(message);
                break;
            //报警，请关好门
            case Constant.IntentId.ALARM_CLOSE_DOOD:
                mRefrshHandle.sendEmptyMessage(VIEW_STATE_4);
                break;
        }
    }

    /**
     * 监视回调
     */
    @Override
    public void callBack(CallMonitorBean callMonitorBean) {
        if (callMonitorBean != null) {
            boolean openDoor = callMonitorBean.isOpenDoor();
            boolean callEnd = callMonitorBean.isCallEnd();
            boolean callTalk = callMonitorBean.isCallTalk();
            //请通话
            if (callTalk) {
                LogUtils.w(TAG + "=====callTalk");
                isPlaying = false;
                isTalking = true;
                isCallEnd = false;
                //关闭声音播放
                PlaySoundUtils.stopPlayAssetsSound();
                setCallState(getString(R.string.call_please), R.color.txt_green);
                mNumView.setVisibility(View.GONE);
                mTvCallRoomNo.setVisibility(View.GONE);
                //主界面底部按钮不能点击
                setMainBtnEnable(false);
                //停止服务
                AppUtils.getInstance().stopScreenService();
            }
            //门开了
            if (openDoor) {
                LogUtils.w(TAG + "=====openDoor");
                mRefrshHandle.sendEmptyMessage(VIEW_STATE_2);
            }
            //通话结束
            if (callEnd) {
                if (isAdded()) {
                    isTalking = false;
                    isCallEnd = true;
                    LogUtils.w(TAG + "=====callEnd");
                    setCallState(getString(R.string.call_talk_end), R.color.txt_green);
                    mNumView.setVisibility(View.GONE);
                    mTvCallRoomNo.setVisibility(View.GONE);
                    //退出界面
                    exitFragment(true);
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
                    //呼叫
                    if (interCommClient != null) {
                        int code = interCommClient.InterCallRoom(roomNo);
                        if (code != 0) {
                            //无此房号
                            setCallState(getString(R.string.call_no_num), R.color.txt_red);
                            //播放语音
                            PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1202_PATH, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                                @Override
                                public void onMediaStatusCompletion(boolean flag) {
                                    if (!isPlaying) {
                                        //退出界面
                                        exitFragment(true);
                                    }
                                    isCallEnd = true;
                                }
                            });
                        }
                    } else {
                        //无此房号
                        setCallState(getString(R.string.call_no_num), R.color.txt_red);
                        //播放语音
                        PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1202_PATH, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                            @Override
                            public void onMediaStatusCompletion(boolean flag) {
                                //退出界面
                                backMainActivity();
                            }
                        });
                    }
                    break;
                //显示门开了
                case VIEW_STATE_2:
                    setOpenDoorState(R.string.call_open_door, CommStorePathDef.VOICE_1501_PATH, null);
                    break;

                //无效卡
                case VIEW_STATE_3:
                    setOpenDoorState(R.string.call_invalid_card, CommStorePathDef.VOICE_1503_PATH, null);
                    break;
                //报警，请关好门
                case VIEW_STATE_4:
                    showOpenDoorState(R.string.call_no_close_door, R.color.txt_red);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideOpenDoorState();
                        }
                    }, 3000);
                    break;

                //刷卡显示门开了
                case VIEW_STATE_5:
                    setOpenDoorState(R.string.call_open_door, CommStorePathDef.VOICE_1501_PATH, (String) msg.obj);
                    break;
            }
        }
    }

    private void setOpenDoorState(int textId, String ringPath, String roomNo) {
        if (isAdded() && interCommClient != null) {
            mRlCallLeft.setVisibility(View.GONE);
            mTvDoorOpen.setVisibility(View.VISIBLE);
            mTvDoorOpen.setText(getString(textId));
            //播放当前开门状态
            isPlaying = true;
            PlaySoundUtils.playAssetsSound(ringPath, roomNo, true, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                @Override
                public void onMediaStatusCompletion(boolean flag) {
                    if (flag) {
                        mRlCallLeft.setVisibility(View.VISIBLE);
                        mTvDoorOpen.setVisibility(View.GONE);
                        isPlaying = false;
                    }
//                    LogUtils.w(TAG + " setOpenDoorState  isPlaying: " + isPlaying);
                    if (isCallEnd && !isPlaying) {
                        exitFragment(true);
                    }
                }
            });
        }
    }

    class CountTimeRun implements Runnable {
        @Override
        public void run() {
            LogUtils.w(TAG + "  CountTimeRun  isPlaying: " + isPlaying + "  isCallEnd: " + isCallEnd);
            if (!AppUtils.getInstance().isMainFragment() && isAdded() && !isPlaying && isCallEnd) {
                backMainActivity();
                if (interCommClient != null) {
                    interCommClient.setInterCallOutListener(null);
                }
            }
            //关闭声音
            if (PlaySoundUtils.isLoopSound()) {
                PlaySoundUtils.stopPlayAssetsSound();
            }
        }
    }


    protected void backspace() {
        View root = getView();
        if (root != null) {
            View focus = root.findFocus();
            if (focus instanceof InputView) {
                InputView iptView = (InputView) focus;
                if (iptView.backspace())
                    return;
            }
        }
        exitFragment(false);
    }

    /**
     * 按照房号识别指纹
     */
    private void setFingerDevNo() {
        if (isStair) {
            SinglechipClientProxy.getInstance().setFingerDevNo(roomNo);
        } else {
            StringBuilder devNo = new StringBuilder();
            devNo.append(mTvBuildNo.getText());
            if (mTvBuildNo.getText().length() == buildNoLen) {
                devNo.append(mTvUnitNo.getText());
                if (mTvUnitNo.getText().length() == cellNoLen) {
                    devNo.append(mTvRoomNo.getText());
                }
            }
            SinglechipClientProxy.getInstance().setFingerDevNo(devNo.toString());
        }
    }

    public void showOpenDoorState(int textId, int colorId) {
        mRlCallLeft.setVisibility(View.GONE);
        mTvDoorOpen.setVisibility(View.VISIBLE);
        mTvDoorOpen.setText(getString(textId));
        mTvDoorOpen.setTextColor(getResources().getColor(colorId));
    }

    public void hideOpenDoorState() {
        mRlCallLeft.setVisibility(View.VISIBLE);
        mTvDoorOpen.setVisibility(View.GONE);
        mTvDoorOpen.setTextColor(getResources().getColor(R.color.txt_white));
    }
}
