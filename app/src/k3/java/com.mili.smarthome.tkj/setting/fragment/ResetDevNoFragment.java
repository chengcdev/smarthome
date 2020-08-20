package com.mili.smarthome.tkj.setting.fragment;

import android.view.View;
import android.widget.TextView;

import com.android.CommSysDef;
import com.android.CommTypeDef;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.widget.InputView;
import com.mili.smarthome.tkj.widget.NumInputView;

public class ResetDevNoFragment extends ResetBaseFragment implements View.OnClickListener {

    private TextView tvTitle;
    private TextView tvDevType;
    private View flDevNo;
    private NumInputView tvTikou;
    private NumInputView tvDev;
    private InputView tvEnabled;

    private int mDevType;
    private FullDeviceNo mFullDeviceNo;
    private boolean enableCellNo;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_reset_devno;
    }

    @Override
    protected void bindView() {
        tvTitle = findView(R.id.tv_title);
        tvDevType = findView(R.id.tv_devtype);
        flDevNo = findView(R.id.fl_devno);
        tvTikou = findView(R.id.tv_tk_no);
        tvDev = findView(R.id.tv_dev_no);
        tvEnabled = findView(R.id.tv_enabled);
        findView(R.id.btn_cancel).setOnClickListener(this);
        findView(R.id.btn_confirm).setOnClickListener(this);
    }

    @Override
    protected void bindData() {
        mFullDeviceNo = new FullDeviceNo(mContext);
        mDevType = mFullDeviceNo.getDeviceType();
        if (mDevType == CommTypeDef.DeviceType.DEVICE_TYPE_AREA) {
            tvTitle.setText(R.string.reset_areano);
            tvDevType.setText(R.string.setting_area_no);
            flDevNo.setVisibility(View.GONE);

            tvTikou.setMaxLength(2);
            tvTikou.setText(mFullDeviceNo.getStairNo());
            tvDev.setMaxLength(1);
            tvDev.setText("0");
        } else {
            tvTitle.setText(R.string.reset_stairno);
            tvDevType.setText(R.string.setting_stair_no);
            flDevNo.setVisibility(View.VISIBLE);

            tvTikou.setMaxLength(2);
            tvTikou.setText(mFullDeviceNo.getStairNo());
            tvDev.setMaxLength(mFullDeviceNo.getStairNoLen());
            tvDev.setText(mFullDeviceNo.getCurrentDeviceNo());
        }
        enableCellNo = mFullDeviceNo.getUseCellNo() == 1;
        tvEnabled.setText(enableCellNo ? R.string.pub_yes : R.string.pub_no);
        tvTikou.requestFocus();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                requestBack();
                break;
            case R.id.btn_confirm:
                onConfirm();
                break;
        }
    }

    @Override
    public boolean onKeyEvent(int keyCode, int keyState) {
        if (keyState == KEYSTATE_DOWN)
            return false;
        switch (keyCode) {
            case KEYCODE_0:
                if (tvTikou.isFocused()
                        && tvTikou.getCursorIndex() == 1
                        && tvTikou.getText().charAt(0) == '0') {
                    return true;
                }
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
                if (tvTikou.isFocused()
                        && tvTikou.getText().charAt(0) == '0'
                        && tvTikou.getText().charAt(1) == '0') {
                    tvTikou.setText("01");
                }
                break;
            case KEYCODE_UP:
            case KEYCODE_DOWN:
                toggle();
                break;
            case KEYCODE_CALL:
                onConfirm();
                break;
        }
        return true;
    }

    private void toggle() {
        if (tvEnabled.isFocused()) {
            enableCellNo = !enableCellNo;
            tvEnabled.setText(enableCellNo ? R.string.pub_yes : R.string.pub_no);
        }
    }

    private void onConfirm() {
        String stairNo = tvTikou.getText().toString();
        String currentDeviceNo = tvDev.getText().toString();
        byte useCellNo = enableCellNo ? (byte) 1 : (byte) 0;
        String deviceNo = mFullDeviceNo.getDeviceNo(stairNo, currentDeviceNo);

        //保存数据
        if (mDevType == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            mFullDeviceNo.setDeviceNo(deviceNo);
            mFullDeviceNo.setCurrentDeviceNo(currentDeviceNo);
            mFullDeviceNo.setStairNo(stairNo);
            mFullDeviceNo.setUseCellNo(useCellNo);
        } else {
            mFullDeviceNo.setDeviceNo(stairNo);
            mFullDeviceNo.setStairNo(stairNo);
            mFullDeviceNo.setUseCellNo(useCellNo);
        }

        //发送广播
        ContextProxy.sendBroadcast(CommSysDef.BROADCAST_DEVICENUMBER);

        gotoNextFragment();
    }
}
