package com.mili.smarthome.tkj.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.mili.smarthome.tkj.utils.LogUtils;

public class ActivityLifecycleObserver implements Application.ActivityLifecycleCallbacks {

    private Activity mTopActivity;

    public Activity getTopActivity() {
        return mTopActivity;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        LogUtils.d("LIFECYCLE: %s--->>>onCreated", activity.getClass().getName());
    }

    @Override
    public void onActivityStarted(Activity activity) {
        LogUtils.d("LIFECYCLE: %s--->>>onStarted", activity.getClass().getName());
    }

    @Override
    public void onActivityResumed(Activity activity) {
        LogUtils.i("LIFECYCLE: %s--->>>onResumed", activity.getClass().getName());
        mTopActivity = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        LogUtils.d("LIFECYCLE: %s--->>>onPaused", activity.getClass().getName());
    }

    @Override
    public void onActivityStopped(Activity activity) {
        LogUtils.d("LIFECYCLE: %s--->>>onStopped", activity.getClass().getName());
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        LogUtils.d("LIFECYCLE: %s--->>>onDestroyed", activity.getClass().getName());
    }
}
