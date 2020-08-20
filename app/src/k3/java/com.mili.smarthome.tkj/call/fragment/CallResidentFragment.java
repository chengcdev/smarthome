package com.mili.smarthome.tkj.call.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
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
import com.android.provider.RoomSubDest;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.appfunc.FreeObservable;
import com.mili.smarthome.tkj.base.K3BaseFragment;
import com.mili.smarthome.tkj.call.CallManage;
import com.mili.smarthome.tkj.call.CallMonitorBean;
import com.mili.smarthome.tkj.call.IActCallBackListener;
import com.mili.smarthome.tkj.main.activity.MainActivity;
import com.mili.smarthome.tkj.main.adapter.NumBitmapAdapter;
import com.mili.smarthome.tkj.main.fragment.MainFragment;
import com.mili.smarthome.tkj.main.widget.GotoMainDefaultTask;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.MediaPlayerUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;
import com.mili.smarthome.tkj.widget.InputView;
import com.mili.smarthome.tkj.widget.MultiImageView;

public class CallResidentFragment extends K3BaseFragment implements InterCommTypeDef.InterCallOutListener, IActCallBackListener {

    private static final String TAG = "CallResidentFragment";
    private static final String ARGS_INPUT_NUM = "args_input_num";
    private final int VIEW_STATE_CALL = 0x001;
    private final int VIEW_STATE_OPEN_DOOR = 0x002;
    private final int VIEW_STATE_INVALID = 0x003;
    private final int VIEW_STATE_ALARM = 0x004;
    private final int VIEW_STATE_OPEN_ROOMNO_DOOR = 0x005;
    private FullDeviceNo fullDeviceNo;
    private LinearLayout mLLCellNo;

    public static Bundle createArguments(int inputNum) {
        if (inputNum >= 0 && inputNum <= 9) {
            Bundle args = new Bundle();
            args.putInt(ARGS_INPUT_NUM, inputNum);
            return args;
        } else {
            return null;
        }
    }

    public static Bundle createArguments(String action) {
        Bundle args = new Bundle();
        args.putString(Const.CallAction.KEY_PARAM, action);
        return args;
    }

    private TextView tvAreaName;
    private TextView tvDevNo;
    private TextView tvOpenDoor;
    private LinearLayout llMain;
    private TextView tvHint;
    private MultiImageView ivNums;
    private NumBitmapAdapter mAdapter;
    private TextView tvCallState;
    private LinearLayout llConfirm;
    private LinearLayout llCancel;
    private TextView tvCancel;

    private LinearLayout llArea;
    private InputView tvBuildNo;
    private InputView tvRoomNo;
    private InputView tvCellNo;

    private InterCommClient interCommClient;
    private String mCallNo = "";
    //是否正在播放声音
    private boolean isPlaying = false;
    //是否正在呼叫
    public static boolean isCalling = false;
    //是否正在通话
    public static boolean isTalking = false;
    //是否播过回铃声
    private boolean isBackRing = true;
    //房号长度
    private int roomNoLen;
    //楼栋号长度
    private int buildNoLen;
    //单元号长度
    private int cellNoLen;
    //是否梯口机
    private boolean isStair;
    //是否呼叫结束
    private boolean isCallEnd;

