package com.mili.smarthome.tkj.app;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import java.lang.ref.WeakReference;

public final class ContextProxy {

    private static WeakReference<Context> mContextRef;

    public static void setContext(Context context) {
        mContextRef = new WeakReference<>(context);
    }

    @NonNull
    public static Context getContext() {
        assert mContextRef != null;
        return mContextRef.get();
    }

    public static Resources getResources() {
        return getContext().getResources();
    }

    public static String getString(@StringRes int resId) {
        return getContext().getString(resId);
    }

    public static String getString(@StringRes int resId, Object... formatArgs) {
        return getContext().getString(resId, formatArgs);
    }

    /** 根据资源名获取字符串ID */
    public static int getStringId(String resName) {
        Context context = getContext();
        String packageName = context.getPackageName();
        return context.getResources().getIdentifier(resName, "string", packageName);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getSystemService(String serviceName) {
        return (T) getContext().getSystemService(serviceName);
    }

    public static void sendBroadcast(String action) {
        Intent intent = new Intent(action);
        getContext().sendBroadcast(intent);
    }

    public static void sendBroadcast(String action, Bundle extras) {
        Intent intent = new Intent(action);
        intent.putExtras(extras);
        getContext().sendBroadcast(intent);
    }
}
