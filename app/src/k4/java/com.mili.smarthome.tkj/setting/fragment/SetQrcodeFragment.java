package com.mili.smarthome.tkj.setting.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.setting.adapter.ItemSelectorAdapter;
import com.mili.smarthome.tkj.widget.NumInputView;

public class SetQrcodeFragment extends K4BaseFragment implements ItemSelectorAdapter.OnItemClickListener {

    private static final int OPENDOOR_TYPE = 0x030401;
    private static final int OPENDOOR_QRCODE = 0x030402;
    private static final int OPENDOOR_BLUETOOTH = 0x030203;
    private static final int OPENDOOR_HINT = 0x030204;

    private RecyclerView mRecyclerView;
    private TextView mTvHead, mTvHint;
    private View mLine;
    private RelativeLayout mLlbutton;
    private LinearLayout mLlRegister;
    private NumInputView mIvRegisterID;

    private int mSetFunc = OPENDOOR_TYPE;
    private ItemSelectorAdapter mAdapter;

    private int mOpendoorType;
    private int mQrcodeState;
    private String mRegisterId;

    @Override
    public boolean onKeyCancel() {
        super.onKeyCancel();
        if (mSetFunc == OPENDOOR_QRCODE){
            setOpendoorType();
        } else if (mSetFunc == OPENDOOR_BLUETOOTH) {
            String text = mIvRegisterID.getText().toString();
            if (text.length() > 0) {
                mIvRegisterID.backspace();
            } else {
                setOpendoorType();
            }
        } else {
            exitFragment();
        }
        return true;
    }

    @Override
    public boolean onKeyConfirm() {
        super.onKeyConfirm();
        if (mSetFunc == OPENDOOR_TYPE){
            int position = mAdapter.getSelection();
            if (position == 0) {
                setQrcodeState();
            } else {
                setBluetooth();
            }
        } else {
            saveParam();
        }
        return true;
    }

    @Override
    public boolean onKey(int code) {
        super.onKey(code);
        mIvRegisterID.input(code);
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_qrcode;
    }

    @Override
    protected void bindView() {
        super.bindView();
        mTvHead = findView(R.id.tv_head);
        mTvHint = findView(R.id.tv_hint);
        mLine = findView(R.id.v_line);

        mRecyclerView = findView(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        mLlbutton = findView(R.id.ll_button);
        mLlRegister = findView(R.id.layout_register);
        mIvRegisterID = findView(R.id.iv_registerId);
    }

    @Override
    protected void bindData() {
        super.bindData();

        mOpendoorType = AppConfig.getInstance().getQrOpenType();
        mQrcodeState = AppConfig.getInstance().getQrScanEnabled();
        mRegisterId = AppConfig.getInstance().getBluetoothDevId();

        mAdapter = new ItemSelectorAdapter(mContext);
        mAdapter.setOnItemClickListener(this);
        setOpendoorType();
    }

    private void setOpendoorType() {
        mSetFunc = OPENDOOR_TYPE;
        mAdapter.setStringArray(mContext.getResources().getStringArray(R.array.setting_qrcode_type));
        mAdapter.setSelection(AppConfig.getInstance().getQrOpenType());
        mRecyclerView.setAdapter(mAdapter);
        setHead(R.string.setting_0304);
        showView(OPENDOOR_TYPE);
    }

    private void setQrcodeState() {
        mSetFunc = OPENDOOR_QRCODE;
        mAdapter.setStringArray(mContext.getResources().getStringArray(R.array.setting_enabled2));
        mAdapter.setSelection(mQrcodeState);
        mRecyclerView.setAdapter(mAdapter);
        setHead(R.string.setting_030401);
        showView(OPENDOOR_QRCODE);
    }

    private void setBluetooth() {
        mSetFunc = OPENDOOR_BLUETOOTH;
        setHead(R.string.setting_030402);
        showView(OPENDOOR_BLUETOOTH);
        mIvRegisterID.requestFocus();
        mIvRegisterID.setText(mRegisterId);
        mIvRegisterID.setCursorIndex(mRegisterId.length());
    }

    protected void setHead(int resId) {
        if (mTvHead != null) {
            mTvHead.setText(resId);
        }
    }

    private void showView(int funcId) {
        switch (funcId) {
            case OPENDOOR_TYPE:
            case OPENDOOR_QRCODE:
                mRecyclerView.setVisibility(View.VISIBLE);
                mTvHint.setVisibility(View.INVISIBLE);
                mLine.setVisibility(View.INVISIBLE);
                mLlbutton.setVisibility(View.INVISIBLE);
                mLlRegister.setVisibility(View.INVISIBLE);
                break;

            case OPENDOOR_BLUETOOTH:
                mRecyclerView.setVisibility(View.INVISIBLE);
                mTvHint.setVisibility(View.INVISIBLE);
                mLine.setVisibility(View.VISIBLE);
                mLlbutton.setVisibility(View.VISIBLE);
                mLlRegister.setVisibility(View.VISIBLE);
                break;

            case OPENDOOR_HINT:
                mRecyclerView.setVisibility(View.INVISIBLE);
                mTvHint.setVisibility(View.VISIBLE);
                mLine.setVisibility(View.VISIBLE);
                mLlbutton.setVisibility(View.INVISIBLE);
                mLlRegister.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void saveParam() {
        AppConfig.getInstance().setQrOpenType(mOpendoorType);
        if (mOpendoorType == 0) {
            AppConfig.getInstance().setQrScanEnabled(mQrcodeState);
        } else {
            String registerId = mIvRegisterID.getText().toString();
            AppConfig.getInstance().setBluetoothDevId(registerId);
        }

        mTvHint.setText(R.string.set_success);
        showView(OPENDOOR_HINT);
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setOpendoorType();
            }
        }, Constant.SET_HINT_TIMEOUT);
    }

    @Override
    public void onItemClick(int position) {
        switch (mSetFunc) {
            case OPENDOOR_TYPE:
                mOpendoorType = position;
                if (mOpendoorType == 0) {
                    setQrcodeState();
                } else {
                    setBluetooth();
                }
                break;

            case OPENDOOR_QRCODE:
                mQrcodeState = position;
                saveParam();
                break;
        }
    }
}
