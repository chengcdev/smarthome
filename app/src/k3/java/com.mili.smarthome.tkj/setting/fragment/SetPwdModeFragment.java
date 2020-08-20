package com.mili.smarthome.tkj.setting.fragment;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.setting.entities.SettingFuncManager;

/**
 * {@link com.mili.smarthome.tkj.setting.entities.SettingFunc.SET_OPEN_PWD_MODE}: 密码开门模式
 */
public class SetPwdModeFragment extends ItemSelectorFragment {

    @Override
    protected String[] getStringArray() {
        return new String[] {
                mContext.getString(R.string.setting_pwd_door1),
                mContext.getString(R.string.setting_pwd_door2)
        };
    }

    @Override
    protected void bindData() {
        super.bindData();
        setSelection(AppConfig.getInstance().getOpenPwdMode());
    }

    @Override
    public void onItemClick(int position) {
        AppConfig.getInstance().setOpenPwdMode(position);
        showResult(R.string.setting_suc, new Runnable() {
            @Override
            public void run() {
                SettingFuncManager.notifyPwdModeChanged();
                requestBack();
            }
        });
    }
}
