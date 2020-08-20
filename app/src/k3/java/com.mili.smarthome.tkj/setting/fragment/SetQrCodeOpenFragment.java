package com.mili.smarthome.tkj.setting.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.setting.adapter.ItemSelectorAdapter;
import com.mili.smarthome.tkj.setting.entities.SettingFuncManager;
import com.mili.smarthome.tkj.widget.NumInputView;

public class SetQrCodeOpenFragment extends BaseSetFragment implements ItemSelectorAdapter.OnItemClickListener {

    private static final int SET_QRCODE_OPEN_TYPE = 0x0304;
    private static final int SET_OPEN_BY_SCAN = 0x030401;
    private static final int SET_OPEN_BY_BLUETOOTH = 0x030402;

    private String[] mQrOpenTypeList;
    private String[] mQrOpenEnabledList;

    private View llTitle;
    private TextView tvTitle;
    private RecyclerView rvSelector;
    private View llBluetooth;
    private NumInputView tvDevid;

    private int mSetFunc = SET_QRCODE_OPEN_TYPE;
    private int mQrOpenType;
    private int mQrOpenTypeTemp;
    private int mQrOpenEnabled;
    private String mRegisterId;

    private ItemSelectorAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_qrcode_open;
    }

    @Override
    protected void bindView() {
        llTitle = findView(R.id.ll_title);
        tvTitle = findView(R.id.tv_title);
        rvSelector = findView(R.id.recyclerview);
        llBluetooth = findView(R.id.ll_bluetooth);
        tvDevid = findView(R.id.tv_devid);

        mQrOpenTypeList = new String[] {
                getString(R.string.setting_030401),
                getString(R.string.setting_030402)
        };
        mQrOpenEnabledList = new String[] {
                getString(R.string.pub_disable),
                getString(R.string.pub_enable)
        };

        mAdapter = new ItemSelectorAdapter(mContext);
        mAdapter.setOnItemClickListener(this);
        rvSelector.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rvSelector.setAdapter(mAdapter);
    }

    @Override
    protected void bindData() {
        mQrOpenType = AppConfig.getInstance().getQrOpenType();
        mQrOpenEnabled = AppConfig.getInstance().getQrScanEnabled();

        setQrOpenType();
    }

    private void setQrOpenType() {
        mSetFunc = SET_QRCODE_OPEN_TYPE;
        llTitle.setVisibility(View.GONE);
        rvSelector.setVisibility(View.VISIBLE);
        llBluetooth.setVisibility(View.GONE);
        tvDevid.clearFocus();

        mAdapter.setOptions(mQrOpenTypeList);
        mAdapter.setSelection(mQrOpenType);
    }

    private void setOpenByScan() {
        mSetFunc = SET_OPEN_BY_SCAN;
        llTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(R.string.setting_030401);
        rvSelector.setVisibility(View.VISIBLE);
        llBluetooth.setVisibility(View.GONE);

        mAdapter.setOptions(mQrOpenEnabledList);
        mAdapter.setSelection(mQrOpenEnabled);
    }

    private void setOpenByBluetooth() {
        mSetFunc = SET_OPEN_BY_BLUETOOTH;
        llTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(R.string.setting_030402);
        rvSelector.setVisibility(View.GONE);
        llBluetooth.setVisibility(View.VISIBLE);
        mRegisterId = AppConfig.getInstance().getBluetoothDevId();
        tvDevid.setText(mRegisterId);
        tvDevid.requestFocus();
    }

    @Override
    public void onBackPressed() {
        if (mSetFunc == SET_QRCODE_OPEN_TYPE) {
            requestBack();
        } else {
            setQrOpenType();
        }
    }

    @Override
    public void onItemClick(int position) {
        switch (mSetFunc) {
            case SET_QRCODE_OPEN_TYPE:
                mQrOpenTypeTemp = position;
                if (position == 0) {
                    setOpenByScan();
                } else {
                    setOpenByBluetooth();
                }
                break;
            case SET_OPEN_BY_SCAN:
                mQrOpenType = mQrOpenTypeTemp;
                mQrOpenEnabled = position;
                AppConfig.getInstance().setQrOpenType(mQrOpenType);
                AppConfig.getInstance().setQrScanEnabled(mQrOpenEnabled);
                SettingFuncManager.notifyQrCodeChanged();
                showResult(R.string.setting_suc, new Runnable() {
                    @Override
                    public void run() {
                        setQrOpenType();
                    }
                });
                break;
        }
    }

    @Override
    public boolean onKeyEvent(int keyCode, int keyState) {
        if (keyState == KEYSTATE_DOWN)
            return false;
        switch (keyCode) {
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
                if (mSetFunc == SET_OPEN_BY_BLUETOOTH) {
                    backspace();
                } else {
                    requestBack();
                }
                break;
            case KEYCODE_CALL:
                if (mSetFunc == SET_OPEN_BY_BLUETOOTH) {
                    mQrOpenType = mQrOpenTypeTemp;
                    mRegisterId = tvDevid.getText().toString();
                    AppConfig.getInstance().setQrOpenType(mQrOpenType);
                    AppConfig.getInstance().setBluetoothDevId(mRegisterId);
                    SettingFuncManager.notifyQrCodeChanged();
                    showResult(R.string.setting_suc, new Runnable() {
                        @Override
                        public void run() {
                            setQrOpenType();
                        }
                    });
                }
                break;
        }
        return true;
    }
}
