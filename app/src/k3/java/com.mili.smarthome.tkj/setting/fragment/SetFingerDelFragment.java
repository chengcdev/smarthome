package com.mili.smarthome.tkj.setting.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.android.CommTypeDef;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.dao.FingerDao;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.utils.RoomNoHelper;
import com.mili.smarthome.tkj.utils.StringUtils;
import com.mili.smarthome.tkj.widget.NumInputView;

public class SetFingerDelFragment extends BaseSetFragment {

    private TextView tvRoomNoLabel;
    private NumInputView tvRoomNo;

    private int mRoomNoLen;
    private RoomNoHelper mRoomNoHelper;
    private String mDefaultRoomNo;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_finger_del;
    }

    @Override
    protected void bindView() {
        tvRoomNoLabel = findView(R.id.tv_room_no_label);
        tvRoomNo = findView(R.id.tv_room_no);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FullDeviceNo fullDeviceNo = new FullDeviceNo(mContext);
        if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            mRoomNoLen = fullDeviceNo.getRoomNoLen();
            if (mRoomNoLen == 4) {
                mRoomNoHelper = new RoomNoHelper();
            }
            tvRoomNoLabel.setText(R.string.setting_room_no);
        } else {
            mRoomNoLen = fullDeviceNo.getStairNoLen() + fullDeviceNo.getRoomNoLen();
            tvRoomNoLabel.setText(R.string.setting_number);
        }
        mDefaultRoomNo = StringUtils.padLeft("0", mRoomNoLen, '0');
        tvRoomNo.setMaxLength(mRoomNoLen);
        tvRoomNo.setText(mDefaultRoomNo);
        tvRoomNo.requestFocus();
    }

    @Override
    public boolean onKeyEvent(int keyCode, int keyState) {
        if (keyState == KEYSTATE_DOWN)
            return false;
        switch (keyCode) {
            case KEYCODE_UP:
                if (mRoomNoHelper != null) {
                    tvRoomNo.setText(mRoomNoHelper.getPreviousRoomNo());
                }
                break;
            case KEYCODE_DOWN:
                if (mRoomNoHelper != null) {
                    tvRoomNo.setText(mRoomNoHelper.getNextRoomNo());
                }
                break;
            case KEYCODE_0:
                inputNum(0);
                break;
            case KEYCODE_1:
            case KEYCODE_2:
            case KEYCODE_3:
            case KEYCODE_4:
            case KEYCODE_5:
            case KEYCODE_6:
            case KEYCODE_7:
            case KEYCODE_8:
            case KEYCODE_9:
                inputNum(keyCode);
                break;
            case KEYCODE_BACK:
                backspace();
                break;
            case KEYCODE_CALL:
                if (tvRoomNo.getText().length() == mRoomNoLen) {
                    fingerDel(tvRoomNo.getText().toString());
                }
                break;
        }
        return true;
    }

    private void fingerDel(String roomNo) {
        SinglechipClientProxy.getInstance().delFinger(roomNo);
        FingerDao fingerDao = new FingerDao();
        fingerDao.deleteByRoomNo(roomNo);
        showResult(R.string.setting_suc, new Runnable() {
            @Override
            public void run() {
                tvRoomNo.clearText();
                tvRoomNo.requestFocus();
            }
        });
    }
}
