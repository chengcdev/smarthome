package com.mili.smarthome.tkj.setting.fragment;

import android.content.Intent;

import com.android.CommSysDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.dao.param.AlarmParamDao;

public class SetAlarmParamFragment extends ItemSelectorFragment {

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
        return mContext.getResources().getStringArray(R.array.setting_enabled);
    }

    @Override
    protected void bindData() {
        super.bindData();
        setSelection(AlarmParamDao.getForceOpen());
        setHead(getResources().getString(R.string.set_forceAlarm));
    }

    @Override
    public void onItemClick(int position) {
        AlarmParamDao.setForceOpen(position);

        //发送广播
        Intent intent = new Intent(CommSysDef.BROADCAST_FORCEDOPENDOOR);
        App.getInstance().sendBroadcast(intent);

        showSetHint(R.string.set_success);
    }
}
