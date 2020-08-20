package com.mili.smarthome.tkj.setting.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.CommTypeDef;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.dao.UserInfoDao;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;
import com.mili.smarthome.tkj.setting.utils.RoomNoHelper;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.widget.NumInputView;

/**
 * 密码管理（添加、删除、清空）
 */
public class PwdManageFragment extends K4BaseFragment implements View.OnClickListener, View.OnTouchListener {

    private LinearLayout mLlAdd, mLlDel;
    private RelativeLayout mLlButton;
    private TextView mTvHead, mTvHint;
    private NumInputView mAddRoomNo, mDelRoomNo, mPassword;
    private TextView mTvRoomText, mTvRoomTextDel;

    private String mFuncCode = SettingFunc.PASSWORD_ADD;
    private int mFocusIndex = 1;

    private RoomNoHelper mRoomNoHelper;
    private int mRoomNoLen = 4;
    private int mDeviceType;

    @Override
    public boolean onKeyCancel() {
        super.onKeyCancel();

        switch (mFuncCode) {
            case SettingFunc.PASSWORD_ADD:
                if (mAddRoomNo == null || mPassword == null) {
                    break;
                }
                if (mFocusIndex == 1) {
                    mPassword.backspace();
                    if (mPassword.getCursorIndex() == 0) {
                        mFocusIndex = 0;
                        mAddRoomNo.requestFocus();
                        mAddRoomNo.setCursorIndex(mRoomNoLen);
                    }
                } else if (mFocusIndex == 0) {
                    if (mAddRoomNo.getCursorIndex() == 0) {
                        exitFragment();
                    }
                    mAddRoomNo.backspace();
                }
                break;

            case SettingFunc.PASSWORD_DEL:
                if (mDelRoomNo == null) {
                    break;
                }
                mDelRoomNo.backspace();
                if (mDelRoomNo.getText().length() == 0) {
                    exitFragment();
                }
                break;

            case SettingFunc.PASSWORD_CLEAR:
                exitFragment();
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyConfirm() {
        super.onKeyConfirm();

        switch (mFuncCode) {
            case SettingFunc.PASSWORD_ADD:
                if (mPassword == null || mAddRoomNo == null) {
                    break;
                }
                if (mPassword.getText().length() == Constant.PASSWORD_LEN) {
                    String roomNo = mAddRoomNo.getText().toString();
                    String password = mPassword.getText().toString();
                    boolean ret = addPassword(roomNo, password);
                    if (ret) {
                        mMainHandler.sendEmptyMessage(MSG_SET_OK);
                    } else {
                        mMainHandler.sendEmptyMessage(MSG_SET_ERROR);
                    }
                }
                break;

            case SettingFunc.PASSWORD_DEL:
                if (mDelRoomNo == null) {
                    break;
                }
                if (mDelRoomNo.getText().length() == mRoomNoLen) {
                    String roomNo = mDelRoomNo.getText().toString();
                    boolean ret = delPassword(roomNo);
                    if (ret) {
                        mMainHandler.sendEmptyMessage(MSG_SET_OK);
                    } else {
                        mMainHandler.sendEmptyMessage(MSG_SET_ERROR);
                    }
                }
                break;

            case SettingFunc.PASSWORD_CLEAR:
                boolean ret = clearPassword();
                if (ret) {
                    mMainHandler.sendEmptyMessage(MSG_SET_OK);
                } else {
                    mMainHandler.sendEmptyMessage(MSG_SET_ERROR);
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onKey(int code) {
        super.onKey(code);
        if (mFuncCode.equals(SettingFunc.PASSWORD_ADD)) {
            if (mFocusIndex == 1) {
                mPassword.input(code);
            } else {
                mAddRoomNo.input(code);
                if (mAddRoomNo.getCursorIndex() >= mRoomNoLen) {
                    mFocusIndex = 1;
                    mPassword.requestFocus();
                    mPassword.setCursorIndex(0);
                }
            }
        } else if (mFuncCode.equals(SettingFunc.PASSWORD_DEL)) {
            if (mDelRoomNo != null) {
                mDelRoomNo.input(code);
            }
        }
        return true;
    }

    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);

        switch (msg.what) {
            case MSG_SET_OK:
                showView(SettingFunc.PASSWORD_CLEAR);
                if (mTvHint != null) {
                    mTvHint.setText(R.string.set_ok);
                }
                mMainHandler.sendEmptyMessageDelayed(MSG_REQUEST_EXIT, Constant.SET_HINT_TIMEOUT);
                break;

            case MSG_SET_ERROR:
                showView(SettingFunc.PASSWORD_CLEAR);
                if (mTvHint != null) {
                    mTvHint.setText(R.string.set_error);
                }
                mMainHandler.sendEmptyMessageDelayed(MSG_REQUEST_EXIT, Constant.SET_HINT_TIMEOUT);
                break;

            case MSG_REQUEST_EXIT:
                if (mFuncCode.equals(SettingFunc.PASSWORD_CLEAR)) {
                    exitFragment();
                } else {
                    nextRoomNo();
                    showView(mFuncCode);
                    if (mFuncCode.equals(SettingFunc.PASSWORD_ADD)) {
                        focusPassword();
                    }

                    //非默认房号长度，删除后进行清空编号
                    if (mFuncCode.equals(SettingFunc.PASSWORD_DEL) && mRoomNoLen != 4) {
                        mDelRoomNo.setText("");
                        setKeyboardText("");
                    }
                }
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_pwd;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void bindView() {
        super.bindView();

        mLlAdd = findView(R.id.ll_add);
        mLlDel = findView(R.id.ll_del);
        mLlButton = findView(R.id.ll_button);

        mTvHead = findView(R.id.tv_head);
        mTvHint = findView(R.id.tv_hint);

        mAddRoomNo = findView(R.id.itv_roomno);
        mDelRoomNo = findView(R.id.itv_roomno_del);
        mPassword = findView(R.id.itv_password);
        mTvRoomText = findView(R.id.tv_room_text);
        mTvRoomTextDel = findView(R.id.tv_room_text_del);

        ImageButton ibDown = findView(R.id.ib_down);
        ImageButton ibUp = findView(R.id.ib_up);
        if (ibDown != null) {
            ibDown.setOnClickListener(this);
        }
        if (ibUp != null) {
            ibUp.setOnClickListener(this);
        }

        mAddRoomNo.setOnTouchListener(this);
        mPassword.setOnTouchListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        //初始数据，获取房号长度和设备类型
        initData();
        mAddRoomNo.setMaxLength(mRoomNoLen);
        mDelRoomNo.setMaxLength(mRoomNoLen);

        //区口机是描述变更为编号
        if (mDeviceType == CommTypeDef.DeviceType.DEVICE_TYPE_AREA) {
            mTvRoomText.setText(R.string.setting_number);
            mTvRoomTextDel.setText(R.string.setting_number);
        }

        //获取功能类别
        Bundle bundle = getArguments();
        if (bundle != null) {
            mFuncCode = bundle.getString(FragmentFactory.ARGS_FUNCCODE);
            String desc = SettingFunc.getNameByCode(mFuncCode);
            if (desc != null && mTvHead != null) {
                mTvHead.setText(desc);
            }
        }
        LogUtils.d(" funcCode is " + mFuncCode);

        showView(mFuncCode);
        switch (mFuncCode) {
            case SettingFunc.PASSWORD_ADD:
                StringBuilder builder = new StringBuilder();
                for (int i=0; i<mRoomNoLen; i++) {
                    builder.append('0');
                }
                mAddRoomNo.setText(builder);
                focusPassword();
                break;
            case SettingFunc.PASSWORD_DEL:
                mDelRoomNo.requestFocus();
                mDelRoomNo.setText("");
                setKeyboardMaxlen(mRoomNoLen);
                setKeyboardText("");
                break;
            case SettingFunc.PASSWORD_CLEAR:
                mTvHint.setText(R.string.set_pwd_hint);
                break;
        }
    }

    @Override
    protected void unbindView() {
        super.unbindView();
        mMainHandler.removeCallbacksAndMessages(0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_down:
                nextRoomNo();
                break;
            case R.id.ib_up:
                preRoomNo();
                break;
        }
    }

    private void initData() {
        FullDeviceNo fullDeviceNo = new FullDeviceNo(getContext());
        mDeviceType = fullDeviceNo.getDeviceType();
        mRoomNoLen = fullDeviceNo.getRoomNoLen();
        if (CommTypeDef.DeviceType.DEVICE_TYPE_AREA == mDeviceType) {
            //如果是区口，编号长度=房号长度+梯口号长度
            mRoomNoLen = mRoomNoLen + fullDeviceNo.getStairNoLen();
        }

        // 默认房号长度为4时显示房号列表
        if (mRoomNoLen == 4) {
            mRoomNoHelper = new RoomNoHelper();
            mRoomNoHelper.reset();
        }
    }

    private void showView(String funcCode) {
        if (funcCode == null) {
            return;
        }
        if (mLlAdd == null || mLlDel == null || mLlButton == null || mTvHint == null) {
            return;
        }

        switch (funcCode) {
            case SettingFunc.PASSWORD_ADD:
                mLlAdd.setVisibility(View.VISIBLE);
                mLlDel.setVisibility(View.INVISIBLE);
                mLlButton.setVisibility(View.VISIBLE);
                mTvHint.setVisibility(View.INVISIBLE);
                break;

            case SettingFunc.PASSWORD_DEL:
                mLlAdd.setVisibility(View.INVISIBLE);
                mLlDel.setVisibility(View.VISIBLE);
                mLlButton.setVisibility(View.VISIBLE);
                mTvHint.setVisibility(View.INVISIBLE);
                break;

            case SettingFunc.PASSWORD_CLEAR:
                mLlAdd.setVisibility(View.INVISIBLE);
                mLlDel.setVisibility(View.INVISIBLE);
                mLlButton.setVisibility(View.INVISIBLE);
                mTvHint.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void focusPassword() {
        mFocusIndex = 1;
        setKeyboardMaxlen(Constant.PASSWORD_LEN);
        setKeyboardText("");
        if (mPassword != null) {
            mPassword.requestFocus();
            mPassword.setText("");
            mPassword.setCursorIndex(0);
        }
    }

    private void preRoomNo() {
        //不是默认长度，不支持房号切换
        if (mRoomNoLen != 4) {
            return;
        }

        String roomNo = "";
        if (mRoomNoHelper != null) {
            roomNo = mRoomNoHelper.getPreviousRoomNo();
        }
        showRoom(roomNo);
        if (mFuncCode.equals(SettingFunc.PASSWORD_DEL)) {
            setKeyboardText(roomNo);
        }
    }

    private void nextRoomNo() {
        //不是默认长度，不支持房号切换
        if (mRoomNoLen != 4) {
            return;
        }

        String roomNo = "";
        if (mRoomNoHelper != null) {
            roomNo = mRoomNoHelper.getNextRoomNo();
        }
        showRoom(roomNo);
        if (mFuncCode.equals(SettingFunc.PASSWORD_DEL)) {
            setKeyboardText(roomNo);
        }
    }

    private void showRoom(String text) {
        if (mFuncCode.equals(SettingFunc.PASSWORD_ADD)) {
            if (mAddRoomNo != null) {
                mAddRoomNo.setText(text);
            }
        } else if (mFuncCode.equals(SettingFunc.PASSWORD_DEL)) {
            if (mDelRoomNo != null) {
                mDelRoomNo.requestFocus();
                mDelRoomNo.setText(text);
            }
        }
    }

    private boolean addPassword(String roomNo, String password) {
        UserInfoDao userInfoDao = new UserInfoDao();
        userInfoDao.addOpenPwd(roomNo, password);
        return true;
    }

    private boolean delPassword(String roomNo) {
        UserInfoDao userInfoDao = new UserInfoDao();
        userInfoDao.deleteOpenPwd(roomNo);
        return true;
    }

    private boolean clearPassword() {
        UserInfoDao userInfoDao = new UserInfoDao();
        userInfoDao.clearAllOpenPwd();
        return true;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() != MotionEvent.ACTION_UP) {
            return false;
        }
        switch(view.getId()) {
            case R.id.itv_roomno:
                mFocusIndex = 0;
                mAddRoomNo.requestFocus();
                mAddRoomNo.setCursorIndex(0);
                break;
            case R.id.itv_password:
                mFocusIndex = 1;
                mAddRoomNo.requestFocus();
                mAddRoomNo.setCursorIndex(0);
                break;
        }
        return false;
    }
}
