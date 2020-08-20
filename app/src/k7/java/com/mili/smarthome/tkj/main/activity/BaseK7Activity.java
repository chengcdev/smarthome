package com.mili.smarthome.tkj.main.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;

import com.mili.smarthome.tkj.base.BaseActivity;
import com.mili.smarthome.tkj.utils.AppManage;

public abstract class BaseK7Activity extends BaseActivity {

    private long lastUpClickTime;
    private long time = 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lastUpClickTime = System.currentTimeMillis();
        time = 1000;
    }

    @Override
    protected void onResume() {
        //开启屏幕服务
        AppManage.getInstance().startScreenService();
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        LogUtils.w(" BaseK7Activity onKeyDown...");
        if (AppManage.getInstance().getmCurrentKeycode() == keyCode) {
            return true;
        }
        //开启屏幕服务
        AppManage.getInstance().startScreenService();
        return super.onKeyDown(keyCode, event);
    }

    public boolean isFastDoubleUpClick() {
        boolean isFast = false;
        long currentTime = System.currentTimeMillis();
//        LogUtils.w(" BaseK7Activity isFastDoubleUpClick... time: " + (currentTime - lastUpClickTime));
        if (currentTime - lastUpClickTime < time) {
            isFast = true;
        }
        lastUpClickTime = currentTime;
        return isFast;
    }
}
