package com.mili.smarthome.tkj.set.fragment;


import android.widget.LinearLayout;

import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.dao.param.AlarmParamDao;
import com.mili.smarthome.tkj.dao.param.ParamDao;
import com.mili.smarthome.tkj.dao.param.VolumeParamDao;
import com.mili.smarthome.tkj.main.fragment.BaseKeyBoardFragment;
import com.mili.smarthome.tkj.set.widget.DeviceInfoTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备信息3
 */

public class DeviceInfo3ragment extends BaseKeyBoardFragment {


    private LinearLayout mLinRoot;
    private List<String> dataList = new ArrayList<>();
    private String keyVolume;

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

        //密码开门模式
        String pwdDoorModelStr;
        //密码进门模式
        if (AppConfig.getInstance().getOpenPwdMode() == 0) {
            pwdDoorModelStr = getString(R.string.device_info_1) + getString(R.string.setting_senior_low);
        }else {
            pwdDoorModelStr = getString(R.string.device_info_1) + getString(R.string.setting_senior_height);
        }
        //强行开门报警
        String alarmParamStr;
        //开门报警
        if (AlarmParamDao.getForceOpen() == 0) {
            alarmParamStr = getString(R.string.device_info_2)+getString(R.string.setting_close);
        }else {
            alarmParamStr = getString(R.string.device_info_2) + getString(R.string.setting_enable);
        }
        //省电模式
        String powerSaveStr;
        //省电模式
        if (AppConfig.getInstance().getPowerSaving() == 0) {
            powerSaveStr = getString(R.string.device_info_6) + getString(R.string.setting_close);
        }else {
            powerSaveStr = getString(R.string.device_info_6)+getString(R.string.setting_enable);
        }
        //屏保
        String screenProStr;
        //屏保
        if (AppConfig.getInstance().getScreenSaver() == 0) {
            screenProStr = getString(R.string.device_info_7) + getString(R.string.setting_close);
        }else {
            screenProStr = getString(R.string.device_info_7)+getString(R.string.setting_enable);
        }

        //灵敏度
        String sensitivitySetStr;
        //灵敏度
        if (ParamDao.getTouchSensitivity() == 0) {
            sensitivitySetStr = getString(R.string.device_info_8) + getString(R.string.setting_senior_h);
        }else if (ParamDao.getTouchSensitivity() == 1) {
            sensitivitySetStr = getString(R.string.device_info_8) + getString(R.string.setting_senior_m);
        }else {
            sensitivitySetStr = getString(R.string.device_info_8) + getString(R.string.setting_senior_l);
        }
        //动态密保

        //卡号位数
        int cardNums = ParamDao.getCardNoLen();

        FullDeviceNo fullDeviceNo = new FullDeviceNo(getContext());
        //梯号长度
        int stairNoLen = fullDeviceNo.getStairNoLen();
        //房号长度
        int roomNoLen = fullDeviceNo.getRoomNoLen();
        //单元号长度
        int cellNoLen = fullDeviceNo.getCellNoLen();
        //分段参数
        int subsection = fullDeviceNo.getSubsection();

        //提示音
        int callVolume = VolumeParamDao.getCallVolume();

        //提示音
        String volumeTipStr;
        //按键音
        String volumeKeyStr;

        if (AppConfig.getInstance().getTipVolume() == 0) {
            //提示音
            volumeTipStr = getString(R.string.device_info_15) + getString(R.string.setting_close);
        }else {
            //提示音
            volumeTipStr = getString(R.string.device_info_15) + getString(R.string.setting_enable);
        }
        if (AppConfig.getInstance().getKeyVolume() == 0) {
            //按键音
            volumeKeyStr = getString(R.string.device_info_16) + getString(R.string.setting_close);
        }else {
            //按键音
            volumeKeyStr = getString(R.string.device_info_16) + getString(R.string.setting_enable);
        }

        //获取数据
        dataList.add(pwdDoorModelStr);
        dataList.add(alarmParamStr);
        dataList.add(powerSaveStr);
        dataList.add(screenProStr);
        dataList.add(sensitivitySetStr);

        dataList.add(getString(R.string.device_info_9) + cardNums+getString(R.string.device_info_position));

        dataList.add(getString(R.string.device_info_10) + stairNoLen);
        dataList.add(getString(R.string.device_info_11) + roomNoLen);
        dataList.add(getString(R.string.device_info_12) + cellNoLen);
        dataList.add(getString(R.string.device_info_13) + subsection);
        dataList.add(getString(R.string.device_info_14) + callVolume);
        dataList.add(volumeTipStr);
        dataList.add(volumeKeyStr);


        for (int i = 0; i < dataList.size(); i++) {
            DeviceInfoTextView textView = new DeviceInfoTextView(getContext());
            textView.setText(dataList.get(i));
            mLinRoot.addView(textView);
        }
    }


    @Override
    public void initListener() {

    }

}
