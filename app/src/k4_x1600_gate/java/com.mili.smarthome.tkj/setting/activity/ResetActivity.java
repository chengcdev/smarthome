package com.mili.smarthome.tkj.setting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.AppPreferences;
import com.mili.smarthome.tkj.base.K4BaseActivity;
import com.mili.smarthome.tkj.base.KeyboardCtrl;
import com.mili.smarthome.tkj.base.KeyboardProxy;
import com.mili.smarthome.tkj.main.activity.MainActivity;
import com.mili.smarthome.tkj.setting.fragment.ResetBaseFragment;
import com.mili.smarthome.tkj.setting.fragment.ResetCardNumFragment;
import com.mili.smarthome.tkj.setting.fragment.ResetDevNoFragment;
import com.mili.smarthome.tkj.setting.fragment.ResetDevTypeFragment;
import com.mili.smarthome.tkj.setting.fragment.ResetLanguageFragment;
import com.mili.smarthome.tkj.setting.fragment.ResetNetworkFragment;
import com.mili.smarthome.tkj.utils.FragmentUtils;

import java.util.ArrayList;
import java.util.List;

import static com.mili.smarthome.tkj.base.KeyboardCtrl.KEYMODE_SET;

public class ResetActivity extends K4BaseActivity {

    private List<ResetBaseFragment> mFragments = new ArrayList<>();
    private int mCurrent;
    private KeyboardCtrl mKeyboardCtrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        mKeyboardCtrl = findView(R.id.keyboard);
    }

    @Override
    protected void onResume() {
        mKeyboardCtrl.setMode(KEYMODE_SET);
        KeyboardProxy.getInstance().setKeyboard(mKeyboardCtrl);
        super.onResume();

        mFragments.clear();
        FragmentUtils.replace(this, R.id.fl_content, getFragment(0));
        mCurrent = 0;
    }

    public void gotoNextFragment() {
        Log.d("rest", "curr " + mCurrent);
        if (mCurrent >= 4) {
            finishReset();
        } else {
            int next = mCurrent + 1;
            ResetBaseFragment fragment = getFragment(next);
            if (fragment != null) {
                FragmentUtils.replace(this, R.id.fl_content, fragment, true);
                mCurrent = next;
            }
        }
        Log.d("rest", "curr 1" + mCurrent);
    }

    public void gotoPreviousFragment() {
        Log.d("rest", "cureet " + mCurrent);
        if (mCurrent > 0) {
            getSupportFragmentManager().popBackStackImmediate();
            mCurrent--;
        }
    }

    private ResetBaseFragment getFragment(int index) {
        ResetBaseFragment fragment;
        if (mFragments.size() > index) {
            fragment = mFragments.get(index);
        } else {
            switch (index) {
                case 0:
                    fragment = new ResetLanguageFragment();
                    break;
                case 1:
                    fragment = new ResetCardNumFragment();
                    break;
                case 2:
                    fragment = new ResetDevTypeFragment();
                    break;
                case 3:
                    fragment = new ResetDevNoFragment();
                    break;
                case 4:
                    fragment = new ResetNetworkFragment();
                    break;
                default:
                    return null;
            }
            mFragments.add(fragment);
        }
        return fragment;
    }

    private void finishReset() {
        AppPreferences.setReset(false);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }
}
