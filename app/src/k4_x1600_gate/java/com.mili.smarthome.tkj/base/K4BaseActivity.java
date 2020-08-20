package com.mili.smarthome.tkj.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;

import com.android.client.SetDriverSinglechipClient;
import com.android.interf.IBodyInductionListener;
import com.android.interf.ICardStateListener;
import com.android.interf.IFingerEventListener;
import com.mili.smarthome.tkj.appfunc.FreeObservable;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;

public abstract class K4BaseActivity extends BaseActivity implements FreeObservable.FreeObserver,
        KeyboardCtrl.IKeyboardListener, ICardStateListener, IFingerEventListener, IBodyInductionListener {

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
        KeyboardProxy.getInstance().addKeyboardListener(this);
        SinglechipClientProxy.getInstance().setBodyInductionListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FreeObservable.getInstance().removeObserver(this);
        KeyboardProxy.getInstance().removeKeyboardListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SinglechipClientProxy.getInstance().removeCardStateListener(this);
        SinglechipClientProxy.getInstance().removeFingerEventListener(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if ( !SetDriverSinglechipClient.getInstance().getSystemSleep()) {
                    SetDriverSinglechipClient.getInstance().setSystemSleep(1);
                    return false;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onKeyCancel() {
        FreeObservable.getInstance().resetFreeTime();
        return false;
    }

    @Override
    public boolean onKeyConfirm() {
        FreeObservable.getInstance().resetFreeTime();
        return false;
    }

    @Override
    public boolean onKey(int code) {
        FreeObservable.getInstance().resetFreeTime();
        return false;
    }

    @Override
    public boolean onKeyText(String text) {
        return false;
    }

    @Override
    public boolean onTextChanged(String text) {
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
