package com.android.interf;

/**
 * 按键事件监听
 */
public interface IKeyEventListener {

    int KEYCODE_0 = 0x0A;
    int KEYCODE_1 = 0x01;
    int KEYCODE_2 = 0x02;
    int KEYCODE_3 = 0x03;
    int KEYCODE_4 = 0x04;
    int KEYCODE_5 = 0x05;
    int KEYCODE_6 = 0x06;
    int KEYCODE_7 = 0x07;
    int KEYCODE_8 = 0x08;
    int KEYCODE_9 = 0x09;
    int KEYCODE_BACK = 0x0B;
    int KEYCODE_UNLOCK = 0x0C;
    int KEYCODE_UP = 0x0D;
    int KEYCODE_DOWN = 0x0E;
    int KEYCODE_CALL = 0x0F;

    int KEYSTATE_DOWN = 0x10;
    int KEYSTATE_UP = 0x11;

    /**
     * 按键触发
     * @param keyCode 按键值
     * @param keyState 按键状态
     * @return 已处理返回true，否则返回false
     */
    boolean onKeyEvent(int keyCode, int keyState);
}
