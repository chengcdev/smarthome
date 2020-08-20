package com.mili.smarthome.tkj.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.utils.LogUtils;

public class AppReceiver extends BroadcastReceiver {

    public void register(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ActionId.KEY_DOWN_UPDATETOUCH);
        filter.addAction(Const.ActionId.SYSTEM_OTA_UPDATE_ACTION);
        context.registerReceiver(this, filter);
    }

    public void unregister(Context context) {
        context.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = (intent == null) ? null : intent.getAction();
        if (action == null)
            return;
        LogUtils.d("AppReceiver>>>>>onReceive: " + action);
        switch (action) {
            case Const.ActionId.KEY_DOWN_UPDATETOUCH:
                SinglechipClientProxy.getInstance().ctrlTouchKeyLampState(true);
                break;

            case Const.ActionId.SYSTEM_OTA_UPDATE_ACTION:
                SinglechipClientProxy.getInstance().delayTimeRebootSystem(300);
                break;
        }
    }
}
