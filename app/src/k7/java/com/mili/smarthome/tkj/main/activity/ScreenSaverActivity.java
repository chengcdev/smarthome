package com.mili.smarthome.tkj.main.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.main.manage.MessageManage;
import com.mili.smarthome.tkj.set.Constant;

import java.util.ArrayList;
import java.util.List;


public class ScreenSaverActivity extends BaseK7Activity implements Runnable{

    ImageView img;
    private List<Integer> drawableList = new ArrayList<>();
    private Handler handler = new Handler();
    private int count = 0;
    private CloseActRececiver receciver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_screen_pro);

        Constant.ScreenId.IS_SCREEN_SAVE = true;

        img = (ImageView) findViewById(R.id.img);
        initRegister();
        drawableList.clear();
        drawableList.add(R.drawable.screensaver0);
        drawableList.add(R.drawable.screensaver1);
        drawableList.add(R.drawable.screensaver2);

        playPic();
        img.setImageResource(drawableList.get(count));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Constant.ScreenId.IS_SCREEN_SAVE = false;
        //信息初始化
        MessageManage.getInstance().initMessage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receciver);
    }

    private void initRegister() {
        receciver = new CloseActRececiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ActionId.ACITON_CLOSE_SCREEN_PROTECT);
        registerReceiver(receciver, intentFilter);
    }

    private void playPic() {
        handler.postDelayed(this, 10000);
    }

    @Override
    public void run() {
        count++;
        if (count > 2) {
            count = 0;
        }
        img.setImageResource(drawableList.get(count));
        playPic();
    }

    public void onViewClicked() {
        handler.removeCallbacks(this);
        finish();
    }

    class CloseActRececiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constant.ActionId.ACITON_CLOSE_SCREEN_PROTECT.equals(action)) {
                finish();
            }
        }
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        finish();
        return super.onKeyUp(keyCode, event);
    }
}
