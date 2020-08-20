package com.mili.smarthome.tkj.base;

import com.android.interf.IKeyEventListener;
import com.mili.smarthome.tkj.appfunc.FreeObservable;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;

public abstract class K3BaseFragment extends BaseFragment implements IKeyEventListener,
        FreeObservable.FreeObserver {

    @Override
    public void onResume() {
        super.onResume();
        FreeObservable.getInstance().addObserver(this);
        SinglechipClientProxy.getInstance().addKeyEventListener(this);
    }

    @Override
    public void onPause() {
        FreeObservable.getInstance().removeObserver(this);
        SinglechipClientProxy.getInstance().removeKeyEventListener(this);
        super.onPause();
    }

    @Override
    public boolean onKeyEvent(int keyCode, int keyState) {
        return false;
    }

    @Override
    public boolean onFreeReport(long freeTime) {
        return false;
    }
}
