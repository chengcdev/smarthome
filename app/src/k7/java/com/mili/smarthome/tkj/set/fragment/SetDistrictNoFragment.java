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
 * 区口号设置
 */

public class SetDistrictNoFragment extends BaseKeyBoardFragment implements ISetCallBackListener {

    private TextView tvTitle;
    private final int qkhId = 1000;
    private final int enableId = 1001;
    private int currentId = qkhId;
    private SetSuccessView successView;
    private String TAG = "SetDeviceNoFragment";
    private String enable;
    private int qkhMaxLen = 2;
    private FullDeviceNo fullDeviceNo;
    private int useCellNo;
    //是否第一次安装跳转
    private boolean isFirst;
    private CustomInputView itQkh;
    private CustomInputView itEnable;
    private String qkhContent;

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
        return R.layout.fragment_setting_district_no;
    }

    @Override
    public void initView() {
        tvTitle = (TextView) getContentView().findViewById(R.id.tv_title);
        itQkh = (CustomInputView) getContentView().findViewById(R.id.it_qkh);
        itEnable = (CustomInputView) getContentView().findViewById(R.id.it_enable);
        successView = (SetSuccessView) getContentView().findViewById(R.id.root);
    }


    @Override
    public void initAdapter() {
        currentId = qkhId;
        fullDeviceNo = new FullDeviceNo(getContext());
        tvTitle.setText(getString(R.string.setting_qkh_set));
        useCellNo = fullDeviceNo.getUseCellNo();
        if (useCellNo == 1) {
            //启用
            enable = getString(R.string.setting_yes);
        } else {
            enable = getString(R.string.setting_no);
        }

        qkhContent = fullDeviceNo.getStairNo();

        itQkh.setFirstFlash(true).init(qkhContent, 2, CustomInputAdapter.INPUT_TYPE_OTHER);
        itEnable.setFirstFlash(false).init(enable, 1, CustomInputAdapter.INPUT_TYPE_OTHER);
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
                qkhContent = itQkh.getNum();
                enable = itEnable.getNum();
                if (getString(R.string.setting_yes).equals(enable)) {
                    useCellNo = 1;
                }else {
                    useCellNo = 0;
                }
                if (Integer.valueOf(qkhContent) != 0) {
                    //保存数据
                    saveDatas(qkhContent);
                    if (!isFirst) {
                        //设置成功
                        successView.showSuccessView(getString(R.string.setting_success),1000,this);
                    }else {
                        //跳转到网络设置界面
                        SetNetFragment setNetFragment = new SetNetFragment();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(Constant.SETTING_FIRST,true);
                        AppManage.getInstance().replaceFragment(getActivity(),setNetFragment,bundle);
                    }
                }else {
                    //设置失败
                    successView.showSuccessView(getString(R.string.setting_fail),1000,this);
                }

                break;
            case Constant.KEY_UP:
                if (currentId == enableId) {
                    if (itEnable.getNum().equals(getString(R.string.setting_yes))) {
                        itEnable.addNum(getString(R.string.setting_no));
                    }else {
                        itEnable.addNum(getString(R.string.setting_yes));
                    }
                }
                break;
            case Constant.KEY_DELETE:
                switch (currentId) {
                    case qkhId:
                        if (itQkh.getCount() == 0) {
                            itQkh.setFirstFlash(true);
                        }
                        itQkh.deleteNum("0");
                        break;
                    case enableId:
                        itEnable.setFirstFlash(false).setEndFlash(false).notifychange();
                        itQkh.setFirstFlash(false).setEndFlash(true).notifychange();
                        currentId = qkhId;
                        break;
                }
                break;
            case Constant.KEY_NEXT:
                if (currentId == enableId) {
                    if (itEnable.getNum().equals(getString(R.string.setting_yes))) {
                        itEnable.addNum(getString(R.string.setting_no));
                    }else {
                        itEnable.addNum(getString(R.string.setting_yes));
                    }
                }
                break;
            default:
                switch (currentId) {
                    case qkhId:
                        if (itQkh.getCount() == 1) {
                            currentId = enableId;
                            itQkh.setEndFlash(false);
                            itEnable.setFirstFlash(true).setEndFlash(true).notifychange();
                            itEnable.setCount(0);
                            itQkh.setCount(1);
                        }
                        itQkh.addNum(keyId);
                        break;
                }
                break;
        }
    }

    private void saveDatas(String deviceNo) {
        fullDeviceNo.setDeviceNo(deviceNo);
        fullDeviceNo.setStairNo(qkhContent);
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
