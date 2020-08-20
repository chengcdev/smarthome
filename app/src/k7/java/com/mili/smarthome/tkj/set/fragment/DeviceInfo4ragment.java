package com.mili.smarthome.tkj.set.fragment;


import android.widget.LinearLayout;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.dao.param.SnapParamDao;
import com.mili.smarthome.tkj.main.fragment.BaseKeyBoardFragment;
import com.mili.smarthome.tkj.set.widget.DeviceInfoTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备信息4
 */

public class DeviceInfo4ragment extends BaseKeyBoardFragment {


    private LinearLayout mLinRoot;
    private List<String> dataList = new ArrayList<>();

    public int getLayout() {
        return R.layout.fragment_decice_info1;
    }

    @Override
    public void setKeyBoard(int viewId, String keyId) {

    }

    @Override
    public void initView() {
        mLinRoot = (LinearLayout) getContentView().findViewById(R.id.root);

    }

    @Override
    public void initAdapter() {

        dataList.clear();
        mLinRoot.removeAllViews();

        //获取数据
        dataList.add(getStr(R.string.device_info_photo_1, SnapParamDao.getVisitorSnap()));
        dataList.add(getStr(R.string.device_info_photo_2, SnapParamDao.getErrorPwdSnap()));
        dataList.add(getStr(R.string.device_info_photo_3, SnapParamDao.getHijackPwdSnap()));
        dataList.add(getStr(R.string.device_info_photo_4, SnapParamDao.getCallCenterSnap()));
        if (AppConfig.getInstance().getFaceModule() == 1 && AppConfig.getInstance().getFaceRecognition() == 1) {
            dataList.add(getStr(R.string.device_info_photo_5, SnapParamDao.getFaceOpenSnap()));
        }
        if (AppConfig.getInstance().getFingerprint() == 1) {
            dataList.add(getStr(R.string.device_info_photo_6, SnapParamDao.getFingerOpenSnap()));
        }
        dataList.add(getStr(R.string.device_info_photo_7, SnapParamDao.getCardOpenSnap()));
        dataList.add(getStr(R.string.device_info_photo_8, SnapParamDao.getPwdOpenSnap()));
        if (AppConfig.getInstance().getQrScanEnabled() == 1) {
            dataList.add(getStr(R.string.device_info_photo_9,SnapParamDao.getQrcodeOpenSnap()));
        }


        for (int i = 0; i < dataList.size(); i++) {
            DeviceInfoTextView textView = new DeviceInfoTextView(getContext());
            textView.setText(dataList.get(i));
            mLinRoot.addView(textView);
        }
    }


    @Override
    public void initListener() {

    }

    private String getStr(int strId, int param) {
        if (param == 0) {
            //关闭
            return getString(strId) + getString(R.string.setting_close);
        } else {
            //启用
            return getString(strId) + getString(R.string.setting_enable);
        }
    }

}
