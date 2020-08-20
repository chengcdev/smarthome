package com.mili.smarthome.tkj.setting.fragment;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;

/**
 * 人脸活体检测
 * {@link com.mili.smarthome.tkj.setting.entities.SettingFunc.SET_FACE_LIVENESS}: 人脸活体检测
 */
public class SetFaceLivenessFragment extends ItemSelectorFragment {

    @Override
    protected String[] getStringArray() {
        return new String[] {
                mContext.getString(R.string.pub_disable),
                mContext.getString(R.string.pub_enable)
        };
    }

    @Override
    protected void bindData() {
        super.bindData();
        setSelection(AppConfig.getInstance().getFaceLiveCheck());
    }

    @Override
    public void onItemClick(int position) {
        AppConfig.getInstance().setFaceLiveCheck(position);
        showResultAndBack(R.string.setting_suc);
    }
}
