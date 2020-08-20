package com.mili.smarthome.tkj.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.CommStorePathDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.main.activity.MainActivity;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.MediaPlayerUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;

import java.util.Objects;

import static com.mili.smarthome.tkj.constant.Constant.OPENDOOR_ROOMNO;


public class HintDialogFragment extends BaseMainFragment implements MediaPlayerUtils.OnMediaStatusCompletionListener {

    private ImageView mImg;
    private TextView mTv;
    private MyRun myRun = new MyRun();
    //当前是否显示该界面
    private boolean isExitFragment;
    private boolean isPlaying;
    private Context mContext;
    private HintDialogReceiver receiver;
    private int count;
    private static final String TAG = "HintDialogFragment";

    @Override
    public void initView(View view) {
        mTv = (TextView) view.findViewById(R.id.tv);
        mImg = (ImageView) view.findViewById(R.id.img);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        receiver = new HintDialogReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.Action.ACTION_HINT_DIALOG);
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.w(TAG + "  onResume  ");
        count = 0;
        if (mContext != null) {
            MainActivity activity = (MainActivity) mContext;
            activity.currentFrag = this;
        }
        isExitFragment = false;
        initData();
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onPause() {
        super.onPause();
        isExitFragment = true;
        mMainHandler.removeCallbacks(myRun);
//        LogUtils.w(TAG + "  onPause  ");
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mContext != null) {
            MainActivity activity = (MainActivity) mContext;
            activity.setBottomBtnEnable(true);
        }
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            String extra = bundle.getString(Constant.IntentId.INTENT_KEY);
//            LogUtils.e("HintDialogFragment initData extra=================" + extra);
            assert extra != null;
            setState(extra);
            if (!Constant.IntentId.INTENT_DOOR_NOT_CLOSE.equals(extra)) {
                //主界面底部tab初始化,并且不能点击
                Intent intent = new Intent(Constant.Action.MAIN_BOTTOM_BTN);
                Objects.requireNonNull(getActivity()).sendBroadcast(intent);
            }
        } else {
            //延时3秒退出界面
            exitFragment();
            //主界面底部tab初始化,并且不能点击
            Intent intent = new Intent(Constant.Action.MAIN_BOTTOM_BTN);
            Objects.requireNonNull(getActivity()).sendBroadcast(intent);
        }
        myRun.run();
    }

    private void setState(String extra) {
        switch (extra) {
            //密码错误
            case Constant.IntentId.INTENT_PWD_ERROR:
                isPlaying = true;
                showViewState(R.color.txt_red, R.string.set_input_pwd_error, R.drawable.main_error_hit);
                //播放语音
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1302_PATH, this);
                break;
            //管理员密码错误，无声音
            case Constant.IntentId.INTENT_PWD_ERROR_NO_SOUND:
                showViewState(R.color.txt_red, R.string.set_input_pwd_error, R.drawable.main_error_hit);
                exitFragment();
                break;
            //开门密码功能未使用
            case Constant.IntentId.INTENT_PWD_NOT_USER:
                isPlaying = true;
                showViewState(R.color.txt_red, R.string.set_input_pwd_unuse, R.drawable.main_error_hit);
                //播放语音
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1303PATH, this);
                break;
            //开门
            case Constant.IntentId.INTENT_OPNE_DOOR:
                isPlaying = true;
                showViewState(R.color.txt_white, R.string.call_open_door, R.drawable.main_door_open);
                //播放语音
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1501_PATH, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                    @Override
                    public void onMediaStatusCompletion(boolean flag) {
                        if (isExitFragment) {
                            //关闭声音播放
                            PlaySoundUtils.stopPlayAssetsSound();
                        } else {
                            exitFragment();
                        }
                        isPlaying = false;
                    }
                });
                break;
            //无效卡
            case Constant.IntentId.INTENT_INVALID_CARD:
                isPlaying = true;
                showViewState(R.color.txt_red, R.string.call_invalid_card, R.drawable.main_error_hit);
                //播放语音
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1503_PATH, this);
                break;
            //门未关
            case Constant.IntentId.INTENT_DOOR_NOT_CLOSE:
                isPlaying = true;
                showViewState(R.color.txt_red, R.string.call_no_close_door, null);
                //播放语音
                PlaySoundUtils.playAlarmSound(this);
                break;
            // TODO: 无效指纹
            case Constant.IntentId.INTENT_INVALID_FINGER:
                isPlaying = true;
                showViewState(R.color.txt_red, R.string.finger_invalid, R.drawable.main_error_hit);
                //播放语音
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1506_PATH, HintDialogFragment.this);
                break;
            // TODO: 请保持手指按下
            case Constant.IntentId.INTENT_KEEP_PRESS:
                showViewState(R.color.txt_green, R.string.finger_keep_press, null);
                break;
            // TODO: 正在对比指纹，请等候！
            case Constant.IntentId.INTENT_VERIFYING_FINGER:
                showViewState(R.color.txt_green, R.string.finger_verifying, null);
                break;
            // TODO: 请重按手指
            case Constant.IntentId.INTENT_FINGER_PRESS_AGAIN:
                showViewState(R.color.txt_red, R.string.finger_press_again, null);
                exitFragment();
                break;

            case Constant.IntentId.INTENT_OPNE_DOOR_REMIND:
                isPlaying = true;
                showViewState(R.color.txt_white, R.string.call_open_door, R.drawable.main_door_open);
                //播放语音
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1501_PATH, OPENDOOR_ROOMNO,true, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                    @Override
                    public void onMediaStatusCompletion(boolean flag) {
                        if (isExitFragment) {
                            //关闭声音播放
                            PlaySoundUtils.stopPlayAssetsSound();
                        } else {
                            exitFragment();
                        }
                        OPENDOOR_ROOMNO = null;
                        isPlaying = false;
                    }
                });
                break;
        }
    }

    private void showViewState(int colorId, int StringId, Object resId) {
        mTv.setTextColor(getResources().getColor(colorId));
        mTv.setText(getString(StringId));
        if (resId != null) {
            mImg.setVisibility(View.VISIBLE);
            mImg.setImageResource((int) resId);
        } else {
            mImg.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMediaStatusCompletion(boolean flag) {
        if (isExitFragment) {
            //关闭声音播放
            PlaySoundUtils.stopPlayAssetsSound();
        } else {
            if (flag) {
                isPlaying = false;
                exitFragment();
            }
        }

    }

    private void exitFragment() {
        count = 0;
    }


    class MyRun implements Runnable {
        @Override
        public void run() {
//            LogUtils.w(TAG+" MyRun count : " + count + " isPalying : " + isPlaying);
            if (count == 3 && !isPlaying) {
                mMainHandler.removeCallbacks(myRun);
                count = 0;
                //如果当前是在呼叫中心和呼叫住户界面不回主界面
                MainActivity activity = (MainActivity) mContext;
                if (activity != null && (activity.currentFrag instanceof CallCenterFragment
                        || activity.currentFrag instanceof CallResidentFragment)) {
                    return;
                }
                backMainActivity();
                return;
            }
            count++;
            mMainHandler.postDelayed(this, 1000);
        }
    }

    class HintDialogReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constant.Action.ACTION_HINT_DIALOG.equals(action)) {
                String extra = intent.getStringExtra(Constant.IntentId.INTENT_KEY);
                count = 0;
                setState(extra);
            }
        }
    }

}
