package com.mili.smarthome.tkj.setting.fragment;

import android.support.v4.app.FragmentActivity;

import com.mili.smarthome.tkj.base.K3BaseFragment;
import com.mili.smarthome.tkj.setting.activity.ResetActivity;

public abstract class ResetBaseFragment extends K3BaseFragment{

    protected void gotoNextFragment() {
        FragmentActivity activity = getActivity();
        if (activity instanceof ResetActivity) {
            ((ResetActivity) activity).gotoNextFragment();
        }
    }

    protected void gotoPreviousFragment() {
        FragmentActivity activity = getActivity();
        if (activity instanceof ResetActivity) {
            ((ResetActivity) activity).gotoPreviousFragment();
        }
    }

    @Override
    public boolean requestBack() {
        gotoPreviousFragment();
        return true;
    }
}
