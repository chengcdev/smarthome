package com.mili.smarthome.tkj.setting.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.CommTypeDef;
import com.android.interf.IKeyEventListener;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.dao.UserInfoDao;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;
import com.mili.smarthome.tkj.utils.RoomNoHelper;
import com.mili.smarthome.tkj.utils.StringUtils;
import com.mili.smarthome.tkj.widget.NumInputView;

/**
 * 密码管理（添加、删除）
 */
public class PwdManageFragment extends BaseSetFragment {

    private TextView tvTitle;
    private TextView tvRoomNoLabel;
    private NumInputView tvRoomNo;
    private NumInputView tvPwd;
    private String mFuncCode;

    private FullDeviceNo mFullDeviceNo;
    private int mRoomNoLen;
    private RoomNoHelper mRoomNoHelper;
    private String mDefaultRoomNo;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_pwd;
    }

    @Override
    protected void bindView() {
        tvTitle = findView(R.id.tv_title);
        tvRoomNoLabel = findView(R.id.tv_room_no_label);
        tvRoomNo = findView(R.id.tv_room_no);
        tvPwd = findView(R.id.tv_pwd);
    }

    @Override
    protected void bindData() {
        mFullDeviceNo = new FullDeviceNo(mContext);
        if (mFullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            mRoomNoLen = mFullDeviceNo.getRoomNoLen();
            if (mRoomNoLen == 4) {
                mRoomNoHelper = new RoomNoHelper();
            }
            tvRoomNoLabel.setText(R.string.setting_room_no);
        } else {
            mRoomNoLen = mFullDeviceNo.getStairNoLen() + mFullDeviceNo.getRoomNoLen();
            tvRoomNoLabel.setText(R.string.setting_number);
        }
        mDefaultRoomNo = StringUtils.padLeft("0", mRoomNoLen, '0');
        tvRoomNo.setMaxLength(mRoomNoLen);

        Bundle args = getArguments();
        if (args != null) {
            mFuncCode = args.getString(FragmentFactory.ARGS_FUNCCODE, SettingFunc.PASSWORD_ADD);
            switch (mFuncCode) {
                case SettingFunc.PASSWORD_ADD:
                    findView(R.id.ll_pwd).setVisibility(View.VISIBLE);
                    tvTitle.setText(R.string.setting_0201);
                    tvRoomNo.setText(mDefaultRoomNo);
                    tvPwd.requestFocus();
                    break;
                case SettingFunc.PASSWORD_DEL:
                    findView(R.id.ll_pwd).setVisibility(View.GONE);
                    tvTitle.setText(R.string.setting_0202);
                    tvRoomNo.clearText();
                    tvRoomNo.requestFocus();
                    break;
            }
        }
    }

    @Override
    public boolean onKeyEvent(int keyCode, int keyState) {
        if (keyState == KEYSTATE_DOWN)
            return false;
        switch (keyCode) {
            case IKeyEventListener.KEYCODE_UP:
                if (mRoomNoHelper != null) {
                    tvRoomNo.setText(mRoomNoHelper.getPreviousRoomNo());
                }
                break;
            case IKeyEventListener.KEYCODE_DOWN:
                if (mRoomNoHelper != null) {
                    tvRoomNo.setText(mRoomNoHelper.getNextRoomNo());
                }
                break;
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
        // 保存数据
        UserInfoDao userInfoDao = new UserInfoDao();
        String roomNo = tvRoomNo.getText().toString();
        if (roomNo.length() != mRoomNoLen) {
            return;
        }
        if (SettingFunc.PASSWORD_ADD.equals(mFuncCode)) {
            String pwd = tvPwd.getText().toString();
            if (pwd.length() != 6) {
                return;
            }
            userInfoDao.addOpenPwd(roomNo, pwd);
        } else {
            userInfoDao.deleteOpenPwd(roomNo);
        }
        // 设置成功提示
        showResult(R.string.setting_suc, new Runnable() {
            @Override
            public void run() {
                if (SettingFunc.PASSWORD_ADD.equals(mFuncCode)) {
                    if (mRoomNoHelper != null) {
                        tvRoomNo.setText(mRoomNoHelper.getNextRoomNo());
                    } else {
                        tvRoomNo.setText(mDefaultRoomNo);
                    }
                    tvPwd.clearText();
                    tvPwd.requestFocus();
                } else {
                    if (mRoomNoHelper != null) {
                        tvRoomNo.setText(mRoomNoHelper.getNextRoomNo());
                    } else {
                        tvRoomNo.clearText();
                    }
                    tvRoomNo.requestFocus();
                }
            }
        });
    }
}
