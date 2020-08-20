package com.mili.smarthome.tkj.set.fragment;


import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.main.fragment.BaseKeyBoardFragment;
import com.mili.smarthome.tkj.main.widget.KeyBoardItemView;
import com.mili.smarthome.tkj.utils.AppManage;

import butterknife.BindView;

/**
 * 呼叫中心
 */

public class CallCenterFragment extends BaseKeyBoardFragment implements KeyBoardItemView.IOnKeyClickListener {

    @BindView(R.id.tv_top)
    TextView tvTop;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.key_ok)
    KeyBoardItemView keyOk;
    @BindView(R.id.key_cancle)
    KeyBoardItemView keyCancle;
    private Handler handler;
    private int totalTime = 3;
    private CountTimeRun countTimeRun;
    private String TAG = "CallCenterFragment";

    @Override
    public int getLayout() {
        return R.layout.fragment_call_center;
    }

    @Override
    public void setKeyBoard(int viewId, String keyId) {
    }

    @Override
    public void initView() {

    }

    @Override
    public void initAdapter() {
        setCountTime();
    }

    private void setCountTime() {
        if (handler == null) {
            handler = new Handler();
        }
        tvTime.setTextColor(Color.GREEN);
        tvTime.setText(totalTime + "S");
        if (countTimeRun == null) {
            countTimeRun = new CountTimeRun();
        }
        handler.postDelayed(countTimeRun, 1000);
    }

    @Override
    public void initListener() {
        KeyBoardItemView.setOnkeyClickListener(this);
    }





    @Override
    public void OnViewDownClick(int code, View view) {
        int position = AppManage.getInstance().getPosition(code);
        switch (position) {
            case 9:
                AppManage.getInstance().keyBoardDown(keyOk);
                break;
            case 11:
                AppManage.getInstance().keyBoardDown(keyCancle);
                break;
            default:
                break;
        }
    }

    @Override
    public void OnViewUpClick(int code, View view) {
        int position = AppManage.getInstance().getPosition(code);
        switch (position) {
            case 9:
                AppManage.getInstance().keyBoardUp(keyOk);
                if (countTimeRun != null) {
                    handler.removeCallbacks(countTimeRun);
                }
                //呼叫管理中心

                break;
            case 11:
                AppManage.getInstance().keyBoardUp(keyCancle);
                if (countTimeRun != null) {
                    handler.removeCallbacks(countTimeRun);
                }
                exitFragment(this);
                break;
        }
    }

    class CountTimeRun implements Runnable {

        @Override
        public void run() {

            totalTime--;

            if (totalTime < 0) {
                handler.removeCallbacks(this);
                exitFragment(CallCenterFragment.this);
                return;
            } else {
                setCountTime();
            }

            if (totalTime == 0) {
                tvTime.setTextColor(Color.RED);
            } else {
                tvTime.setTextColor(Color.GREEN);
            }

        }
    }
}
