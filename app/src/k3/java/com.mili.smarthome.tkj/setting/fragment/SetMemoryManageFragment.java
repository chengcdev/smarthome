package com.mili.smarthome.tkj.setting.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;
import com.mili.smarthome.tkj.utils.ExternalMemoryUtils;
import com.mili.smarthome.tkj.utils.FragmentUtils;

public class SetMemoryManageFragment extends ItemSelectorFragment {

    private String[] mFuncCodes;

    @Override
    protected String[] getStringArray() {
        if (ExternalMemoryUtils.externalMemoryAvailable()) {
            mFuncCodes = new String[] {
                    SettingFunc.SET_MEMORY_CAPACITY,
                    SettingFunc.SET_MEMORY_EXT_CAPACITY,
                    SettingFunc.SET_MEMORY_FORMAT,
                    SettingFunc.SET_MEMORY_MEDIA,
            };
            return new String[] {
                    mContext.getString(R.string.setting_040701),
                    mContext.getString(R.string.setting_040704),
                    mContext.getString(R.string.setting_040702),
                    mContext.getString(R.string.setting_040703)
            };
        } else {
            mFuncCodes = new String[] {
                    SettingFunc.SET_MEMORY_CAPACITY,
                    SettingFunc.SET_MEMORY_FORMAT,
                    SettingFunc.SET_MEMORY_MEDIA,
            };
            return new String[] {
                    mContext.getString(R.string.setting_040701),
                    mContext.getString(R.string.setting_040702),
                    mContext.getString(R.string.setting_040703)
            };
        }
    }

    @Override
    protected void bindData() {
        super.bindData();
        setSelection(-1);
    }

    @Override
    public void onItemClick(int position) {
        final String funcCode = mFuncCodes[position];
        FragmentManager fm = getFragmentManager();
        Fragment fragment = FragmentFactory.create(funcCode);
        if (fm == null || fragment == null) {
            return;
        }
        FragmentUtils.replace(fm, R.id.fl_container, fragment, true);
    }
}
