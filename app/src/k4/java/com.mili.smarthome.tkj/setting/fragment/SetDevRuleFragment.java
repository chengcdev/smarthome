package com.mili.smarthome.tkj.setting.fragment;

import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.CommSysDef;
import com.android.CommTypeDef;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.widget.NumInputView;

import java.util.Objects;

/**
 * 编号规则设置
 */
public class SetDevRuleFragment extends K4BaseFragment {

    private static final String Tag = "DevnoRuleFragment";
    private LinearLayout mLlContent;
    private RelativeLayout mLlButton;
    private NumInputView mIvStair, mIvRoom, mIvCell;
    private TextView mTvHint;

    private FullDeviceNo mFullDeviceNo;
    private int mStairLen, mRoomLen, mCellLen;

    @Override
    public boolean onKeyCancel() {
        super.onKeyCancel();
        backspaceExit();
        return true;
    }

    @Override
    public boolean onKeyConfirm() {
        super.onKeyConfirm();

        int stairlen = Integer.parseInt(mIvStair.getText().toString());
        int roomlen = Integer.parseInt(mIvRoom.getText().toString());
        int celllen = Integer.parseInt(mIvCell.getText().toString());
        Log.d(Tag, "stairlen=" + stairlen + ", roomlen=" + roomlen + ", celllen=" + celllen);

        saveRule(stairlen, roomlen, celllen);
        return true;
    }

    @Override
    public boolean onKey(int code) {
        super.onKey(code);
        inputNum(code);
        return true;
    }

    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case MSG_SET_OK:
                exitFragment();
                break;
            case MSG_SET_ERROR:
                showHintView(false);
                setViewData();
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_devrule;
    }

    @Override
    protected void bindView() {
        super.bindView();
        TextView head = findView(R.id.tv_head);
        if (head != null) {
            head.setText(R.string.setting_0403);
        }
        mLlContent = findView(R.id.ll_content);
        mLlButton = findView(R.id.ll_button);
        mIvStair = findView(R.id.iv_stairlen);
        mIvRoom = findView(R.id.iv_roomlen);
        mIvCell = findView(R.id.iv_unitlen);
        mTvHint = findView(R.id.tv_hint);
        if (mTvHint != null) {
            mTvHint.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mFullDeviceNo = new FullDeviceNo(getContext());
        mStairLen = mFullDeviceNo.getStairNoLen();
        mRoomLen = mFullDeviceNo.getRoomNoLen();
        mCellLen = mFullDeviceNo.getCellNoLen();

        setViewData();
        showHintView(false);
    }

    private void setViewData() {
        mIvStair.setText(String.valueOf(mStairLen));
        mIvRoom.setText(String.valueOf(mRoomLen));
        mIvCell.setText(String.valueOf(mCellLen));
        mIvStair.requestFocus();
        mIvStair.setCursorIndex(0);
    }

    private void saveRule(int stairlen, int roomlen, int celllen) {
        if (mFullDeviceNo != null) {
            int code = mFullDeviceNo.isCheckDeviceNo(stairlen, roomlen, celllen);
            if (code == 0) {
                int section = mFullDeviceNo.getCurrentSubsection(stairlen, roomlen, celllen);
                mFullDeviceNo.setStairNoLen((byte) stairlen);
                mFullDeviceNo.setRoomNoLen((byte)roomlen);
                mFullDeviceNo.setCellNoLen((byte)celllen);
                mFullDeviceNo.setSubsection(section);

                /* 梯口机根据设置的梯口号长度调整设备号 */
                if (mFullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
                    String deviceNo = mFullDeviceNo.getDeviceNo();
                    String str = deviceNo;
                    if (stairlen < deviceNo.length()) {
                        str = deviceNo.substring(0, stairlen);
                    }
                    mFullDeviceNo.setCurrentDeviceNo(str);
                    mFullDeviceNo.notifyDeviceNo();
                }

                //发送广播
                Intent intent = new Intent(CommSysDef.BROADCAST_DEVICENORULE);
                Objects.requireNonNull(getActivity()).sendBroadcast(intent);

                mTvHint.setText(R.string.set_ok);
                showHintView(true);
                mMainHandler.sendEmptyMessageDelayed(MSG_SET_OK, Constant.SET_HINT_TIMEOUT);
            } else {
                mTvHint.setText(R.string.set_error);
                showHintView(true);
                mMainHandler.sendEmptyMessageDelayed(MSG_SET_ERROR, Constant.SET_HINT_TIMEOUT);
            }

        }
    }

    private void showHintView(boolean show) {
        if (show) {
            mLlContent.setVisibility(View.INVISIBLE);
            mLlButton.setVisibility(View.INVISIBLE);
            mTvHint.setVisibility(View.VISIBLE);
        } else {
            mLlContent.setVisibility(View.VISIBLE);
            mLlButton.setVisibility(View.VISIBLE);
            mTvHint.setVisibility(View.INVISIBLE);
        }
    }
}
