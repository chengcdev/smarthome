package com.mili.smarthome.tkj.setting.fragment;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.CommSysDef;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.widget.NumInputView;

import java.util.Objects;

/**
 * 梯口号设置
 */
public class SetDevNoFragment extends K4BaseFragment implements View.OnClickListener {

    private static final String Tag = "SetDevNoFragment";
    private LinearLayout mLlContent;
    private RelativeLayout mLlButton;
    private TextView mTvHint, mTvUnit;
    private NumInputView mIvStairno, mIvDevno;

    private boolean mUseCellNo = false;
    private FullDeviceNo mFullDeviceNo;

    @Override
    public boolean onKeyCancel() {
        super.onKeyCancel();
        backspaceExit();
        return true;
    }

    @Override
    public boolean onKeyConfirm() {
        super.onKeyConfirm();

        if (mFullDeviceNo != null) {
            String stairNo = mIvStairno.getText().toString();
            String deviceNo = mIvDevno.getText().toString();
            String fullDevno = mFullDeviceNo.getDeviceNo(stairNo, deviceNo);
            boolean ret = false;

            // 分机号不能设置为0
            if (stairNo.equals("00")) {
                showSetHint(false);
                return true;
            }

            if (mFullDeviceNo.DeviceNoisright(fullDevno) == 0) {
                ret = saveData(fullDevno, deviceNo, stairNo);
            }
            showSetHint(ret);
        }
        return true;
    }

    @Override
    public boolean onKey(int code) {
        super.onKey(code);
        inputNum(code);
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_devno;
    }

    @Override
    protected void bindView() {
        super.bindView();

        TextView head = findView(R.id.tv_head);
        if (head != null) {
            head.setText(R.string.setting_0401);
        }

        mLlButton = findView(R.id.ll_button);
        mLlContent = findView(R.id.ll_content);
        mTvHint = findView(R.id.tv_hint);
        mTvUnit = findView(R.id.tv_unit);

        mIvStairno = findView(R.id.iv_stairno);
        mIvDevno = findView(R.id.iv_devno);

        ImageButton ibUp = findView(R.id.ib_up);
        ImageButton ibDown = findView(R.id.ib_down);
        if (ibUp != null) {
            ibUp.setOnClickListener(this);
        }
        if (ibDown != null) {
            ibDown.setOnClickListener(this);
        }
    }

    @Override
    protected void bindData() {
        super.bindData();
        initData();
        mIvStairno.requestFocus();
    }

    @Override
    protected void unbindView() {
        super.unbindView();
        mMainHandler.removeCallbacksAndMessages(0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_up:
            case R.id.ib_down:
                if (mUseCellNo) {
                    mUseCellNo = false;
                    mTvUnit.setText(R.string.pub_no);
                } else {
                    mUseCellNo = true;
                    mTvUnit.setText(R.string.pub_yes);
                }
                break;
        }
    }

    private void initData() {
        mFullDeviceNo = new FullDeviceNo(getContext());
        String stairNo = mFullDeviceNo.getStairNo();
        String devno = mFullDeviceNo.getCurrentDeviceNo();
        int useCellNo = mFullDeviceNo.getUseCellNo();
        int roomNoLen = mFullDeviceNo.getStairNoLen();
        Log.d(Tag, "initData: extNo=" + stairNo + ", stairno=" + devno + ",roomlen=" + roomNoLen);

        mIvStairno.setMaxLength(2);
        mIvStairno.setText(stairNo);
        mIvDevno.setMaxLength(roomNoLen);
        mIvDevno.setText(devno);
        if (useCellNo == 1) {
            mUseCellNo = true;
            mTvUnit.setText(R.string.pub_yes);
        } else {
            mUseCellNo = false;
            mTvUnit.setText(R.string.pub_no);
        }
    }

    private boolean saveData(String fullDevno, String deviceNo, String stairNo) {
        if (mFullDeviceNo == null) {
            Log.d(Tag, "saveData: mFullDeviceNo is null.");
            return false;
        }

        //是否启用单元号
        byte useCellNo = 0;
        if (mUseCellNo) {
            useCellNo = 1;
        }

        //保存数据
        mFullDeviceNo.setDeviceNo(fullDevno);
        mFullDeviceNo.setCurrentDeviceNo(deviceNo);
        mFullDeviceNo.setStairNo(stairNo);
        mFullDeviceNo.setUseCellNo(useCellNo);

        //发送广播
        Intent intent = new Intent(CommSysDef.BROADCAST_DEVICENUMBER);
        Objects.requireNonNull(getActivity()).sendBroadcast(intent);
        return true;
    }

    private void showSetHint(boolean state) {
        mLlContent.setVisibility(View.INVISIBLE);
        mLlButton.setVisibility(View.INVISIBLE);
        mTvHint.setVisibility(View.VISIBLE);
        if (state) {
            mTvHint.setText(R.string.set_ok);
        } else {
            mTvHint.setText(R.string.set_error);
        }
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                exitFragment();
            }
        }, Constant.SET_HINT_TIMEOUT);
    }
}
