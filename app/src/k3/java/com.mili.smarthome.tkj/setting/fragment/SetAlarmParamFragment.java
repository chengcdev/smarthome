package com.mili.smarthome.tkj.setting.fragment;

import com.android.CommSysDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.dao.param.AlarmParamDao;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;

/**
 * {@link com.mili.smarthome.tkj.setting.entities.SettingFunc.SET_ALARM_PARAM}: 报警参数
 */
public class SetAlarmParamFragment extends SetSelectorFragment {

    @Override
    protected void bindData() {
        super.bindData();
        setFuncList(new String[]{
                SettingFunc.SET_ADVANCED,
                SettingFunc.SET_FORCED_OPEN_ALARM
        });
        setOptions(new String[]{
                mContext.getString(R.string.setting_close),
                mContext.getString(R.string.setting_enable)
        });
        setSelection(AlarmParamDao.getForceOpen());
    }

    @Override
    public void onItemClick(int position) {
        AlarmParamDao.setForceOpen(position);

        //发送广播
        ContextProxy.sendBroadcast(CommSysDef.BROADCAST_FORCEDOPENDOOR);

        showResultAndBack(R.string.setting_suc);
    }
}
