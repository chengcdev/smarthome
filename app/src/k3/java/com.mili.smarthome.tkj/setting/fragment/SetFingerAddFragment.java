package com.mili.smarthome.tkj.setting.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.android.CommTypeDef;
import com.android.interf.FingerEventListenerAdapter;
import com.android.interf.IFingerEventListener;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.dao.FingerDao;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.utils.RoomNoHelper;
import com.mili.smarthome.tkj.utils.StringUtils;
import com.mili.smarthome.tkj.widget.NumInputView;

public class SetFingerAddFragment extends BaseSetFragment {

    private TextView tvRoomNoLabel;
    private NumInputView tvRoomNo;
    private TextView tvHint1;
    private TextView tvHint2;

    private int mRoomNoLen;
    private RoomNoHelper mRoomNoHelper;
    private String mDefaultRoomNo;

    private IFingerEventListener mFingerEventListener = new FingerEventListener();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_finger_add;
    }

    @Override
    protected void bindView() {
        tvRoomNoLabel = findView(R.id.tv_room_no_label);
        tvRoomNo = findView(R.id.tv_room_no);
        tvHint1 = findView(R.id.tv_hint1);
        tvHint2 = findView(R.id.tv_hint2);
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
        tvRoomNo.requestFocus();

        SinglechipClientProxy.getInstance().addFingerEventListener(mFingerEventListener);
        SinglechipClientProxy.getInstance().setCardState(3);
        beginAddFinger(mDefaultRoomNo);
    }

    @Override
    public void onDestroyView() {
        SinglechipClientProxy.getInstance().stopAddFinger();
        SinglechipClientProxy.getInstance().setCardState(0);
        SinglechipClientProxy.getInstance().removeFingerEventListener(mFingerEventListener);
        super.onDestroyView();
    }

    @Override
    public boolean onKeyEvent(int keyCode, int keyState) {
        if (keyState == KEYSTATE_DOWN)
            return false;
        switch (keyCode) {
            case KEYCODE_UP:
                if (mRoomNoHelper != null) {
                    beginAddFinger(mRoomNoHelper.getPreviousRoomNo());
                }
                break;
            case KEYCODE_DOWN:
                if (mRoomNoHelper != null) {
                    beginAddFinger(mRoomNoHelper.getNextRoomNo());
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
        }
        return true;
    }

    @Override
    protected void inputNum(int num) {
        super.inputNum(num);
        if (tvRoomNo.getText().length() == mRoomNoLen) {
            beginAddFinger(tvRoomNo.getText().toString());
        }
    }

    @Override
    protected void backspace() {
        if (tvRoomNo.isFocused()) {
            SinglechipClientProxy.getInstance().stopAddFinger();
        }
        super.backspace();
    }

    private void beginAddFinger(String roomNo) {
        int state = SinglechipClientProxy.getInstance().addFinger(roomNo);
        if (state == 0) {
            tvHint1.setText(R.string.set_input_room_no_error);
            tvHint2.setText("");
            tvHint2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    beginAddFinger(mDefaultRoomNo);
                    tvRoomNo.requestFocus();
                }
            }, 3000);
        } else {
            tvRoomNo.setText(roomNo);
            tvHint1.setText(getString(R.string.finger_collect_format, 1));
            tvHint2.setText(R.string.finger_press);
        }
    }

    private class FingerEventListener extends FingerEventListenerAdapter {

        @Override
        public void onFingerCollect(int code, int press, int count) {
            if (press == 0) {
                if (count < 4) {
                    tvHint1.setText(getString(R.string.finger_collect_format, count));
                } else {
                    tvHint1.setText(R.string.finger_collect_begin);
                }
                tvHint2.setText(R.string.finger_press);
            } else {
                if (code == 0) {
                    tvHint1.setText(R.string.finger_collect_0);
                    tvHint2.setText(R.string.finger_raise);
                } else {
                    int resid = ContextProxy.getStringId("finger_collect_" + code);
                    if (resid != 0) {
                        tvHint2.setText(resid);
                    } else {
                        tvHint2.setText(R.string.finger_collect_255);
                    }
                }
            }
        }

        @Override
        public void onFingerAdd(int code, int fingerId, int valid, byte[] fingerData) {
            FingerDao fingerDao = new FingerDao();
            if (code == 1) {
                fingerDao.insert(fingerId, valid, fingerData, tvRoomNo.getText().toString());
                showResult(R.string.setting_suc, new Runnable() {
                    @Override
                    public void run() {
                        if (mRoomNoHelper != null) {
                            beginAddFinger(mRoomNoHelper.getNextRoomNo());
                        } else {
                            beginAddFinger(mDefaultRoomNo);
                        }
                        tvRoomNo.requestFocus();
                    }
                });
            } else if (code == 3) {
                showResultAndBack(R.string.finger_full);
            } else {
                showResultAndBack(R.string.setting_fail);
            }
        }
    }
}
