package com.mili.smarthome.tkj.main.activity;

import android.view.KeyEvent;
import android.view.MotionEvent;

import com.mili.smarthome.tkj.base.BaseActivity;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.inteface.IActCallBackListener;
import com.mili.smarthome.tkj.utils.AppUtils;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;

import java.util.Timer;
import java.util.TimerTask;

public class BaseMainActivity extends BaseActivity {

    public IActCallBackListener actCallBackListener;
    private MyTimeTask myTimeTask;
    private Timer timer;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //开屏屏幕服务
                AppUtils.getInstance().startScreenService();
                //操作
                if (timer != null) {
                    timer.cancel();
                    myTimeTask.cancel();
                    timer = null;
                    myTimeTask = null;
                }
                Constant.ScreenId.SCREEN_NO_TOUCH = true;
//                LogUtils.w(" BaseMainActivity ACTION_DOWN dispatchTouchEvent isAlarm：" + Constant.IS_ALARM);
                if (Constant.IS_ALARM) {
                    //关闭声音
                    PlaySoundUtils.stopPlayAssetsSound();
                    Constant.IS_ALARM = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                //10秒后视为不操作
                timer = new Timer();
                myTimeTask = new MyTimeTask();
                timer.schedule(myTimeTask, 10000);
//                LogUtils.w(" BaseMainActivity ACTION_UP dispatchTouchEvent isAlarm：" + Constant.IS_ALARM);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //开启屏保和关屏服务
        AppUtils.getInstance().startScreenService();
    }

    public void setActCallBackListener(IActCallBackListener actCallBackListener) {
        this.actCallBackListener = actCallBackListener;
    }

    class MyTimeTask extends TimerTask {
        @Override
        public void run() {
            Constant.ScreenId.SCREEN_NO_TOUCH = false;
        }
    }
}
