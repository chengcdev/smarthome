package com.mili.smarthome.tkj.set.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.CommStorePathDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.main.activity.BaseK7Activity;
import com.mili.smarthome.tkj.main.entity.HintBean;
import com.mili.smarthome.tkj.main.interf.IHintEventListener;
import com.mili.smarthome.tkj.main.manage.HintEventManage;
import com.mili.smarthome.tkj.main.widget.KeyBoardItemView;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.set.call.CallHelper;
import com.mili.smarthome.tkj.utils.AppManage;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.MediaPlayerUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;

import static com.mili.smarthome.tkj.set.Constant.OPENDOOR_ROOMNO;

public class HintActivity extends BaseK7Activity implements IHintEventListener, KeyBoardItemView.IOnKeyClickListener {
    private String TAG = "HintActivity";
    private KeyBoardItemView keyCancle;
    private ImageView imgConnect;
    private TextView tvConnectState;
    private TextView tvNum;
    private ImageView imgOpenDoor;
    private AnimationDrawable animationDrawable;
    private Handler handler = new Handler();
    private HintRun hintRun = new HintRun();
    private CountTimeRun countTimeRun;
    private int time = 2000;
    private int backCount = 0;
    private boolean isCalling; //正在呼叫
    public static boolean isTalking; //正在通话
    private boolean isTalkEnd = true; //通话结束
    private boolean isPlaying; //正在播放声音
    private boolean isRecording; //正在留言
    private boolean isHintCallRing = true;  //是否播过回铃声
    private boolean isAlarm = false;  //是否显示门未关
    private AnimationDrawable openDoorAni;
    private TextView tvOpenDoor;
    private boolean isHasFocus; //界面是否初始化完成
    private int mTipVoluem = 1; //1 启用提示音 0关闭提示音

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hint);
        initView();
    }

    private void initView() {
        isTalking = false;
        isAlarm = false;
        keyCancle = (KeyBoardItemView) findViewById(R.id.key_cancle);
        imgConnect = (ImageView) findView(R.id.img_connect);
        tvConnectState = (TextView) findView(R.id.tv_connect_state);
        tvNum = (TextView) findView(R.id.tv_num);
        imgOpenDoor = (ImageView) findView(R.id.img_open_door);
        tvOpenDoor = (TextView) findView(R.id.tv_open_door);
        if (countTimeRun == null) {
            countTimeRun = new CountTimeRun();
        }
        HintEventManage.getInstance().setHintEventListener(this);
        KeyBoardItemView.setOnkeyClickListener(this);
        Constant.ScreenId.SCREEN_IS_SET = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //关闭所有屏幕服务操作
        AppManage.getInstance().stopScreenService();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            HintBean hintBean = (HintBean) intent.getSerializableExtra(Constant.KEY_HINT);
            notifyView(hintBean);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(hintRun);
        //挂断
        CallHelper.getInstance().callHandDown();
        //关闭循环播放声音
        if (PlaySoundUtils.isLoopSound()) {
            PlaySoundUtils.stopPlayAssetsSound();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //开启所有屏幕服务操作
        AppManage.getInstance().startScreenService();
        //还原提示音设置
        if (mTipVoluem == 0) {
            AppConfig.getInstance().setTipVolume(1);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        isHasFocus = hasFocus;
//        LogUtils.w(" HintActivity onWindowFocusChanged isHasFocus: " + isHasFocus);
        super.onWindowFocusChanged(hasFocus);
    }

    /**
     * 状态变化
     */
    private void notifyCallState(String num, String state, int resId) {
        if (animationDrawable != null) {
            animationDrawable.stop();
        }
        if (num != null) {
            tvNum.setText(num);
        }
        imgConnect.setImageResource(resId);
        tvConnectState.setTextColor(Color.GREEN);
        tvConnectState.setText(state);
        tvConnectState.setVisibility(View.VISIBLE);
        keyCancle.setVisibility(View.VISIBLE);
        imgOpenDoor.setVisibility(View.GONE);
        tvOpenDoor.setVisibility(View.GONE);
        imgConnect.setVisibility(View.VISIBLE);
    }


    /**
     * 开门
     *
     * @param isActiveCall 是否主动呼叫
     */
    private void openDoor(boolean isActiveCall, String roomNo) {
        isPlaying = true;
        PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1501_PATH, roomNo, true, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
            @Override
            public void onMediaStatusCompletion(boolean flag) {
                notifyView();
                OPENDOOR_ROOMNO = null;
            }
        });
        if (isActiveCall || isTalking || isCalling) {
            keyCancle.setVisibility(View.VISIBLE);
        } else {
            keyCancle.setVisibility(View.GONE);
        }
        imgOpenDoor.setImageResource(R.drawable.anim_door_open);
        tvConnectState.setVisibility(View.GONE);
        imgConnect.setVisibility(View.INVISIBLE);
        imgOpenDoor.setVisibility(View.VISIBLE);
        tvOpenDoor.setVisibility(View.VISIBLE);
        tvOpenDoor.setTextColor(Color.GREEN);
        tvOpenDoor.setText(getString(R.string.call_open_door));
        if (animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
        animationDrawable = (AnimationDrawable) imgOpenDoor.getDrawable();
        animationDrawable.start();
    }

    private void notifyView() {
        isPlaying = false;
        if (isCalling || isTalking || isRecording) {
            imgOpenDoor.setVisibility(View.GONE);
            tvOpenDoor.setVisibility(View.GONE);
            tvConnectState.setVisibility(View.VISIBLE);
            imgConnect.setVisibility(View.VISIBLE);
            return;
        }
        exit(true);
    }

    /**
     * 门未关
     */
    private void notCLoseDoor() {
        if (!isTalking) {
            isPlaying = true;
            PlaySoundUtils.playAlarmSound(new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                @Override
                public void onMediaStatusCompletion(boolean flag) {
                    notifyView();
                }
            });
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    notifyView();
                }
            }, 3000);
        }
        imgOpenDoor.setImageResource(R.drawable.door_close);
        tvConnectState.setVisibility(View.VISIBLE);
        keyCancle.setVisibility(View.GONE);
        imgConnect.setVisibility(View.INVISIBLE);
        imgOpenDoor.setVisibility(View.VISIBLE);
        tvOpenDoor.setVisibility(View.GONE);
        tvConnectState.setTextColor(Color.RED);
        tvConnectState.setText(getString(R.string.call_door_tip1));
        isAlarm = true;
    }

    /**
     * 无效卡
     */
    private void cardInvalid() {
        isPlaying = true;
        PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1503_PATH, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
            @Override
            public void onMediaStatusCompletion(boolean flag) {
                notifyView();
            }
        });
        imgOpenDoor.setImageResource(R.drawable.card_invalid);
        tvConnectState.setVisibility(View.VISIBLE);
        imgConnect.setVisibility(View.INVISIBLE);
        imgOpenDoor.setVisibility(View.VISIBLE);
        tvOpenDoor.setVisibility(View.GONE);
        if (!isCalling && !isTalking) {
            tvConnectState.setTextColor(Color.RED);
            tvConnectState.setText(getString(R.string.call_invalid_card));
        }
        if (isCalling || isTalking) {
            keyCancle.setVisibility(View.VISIBLE);
        } else {
            keyCancle.setVisibility(View.GONE);
        }
    }

    /**
     * 密码错误
     */
    private void pwdFail(boolean isAdminPwd) {
        isPlaying = true;
        if (!isAdminPwd) {
            PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1302_PATH, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                @Override
                public void onMediaStatusCompletion(boolean flag) {
                    isPlaying = false;
                    exit(true);
                }
            });
        } else {
            //不是管理员密码延时退出
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isPlaying = false;
                    exit(true);
                }
            }, 3000);
        }
        imgOpenDoor.setImageResource(R.drawable.pwd_error);
        tvConnectState.setVisibility(View.VISIBLE);
        imgConnect.setVisibility(View.INVISIBLE);
        imgOpenDoor.setVisibility(View.VISIBLE);
        tvConnectState.setTextColor(Color.RED);
        tvConnectState.setText(getString(R.string.setting_pwd_fail));
        keyCancle.setVisibility(View.GONE);
        tvOpenDoor.setVisibility(View.GONE);
    }


    /**
     * 设置呼叫状态
     */
    private void setCallingState(String num, String state, int resId) {
        tvOpenDoor.setVisibility(View.GONE);
        tvConnectState.setTextColor(Color.GREEN);
        tvConnectState.setText(state);
        tvNum.setText(num);
        keyCancle.setVisibility(View.VISIBLE);
        imgConnect.setVisibility(View.VISIBLE);
        imgOpenDoor.setVisibility(View.GONE);
        if (openDoorAni != null && openDoorAni.isRunning()) {
            return;
        }
        imgConnect.setImageResource(resId);
        openDoorAni = (AnimationDrawable) imgConnect.getDrawable();
        openDoorAni.start();
    }

    private void exit(boolean isDelayBack) {
        if (isDelayBack) {
            countTimeRun.setCountTimeRun();
            handler.post(countTimeRun);
        } else {
            handler.removeCallbacks(countTimeRun);
            //关闭循环播放声音
            if (PlaySoundUtils.isLoopSound()) {
                PlaySoundUtils.stopPlayAssetsSound();
            }
            //刷新主界面
            AppManage.getInstance().sendReceiver(Constant.ActionId.ACTION_INIT_MAIN);
            if (App.getInstance().getCurrentActivity() instanceof HintActivity) {
                finish();
            }
        }
    }

    @Override
    public void onHintEvent(HintBean hintBean) {
        notifyView(hintBean);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCancle.getVisibility() == View.GONE && !isAlarm) {
            return true;
        }

        View decorView = getWindow().getDecorView();
        View focusView = decorView.findFocus();
        if (isAlarm) {
            PlaySoundUtils.stopPlayAssetsSound();
        }
        //noinspection StatementWithEmptyBody
        if (focusView instanceof KeyBoardItemView) {

        } else {
            if (AppManage.getInstance().getmCurrentKeycode() == keyCode) {
                return true;
            }
            AppManage.getInstance().setmCurrentKeycode(keyCode);
            touchDown(keyCode);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (keyCancle.getVisibility() == View.GONE) {
            return true;
        }

        View decorView = getWindow().getDecorView();
        View focusView = decorView.findFocus();
        if (focusView instanceof KeyBoardItemView) {

        } else {
            AppManage.getInstance().setmDefaultKeycode();
            touchUp(keyCode);
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void OnViewDownClick(int code, View view) {
        touchDown(code);
    }

    @Override
    public void OnViewUpClick(int code, View view) {
        touchUp(code);
    }


    private void touchUp(int keyCode) {
        int position = AppManage.getInstance().getPosition(keyCode);
        switch (position) {
            case Constant.KeyNumId.KEY_NUM_12:
            case Constant.KeyNumId.KEY_NUM_13:
            case Constant.KeyNumId.KEY_NUM_14:
                AppManage.getInstance().keyBoardUp(keyCancle);
//                LogUtils.w(" HintActivity touchUp isFastDoubleUpClick: " + isFastDoubleUpClick() + " isHasFocus: " + isHasFocus);
                if (isFastDoubleUpClick() && !isHasFocus) {
                    return;
                }
                exit(false);
                break;
        }
    }

    private void touchDown(int code) {
        int position = AppManage.getInstance().getPosition(code);
        switch (position) {
            case Constant.KeyNumId.KEY_NUM_12:
            case Constant.KeyNumId.KEY_NUM_13:
            case Constant.KeyNumId.KEY_NUM_14:
                AppManage.getInstance().keyBoardDown(keyCancle);
                break;
        }
    }

    class CountTimeRun implements Runnable {
        void setCountTimeRun() {
            backCount = 0;
        }

        @Override
        public void run() {
            LogUtils.w(TAG + " CountTimeRun backCount: " + backCount);
            backCount++;
            if (backCount == 2) {
                backCount = 0;
                if (handler != null) {
                    handler.removeCallbacks(this);
                }
                LogUtils.w(TAG + " CountTimeRun isTalkEnd: " + isTalkEnd + " isPlaying: " + isPlaying);
                //刷新主界面
                if (isTalkEnd && !isPlaying && App.getInstance().getCurrentActivity() instanceof HintActivity) {
                    AppManage.getInstance().sendReceiver(Constant.ActionId.ACTION_INIT_MAIN);
                    finish();
                }
                return;
            }
            handler.postDelayed(this, time);
        }


    }

    private void notifyView(HintBean hintBean) {
        if (hintBean != null) {
            isAlarm = false;
            int type = hintBean.getType();
            switch (type) {
                case Constant.MONITOR_OPEN_DOOR:
                    //开门成功动画
                    openDoor(hintBean.isActiveCall(), null);
                    break;
                case Constant.MONITOR_NOT_CLOSE_DOOR:
                    //门未关
                    notCLoseDoor();
                    break;
                case Constant.MONITOR_INVALID_CARD:
                    //无效卡
                    cardInvalid();
                    break;
                case Constant.KEY_PWD_FAIL:
                    //密码输入错误
                    pwdFail(hintBean.isAdminPwd());
                    break;

                case Constant.OPEN_DOOR_REMIND:
                    openDoor(hintBean.isActiveCall(), OPENDOOR_ROOMNO);
                    break;

                case Constant.SetHintId.HINT_CONNECTING:
                    isCalling = true;
                    //连接中
                    notifyCallState(hintBean.getRoomName(), getString(R.string.call_connect_loading), R.drawable.call_connect);
                    break;
                case Constant.SetHintId.HINT_CALL_RING:
                    if (isPlaying) {
                        isHintCallRing = false;
                        return;
                    }
                    //循环播放
                    PlaySoundUtils.playAssetsSoundLoop(CommStorePathDef.CALL_OUT_PATH, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                        @Override
                        public void onMediaStatusCompletion(boolean flag) {
                            //回铃声结束
                            isHintCallRing = false;
                        }
                    });
                    break;
                case Constant.SetHintId.HINT_CALLING:
                    if (isPlaying) {
                        return;
                    }
                    isCalling = true;
                    //呼叫
                    if (hintBean.getCallFrome().equals(Constant.SetHintId.HINT_CALL_FROME_RESIDENT)) {
                        setCallingState(hintBean.getRoomName(), getString(R.string.call_calling), R.drawable.anim_call_zhuhu);
                    } else if (hintBean.getCallFrome().equals(Constant.SetHintId.HINT_CALL_FROME_CENTER)) {
                        setCallingState(hintBean.getRoomName(), getString(R.string.call_calling), R.drawable.anim_call_center);
                    }
                    if (!isHintCallRing) {
                        isHintCallRing = true;
                        PlaySoundUtils.playAssetsSoundLoop(CommStorePathDef.CALL_OUT_PATH, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                            @Override
                            public void onMediaStatusCompletion(boolean flag) {
                                isHintCallRing = false;
                            }
                        });
                    }
                    break;
                case Constant.SetHintId.HINT_TALKING:
                    isCalling = false;
                    isTalking = true;
                    isRecording = false;
                    isTalkEnd = false;
                    //请通话
                    if (hintBean.getCallFrome().equals(Constant.SetHintId.HINT_CALL_FROME_RESIDENT)) {
                        notifyCallState(hintBean.getRoomName(), getString(R.string.call_please), R.drawable.call_zhuhu);
                    } else if (hintBean.getCallFrome().equals(Constant.SetHintId.HINT_CALL_FROME_CENTER)) {
                        notifyCallState(hintBean.getRoomName(), getString(R.string.call_please), R.drawable.call_center);
                    }
                    if (hintBean.isActiveCall()) {
                        tvNum.setVisibility(View.VISIBLE);
                    } else {
                        tvNum.setVisibility(View.GONE);
                    }
                    break;
                case Constant.SetHintId.HINT_RECORD_WAIT:
                    isCalling = false;
                    isTalking = true;
                    isRecording = true;
                    notifyCallState(hintBean.getRoomName(), getString(R.string.call_please_ly), R.drawable.recording);
                    break;
                case Constant.SetHintId.HINT_RECORDING:
                    isCalling = false;
                    isTalking = true;
                    isRecording = true;
                    //关闭提示音
                    mTipVoluem = AppConfig.getInstance().getTipVolume();
                    if (mTipVoluem == 1) {
                        AppConfig.getInstance().setTipVolume(0);
                        mTipVoluem = 0;
                    } else {
                        mTipVoluem = 1;
                    }
                    notifyCallState(hintBean.getRoomName(), getString(R.string.intercallhit_recordhit), R.drawable.record_hint);
                    break;
                case Constant.SetHintId.HINT_CALL_END:
                    isCalling = false;
                    isTalking = false;
                    isRecording = false;
                    isTalkEnd = true;
                    exit(true);
                    break;
                case Constant.SetHintId.HINT_CALL_STATUS:
                    notifyViewStatus(hintBean);
                    break;
            }
        }
    }

    private void notifyViewStatus(HintBean hintBean) {
        int callStatus = hintBean.getCallStatus();
        int callStatusColor = hintBean.getCallStatusColor();
        int callStatusImgId = hintBean.getCallStatusImgId();
        if (animationDrawable != null) {
            animationDrawable.stop();
        }
        tvConnectState.setVisibility(View.VISIBLE);
        tvConnectState.setText(callStatus);
        tvConnectState.setTextColor(getResources().getColor(callStatusColor));
        imgOpenDoor.setVisibility(View.GONE);
        tvOpenDoor.setVisibility(View.GONE);
        imgConnect.setVisibility(View.VISIBLE);
        imgConnect.setImageResource(callStatusImgId);
        keyCancle.setVisibility(View.VISIBLE);
        if (hintBean.isActiveCall()) {
            tvNum.setVisibility(View.VISIBLE);
        } else {
            tvNum.setVisibility(View.GONE);
        }
    }

    class HintRun implements Runnable {
        int count;

        HintRun() {
            count = 0;
        }

        public HintRun setCount(int count) {
            this.count = count;
            return this;
        }

        @Override
        public void run() {
            if (count == 3) {
                notifyView();
            }
            count++;
            handler.post(this);
        }
    }

}
