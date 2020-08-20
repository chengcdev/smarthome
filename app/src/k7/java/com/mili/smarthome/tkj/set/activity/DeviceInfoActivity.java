package com.mili.smarthome.tkj.set.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.main.activity.BaseK7Activity;
import com.mili.smarthome.tkj.main.widget.KeyBoardItemView;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.set.fragment.DeviceInfo1ragment;
import com.mili.smarthome.tkj.set.fragment.DeviceInfo2ragment;
import com.mili.smarthome.tkj.set.fragment.DeviceInfo3ragment;
import com.mili.smarthome.tkj.set.fragment.DeviceInfo4ragment;
import com.mili.smarthome.tkj.set.fragment.DeviceInfo5ragment;
import com.mili.smarthome.tkj.utils.AppManage;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceInfoActivity extends BaseK7Activity implements KeyBoardItemView.IOnKeyClickListener{

    @BindView(R.id.fl_container)
    FrameLayout flContent;
    @BindView(R.id.tv_page)
    TextView tvPage;
    @BindView(R.id.key_last)
    KeyBoardItemView keyLast;
    @BindView(R.id.key_cancle)
    KeyBoardItemView keyCancle;
    @BindView(R.id.lin_bottom)
    LinearLayout linBottom;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.key_next)
    KeyBoardItemView keyNext;
    private DeviceInfo1ragment deviceInfo1ragment;
    private int index;
    private DeviceInfo2ragment deviceInfo2ragment;
    private DeviceInfo3ragment deviceInfo3ragment;
    private DeviceInfo4ragment deviceInfo4ragment;
    private DeviceInfo5ragment deviceInfo5ragment;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_device_info);
        ButterKnife.bind(this);

        tvTitle.setText(getString(R.string.device_info));
        tvPage.setTextColor(Color.GREEN);
        tvPage.setText((index+1)+"/5");

        if (deviceInfo1ragment == null) {
            deviceInfo1ragment = new DeviceInfo1ragment();
        }
        AppManage.getInstance().replaceFragment(this,deviceInfo1ragment);
        KeyBoardItemView.setOnkeyClickListener(this);
    }


    @SuppressLint("SetTextI18n")
    private void toFragment() {
        tvPage.setText((index+1)+"/5");
        switch (index) {
            case 0:
                if (deviceInfo1ragment == null) {
                    deviceInfo1ragment = new DeviceInfo1ragment();
                }
                AppManage.getInstance().replaceFragment(this,deviceInfo1ragment);
                break;
            case 1:
                if (deviceInfo2ragment == null) {
                    deviceInfo2ragment = new DeviceInfo2ragment();
                }
                AppManage.getInstance().replaceFragment(this,deviceInfo2ragment);
                break;
            case 2:
                if (deviceInfo3ragment == null) {
                    deviceInfo3ragment = new DeviceInfo3ragment();
                }
                AppManage.getInstance().replaceFragment(this,deviceInfo3ragment);
                break;
            case 3:
                if (deviceInfo4ragment == null) {
                    deviceInfo4ragment = new DeviceInfo4ragment();
                }
                AppManage.getInstance().replaceFragment(this,deviceInfo4ragment);
                break;
            case 4:
                if (deviceInfo5ragment == null) {
                    deviceInfo5ragment = new DeviceInfo5ragment();
                }
                AppManage.getInstance().replaceFragment(this,deviceInfo5ragment);
                break;
        }
    }

    @Override
    public void OnViewDownClick(int code, View view) {
        int position = AppManage.getInstance().getPosition(code);
        switch (position) {
            case 12:
                AppManage.getInstance().keyBoardDown(keyLast);
                break;
            case 13:
                AppManage.getInstance().keyBoardDown(keyNext);
                break;
            case 14:
                AppManage.getInstance().keyBoardDown(keyCancle);
                break;
        }
    }

    @Override
    public void OnViewUpClick(int code, View view) {
        int position = AppManage.getInstance().getPosition(code);
        switch (position) {
            case 12:
                AppManage.getInstance().keyBoardUp(keyLast);
                index--;
                if (index < 0) {
                    index = 4;
                }
                toFragment();
                break;
            case 13:
                AppManage.getInstance().keyBoardUp(keyNext);
                index++;
                if (index > 4) {
                    index = 0;
                }
                toFragment();
                break;
            case 14:
                AppManage.getInstance().keyBoardUp(keyCancle);
                Intent intent = new Intent(Constant.ActionId.ACTION_REFRESH_MAIN_KEYBOARD);
                sendBroadcast(intent);
                finish();
                break;
        }
    }

}
