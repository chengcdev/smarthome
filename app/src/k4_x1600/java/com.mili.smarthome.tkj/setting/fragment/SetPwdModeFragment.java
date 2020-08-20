package com.mili.smarthome.tkj.setting.fragment;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;

public class SetPwdModeFragment extends ItemSelectorFragment {

    @Override
    public boolean onKeyCancel() {
        super.onKeyCancel();
        exitFragment();
        return true;
    }

    @Override
    public boolean onKeyConfirm() {
        super.onKeyConfirm();
        int position = getSelection();
        onItemClick(position);
        return true;
    }

    @Override
    protected String[] getStringArray() {
        return mContext.getResources().getStringArray(R.array.setting_pwd_mode);
    }

    @Override
    protected void bindData() {
        super.bindData();
        setSelection(AppConfig.getInstance().getPwdDoorMode());
        setHead(getResources().getString(R.string.setting_0501));
    }

    @Override
    public void onItemClick(int position) {
        AppConfig.getInstance().setPwdDoorMode(position);
        showSetHint(R.string.set_success);
    }
}
