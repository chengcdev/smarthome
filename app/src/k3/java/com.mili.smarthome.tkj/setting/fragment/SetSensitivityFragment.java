package com.mili.smarthome.tkj.setting.fragment;

import com.android.CommSysDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.dao.param.ParamDao;

/**
 * 灵敏度设置
 * {@link com.mili.smarthome.tkj.setting.entities.SettingFunc.SET_SENSITIVITY}: 灵敏度设置
 */
public class SetSensitivityFragment extends ItemSelectorFragment {

    @Override
    protected String[] getStringArray() {
        return new String[] {
                mContext.getString(R.string.key_sensitivity_0),
                mContext.getString(R.string.key_sensitivity_1),
                mContext.getString(R.string.key_sensitivity_2)
        };
    }

    @Override
    protected void bindData() {
        super.bindData();
        setSelection(ParamDao.getTouchSensitivity());
    }

    @Override
    public void onItemClick(int position) {
        ParamDao.setTouchSensitivity(position);

        //发送广播
        ContextProxy.sendBroadcast(CommSysDef.BROADCAST_TOUCHSENS);

        showResultAndBack(R.string.setting_suc);
    }
}
