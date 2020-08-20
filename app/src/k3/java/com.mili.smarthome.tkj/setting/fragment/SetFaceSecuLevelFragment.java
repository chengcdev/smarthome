package com.mili.smarthome.tkj.setting.fragment;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;

/**
 * 人脸安全级别
 * {@link com.mili.smarthome.tkj.setting.entities.SettingFunc.SET_FACE_SECU_LEVEL}: 人脸安全级别
 */
public class SetFaceSecuLevelFragment extends ItemSelectorFragment {

    @Override
    protected String[] getStringArray() {
        return new String[] {
                mContext.getString(R.string.face_level_2),
                mContext.getString(R.string.face_level_1),
                mContext.getString(R.string.face_level_0)
        };
    }

    @Override
    protected void bindData() {
        super.bindData();
        setSelection(AppConfig.getInstance().getFaceSafeLevel());
    }

    @Override
    public void onItemClick(int position) {
        AppConfig.getInstance().setFaceSafeLevel(position);
        showResultAndBack(R.string.setting_suc);
    }
}
