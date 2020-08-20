package com.mili.smarthome.tkj.setting.fragment;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;

/**
 * 省电模式
 * <p>{@link com.mili.smarthome.tkj.setting.entities.SettingFunc.SET_POWER_SAVING}: 省电模式
 */
public class SetPowerSavingFragment extends ItemSelectorFragment {

    @Override
    protected String[] getStringArray() {
        return new String[] {
                mContext.getString(R.string.setting_close),
                mContext.getString(R.string.setting_enable)
        };
    }

    @Override
    protected void bindData() {
        super.bindData();
        setSelection(AppConfig.getInstance().getPowerSaving());
    }

    @Override
    public void onItemClick(int position) {
        AppConfig.getInstance().setPowerSaving(position);
        showResultAndBack(R.string.setting_suc);
    }
}
