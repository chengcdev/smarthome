package com.mili.smarthome.tkj.main.manage;

import com.mili.smarthome.tkj.main.interf.IKeyBoardListener;
import com.mili.smarthome.tkj.main.interf.IKeyBoardRefreshListener;

public class KeyBoardEventManage {

    public static KeyBoardEventManage keyboardManage;
    private IKeyBoardRefreshListener mKeyBoardRefreshListener;
    private IKeyBoardListener mKeBoardListener;
    public static final int INPUT_TYPE_MAIN = 0x0F;
    public static final int INPUT_TYPE_ROOM_NO = 0x1F;
    public static final int INPUT_TYPE_OPEN_PWD = 0x2F;
    public static final int INPUT_TYPE_ADMIN_PWD = 0x3F;
    public static final int INPUT_TYPE_SET = 0x4F;
    public static final int INPUT_TYPE_OPEN_PWD_DIRECT = 0x5F;
    public static final int INPUT_TYPE_ROOM_NO_SHOW = 0x6F;

    public static KeyBoardEventManage getInstance() {
        if (keyboardManage == null) {
            keyboardManage = new KeyBoardEventManage();
        }
        return keyboardManage;
    }

    public void setKeyBoardListener(IKeyBoardListener keyBoardListener) {
        mKeBoardListener = keyBoardListener;
    }

    public void setKeyBoard(int viewId,String kid) {
        if (mKeBoardListener != null) {
            mKeBoardListener.onKeyBoard(viewId,kid);
        }
    }


    public void setRefreshListener(IKeyBoardRefreshListener keyBoardRefreshListener) {
        mKeyBoardRefreshListener = keyBoardRefreshListener;
    }

    public void notifyKeyBoard(int inputType) {
        if (mKeyBoardRefreshListener != null) {
            mKeyBoardRefreshListener.onRefresh(inputType);
        }
    }
}
