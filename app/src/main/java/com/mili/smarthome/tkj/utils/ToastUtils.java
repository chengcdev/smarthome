package com.mili.smarthome.tkj.utils;

import android.content.Context;
import android.widget.Toast;

/**
 *
 * 2017-12-07: Created by zenghm.
 */
public final class ToastUtils {

    private ToastUtils() {}

    private static Toast mToast;

    public static void showShort(Context context, int msgId) {
        String msg = context.getString(msgId);
        show(context, msg, Toast.LENGTH_SHORT);
    }

    public static void showShort(Context context, String msg) {
        show(context, msg, Toast.LENGTH_SHORT);
    }

    public static void show(Context context, int msgId) {
        String msg = context.getString(msgId);
        show(context, msg, Toast.LENGTH_LONG);
    }

    public static void show(Context context, String msg) {
        show(context, msg, Toast.LENGTH_LONG);
    }

    public static void show(Context context, String msg, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, duration);
        }
        else {
            mToast.setText(msg);
            mToast.setDuration(duration);
        }
        mToast.show();
    }

    public static void cancel() {
        if (mToast != null)
            mToast.cancel();
    }
}
