package com.mili.smarthome.tkj.setting.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.CommTypeDef;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.adapter.KeyBoardAdapter;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.dao.UserInfoDao;
import com.mili.smarthome.tkj.entity.KeyBoardBean;
import com.mili.smarthome.tkj.entity.RoomNoHelper;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;
import com.mili.smarthome.tkj.view.KeyBoardView;
import com.mili.smarthome.tkj.view.SetOperateView;
import com.mili.smarthome.tkj.widget.NumInputView;

/**
 * 密码管理（添加、删除）
 */
public class PwdManageFragment extends BaseFragment implements KeyBoardAdapter.IKeyBoardListener, SetOperateView.IOperateListener{

    private NumInputView mRoomNo;
    private NumInputView mPwd;
    private String mFuncCode;
    private SetOperateView mOperateView;
    private KeyBoardView keyBoardView;
    private String currentPwdNo = "";
    private String currentRoomNo = "";
    private FullDeviceNo fullDeviceNo;
    private int roomNoLen;
    private int stairNoLen;
    private int pwdLen = 6;
    private TextView mTvLeftRoom;
    private LinearLayout mLinPwd;
    private RoomNoHelper mRoomNoHelper;
    private int mDeviceType;
    //默认房号长度
    private final int defaaultRoomNoLen = 4;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_pwd;
    }

    @Override
    protected void bindView() {
        mRoomNo = findView(R.id.tv_room_no);
        mPwd = findView(R.id.tv_pwd);
        mOperateView = findView(R.id.rootview);
        keyBoardView = findView(R.id.keyboardview);
        mTvLeftRoom = findView(R.id.tv_left_room);
        mLinPwd = findView(R.id.lin_pwd);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void bindData() {
        keyBoardView.init(KeyBoardView.KEY_BOARD_SET);
        keyBoardView.setKeyBoardListener(this);
        mOperateView.setSuccessListener(this);

        Bundle args = getArguments();
        if (args != null) {
            mFuncCode = args.getString(FragmentFactory.ARGS_FUNCCODE, SettingFunc.PASSWORD_ADD);
            switch (mFuncCode) {
                case SettingFunc.PASSWORD_ADD:
                    mLinPwd.setVisibility(View.VISIBLE);
                    break;
                case SettingFunc.PASSWORD_DEL:
                    mLinPwd.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }

        initData();
    }

    private void initData() {
        fullDeviceNo = new FullDeviceNo(getContext());
        //房号长度
        roomNoLen = fullDeviceNo.getRoomNoLen();
        //梯号长度
        stairNoLen = fullDeviceNo.getStairNoLen();
        //设备类型
        mDeviceType = fullDeviceNo.getDeviceType();

        if (CommTypeDef.DeviceType.DEVICE_TYPE_AREA == mDeviceType) {
            //如果是区口，编号长度=房号长度+梯口号长度
            mTvLeftRoom.setText(getString(R.string.setting_number));
            roomNoLen = roomNoLen + stairNoLen;
        } else {
            mTvLeftRoom.setText(getString(R.string.setting_room_no));
        }

        //获取住户列表
        if (roomNoLen == defaaultRoomNoLen) {
            mRoomNoHelper = new RoomNoHelper();
            mRoomNoHelper.reset();
            currentRoomNo = mRoomNoHelper.getCurrentRoomNo();
        }


        editInputView();
    }

    private void editInputView() {
        setBackVisibility(View.VISIBLE);
        mRoomNo.setMaxLength(roomNoLen);
        mPwd.setMaxLength(pwdLen);

        if (mFuncCode.equals(SettingFunc.PASSWORD_ADD)) {
            if ((mDeviceType == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) && roomNoLen == defaaultRoomNoLen) {
                currentRoomNo = mRoomNoHelper.getCurrentRoomNo();
            } else {
                currentRoomNo = "";
                for (int i = 0; i < roomNoLen; i++) {
                    currentRoomNo = String.format("%s0", currentRoomNo);
                }
            }
        } else {
            currentRoomNo = "";
        }

        mRoomNo.setText(currentRoomNo);
        if (mFuncCode.equals(SettingFunc.PASSWORD_DEL)) {
            mRoomNo.requestFocus();
        } else {
            mPwd.requestFocus();
        }
    }


    @Override
    public void onKeyBoardListener(View view, int potion, KeyBoardBean keyBoardBean) {
        switch (keyBoardBean.getkId()) {
            case Const.KeyBoardId.KEY_CANCEL:
                if (mRoomNo.getCursorIndex() == 0 && !mPwd.isFocused()) {
                    requestBack();
                } else {
                    backspace();
                }
                break;
            case Const.KeyBoardId.KEY_CONFIRM:

                currentRoomNo = mRoomNo.getText().toString();
                currentPwdNo = mPwd.getText().toString();

                if (SettingFunc.PASSWORD_ADD.equals(mFuncCode)) {
                    //添加密码操作
                    addPwd();
                } else {
                    //删除密码操作
                    deletePwd();
                }
                break;
            default:
                if (mRoomNo.getCursorIndex() == 0 && mRoomNo.getText().toString().equals("")) {
                    mRoomNo.requestFocus();
                }
                String id = keyBoardBean.getkId();
                inputNum(Integer.valueOf(id));
                break;
        }
    }

    private void deletePwd() {
        if (currentRoomNo.length() < roomNoLen) {
            return;
        }

        UserInfoDao userInfoDao = new UserInfoDao();
        userInfoDao.deleteOpenPwd(currentRoomNo);

        setCount();
        currentPwdNo = "";
        mOperateView.operateBackState(getString(R.string.set_success));

        editInputView();
        setBackVisibility(View.GONE);
    }


    private void addPwd() {
        if (currentRoomNo.length() < roomNoLen || currentPwdNo.length() < 6) {
            return;
        }
        //保存
        UserInfoDao userInfoDao = new UserInfoDao();
        userInfoDao.addOpenPwd(currentRoomNo, currentPwdNo);

        setCount();
        mOperateView.operateBackState(getString(R.string.set_success));

        editInputView();
        setBackVisibility(View.GONE);
    }

    private void setCount() {
        if (mRoomNoHelper != null) {
            if (mDeviceType == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR && roomNoLen == defaaultRoomNoLen) {
                //设置列表计数
                if (!currentRoomNo.equals(mRoomNoHelper.getCurrentRoomNo())) {
                    if (!currentRoomNo.equals(mRoomNoHelper.getPreviousRoomNo())) {
                        mRoomNoHelper.reset();
                    }
                }
                currentRoomNo = mRoomNoHelper.getNextRoomNo();
            }
        }
    }


    @Override
    public void success() {
        mPwd.clearText();
        mPwd.requestFocus();
        editInputView();
    }

    @Override
    public void fail() {

    }
}
