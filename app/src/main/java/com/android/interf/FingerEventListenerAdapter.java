package com.android.interf;

/**
 * 指纹事件监听适配器
 */
public class FingerEventListenerAdapter implements IFingerEventListener {

    @Override
    public void onFingerCollect(int code, int press, int count) {

    }

    @Override
    public void onFingerAdd(int code, int fingerId, int valid, byte[] fingerData) {

    }

    @Override
    public void onFingerOpen(int code, String roomNo) {

    }
}
