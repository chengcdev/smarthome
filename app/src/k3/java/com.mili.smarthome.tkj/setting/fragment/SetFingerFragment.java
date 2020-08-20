package com.mili.smarthome.tkj.setting.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.android.CommSysDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;
import com.mili.smarthome.tkj.utils.FragmentUtils;

public class SetFingerFragment extends ItemSelectorFragment {

    @Override
    protected String[] getStringArray() {
        int fingerprint = AppConfig.getInstance().getFingerprint();
        if (fingerprint == 0) {
            return new String[] {
                    mContext.getString(R.string.pub_disable),
                    mContext.getString(R.string.pub_enable)
            };
        } else {
            return new String[] {
                    mContext.getString(R.string.pub_disable),
                    mContext.getString(R.string.pub_enable),
                    mContext.getString(R.string.setting_030501),
                    mContext.getString(R.string.setting_030502),
                    mContext.getString(R.string.setting_030503)
            };
        }
    }

    @Override
    protected void bindData() {
        super.bindData();
        setSelection(AppConfig.getInstance().getFingerprint());
    }

    @Override
    public void onItemClick(int position) {
        String funcCode;
        switch (position) {
            case 0: // 禁用
            case 1: // 启用
                AppConfig.getInstance().setFingerprint(position);
                //发送广播
                ContextProxy.sendBroadcast(CommSysDef.BROADCAST_ENABLE_FINGER);
                //
                showResultAndBack(R.string.setting_suc);
                return;
            case 2: // 添加指纹
                funcCode = SettingFunc.SET_FINGER_ADD;
                break;
            case 3: // 删除指纹
                funcCode = SettingFunc.SET_FINGER_DEL;
                break;
            case 4: // 清空指纹
                funcCode = SettingFunc.SET_FINGER_CLEAR;
                break;
            default:
                return;
        }
        FragmentManager fm = getFragmentManager();
        Fragment fragment = FragmentFactory.create(funcCode);
        if (fm == null || fragment == null) {
            return;
        }
        FragmentUtils.replace(fm, R.id.fl_container, fragment, true);
    }
}
