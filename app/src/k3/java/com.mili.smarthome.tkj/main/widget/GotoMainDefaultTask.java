package com.mili.smarthome.tkj.main.widget;

import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.app.ContextProxy;

public class GotoMainDefaultTask implements Runnable {

    private static GotoMainDefaultTask mInstance;

    public static GotoMainDefaultTask getInstance() {
        if (mInstance == null)
            mInstance = new GotoMainDefaultTask();
        return mInstance;
    }

    private GotoMainDefaultTask() {
    }

    @Override
    public void run() {
        ContextProxy.sendBroadcast(Const.Action.MAIN_DEFAULT);
    }
}
