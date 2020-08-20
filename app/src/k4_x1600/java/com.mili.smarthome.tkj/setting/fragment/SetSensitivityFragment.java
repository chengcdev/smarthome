package com.mili.smarthome.tkj.setting.fragment;

import android.content.Intent;

import com.android.CommSysDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.dao.param.ParamDao;

public class SetSensitivityFragment extends ItemSelectorFragment {

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
        return mContext.getResources().getStringArray(R.array.setting_sensitivity);
    }

    @Override
    protected void bindData() {
        super.bindData();
        setSelection(ParamDao.getTouchSensitivity());
        setHead(getResources().getString(R.string.setting_0507));
    }

    @Override
    public void onItemClick(int position) {
        ParamDao.setTouchSensitivity(position);
        showSetHint(R.string.set_success);

        Intent intent = new Intent(CommSysDef.BROADCAST_TOUCHSENS);
        App.getInstance().sendBroadcast(intent);
    }
}
