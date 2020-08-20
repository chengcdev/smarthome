package com.mili.smarthome.tkj.setting.fragment;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;

public class SetPowerModeFragment extends ItemSelectorFragment{

    @Override
    public boolean onKeyCancel() {
        super.onKeyCancel();
        exitFragment();
        return true;
    }

    @Override
    public boolean onKeyConfirm() {
        super.onKeyConfirm();
        onItemClick(getSelection());
        return true;
    }

    @Override
    protected String[] getStringArray() {
        return mContext.getResources().getStringArray(R.array.setting_switch);
    }

    @Override
    protected void bindData() {
        super.bindData();
        setSelection(AppConfig.getInstance().getPowerSaving());
        setHead(getResources().getString(R.string.setting_0504));
    }

    @Override
    public void onItemClick(int position) {
        AppConfig.getInstance().setPowerSaving(position);
        showSetHint(R.string.set_success);
    }
}
