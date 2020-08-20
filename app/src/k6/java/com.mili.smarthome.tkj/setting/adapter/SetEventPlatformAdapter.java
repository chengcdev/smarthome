package com.mili.smarthome.tkj.setting.adapter;

import android.content.Context;
import android.content.Intent;

import com.android.CommSysDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.setting.interf.IOnItemClickListener;

public class SetEventPlatformAdapter extends ItemSelectorAdapter {

    private IOnItemClickListener itemClickListener;

    public SetEventPlatformAdapter(Context context) {
        super(context);
    }

    public SetEventPlatformAdapter(Context context, IOnItemClickListener itemClickListener) {
        super(context);
        this.itemClickListener = itemClickListener;
        setSelection(AppConfig.getInstance().getEventPlatform());
    }

    @Override
    protected int getStringArrayId() {
        return R.array.setting_event_platform;
    }

    @Override
    protected void onItemClick(int position) {
        switch (position) {
            case 0:
                AppConfig.getInstance().setEventPlatform(0);
                break;
            case 1:
                AppConfig.getInstance().setEventPlatform(1);
                break;
        }
        if (itemClickListener != null) {
            itemClickListener.OnItemListener(position);
        }

        Intent intent = new Intent(CommSysDef.BROADCAST_EVENT_PLATFORM);
        App.getInstance().sendBroadcast(intent);
    }
}