    private RefresHandle mRefrshHandle = new RefresHandle();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_call_resident;
    }

    @Override
    protected void bindView() {
        tvAreaName = findView(R.id.tv_area_name);
        tvDevNo = findView(R.id.tv_dev_no);
        tvOpenDoor = findView(R.id.tv_open_door);
        llMain = findView(R.id.ll_main);
        tvHint = findView(R.id.tv_hint);
        ivNums = findView(R.id.iv_nums);
        tvCallState = findView(R.id.tv_call_state);
        llConfirm = findView(R.id.ll_confirm);
        llCancel = findView(R.id.ll_cancel);
        tvCancel = findView(R.id.tv_cancle);
        llArea = findView(R.id.lin_area);
        tvBuildNo = findView(R.id.tv_build_no);
        tvRoomNo = findView(R.id.tv_room_no);
        tvCellNo = findView(R.id.tv_cell_no);
        mLLCellNo = findView(R.id.ll_cell_no);

        ivNums.setAdapter(mAdapter = new NumBitmapAdapter(mContext));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        LogUtils.w(TAG + "  onViewCreated");

        //SinglechipClientProxy.getInstance().ctrlCcdLed(1);
        initData();
        mCallNo = "";
        isCalling = false;
        isPlaying = false;
        isTalking = false;
        isBackRing = true;
        isCallEnd = false;
        if (isStair) {
            llArea.setVisibility(View.GONE);
            llMain.setVisibility(View.VISIBLE);
            tvHint.setVisibility(View.VISIBLE);
            tvCallState.setVisibility(View.INVISIBLE);
            llConfirm.setVisibility(View.VISIBLE);
            llCancel.setVisibility(View.VISIBLE);
            tvCancel.setText(R.string.delete_hint);
            ivNums.setVisibility(View.VISIBLE);
        } else {
            llMain.setVisibility(View.GONE);
            llArea.setVisibility(View.VISIBLE);
            if (fullDeviceNo.getUseCellNo() == 1) {
                mLLCellNo.setVisibility(View.VISIBLE);
            } else {
                mLLCellNo.setVisibility(View.GONE);
            }
            tvBuildNo.setMaxLength(buildNoLen);
            tvRoomNo.setMaxLength(roomNoLen);
            tvCellNo.setMaxLength(cellNoLen);
            tvBuildNo.setText("");
            tvRoomNo.setText("");
            tvCellNo.setText("");
            if (buildNoLen <= 0) {
                //是否启用单元号
                if (fullDeviceNo.getUseCellNo() == 1 && cellNoLen > 0) {
                    tvCellNo.requestFocus();
                } else {
                    tvRoomNo.requestFocus();
                }
            } else {
                tvBuildNo.requestFocus();
            }
        }
        tvOpenDoor.setVisibility(View.GONE);
        mAdapter.clear();

        Bundle args = getArguments();
        if (args != null) {
            int inputNum = args.getInt(ARGS_INPUT_NUM, -1);
            if (inputNum >= 0 && inputNum <= 9) {
                inputNum(inputNum);
                setFingerDevNo();
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1201_PATH);
            }
        } else {
            PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1201_PATH);
        }

        initListener();
        //监视通话跳转
        toShowMonitorTalk();
    }


    @Override
    public void onDestroyView() {
        //SinglechipClientProxy.getInstance().ctrlCcdLed(0);
        FreeObservable.getInstance().observeFree();
        super.onDestroyView();
        if (interCommClient != null && (isCalling || isTalking)) {
            interCommClient.InterHandDown();
        }
        isTalking = false;
        isCalling = false;
    }

    private void toShowMonitorTalk() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            String param = bundle.getString(Const.CallAction.KEY_PARAM);
            if (param != null && param.equals(Const.CallAction.CALL_FROM_RESIDENT)) {
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
                ivNums.setVisibility(View.GONE);
                //底部按钮不能点击
                setMainBtnEnable();
            }
        }
    }

    private void setMainBtnEnable() {
        Activity activity = getActivity();
        if (activity instanceof MainActivity) {
            MainActivity act = (MainActivity) activity;
            act.setTabEnabled(false);
        }
    }

    private void initData() {
        tvAreaName.setText(AppConfig.getInstance().getAreaName());

        RoomSubDest roomSubDest = new RoomSubDest(getContext());
        tvDevNo.setText(roomSubDest.getSubDestDevNumber());

        fullDeviceNo = new FullDeviceNo(getContext());
        roomNoLen = fullDeviceNo.getRoomNoLen();
        int stairNoLen = fullDeviceNo.getStairNoLen();
        cellNoLen = fullDeviceNo.getCellNoLen();
        buildNoLen = stairNoLen - cellNoLen;
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


    @Override
    public boolean onKeyEvent(int keyCode, int keyState) {
        if (keyState == KEYSTATE_DOWN || keyCode == KEYCODE_UNLOCK)
            return false;
        switch (keyCode) {
            case KEYCODE_0:
                if (!isTalking && !isCalling) {
                    inputNum(0);
                    setFingerDevNo();
                }
                break;
            case KEYCODE_1:
            case KEYCODE_2:
            case KEYCODE_3:
            case KEYCODE_4:
            case KEYCODE_5:
            case KEYCODE_6:
            case KEYCODE_7:
            case KEYCODE_8:
            case KEYCODE_9:
                if (!isTalking && !isCalling) {
                    inputNum(keyCode);
                    setFingerDevNo();
                }
                break;
            case KEYCODE_BACK:
                isCallEnd = true;
                backspace(false);
                setFingerDevNo();
                break;
            case KEYCODE_CALL:
                if (isStair) {
                    mCallNo = mAdapter.getText();
                    if (mCallNo.length() < 3) {
                        return true;
                    }
                } else {
                    String buildNo = tvBuildNo.getText().toString();
                    String cellNo = tvCellNo.getText().toString();
                    String roomNo = tvRoomNo.getText().toString();
                    if (buildNo.length() < buildNoLen || cellNo.length() < cellNoLen || roomNo.length() < roomNoLen) {
                        return true;
                    }
                    mCallNo = buildNo + cellNo + roomNo;
                }
                //开始呼叫
                startCall();
                break;
        }
        return true;
    }

    protected void inputNum(int num) {
        if (isStair) {
            if (mAdapter.getCount() >= roomNoLen)
                return;
            mAdapter.input(num);
            tvHint.setVisibility(View.INVISIBLE);
        } else {
            View root = getView();
            if (root == null)
                return;
            View focus = root.findFocus();
            if (focus instanceof InputView) {
                InputView iptView = (InputView) focus;
                iptView.input(num);
            }
        }
    }

    protected boolean backText() {
        if (isStair) {
            if (mAdapter.getCount() > 0) {
                mAdapter.backspace();
            }
            return mAdapter.getCount() > 0;
        } else {
            View root = getView();
            if (root != null) {
                View focus = root.findFocus();
                if (focus instanceof InputView) {
                    InputView iptView = (InputView) focus;
                    return (iptView.backspace());
                }
            }
        }
        return false;
    }

    /**
     * 退出回主界面
     *
     * @param isDelayBack 是否延时退出
     */
    private void backspace(boolean isDelayBack) {
//        LogUtils.w(TAG+" backspace isPlaying: "+isPlaying+" isCallEnd: "+isCallEnd);
        if (isDelayBack) {
            mMainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isPlaying && isCallEnd) {
                        if (PlaySoundUtils.isLoopSound()) {
                            PlaySoundUtils.stopPlayAssetsSound();
                        }
                        MainActivity activity = (MainActivity) getActivity();
                        assert activity != null;
                        if (isAdded() && activity.fmCurrent instanceof CallResidentFragment) {
                            if (interCommClient != null) {
                                interCommClient.setInterCallOutListener(null);
                            }
                            GotoMainDefaultTask.getInstance().run();
                        }
                    }
                }
            }, 2000);
        } else {
            if (isTalking || isCalling || isPlaying || !backText()) {
                //关闭监视回调
                if (isTalking) {
                    CallManage.getInstance().stopCallMonitor();
                }
                if (interCommClient != null) {
                    interCommClient.setInterCallOutListener(null);
                }
                GotoMainDefaultTask.getInstance().run();
                //关闭声音
                if (PlaySoundUtils.isLoopSound()) {
                    PlaySoundUtils.stopPlayAssetsSound();
                }
            }
        }
    }

    private void setCallState(int stateId, int colorId) {
        if (isAdded()) {
            llMain.setVisibility(View.VISIBLE);
            tvHint.setVisibility(View.GONE);
            llConfirm.setVisibility(View.GONE);
            llCancel.setVisibility(View.VISIBLE);
            tvCancel.setText(R.string.back_hint);
            llArea.setVisibility(View.GONE);
            tvOpenDoor.setVisibility(View.GONE);
            if (!isStair) {
                mAdapter.setText(mCallNo);
            }
            tvCallState.setVisibility(View.VISIBLE);
            tvCallState.setText(getString(stateId));
            tvCallState.setTextColor(getResources().getColor(colorId));
        }
    }


    private void setOpenDoorState(int textId, String ringPath, String roomNo) {
        if (isAdded() && interCommClient != null) {
            llArea.setVisibility(View.GONE);
            llMain.setVisibility(View.GONE);
            tvOpenDoor.setVisibility(View.VISIBLE);
            tvOpenDoor.setText(getString(textId));
            isPlaying = true;
            //播放当前开门状态
            PlaySoundUtils.playAssetsSound(ringPath, roomNo, true, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                @Override
                public void onMediaStatusCompletion(boolean flag) {
                    if (flag) {
                        if (isStair) {
                            llMain.setVisibility(View.VISIBLE);
                            llArea.setVisibility(View.GONE);
                        } else {
                            if (isCalling || isTalking || isCallEnd) {
                                llMain.setVisibility(View.VISIBLE);
                                llArea.setVisibility(View.GONE);
                            } else {
                                llMain.setVisibility(View.GONE);
                                llArea.setVisibility(View.VISIBLE);
                            }
                        }
                        tvOpenDoor.setVisibility(View.GONE);
                        isPlaying = false;
                    }

                    if (isCallEnd && !isPlaying) {
                        backspace(true);
                    }
                }
            });
        }
    }

    private void startCall() {

        if (isCalling || isTalking || isPlaying) {
            return;
        }

        ivNums.setVisibility(View.VISIBLE);
        setCallState(R.string.call_connecting, R.color.txt_green);
        mRefrshHandle.sendEmptyMessage(VIEW_STATE_CALL);
    }

    /**
     * 拨号最后结束回调
     */
    @Override
    public void InterCallOutNone(int param) {
        LogUtils.w(TAG, "InterCallOutNone>>>>>>>" + "结束.... isPlaying" + isPlaying);
        if (isCalling || isTalking) {
            isCallEnd = true;
        }
        if (!isPlaying) {
            //退出界面
            backspace(true);
        }
    }

    /**
     * 呼叫中
     */
    @Override
    public void InterCallOutCalling(int param) {
        isCalling = true;
        //呼叫中
        if (!isPlaying) {
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
        isCallEnd = true;
        LogUtils.w(TAG, "InterCallOutEnd>>>>>>>" + "结束.... isPlaying" + isPlaying);
        if (!isPlaying) {
            //退出界面
            backspace(true);
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
//                Log.e(TAG, "InterCallOutHitState>>>>>>>" + "通话结束");
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
                llMain.setVisibility(View.VISIBLE);
                tvOpenDoor.setVisibility(View.GONE);
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1804_PATH, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                    @Override
                    public void onMediaStatusCompletion(boolean flag) {
                        if (flag) {
//                            LogUtils.e(TAG + "========您呼叫的住户暂时无人接听");
                            isPlaying = false;
                            //退出界面
                            backspace(true);
                        }
                    }
                });
                break;
            //您呼叫的住户暂时无法接通
            case CommTypeDef.VoiceHint.VOICE_HINT_CALL_NOTCONNECT:
                isPlaying = true;
                llMain.setVisibility(View.VISIBLE);
                tvOpenDoor.setVisibility(View.GONE);
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1803_PATH, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                    @Override
                    public void onMediaStatusCompletion(boolean flag) {
                        if (flag) {
//                            LogUtils.e(TAG + "========您呼叫的住户暂时无法接通");
                            isPlaying = false;
                            //退出界面
                            backspace(true);
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
                setCallState(R.string.call_calling, R.color.txt_green);
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
        if (isCalling || isTalking) {
            switch (param) {
                //无效卡
                case Const.CardAction.INVALID_CARD:
                    mRefrshHandle.sendEmptyMessage(VIEW_STATE_INVALID);
                    break;
                //门开了
                case Const.CardAction.OPNE_DOOR:
                    Message message = new Message();
                    message.what = VIEW_STATE_OPEN_ROOMNO_DOOR;
                    message.obj = roomNo;
                    mRefrshHandle.sendMessage(message);
                    break;
                //报警，请关好门
                case Const.AlarmAction.ALARM_OPEN_DOOR:
                    mRefrshHandle.sendEmptyMessage(VIEW_STATE_ALARM);
                    break;
                default:
                    break;
            }
        } else {
            Bundle bundle = new Bundle();
            switch (param) {
                //无效卡
                case Const.CardAction.INVALID_CARD:
                    bundle.putInt(MainFragment.COLOR_ID, R.color.txt_white);
                    bundle.putInt(MainFragment.TEXT_ID, R.string.call_invalid_card);
                    break;
                //门开了
                case Const.CardAction.OPNE_DOOR:
                    bundle.putInt(MainFragment.COLOR_ID, R.color.txt_white);
                    bundle.putInt(MainFragment.TEXT_ID, R.string.call_open_door);
                    break;
                //报警，请关好门
                case Const.AlarmAction.ALARM_OPEN_DOOR:
                    bundle.putInt(MainFragment.COLOR_ID, R.color.txt_red);
                    bundle.putInt(MainFragment.TEXT_ID, R.string.call_no_close_door);
                    break;
            }
            ContextProxy.sendBroadcast(Const.Action.MAIN_DEFAULT, bundle);
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
                ivNums.setVisibility(View.GONE);
                setCallState(R.string.call_please, R.color.txt_green);
                //底部按钮不能点击
                setMainBtnEnable();
            }
            //门开了
            if (openDoor) {
//                LogUtils.e(TAG + "=========InterLock");
                mRefrshHandle.sendEmptyMessage(VIEW_STATE_OPEN_DOOR);
            }
            //通话结束
            if (callEnd) {
                if (isAdded()) {
                    isCallEnd = true;
//                    LogUtils.e(TAG + "==========callEnd");
                    setCallState(R.string.call_talk_end, R.color.txt_green);
                    //退出界面
                    backspace(true);
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
                case VIEW_STATE_CALL:
                    FreeObservable.getInstance().cancelObserveFree();
                    //底部按钮不能点击
                    setMainBtnEnable();

                    setCallState(R.string.call_calling, R.color.txt_green);
                    //呼叫
                    int code = interCommClient.InterCallRoom(mCallNo);
                    isCalling = true;
                    if (code != 0) {
                        //无此房号
                        setCallState(R.string.call_no_num, R.color.txt_red);
                        llMain.setVisibility(View.VISIBLE);
                        tvOpenDoor.setVisibility(View.GONE);
                        //播放语音
                        PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1202_PATH, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                            @Override
                            public void onMediaStatusCompletion(boolean flag) {
                                if (!isPlaying) {
                                    //退出界面
                                    backspace(true);
                                }
                                isCallEnd = true;
                            }
                        });

                    }
                    break;

                case VIEW_STATE_OPEN_DOOR:
                    setOpenDoorState(R.string.call_open_door, CommStorePathDef.VOICE_1501_PATH, null);
                    break;

                case VIEW_STATE_OPEN_ROOMNO_DOOR:
                    setOpenDoorState(R.string.call_open_door, CommStorePathDef.VOICE_1501_PATH, (String) msg.obj);
                    break;
                case VIEW_STATE_INVALID:
                    setOpenDoorState(R.string.call_invalid_card, CommStorePathDef.VOICE_1503_PATH, null);
                    break;
                case VIEW_STATE_ALARM:
                    showOpenDoorState(R.string.call_no_close_door, R.color.txt_red);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideOpenDoorState();
                        }
                    }, 3000);
                    break;
            }
        }
    }

    public void showOpenDoorState(int textId, int colorId) {
        llMain.setVisibility(View.GONE);
        tvOpenDoor.setVisibility(View.VISIBLE);
        tvOpenDoor.setText(textId);
        tvOpenDoor.setTextColor(getResources().getColor(colorId));
    }

    public void hideOpenDoorState() {
        llMain.setVisibility(View.VISIBLE);
        tvOpenDoor.setVisibility(View.GONE);
        tvOpenDoor.setTextColor(getResources().getColor(R.color.txt_white));
    }


    /**
     * 按照房号识别指纹
     */
    private void setFingerDevNo() {
        if (isStair) {
            SinglechipClientProxy.getInstance().setFingerDevNo(mAdapter.getText());
        } else {
            StringBuilder devNo = new StringBuilder();
            devNo.append(tvBuildNo.getText());
            if (tvBuildNo.getText().length() == buildNoLen) {
                devNo.append(tvCellNo.getText());
                if (tvCellNo.getText().length() == cellNoLen) {
                    devNo.append(tvRoomNo.getText());
                }
            }
            SinglechipClientProxy.getInstance().setFingerDevNo(devNo.toString());
        }
    }
}
