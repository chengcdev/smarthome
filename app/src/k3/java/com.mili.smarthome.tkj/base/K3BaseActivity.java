package com.mili.smarthome.tkj.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.interf.IBodyInductionListener;
import com.android.interf.ICardStateListener;
import com.android.interf.IFingerEventListener;
import com.android.interf.IKeyEventListener;
import com.mili.smarthome.tkj.appfunc.FreeObservable;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;

public abstract class K3BaseActivity extends BaseActivity implements FreeObservable.FreeObserver,
        IKeyEventListener, ICardStateListener, IFingerEventListener, IBodyInductionListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SinglechipClientProxy.getInstance().addCardStateListener(this);
        SinglechipClientProxy.getInstance().addFingerEventListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FreeObservable.getInstance().addObserver(this);
        SinglechipClientProxy.getInstance().addKeyEventListener(this);
        SinglechipClientProxy.getInstance().setBodyInductionListener(this);
    }

    @Override
    protected void onPause() {
        FreeObservable.getInstance().removeObserver(this);
        SinglechipClientProxy.getInstance().removeKeyEventListener(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SinglechipClientProxy.getInstance().removeCardStateListener(this);
        SinglechipClientProxy.getInstance().removeFingerEventListener(this);
    }

    @Override
    public boolean onKeyEvent(int keyCode, int keyState) {
        return false;
    }

    @Override
    public void onCardState(int state, String roomNo) {
    }

    @Override
    public void onFingerCollect(int code, int press, int count) {
    }

    @Override
    public void onFingerAdd(int code, int fingerId, int valid, byte[] fingerData) {
    }

    @Override
    public void onFingerOpen(int code, String roomNo) {
    }

    @Override
    public void onBodyInduction() {

    }

    @Override
    public boolean onFreeReport(long freeTime) {
        return false;
    }
}
