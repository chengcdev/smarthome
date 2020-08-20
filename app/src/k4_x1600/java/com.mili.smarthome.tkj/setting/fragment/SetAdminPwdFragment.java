package com.mili.smarthome.tkj.setting.fragment;

import android.annotation.SuppressLint;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.dao.UserInfoDao;
import com.mili.smarthome.tkj.widget.NumInputView;

/**
 * 修改管理密码
 */
public class SetAdminPwdFragment extends K4BaseFragment implements View.OnTouchListener {

    private NumInputView mIvPassword, mIvPassword1;
    private LinearLayout mLlContent;
    private RelativeLayout mLlButton;
    private TextView mTvHint;

    private int mFocusIndex = 0;
    private int mCursorIndex = 0;

    @Override
    public boolean onKeyCancel() {
        super.onKeyCancel();
        if (mFocusIndex == 0) {
            mIvPassword.backspace();
            if (mCursorIndex > 0) {
                mCursorIndex--;
            } else {
                exitFragment();
            }
        } else {
            if (mCursorIndex > 0) {
                mIvPassword1.backspace();
                mCursorIndex--;
            } else {
                mIvPassword.backspace();
                mFocusIndex = 0;
                mCursorIndex = 7;
                mIvPassword.requestFocus();
                mIvPassword.setCursorIndex(mCursorIndex);
            }
        }
        return true;
    }

    @Override
    public boolean onKeyConfirm() {
        super.onKeyConfirm();

        int resId;
        String password = mIvPassword.getText().toString();
        String password1 = mIvPassword1.getText().toString();
        if (password.length() != Constant.PASSWORD_ADMIN_LEN || password1.length() != Constant.PASSWORD_ADMIN_LEN) {
            resId = R.string.set_input_error;
        } else if (!password.equals(password1)) {
            resId = R.string.set_pwd_error;
        } else {
            UserInfoDao userInfoDao = new UserInfoDao();
            userInfoDao.setAdminPwd(password);
            resId = R.string.set_ok;
        }

        mTvHint.setText(resId);
        showView(false);
        mMainHandler.sendEmptyMessageDelayed(MSG_SET_OK, Constant.SET_HINT_TIMEOUT);
        return true;
    }

    @Override
    public boolean onKey(int code) {
        super.onKey(code);
        if (mFocusIndex == 0) {
            mIvPassword.input(code);
            mCursorIndex++;
            if (mCursorIndex >= 8) {
                mFocusIndex = 1;
                mCursorIndex = 0;
                mIvPassword1.setCursorIndex(mCursorIndex);
            }
        } else {
            mIvPassword1.input(code);
            if (mCursorIndex < 8) {
                mCursorIndex++;
            }
        }
        return true;
    }

    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.what == MSG_SET_OK) {
            showView(true);
            mIvPassword.setText("");
            mIvPassword1.setText("");
            mFocusIndex = 0;
            mCursorIndex = 0;
            mIvPassword.requestFocus();
            mIvPassword.setCursorIndex(mCursorIndex);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_pwd_admin;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void bindView() {
        super.bindView();
        TextView head = findView(R.id.tv_head);
        if (head != null) {
            head.setText(R.string.setting_0204);
        }
        mIvPassword = findView(R.id.iv_password);
        mIvPassword1 = findView(R.id.iv_password1);
        mLlContent = findView(R.id.ll_content);
        mLlButton = findView(R.id.ll_button);
        mTvHint = findView(R.id.tv_hint);

        mIvPassword.setOnTouchListener(this);
        mIvPassword1.setOnTouchListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mFocusIndex = 0;
        mCursorIndex = 0;
        mIvPassword.setCursorIndex(mCursorIndex);
        showView(true);
    }

    @Override
    protected void unbindView() {
        super.unbindView();
        mMainHandler.removeCallbacksAndMessages(0);
    }

    private void showView(boolean show) {
        if (show) {
            mLlContent.setVisibility(View.VISIBLE);
            mLlButton.setVisibility(View.VISIBLE);
            mTvHint.setVisibility(View.INVISIBLE);
        } else {
            mLlContent.setVisibility(View.INVISIBLE);
            mLlButton.setVisibility(View.INVISIBLE);
            mTvHint.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() != MotionEvent.ACTION_UP) {
            return false;
        }
        switch(view.getId()) {
            case R.id.iv_password:
                mIvPassword.requestFocus();
                mIvPassword.setCursorIndex(0);
                mFocusIndex = 0;
                mCursorIndex = 0;
                break;
            case R.id.iv_password1:
                mIvPassword1.requestFocus();
                mIvPassword1.setCursorIndex(0);
                mFocusIndex = 1;
                mCursorIndex = 0;
                break;
        }
        return false;
    }
}
