package com.mili.smarthome.tkj.base;

import java.util.ArrayList;
import java.util.List;

public class KeyboardProxy implements KeyboardCtrl.IKeyboardListener {

    private static final KeyboardProxy instance = new KeyboardProxy();
    private KeyboardCtrl keyboardUtil;
    private List<KeyboardCtrl.IKeyboardListener> mKeyboardtListenerList = new ArrayList<>();


    public static KeyboardProxy getInstance() {
        return instance;
    }

    public void setKeyboard(KeyboardCtrl keyboardView) {
        keyboardUtil = keyboardView;
        keyboardUtil.setKeyboardListener(this);
        mKeyboardtListenerList.clear();
    }

    public KeyboardCtrl getKeyboard() {
        return keyboardUtil;
    }

    public void addKeyboardListener(KeyboardCtrl.IKeyboardListener listener) {
        mKeyboardtListenerList.remove(listener);
        mKeyboardtListenerList.add(0, listener);
    }

    public void removeKeyboardListener(KeyboardCtrl.IKeyboardListener listener) {
        mKeyboardtListenerList.remove(listener);
    }

    @Override
    public boolean onKeyCancel() {
        for (KeyboardCtrl.IKeyboardListener listener : mKeyboardtListenerList) {
            if (listener.onKeyCancel()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onKeyConfirm() {
        for (KeyboardCtrl.IKeyboardListener listener : mKeyboardtListenerList) {
            if (listener.onKeyConfirm()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onKey(int code) {
        for (KeyboardCtrl.IKeyboardListener listener : mKeyboardtListenerList) {
            if (listener.onKey(code)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onKeyText(String text) {
        for (KeyboardCtrl.IKeyboardListener listener : mKeyboardtListenerList) {
            if (listener.onKeyText(text)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onTextChanged(String text) {
        for (KeyboardCtrl.IKeyboardListener listener : mKeyboardtListenerList) {
            if (listener.onTextChanged(text)) {
                return true;
            }
        }
        return false;
    }
}
