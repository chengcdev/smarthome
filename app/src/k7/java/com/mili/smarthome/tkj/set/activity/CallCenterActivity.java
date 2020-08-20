package com.mili.smarthome.tkj.set.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.main.activity.BaseK7Activity;
import com.mili.smarthome.tkj.main.widget.KeyBoardItemView;
import com.mili.smarthome.tkj.set.call.CallHelper;
import com.mili.smarthome.tkj.utils.AppManage;

public class CallCenterActivity extends BaseK7Activity implements KeyBoardItemView.IOnKeyClickListener {

    TextView tvTop;
    TextView tvTime;
    KeyBoardItemView keyOk;
    KeyBoardItemView keyCancle;
    private Handler handler;
    private int totalTime = 3;
    private CountTimeRun countTimeRun;
    private String TAG = "CallCenterFragment";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_call_center);

        initView();
        KeyBoardItemView.setOnkeyClickListener(this);
        setCountTime();
    }

    private void initView() {
        tvTop = (TextView) findView(R.id.tv_top);
        tvTime = (TextView) findView(R.id.tv_time);
        keyOk = (KeyBoardItemView) findView(R.id.key_ok);
        keyCancle = (KeyBoardItemView) findView(R.id.key_cancle);
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
                CallHelper.getInstance().callCenter(this);
                finish();
                break;
            case 11:
                AppManage.getInstance().keyBoardUp(keyCancle);
                if (countTimeRun != null) {
                    handler.removeCallbacks(countTimeRun);
                }
                finish();
                break;
        }
    }


    class CountTimeRun implements Runnable {
        @Override
        public void run() {

            totalTime--;

            if (totalTime < 0) {
                handler.removeCallbacks(this);
                finish();
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
