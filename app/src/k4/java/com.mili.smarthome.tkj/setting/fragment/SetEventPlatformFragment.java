package com.mili.smarthome.tkj.setting.fragment;

import android.content.Intent;

import com.android.CommSysDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.appfunc.AppConfig;

public class SetEventPlatformFragment extends ItemSelectorFragment {

    @Override
    public boolean onKeyCancel() {
        super.onKeyCancel();
        exitFragment();
        return true;
    }

    @Override
    public boolean onKeyConfirm() {
        super.onKeyConfirm();
        onItemClick(getSelection());
        return true;
    }

    @Override
    protected String[] getStringArray() {
        return mContext.getResources().getStringArray(R.array.setting_event_platform);
    }

    @Override
    protected void bindData() {
        super.bindData();
        int mode = AppConfig.getInstance().getEventPlatform();
        setSelection(mode);
        setHead(mContext.getResources().getString(R.string.setting_0508));
    }

    @Override
    public void onItemClick(int position) {
        AppConfig.getInstance().setEventPlatform(position);
        showSetHint(R.string.set_success);

        Intent intent = new Intent(CommSysDef.BROADCAST_EVENT_PLATFORM);
        App.getInstance().sendBroadcast(intent);
    }
}
