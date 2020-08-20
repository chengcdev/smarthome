package com.mili.smarthome.tkj.set.fragment;


import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.main.fragment.BaseKeyBoardFragment;
import com.mili.smarthome.tkj.main.interf.ISetCallBackListener;
import com.mili.smarthome.tkj.main.widget.SetSuccessView;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.set.widget.inputview.CustomInputAdapter;
import com.mili.smarthome.tkj.set.widget.inputview.CustomInputView;

/**
 * 蓝牙开门器
 */

public class BluetoothOpenFragment extends BaseKeyBoardFragment implements ISetCallBackListener {


    private CustomInputView mRegisterId;
    private TextView mTvTitle;
    private SetSuccessView successView;
    private String currentRegisterId;

    public int getLayout() {
        return R.layout.fragment_blue_tooth_open;
    }


    @Override
    public void initView() {
        mTvTitle = (TextView) getContentView().findViewById(R.id.tv_title);
        mRegisterId = (CustomInputView) getContentView().findViewById(R.id.it_register_id);
        successView = (SetSuccessView) getContentView().findViewById(R.id.root);
    }

    @Override
    public void initAdapter() {
        mTvTitle.setText(getString(R.string.setting_blue_tooth_open));

        if (AppConfig.getInstance().getBluetoothDevId() != null) {
            String registerId = AppConfig.getInstance().getBluetoothDevId();
            mRegisterId.init(registerId,8, CustomInputAdapter.INPUT_TYPE_2);
            mRegisterId.notifychange();
            if (!registerId.equals("")) {
                mRegisterId.setEndFlash(false);
            }
        }
    }


    @Override
    public void initListener() {
        successView.setSuccessListener(this);
    }

    @Override
    public void success() {
        if (isAdded()) {
            exitFragment(this);
        }
    }

    @Override
    public void fail() {

    }

    @Override
    public void setKeyBoard(int viewId, String keyId) {
        switch (keyId) {
            case Constant.KEY_CANCLE:
                //退出界面
                exitFragment(this);
                break;
            case Constant.KEY_CONFIRM:
                currentRegisterId = mRegisterId.getNum();
                if (currentRegisterId != null) {
                    //保存数据
                    AppConfig.getInstance().setBluetoothDevId(currentRegisterId);
                    successView.showSuccessView(getString(R.string.setting_success),1000,this);
                }else {
                    successView.showSuccessView(getString(R.string.setting_fail), 1000, this);
                }
                break;
            case Constant.KEY_UP:
                break;
            case Constant.KEY_DELETE:
                mRegisterId.deleteNum("");
                if (mRegisterId.getNum().length() == 0) {
                    mRegisterId.setEndFlash(true);
                    mRegisterId.notifychange();
                }
                break;
            case Constant.KEY_NEXT:

                break;
            default:
                if (mRegisterId.getNum().length() != 8) {
                    mRegisterId.setEndFlash(false);
                    mRegisterId.addNum(keyId);
                }
                break;
        }
    }
}
