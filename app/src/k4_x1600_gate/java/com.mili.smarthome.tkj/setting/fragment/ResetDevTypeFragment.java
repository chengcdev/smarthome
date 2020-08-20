package com.mili.smarthome.tkj.setting.fragment;

import com.android.CommTypeDef;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;

public class ResetDevTypeFragment extends ResetSelectorFragment {

    private FullDeviceNo mFullDeviceNo;

    @Override
    public boolean onKeyCancel() {
        gotoPreviousFragment();
        return true;
    }

    @Override
    public boolean onKeyConfirm() {
        onItemClick(getSelection());
        return true;
    }

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
        if (mFullDeviceNo == null) {
            mFullDeviceNo = new FullDeviceNo(mContext);
        }
        if (mFullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_AREA) {
            setSelection(1);
        } else {
            setSelection(0);
        }
    }

    @Override
    protected void onItemClick(int position) {
        int devType;
        switch (position) {
            case 0:
                devType = CommTypeDef.DeviceType.DEVICE_TYPE_STAIR;
                break;
            case 1:
                devType = CommTypeDef.DeviceType.DEVICE_TYPE_AREA;
                break;
            default:
                return;
        }
        mFullDeviceNo.setDeviceType((byte) devType);
        gotoNextFragment();
    }
}
