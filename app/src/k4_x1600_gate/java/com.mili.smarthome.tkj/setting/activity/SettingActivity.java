package com.mili.smarthome.tkj.setting.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.base.K4BaseActivity;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.setting.fragment.DevInfoFragment;
import com.mili.smarthome.tkj.setting.fragment.SettingFragment;


public class SettingActivity extends K4BaseActivity {

    private RadioGroup mRgSetType1, mRgSetType2;
    private SettingFragment mFmSetting;
    private DevInfoFragment mFmDevInfo;
    private Fragment mFmCurrent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();

        mFmSetting = new SettingFragment();
        fragmentReplace(mFmSetting);
        mFmCurrent = mFmSetting;
    }

    public void fragmentReplace(Fragment fragment) {
        if (mFmCurrent != fragment) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            ft.commitAllowingStateLoss();
            mFmCurrent = fragment;
        }
    }

    private void initView() {
        mRgSetType1 = findView(R.id.rg_settype1);
        mRgSetType2 = findView(R.id.rg_settype2);
        mRgSetType1.check(R.id.rb_card);

        RadioButton rb1 = findView(R.id.rb_card);
        RadioButton rb2 = findView(R.id.rb_password);
        RadioButton rb3 = findView(R.id.rb_door);
        RadioButton rb4 = findView(R.id.rb_system);
        RadioButton rb5 = findView(R.id.rb_advanced);
        RadioButton rb6 = findView(R.id.rb_info);

        rb1.setOnClickListener(mRbClickListener);
        rb2.setOnClickListener(mRbClickListener);
        rb3.setOnClickListener(mRbClickListener);
        rb4.setOnClickListener(mRbClickListener);
        rb5.setOnClickListener(mRbClickListener);
        rb6.setOnClickListener(mRbClickListener);
    }

    private View.OnClickListener mRbClickListener = new RadioGroup.OnClickListener() {
        @Override
        public void onClick(View view) {

            /*解决两个RadioGroup之间的选中问题*/
            int viewId = view.getId();
            switch (viewId) {
                case R.id.rb_card:
                case R.id.rb_password:
                case R.id.rb_door:
                    mRgSetType2.clearCheck();
                    break;
                case R.id.rb_system:
                case R.id.rb_advanced:
                case R.id.rb_info:
                    mRgSetType1.clearCheck();
                    break;
            }

            /*切换设置功能*/
            switch (viewId) {
                case R.id.rb_card:
                case R.id.rb_password:
                case R.id.rb_door:
                case R.id.rb_system:
                case R.id.rb_advanced:
                    if (mFmSetting == null) {
                        mFmSetting = new SettingFragment();
                    }
                    mFmSetting.resetFunc(getSetType(viewId));
                    fragmentReplace(mFmSetting);
                    break;

                case R.id.rb_info:
                    if (mFmDevInfo == null) {
                        mFmDevInfo = new DevInfoFragment();
                    }
                    fragmentReplace(mFmDevInfo);
                    break;
            }
        }
    };

    private int getSetType(int viewId) {
        int setType = 0;
        switch (viewId) {
            case R.id.rb_card:
                setType = 0;
                break;
            case R.id.rb_password:
                setType = 1;
                break;
            case R.id.rb_door:
                setType = 2;
                break;
            case R.id.rb_system:
                setType = 3;
                break;
            case R.id.rb_advanced:
                setType = 4;
                break;
        }
        return setType;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onFreeReport(long freeTime) {
        if (freeTime > Constant.SCREEN_BACKMAIN_SET_TIME) {
            finish();
        }
        return true;
    }
}
