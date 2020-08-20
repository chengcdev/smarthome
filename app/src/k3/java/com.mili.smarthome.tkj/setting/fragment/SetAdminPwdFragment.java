package com.mili.smarthome.tkj.setting.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.android.interf.IKeyEventListener;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.dao.param.ParamDao;
import com.mili.smarthome.tkj.widget.NumInputView;

/**
 * 修改管理密码
 */
public class SetAdminPwdFragment extends BaseSetFragment {

    private NumInputView tvPwd1;
    private NumInputView tvPwd2;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_admin_pwd;
    }

    @Override
    protected void bindView() {
        tvPwd1 = findView(R.id.tv_pwd);
        tvPwd2 = findView(R.id.tv_pwd_again);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvPwd1.requestFocus();
    }

    @Override
    public boolean onKeyEvent(int keyCode, int keyState) {
        if (keyState == IKeyEventListener.KEYSTATE_DOWN)
            return false;
        switch (keyCode) {
            case IKeyEventListener.KEYCODE_0:
                inputNum(0);
                break;
            case IKeyEventListener.KEYCODE_1:
            case IKeyEventListener.KEYCODE_2:
            case IKeyEventListener.KEYCODE_3:
            case IKeyEventListener.KEYCODE_4:
            case IKeyEventListener.KEYCODE_5:
            case IKeyEventListener.KEYCODE_6:
            case IKeyEventListener.KEYCODE_7:
            case IKeyEventListener.KEYCODE_8:
            case IKeyEventListener.KEYCODE_9:
                inputNum(keyCode);
                break;
            case IKeyEventListener.KEYCODE_BACK:
                backspace();
                break;
            case IKeyEventListener.KEYCODE_CALL:
                save();
                break;
        }
        return true;
    }

    private void save() {
        String pwd1 = tvPwd1.getText().toString();
        String pwd2 = tvPwd2.getText().toString();
        if (pwd1.length() != 8 || pwd2.length() != 8) {
            showResult(R.string.setting_input_error, new Runnable() {
                @Override
                public void run() {
                    tvPwd2.clearText();
                    tvPwd1.clearText();
                    tvPwd1.requestFocus();
                }
            });
        } else if (!pwd1.equals(pwd2)) {
            showResult(R.string.setting_pwd_diff_error, new Runnable() {
                @Override
                public void run() {
                    tvPwd2.clearText();
                    tvPwd1.clearText();
                    tvPwd1.requestFocus();
                }
            });
        } else {
            ParamDao.setAdminPwd(tvPwd1.getText().toString());
            showResultAndBack(R.string.setting_suc);
        }
    }

}
