package com.mili.smarthome.tkj.base;

import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.mili.smarthome.tkj.call.CallMonitorBean;
import com.mili.smarthome.tkj.main.activity.MainActivity;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.widget.InputView;

public abstract class K4BaseFragment extends BaseFragment implements KeyboardCtrl.IKeyboardListener {

    public static final int MSG_MAIN_HINT = 1;
    public static final int MSG_CALL_HINT = 2;
    public static final int MSG_TIME_HINT = 3;
    public static final int MSG_REQUEST_EXIT = 4;
    public static final int MSG_SET_OK = 5;
    public static final int MSG_SET_ERROR = 6;
    public static final int MSG_SET_SHOW = 7;

    private KeyboardCtrl keyboardCtrl;
    private FragmentProxy.FragmentListener mFragmentListener;



    @Override
    protected void bindData() {
        super.bindData();
        KeyboardProxy.getInstance().addKeyboardListener(this);
        keyboardCtrl = KeyboardProxy.getInstance().getKeyboard();
        mFragmentListener = FragmentProxy.getInstance().getFragmentListener();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        KeyboardProxy.getInstance().addKeyboardListener(this);
//        keyboardCtrl = KeyboardProxy.getInstance().getKeyboard();
//        mFragmentListener = FragmentProxy.getInstance().getFragmentListener();
//    }

    @Override
    public void onPause() {
        super.onPause();
        KeyboardProxy.getInstance().removeKeyboardListener(this);
        mMainHandler.removeCallbacksAndMessages(0);
    }

    /**
     * 退出fragment
     */
    public void exitFragment() {
        if (mFragmentListener != null) {
            mFragmentListener.onExitFragment();
        }
    }

    public void setFragmentClickable(boolean clickable) {
        if (mFragmentListener != null) {
            mFragmentListener.setClickable(clickable);
        }
    }

    /**
     * 控制主activity的控件点击状态
     * @param mode          0-功能键和键盘  1-功能键
     * @param clickable     是否可以点击
     */
    public void setMainClickable(int mode, boolean clickable) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            MainActivity mainActivity = (MainActivity) activity;
            if (mode == 0) {
                mainActivity.setMainEnable(clickable);
            } else if (mode == 1){
                mainActivity.setRadioButtonEnable(clickable);
            }
        }
    }

    /**
     *  呼叫或通话时刷卡等提示处理
     * @param action    动作类型
     * @param roomNo    房号
     */
    public void actionCallback(String action, String roomNo) {

    }

    /**
     *  呼叫住户和呼叫中心界面，处理开锁和监视功能
     * @param callMonitorBean   呼叫监视参数
     */
    public void onMonitor(CallMonitorBean callMonitorBean) {

    }

    public void setKeboardListener() {
        KeyboardProxy.getInstance().addKeyboardListener(this);
    }

    @Override
    public boolean onKeyCancel() {
        return false;
    }

    @Override
    public boolean onKeyConfirm() {
        return false;
    }

    @Override
    public boolean onKey(int code) {
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

    protected void setKeyboardMaxlen(int len) {
        if (keyboardCtrl != null) {
            keyboardCtrl.setTextMaxLen(len);
        } else {
            LogUtils.e(" keyboard is null. ");
        }
    }

    /**
     * 设置键盘当前文本内容
     * @param text  键盘内容
     */
    protected void setKeyboardText(String text) {
        if (keyboardCtrl != null) {
            keyboardCtrl.setText(text);
        }
    }

    /**
     * 设置键盘模式
     * @param mode 键盘模式, 参考KeyboardCtrl.KEYMODE_PASSWORD
     */
    protected void setKeyboardMode(int mode) {
        if (keyboardCtrl != null) {
            keyboardCtrl.setMode(mode);
        }
    }

    /**
     *  动态键盘
     */
    protected void shuffleKeyboard() {
        if (keyboardCtrl != null) {
            keyboardCtrl.shuffleKeyboard();
        }
    }

    /**
     *  恢复默认键盘
     */
    protected void resetKeyboard() {
        if (keyboardCtrl != null) {
            keyboardCtrl.resetKeyboard();
        }
    }

    /**
     *  编辑框退格
     */
    protected void backspaceExit() {
        View root = getView();
        if (root != null) {
            View focus = root.findFocus();
            if (focus instanceof InputView) {
                InputView iptView = (InputView) focus;
                if (iptView.backspace())
                    return;
            }
        }
        exitFragment();
    }
}
