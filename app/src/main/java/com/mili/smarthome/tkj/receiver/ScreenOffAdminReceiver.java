package com.mili.smarthome.tkj.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenOffAdminReceiver extends DeviceAdminReceiver {
    private static final int REQUEST_ACTIVATE = 0;

    @Override
    public void onEnabled(Context context, Intent intent) {
//        ToastUtils.show(context,"设备管理器使能");
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
//        ToastUtils.show(context,"设备管理器没有使能");

    }
}
