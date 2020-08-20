package com.mili.smarthome.tkj.setting.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.CommSysDef;
import com.android.CommTypeDef;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.adapter.KeyBoardAdapter;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.entity.KeyBoardBean;
import com.mili.smarthome.tkj.utils.AppUtils;
import com.mili.smarthome.tkj.view.KeyBoardView;
import com.mili.smarthome.tkj.view.SetOperateView;
import com.mili.smarthome.tkj.widget.MyTextView;
import com.mili.smarthome.tkj.widget.NumInputView;

import java.util.Objects;

/**
 * 梯口号设置
 */
public class SetDevNoFragment extends BaseFragment implements KeyBoardAdapter.IKeyBoardListener, SetOperateView.IOperateListener, View.OnClickListener {

    private NumInputView mTikou;
    private NumInputView mDev;
    private MyTextView mEnabled;
    private KeyBoardView keyBoardView;
    private SetOperateView mOperateView;
    //梯口号
    private String stairNo = "";
    //设备号
    private String currentDeviceNo = "";
    //是否启用单元号
    private int useCellNo = 0;
    private int devNoLen;
    private int tkhMaxLen = 2;
    private int enableMaxLen = 1;
    private String enableContent;

    private FullDeviceNo fullDeviceNo;
    //最终设备号
    private String deviceNo;
    //恢复出厂第一次设置
    private boolean isFirstEnable;
    private LinearLayout mLlTitle;
    private TextView mTvLeftStair;
    private LinearLayout mLinDeviceNo;
    //是否梯口机
    private boolean isStair = false;
    private TextView mTvTitle;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_devno;
    }

    @Override
    protected void bindView() {
        mTikou = findView(R.id.tv_tk_no);
        mDev = findView(R.id.tv_dev_no);
        mEnabled = findView(R.id.tv_enabled);
        keyBoardView = findView(R.id.keyboardview);
        mOperateView = findView(R.id.rootview);
        mLlTitle = findView(R.id.lin_title);
        mLinDeviceNo = findView(R.id.lin_device_no);
        mTvLeftStair = findView(R.id.tv_left_stair);
        mTvTitle = findView(R.id.tv_title);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void bindData() {
        keyBoardView.init(KeyBoardView.KEY_BOARD_SET);
        keyBoardView.setKeyBoardListener(this);
        mOperateView.setSuccessListener(this);
        mEnabled.setOnClickListener(this);
        initDeviceNo();
        //是否恢复出厂后第一次设置
        initFirstSet();
    }



    private void initFirstSet() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            isFirstEnable = bundle.getBoolean(Constant.IntentId.INTENT_KEY);
        }
        if (isFirstEnable) {
            mLlTitle.setVisibility(View.VISIBLE);
        } else {
            mLlTitle.setVisibility(View.GONE);
        }
    }

    private void initDeviceNo() {
        fullDeviceNo = new FullDeviceNo(getContext());
        stairNo = fullDeviceNo.getStairNo();
        currentDeviceNo = fullDeviceNo.getCurrentDeviceNo();
        useCellNo = fullDeviceNo.getUseCellNo();
        devNoLen = fullDeviceNo.getStairNoLen();
        byte deviceType = fullDeviceNo.getDeviceType();
        if (deviceType == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            isStair = true;
        } else {
            isStair = false;
        }
        mTikou.setMaxLength(tkhMaxLen);
        mDev.setMaxLength(currentDeviceNo.length());
        mEnabled.setMaxLength(enableMaxLen);


        if (useCellNo == 1) {
            //启用
            enableContent = getString(R.string.setting_yes);
        } else {
            enableContent = getString(R.string.setting_no);
        }

        mTikou.setText(stairNo);
        mDev.setText(currentDeviceNo);
        mEnabled.setText(enableContent);
        mTikou.requestFocus();

        if (isStair) {
            //梯口机
            mLinDeviceNo.setVisibility(View.VISIBLE);
            mTvLeftStair.setText(getString(R.string.setting_tkh));
            mTvTitle.setText(getString(R.string.setting_stair_no));
        } else {
            //区口机
            mLinDeviceNo.setVisibility(View.GONE);
            mTvLeftStair.setText(getString(R.string.setting_qkh));
            mTvTitle.setText(getString(R.string.setting_area_no));
        }
    }

    @Override
    public void onKeyBoardListener(View view, int potion, KeyBoardBean keyBoardBean) {
        switch (keyBoardBean.getkId()) {
            case Const.KeyBoardId.KEY_CANCEL:
                if (mTikou.getCursorIndex() == 0 && !mDev.isFocused() && !mEnabled.isFocused()) {
                    requestBack();
                }else {
                    backspace();
                }
                break;
            case Const.KeyBoardId.KEY_CONFIRM:
                stairNo = mTikou.getText().toString();
                currentDeviceNo = mDev.getText().toString();
                String enble = mEnabled.getText().toString();
                if (enble.equals(getString(R.string.setting_yes))) {
                    useCellNo = 1;
                } else {
                    useCellNo = 0;
                }

                if (isStair) {
                    //保存梯口号数据
                    saveStairDatas();
                } else {
                    //保存区口号数据
                    saveAreaDatas();
                }
                break;
            default:
                int id = Integer.valueOf(keyBoardBean.getkId());
                inputNum(id);
                break;
        }
    }


    @Override
    public void success() {
        setBackVisibility(View.VISIBLE);
        requestBack();
    }

    private void saveStairDatas() {
        deviceNo = fullDeviceNo.getDeviceNo(stairNo, currentDeviceNo);
        if (fullDeviceNo.DeviceNoisright(deviceNo) == 0) {
            //保存数据
            fullDeviceNo.setDeviceNo(deviceNo);
            fullDeviceNo.setCurrentDeviceNo(currentDeviceNo);
            fullDeviceNo.setStairNo(stairNo);
            fullDeviceNo.setUseCellNo((byte) useCellNo);
            //发送广播
            Intent intent = new Intent(CommSysDef.BROADCAST_DEVICENUMBER);
            Objects.requireNonNull(getActivity()).sendBroadcast(intent);

            if (isFirstEnable) {
                SetNetworkFragment networkFragment = new SetNetworkFragment();
                //跳转到设置IP界面
                AppUtils.getInstance().replaceFragment(getActivity(), networkFragment, R.id.fl, "SetNetworkFragment",
                        Constant.IntentId.INTENT_KEY, true);
            } else {
                //设置成功
                mOperateView.operateBackState(getString(R.string.set_success));
                setBackVisibility(View.GONE);
            }
        } else {
            //设置失败
            mOperateView.operateBackState(getString(R.string.set_fail));
            setBackVisibility(View.GONE);
        }
    }

    private void saveAreaDatas() {
        if (Integer.valueOf(stairNo) > 0) {
            //保存数据
            fullDeviceNo.setDeviceNo(stairNo);
            fullDeviceNo.setStairNo(stairNo);
            fullDeviceNo.setUseCellNo((byte) useCellNo);
            //发送广播
            Intent intent = new Intent(CommSysDef.BROADCAST_DEVICENUMBER);
            Objects.requireNonNull(getActivity()).sendBroadcast(intent);

            if (isFirstEnable) {
                SetNetworkFragment networkFragment = new SetNetworkFragment();
                //跳转到设置IP界面
                AppUtils.getInstance().replaceFragment(getActivity(), networkFragment, R.id.fl, "SetNetworkFragment",
                        Constant.IntentId.INTENT_KEY, true);
            } else {
                //设置成功
                mOperateView.operateBackState(getString(R.string.set_success));
                setBackVisibility(View.GONE);
            }
        }else {
            //设置成功
            mOperateView.operateBackState(getString(R.string.set_fail));
            setBackVisibility(View.GONE);
        }
    }

    @Override
    public void fail() {
        setBackVisibility(View.VISIBLE);
        requestBack();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_enabled:
                String str = mEnabled.getText().toString();
                if (getString(R.string.setting_yes).equals(str)) {
                    mEnabled.setText(getString(R.string.setting_no));
                } else {
                    mEnabled.setText(getString(R.string.setting_yes));
                }
                break;
        }
    }

}
