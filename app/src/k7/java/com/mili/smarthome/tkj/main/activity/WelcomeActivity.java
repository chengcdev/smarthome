package com.mili.smarthome.tkj.main.activity;

import android.content.Intent;
import android.os.Bundle;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.main.activity.direct.DirectPressMainActivity;

public class WelcomeActivity extends BaseK7Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //根据呼叫方式不同，跳转不同界面
        if (AppConfig.getInstance().getCallType() == 0) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, DirectPressMainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
