package com.mili.smarthome.tkj.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;

import com.mili.smarthome.tkj.app.AppPreferences;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.appfunc.BuildConfigHelper;
import com.mili.smarthome.tkj.appfunc.FreeObservable;

public abstract class BaseActivity extends FragmentActivity {

    protected Context mContext;
    protected Handler mMainHandler;

    protected void handleMessage(Message msg) {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(AppPreferences.getAppTheme());
        mContext = this;
        mMainHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                BaseActivity.this.handleMessage(msg);
                return true;
            }
        });
        //设置屏幕方向
        setScreenDirection();
    }

    protected  void setScreenDirection(){
        if (BuildConfigHelper.isK3() || BuildConfigHelper.isK6()) {
            //横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else {
            //竖屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMainHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            Const.SystemBootUpTouchEvent.SYSTEM_BOOT_UP_HAVE_TOUCH_ENEVT = true;

            //重置空闲时间
            FreeObservable.getInstance().resetFreeTime();
        }
        return super.dispatchTouchEvent(ev);
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T findView(@IdRes int id) {
        return (T) super.findViewById(id);
    }
}
