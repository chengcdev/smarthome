package com.mili.smarthome.tkj.setting.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.CommSysDef;
import com.android.CommTypeDef;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.widget.NumInputView;

public class ResetDevNoFragment extends ResetBaseFragment implements View.OnClickListener {

    private LinearLayout mLlStair, mLlArea;
    private TextView tvTitle;
    private NumInputView mNvStairno, mNvDevno;
    private NumInputView mNvAreaNo;
    private TextView mTvEnable, mTvAreaEnable;

    private FullDeviceNo mFullDeviceNo;
    private int mDevType;
    private boolean mEnableCellNo;

    @Override
    public boolean onKeyCancel() {
        backspacePrevious();
        return true;
    }

    @Override
    public boolean onKeyConfirm() {
        onConfirm();
        return true;
    }

    @Override
    public boolean onKey(int code) {
        inputNum(code);
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_reset_devno;
    }

    @Override
    protected void bindView() {
        super.bindView();
        tvTitle = findView(R.id.tv_title);
        mLlStair = findView(R.id.ll_stair);
        mLlArea = findView(R.id.ll_area);
        mNvStairno = findView(R.id.nv_tk_no);
        mNvAreaNo = findView(R.id.nv_area_no);
        mNvDevno = findView(R.id.nv_dev_no);
        mTvEnable = findView(R.id.tv_enabled);
        mTvAreaEnable = findView(R.id.tv_area_enabled);

        ImageButton ibDown = findView(R.id.ib_down);
        ImageButton ibUp = findView(R.id.ib_up);
        assert ibDown != null;
        ibDown.setOnClickListener(this);
        assert ibUp != null;
        ibUp.setOnClickListener(this);
    }

    @Override
    protected void bindData() {
        super.bindData();
        if (mFullDeviceNo == null) {
            mFullDeviceNo = new FullDeviceNo(mContext);
        }
        mDevType = mFullDeviceNo.getDeviceType();
        mEnableCellNo = mFullDeviceNo.getUseCellNo() == 1;

        if (mDevType == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            tvTitle.setText(R.string.reset_stairno);
            mLlStair.setVisibility(View.VISIBLE);
            mLlArea.setVisibility(View.GONE);

            mNvStairno.setText(mFullDeviceNo.getStairNo());
            mNvDevno.setText(mFullDeviceNo.getCurrentDeviceNo());
            mTvEnable.setText(mEnableCellNo ? R.string.pub_yes : R.string.pub_no);
            mNvStairno.requestFocus();
        } else {
            tvTitle.setText(R.string.reset_areano);
            mLlStair.setVisibility(View.GONE);
            mLlArea.setVisibility(View.VISIBLE);
            mNvAreaNo.setText(mFullDeviceNo.getStairNo());
            mTvAreaEnable.setText(mEnableCellNo ? R.string.pub_yes : R.string.pub_no);
            mNvAreaNo.requestFocus();
        }
    }

    private void onConfirm() {
        //保存数据
        if (mDevType == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            String stairNo = mNvStairno.getText().toString();
            String currentDeviceNo = mNvDevno.getText().toString();
            byte useCellNo = mEnableCellNo ? (byte) 1 : (byte) 0;
            String deviceNo = mFullDeviceNo.getDeviceNo(stairNo, currentDeviceNo);
            //分机号不能设置为0，否则会报错
            if (stairNo.equals("00")) {
                return;
            }
            mFullDeviceNo.setDeviceNo(deviceNo);
            mFullDeviceNo.setCurrentDeviceNo(currentDeviceNo);
            mFullDeviceNo.setStairNo(stairNo);
            mFullDeviceNo.setUseCellNo(useCellNo);
        } else {
            String areaNo = mNvAreaNo.getText().toString();
            byte useCellNo = mEnableCellNo ? (byte) 1 : (byte) 0;
            //区口号不能设置为0
            if (areaNo.equals("00")) {
                return;
            }
            mFullDeviceNo.setDeviceNo(areaNo);
            mFullDeviceNo.setStairNo(areaNo);
            mFullDeviceNo.setUseCellNo(useCellNo);
        }

        //发送广播
        Intent intent = new Intent(CommSysDef.BROADCAST_DEVICENUMBER);
        App.getInstance().sendBroadcast(intent);

        gotoNextFragment();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_down:
            case R.id.ib_up:
                mEnableCellNo = !mEnableCellNo;
                if (mDevType == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
                    mTvEnable.setText(mEnableCellNo ? R.string.pub_yes : R.string.pub_no);
                } else {
                    mTvAreaEnable.setText(mEnableCellNo ? R.string.pub_yes : R.string.pub_no);
                }
                break;
        }
    }
}
