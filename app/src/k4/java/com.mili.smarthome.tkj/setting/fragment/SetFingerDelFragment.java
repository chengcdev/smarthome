package com.mili.smarthome.tkj.setting.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.android.CommTypeDef;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.base.FragmentProxy;
import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.dao.FingerDao;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.utils.RoomNoHelper;
import com.mili.smarthome.tkj.widget.NumInputView;

public class SetFingerDelFragment extends K4BaseFragment implements View.OnClickListener {

    private View llContent, mFooter;
    private NumInputView tvRoomNo;
    private TextView tvHead, tvHint;

    private RoomNoHelper mRoomNoHelper;
    private int mRoomNoLen = 4;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_finger_del;
    }

    @Override
    protected void bindView() {
        super.bindView();
        tvHead = findView(R.id.tv_head);
        tvHint = findView(R.id.tv_hint);
        llContent = findView(R.id.ll_content);
        tvRoomNo = findView(R.id.tv_roomno);
        mFooter = findView(R.id.listview_footer);
        findView(R.id.ib_up).setOnClickListener(this);
        findView(R.id.ib_down).setOnClickListener(this);

        mRoomNoHelper = new RoomNoHelper();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvHead.setText(R.string.setting_030502);

        FullDeviceNo fullDeviceNo = new FullDeviceNo(mContext);
        if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            mRoomNoLen = fullDeviceNo.getRoomNoLen();
        } else {
            mRoomNoLen = fullDeviceNo.getStairNoLen() + fullDeviceNo.getRoomNoLen();
        }
        tvRoomNo.setMaxLength(mRoomNoLen);
        tvRoomNo.requestFocus();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_up:
                tvRoomNo.setText(mRoomNoHelper.getPreviousRoomNo());
                break;
            case R.id.ib_down:
                tvRoomNo.setText(mRoomNoHelper.getNextRoomNo());
                break;
        }
    }

    @Override
    public boolean onKey(int code) {
        super.onKey(code);
        inputNum(code);
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

    @Override
    public boolean onKeyConfirm() {
        super.onKeyConfirm();
        if (tvRoomNo.getText().length() == mRoomNoLen) {
            String roomNo = tvRoomNo.getText().toString();
            SinglechipClientProxy.getInstance().delFinger(roomNo);
            FingerDao fingerDao = new FingerDao();
            fingerDao.deleteByRoomNo(roomNo);
            showSetHint();
        }
        return true;
    }

    private void showSetHint() {
        tvHint.setVisibility(View.VISIBLE);
        llContent.setVisibility(View.GONE);
        mFooter.setVisibility(View.GONE);
        tvHint.setText(R.string.set_ok);
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvHint.setVisibility(View.GONE);
                llContent.setVisibility(View.VISIBLE);
                mFooter.setVisibility(View.VISIBLE);
                tvRoomNo.clearText();
                tvRoomNo.requestFocus();
            }
        }, 1000);
    }

}
