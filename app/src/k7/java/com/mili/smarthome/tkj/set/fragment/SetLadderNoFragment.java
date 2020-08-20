package com.mili.smarthome.tkj.set.fragment;


import android.os.Bundle;
import android.widget.TextView;

import com.android.CommSysDef;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.main.fragment.BaseKeyBoardFragment;
import com.mili.smarthome.tkj.main.interf.ISetCallBackListener;
import com.mili.smarthome.tkj.main.widget.SetSuccessView;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.set.widget.inputview.CustomInputAdapter;
import com.mili.smarthome.tkj.set.widget.inputview.CustomInputView;
import com.mili.smarthome.tkj.utils.AppManage;

/**
 * 梯口号设置
 */

public class SetLadderNoFragment extends BaseKeyBoardFragment implements ISetCallBackListener {


    private TextView tvTitle;
    private CustomInputView inputTkh;
    private CustomInputView inputDeviceNo;
    private CustomInputView inputEnable;
    private final int tkhId = 1000;
    private final int deviceId = 1001;
    private final int enableId = 1002;
    private int currentId = tkhId;
    private SetSuccessView successView;
    private String TAG = "SetDeviceNoFragment";
    private String tkhContent = "01";
    private String sbhContent = "0101";
    private String enable = "是";
    private int tkhMaxLen = 2;
    private int sbhMaxlen = 1;
    private FullDeviceNo fullDeviceNo;
    private int useCellNo;
    private String currentDeviceNo = "";
    //是否第一次安装跳转
    private boolean isFirst;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            isFirst = (boolean) bundle.getSerializable(Constant.SETTING_FIRST);
        }
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_setting_ladder_no;
    }

    @Override
    public void initView() {
        tvTitle = (TextView) getContentView().findViewById(R.id.tv_title);
        inputTkh = (CustomInputView) getContentView().findViewById(R.id.it_tkh);
        inputDeviceNo = (CustomInputView) getContentView().findViewById(R.id.it_device_no);
        inputEnable = (CustomInputView) getContentView().findViewById(R.id.it_enable);
        successView = (SetSuccessView) getContentView().findViewById(R.id.root);
    }


    @Override
    public void initAdapter() {
        currentId = tkhId;
        fullDeviceNo = new FullDeviceNo(getContext());
        tvTitle.setText(getString(R.string.setting_tkh_set));
        sbhMaxlen = fullDeviceNo.getStairNoLen();
        useCellNo = fullDeviceNo.getUseCellNo();
//        inputDeviceNo.setMaxNum(sbhMaxlen);
        if (useCellNo == 1) {
            //启用
            enable = getString(R.string.setting_yes);
        } else {
            enable = getString(R.string.setting_no);
        }

        tkhContent = fullDeviceNo.getStairNo();
        sbhContent = fullDeviceNo.getCurrentDeviceNo();

        inputTkh.setFirstFlash(true).init(tkhContent, 2, CustomInputAdapter.INPUT_TYPE_OTHER);
        inputDeviceNo.setFirstFlash(false).init(sbhContent, sbhMaxlen, CustomInputAdapter.INPUT_TYPE_OTHER);
        inputEnable.setFirstFlash(false).init(enable, 1, CustomInputAdapter.INPUT_TYPE_OTHER);
    }


    @Override
    public void initListener() {

    }

    @Override
    public void onResume() {
        super.onResume();
        successView.setSuccessListener(this);
    }

    @Override
    public void setKeyBoard(int viewId, String keyId) {
        switch (keyId) {
            case Constant.KEY_CANCLE:
                //退出界面
                exitFragment(this);
                break;

            //确定
            case Constant.KEY_CONFIRM:
                tkhContent = inputTkh.getNum();
                sbhContent = inputDeviceNo.getNum();
                if (inputEnable.getNum().equals(getString(R.string.setting_yes))) {
                    useCellNo = 1;
                }else {
                    useCellNo = 0;
                }

                currentDeviceNo = sbhContent;
                String deviceNo = fullDeviceNo.getDeviceNo(tkhContent, currentDeviceNo);
                if (fullDeviceNo.DeviceNoisright(deviceNo) == 0) {
                    //保存数据
                    saveDatas(deviceNo);
                    if (!isFirst) {
//                        //设置成功
                        successView.showSuccessView(getString(R.string.setting_success), 1000, this);
                    } else {
                        //跳转到网络设置界面
                        SetNetFragment setNetFragment = new SetNetFragment();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(Constant.SETTING_FIRST, true);
                        AppManage.getInstance().replaceFragment(getActivity(),setNetFragment,bundle);
                    }
                } else {
                    //设置失败
                    successView.showSuccessView(getString(R.string.setting_fail), 1000, this);
                }

                break;
            case Constant.KEY_UP:
                if (currentId == enableId) {
                    if (inputEnable.getNum().equals(getString(R.string.setting_yes))) {
                        inputEnable.addNum(getString(R.string.setting_no));
                    }else {
                        inputEnable.addNum(getString(R.string.setting_yes));
                    }
                }
                break;
            case Constant.KEY_DELETE:
                switch (currentId) {
                    case tkhId:
                        inputTkh.deleteNum("0");
                        if (inputTkh.getCount() == 0) {
                            inputTkh.setFirstFlash(true).setEndFlash(false).notifychange();
                        }
                        break;
                    case deviceId:
                        if (inputDeviceNo.getCount() == 0) {
                            currentId = tkhId;
                            inputTkh.setFirstFlash(false).setEndFlash(true).notifychange();
                            inputDeviceNo.setFirstFlash(false).setEndFlash(false).notifychange();
                        }
                        inputDeviceNo.deleteNum("0");
                        break;
                    case enableId:
                        inputEnable.setFirstFlash(false).setEndFlash(false).notifychange();
                        inputDeviceNo.setFirstFlash(false).setEndFlash(true).notifychange();
                        currentId = deviceId;
                        break;
                }
                break;
            case Constant.KEY_NEXT:
                if (currentId == enableId) {
                    if (inputEnable.getNum().equals(getString(R.string.setting_yes))) {
                        inputEnable.addNum(getString(R.string.setting_no));
                    }else {
                        inputEnable.addNum(getString(R.string.setting_yes));
                    }
                }
                break;
            default:
                switch (currentId) {
                    case tkhId:
                        if (inputTkh.getCount() == 1) {
                            currentId = deviceId;
                            inputTkh.setEndFlash(false);
                            inputDeviceNo.setFirstFlash(true);
                            inputDeviceNo.notifychange();
                        }
                        inputTkh.addNum(keyId);
                        break;
                    case deviceId:
                        if (inputDeviceNo.getCount() == sbhMaxlen-1) {
                            currentId = enableId;
                            inputDeviceNo.setEndFlash(false);
                            inputEnable.setFirstFlash(true).setEndFlash(true);
                            inputEnable.notifychange();
                        }
                        inputDeviceNo.addNum(keyId);
                        break;
                }
                break;
        }
    }


    private void saveDatas(String deviceNo) {
        fullDeviceNo.setDeviceNo(deviceNo);
        fullDeviceNo.setCurrentDeviceNo(currentDeviceNo);
        fullDeviceNo.setStairNo(tkhContent);
        fullDeviceNo.setUseCellNo((byte) useCellNo);
    }

    @Override
    public void success() {
        exitFragment(this);
    }

    @Override
    public void fail() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //发送广播
        AppManage.getInstance().sendReceiver(CommSysDef.BROADCAST_DEVICENUMBER);
    }
}
