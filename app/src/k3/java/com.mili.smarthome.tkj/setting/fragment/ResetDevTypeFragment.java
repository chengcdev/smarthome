package com.mili.smarthome.tkj.setting.fragment;

import com.android.CommTypeDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;

public class ResetDevTypeFragment extends ResetSelectorFragment {

    private int mDevType;

    @Override
    protected int getTitleId() {
        return R.string.reset_devtype;
    }

    @Override
    protected int getStringArrayId() {
        return R.array.devtype_list;
    }

    @Override
    protected void bindData() {
        super.bindData();
        mDevType = AppConfig.getInstance().getDevType();
        if (mDevType == CommTypeDef.DeviceType.DEVICE_TYPE_AREA) {
            setSelection(1);
        } else {
            setSelection(0);
        }
    }

    @Override
    protected void onItemClick(int position) {
        setSelection(position);
    }

    @Override
    protected void onCancel() {
        requestBack();
    }

    @Override
    protected void onConfirm() {
        int position = getSelection();
        switch (position) {
            case 0:
                mDevType = CommTypeDef.DeviceType.DEVICE_TYPE_STAIR;
                break;
            case 1:
                mDevType = CommTypeDef.DeviceType.DEVICE_TYPE_AREA;
                break;
            default:
                return;
        }
        AppConfig.getInstance().setDevType((byte) mDevType);
        gotoNextFragment();
    }
}
