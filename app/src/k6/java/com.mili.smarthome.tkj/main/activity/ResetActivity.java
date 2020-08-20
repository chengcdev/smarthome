package com.mili.smarthome.tkj.main.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.base.BaseActivity;
import com.mili.smarthome.tkj.setting.fragment.ResetSelectorFragment;
import com.mili.smarthome.tkj.utils.AppUtils;

/**
 * 恢复出厂后启动activity
 */
@SuppressLint("Registered")
public class ResetActivity extends BaseActivity {

    private ResetSelectorFragment resetSelectorFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_act);

        init();
    }

    private void init() {
        if (resetSelectorFragment == null) {
            resetSelectorFragment = new ResetSelectorFragment();
        }
        //显示选择语言界面
        AppUtils.getInstance().replaceFragment(this,resetSelectorFragment,R.id.fl,"ResetSelectorFragment");
    }


}
