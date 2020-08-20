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
import com.mili.smarthome.tkj.base.FragmentProxy;
import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.dao.FingerDao;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.utils.ResUtils;
import com.mili.smarthome.tkj.utils.RoomNoHelper;
import com.mili.smarthome.tkj.utils.StringUtils;
import com.mili.smarthome.tkj.widget.NumInputView;

public class SetFingerAddFragment extends K4BaseFragment implements View.OnClickListener {

    private View llContent, mFooter;
    private NumInputView tvRoomNo;
    private TextView tvHead, tvHint, tvHint1, tvHint2;

    private static final int DEFAULT_ROOMLEN = 4;
    private RoomNoHelper mRoomNoHelper;
    private int mRoomNoLen = DEFAULT_ROOMLEN;

    private IFingerEventListener mFingerEventListener = new FingerEventListener();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_finger_add;
    }

    @Override
    protected void bindView() {
        super.bindView();
        tvHead = findView(R.id.tv_head);
        tvHint = findView(R.id.tv_hint);
        llContent = findView(R.id.ll_content);
        tvRoomNo = findView(R.id.tv_roomno);
        tvHint1 = findView(R.id.tv_hint1);
        tvHint2 = findView(R.id.tv_hint2);
        mFooter = findView(R.id.listview_footer);
        findView(R.id.ib_up).setOnClickListener(this);
        findView(R.id.ib_down).setOnClickListener(this);

        mRoomNoHelper = new RoomNoHelper();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvHead.setText(R.string.setting_030501);
        showSetHint(R.string.finger_set_hint, null);

        FullDeviceNo fullDeviceNo = new FullDeviceNo(mContext);
        if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            mRoomNoLen = fullDeviceNo.getRoomNoLen();
        } else {
            mRoomNoLen = fullDeviceNo.getStairNoLen() + fullDeviceNo.getRoomNoLen();
        }
        tvRoomNo.setMaxLength(mRoomNoLen);

        SinglechipClientProxy.getInstance().addFingerEventListener(mFingerEventListener);
        SinglechipClientProxy.getInstance().setCardState(3);

        if (mRoomNoLen == DEFAULT_ROOMLEN) {
            beginAddFinger(mRoomNoHelper.getCurrentRoomNo());
        } else {
            beginAddFinger(StringUtils.padLeft("0", mRoomNoLen, '0'));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SinglechipClientProxy.getInstance().stopAddFinger();
        SinglechipClientProxy.getInstance().removeFingerEventListener(mFingerEventListener);
        SinglechipClientProxy.getInstance().setCardState(0);
        super.onDestroyView();
    }

    private void beginAddFinger(String roomNo) {
        int state = SinglechipClientProxy.getInstance().addFinger(roomNo);
        if (state == 0) {
            tvHint1.setText(R.string.set_input_room_no_error);
            tvHint2.setText("");
            tvHint2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    beginAddFinger(StringUtils.padLeft("0", mRoomNoLen, '0'));
                }
            }, 3000);
        } else {
            tvRoomNo.setText(roomNo);
            tvRoomNo.requestFocus();
            tvRoomNo.setCursorIndex(roomNo.length());
            tvHint1.setText(getString(R.string.finger_collect_format, 1));
            tvHint2.setText(R.string.finger_press);
        }
    }

    @Override
    public void onClick(View view) {
        if (mRoomNoLen != DEFAULT_ROOMLEN) {
            return;
        }
        switch (view.getId()) {
            case R.id.ib_up:
                beginAddFinger(mRoomNoHelper.getPreviousRoomNo());
                break;
            case R.id.ib_down:
                beginAddFinger(mRoomNoHelper.getNextRoomNo());
                break;
        }
    }

    @Override
    public boolean onKey(int code) {
        super.onKey(code);
        tvRoomNo.input(code);
        if (tvRoomNo.getText().length() == mRoomNoLen) {
            beginAddFinger(tvRoomNo.getText().toString());
        }
        return true;
    }

    @Override
    public boolean onKeyCancel() {
        super.onKeyCancel();
        if (tvRoomNo.getText().length() == 0) {
            FragmentProxy.getInstance().exitFragment();
        } else {
            tvRoomNo.backspace();
        }
        return true;
    }

    class FingerEventListener extends FingerEventListenerAdapter {

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
                    int resid = ResUtils.getStringId(mContext, "finger_collect_" + code);
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
            if (code == 1) {
                FingerDao fingerDao = new FingerDao();
                fingerDao.insert(fingerId, valid, fingerData, tvRoomNo.getText().toString());
                showSetHint(R.string.set_ok, new Runnable() {
                    @Override
                    public void run() {
                        if (mRoomNoLen == DEFAULT_ROOMLEN) {
                            beginAddFinger(mRoomNoHelper.getNextRoomNo());
                        } else {
                            beginAddFinger(tvRoomNo.getText().toString());
                        }
                    }
                });
            } else if (code == 3) {
                showSetHint(R.string.finger_full, new Runnable() {
                    @Override
                    public void run() {
                        exitFragment();
                    }
                });
            } else {
                showSetHint(R.string.set_error, new Runnable() {
                    @Override
                    public void run() {
                        if (mRoomNoLen == DEFAULT_ROOMLEN) {
                            beginAddFinger(mRoomNoHelper.getCurrentRoomNo());
                        } else {
                            beginAddFinger(tvRoomNo.getText().toString());
                        }
                    }
                });
            }
        }
    }

    private void showSetHint(int resid, final Runnable action) {
        tvHint.setVisibility(View.VISIBLE);
        llContent.setVisibility(View.GONE);
        mFooter.setVisibility(View.GONE);
        tvHint.setText(resid);
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvHint.setVisibility(View.GONE);
                llContent.setVisibility(View.VISIBLE);
                mFooter.setVisibility(View.VISIBLE);
                if (action != null)
                    action.run();
            }
        }, 1000);
    }

}
