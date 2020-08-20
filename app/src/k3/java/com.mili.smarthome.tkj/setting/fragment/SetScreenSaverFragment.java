package com.mili.smarthome.tkj.setting.fragment;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;

/**
 * 屏保设置
 * <p>{@link com.mili.smarthome.tkj.setting.entities.SettingFunc.SET_SCREEN_SAVER}: 屏保设置
 */
public class SetScreenSaverFragment extends ItemSelectorFragment {

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
        setSelection(AppConfig.getInstance().getScreenSaver());
    }

    @Override
    public void onItemClick(int position) {
        AppConfig.getInstance().setScreenSaver(position);
        showResultAndBack(R.string.setting_suc);
    }
}
