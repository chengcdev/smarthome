package com.mili.smarthome.tkj.setting.fragment;

import android.content.Intent;

import com.android.CommSysDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.appfunc.AppConfig;


public class SetEventPlatformFragment extends ItemSelectorFragment {

    @Override
    protected String[] getStringArray() {
        return mContext.getResources().getStringArray(R.array.setting_event_platform);
    }

    @Override
    protected void bindData() {
        super.bindData();
        int mode = AppConfig.getInstance().getEventPlatform();
        setSelection(mode);
    }

    @Override
    public void onItemClick(int position) {
        AppConfig.getInstance().setEventPlatform(position);
        showResultAndBack(R.string.setting_suc);

        Intent intent = new Intent(CommSysDef.BROADCAST_EVENT_PLATFORM);
        App.getInstance().sendBroadcast(intent);
    }
}
