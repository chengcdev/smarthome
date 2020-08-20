package com.mili.smarthome.tkj.setting.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.CommSysDef;
import com.android.CommTypeDef;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;
import com.mili.smarthome.tkj.widget.InputView;
import com.mili.smarthome.tkj.widget.NumInputView;

/**
 * 梯口号设置
 */
public class SetDevNoFragment extends BaseSetFragment {

    private TextView tvFunc;
    private TextView tvDevType;
    private View llDevNo;
    private NumInputView tvTikou;
    private NumInputView tvDev;
    private InputView tvEnabled;

    private int mDevType;
    private FullDeviceNo mFullDeviceNo;
    private boolean enableCellNo;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_devno;
    }

    @Override
    protected void bindView() {
        tvFunc = findView(R.id.tv_func);
        tvDevType = findView(R.id.tv_devtype);
        llDevNo = findView(R.id.ll_devno);
        tvTikou = findView(R.id.tv_tk_no);
        tvDev = findView(R.id.tv_dev_no);
        tvEnabled = findView(R.id.tv_enabled);
    }

    @Override
    protected void bindData() {
        Bundle args = getArguments();
        if (args != null) {
            mFullDeviceNo = new FullDeviceNo(mContext);
            String funcCode = args.getString(FragmentFactory.ARGS_FUNCCODE);
            if (SettingFunc.SET_AREA_NO.equals(funcCode)) {
                mDevType = CommTypeDef.DeviceType.DEVICE_TYPE_AREA;
                tvFunc.setText(R.string.setting_0411);
                tvDevType.setText(R.string.setting_area_no);
                llDevNo.setVisibility(View.GONE);

                tvTikou.setMaxLength(2);
                tvTikou.setText(mFullDeviceNo.getStairNo());
                tvDev.setMaxLength(1);
                tvDev.setText("0");
            } else {
                mDevType = CommTypeDef.DeviceType.DEVICE_TYPE_STAIR;
                tvFunc.setText(R.string.setting_0401);
                tvDevType.setText(R.string.setting_stair_no);
                llDevNo.setVisibility(View.VISIBLE);

                tvTikou.setMaxLength(2);
                tvTikou.setText(mFullDeviceNo.getStairNo());
                tvDev.setMaxLength(mFullDeviceNo.getStairNoLen());
                tvDev.setText(mFullDeviceNo.getCurrentDeviceNo());
            }
            enableCellNo = mFullDeviceNo.getUseCellNo() == 1;
            tvEnabled.setText(enableCellNo ? R.string.pub_yes : R.string.pub_no);
            tvTikou.requestFocus();
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
            case KEYCODE_UP:
            case KEYCODE_DOWN:
                toggle();
                break;
            case KEYCODE_CALL:
                save();
                break;
            case KEYCODE_BACK:
                backspace();
                if (tvTikou.isFocused()
                        && tvTikou.getText().charAt(0) == '0'
                        && tvTikou.getText().charAt(1) == '0') {
                    tvTikou.setText("01");
                }
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

    private void save() {
        String stairNo = tvTikou.getText().toString();
        String currentDeviceNo = tvDev.getText().toString();
        byte useCellNo = enableCellNo ? (byte) 1 : (byte) 0;
        String deviceNo = mFullDeviceNo.getDeviceNo(stairNo, currentDeviceNo);

        //保存数据
        if (mDevType == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            if (mFullDeviceNo.DeviceNoisright(deviceNo) == 0) {
                mFullDeviceNo.setDeviceNo(deviceNo);
                mFullDeviceNo.setCurrentDeviceNo(currentDeviceNo);
                mFullDeviceNo.setStairNo(stairNo);
                mFullDeviceNo.setUseCellNo(useCellNo);
                //发送广播
                ContextProxy.sendBroadcast(CommSysDef.BROADCAST_DEVICENUMBER);
                showResultAndBack(R.string.setting_suc);
            }else {
                showResultAndBack(R.string.setting_fail);
            }
        } else {
            if (!stairNo.equals("00")){
                mFullDeviceNo.setDeviceNo(stairNo);
                mFullDeviceNo.setStairNo(stairNo);
                mFullDeviceNo.setUseCellNo(useCellNo);
                //发送广播
                ContextProxy.sendBroadcast(CommSysDef.BROADCAST_DEVICENUMBER);
                showResultAndBack(R.string.setting_suc);
            }else {
                showResultAndBack(R.string.setting_fail);
            }
        }
    }
}
