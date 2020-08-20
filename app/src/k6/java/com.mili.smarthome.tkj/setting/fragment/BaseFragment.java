package com.mili.smarthome.tkj.setting.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.setting.activity.SettingActivity;

import java.util.Objects;

public class BaseFragment extends com.mili.smarthome.tkj.base.BaseFragment {

    public void setBackVisibility(int visibility) {
        FragmentActivity activity = getActivity();
        if (activity instanceof SettingActivity) {
            ((SettingActivity) activity).setBackVisibility(visibility);
        }
    }

    /**
     * 刷新设置列表显示
     */
    public void notifySetList() {
        Intent intent = new Intent(Constant.Action.SETTING_NOTIFYCHANGE);
        App.getInstance().sendBroadcast(intent);
    }

    public void exitFragment(Fragment fragment) {
        if (fragment.isAdded()) {
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
        }
    }


    public String getResString(int strId) {
        return App.getInstance().getString(strId);
    }
}
