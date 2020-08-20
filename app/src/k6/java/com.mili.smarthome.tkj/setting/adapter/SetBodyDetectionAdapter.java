package com.mili.smarthome.tkj.setting.adapter;

import android.content.Context;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;
import com.mili.smarthome.tkj.setting.interf.IOnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 人体感应
 */
public class SetBodyDetectionAdapter extends ItemSelectorAdapter {

    private final String body_touch = "F1";
    private final String body_face = "F2";
    private final String body_scan = "F3";
    private final String body_blutooth = "F4";

    private IOnItemClickListener itemClickListener;
    private List<SettingFunc> list = new ArrayList<>();

    public SetBodyDetectionAdapter(Context context) {
        super(context);
    }

    public SetBodyDetectionAdapter(Context context, IOnItemClickListener itemClickListener) {
        super(context);
        this.itemClickListener = itemClickListener;
        initList(context);
    }

    private void initList(Context context) {
        //触发开屏
        list.add(new SettingFunc(body_touch, context.getString(R.string.setting_touch_screen)));

        int faceRecognition = AppConfig.getInstance().getFaceRecognition();
        int faceModule = AppConfig.getInstance().getFaceModule();
        if (faceModule == 1 && faceRecognition == 1) {
            //人脸
            list.add(new SettingFunc(body_face, context.getString(R.string.setting_0303)));
        }
        //扫码开门方式
        int qrOpenDoorType = AppConfig.getInstance().getQrOpenType();
        if (qrOpenDoorType == 0) {
            //扫码开门
            int sweepCodeOpen = AppConfig.getInstance().getQrScanEnabled();
            if (sweepCodeOpen == 1) {
                list.add(new SettingFunc(body_scan, context.getString(R.string.setting_030401)));
            }
        } else {
            //蓝牙开门器
            if (AppConfig.getInstance().getBluetoothDevId() != null && !AppConfig.getInstance().getBluetoothDevId().equals("")) {
                list.add(new SettingFunc(body_blutooth, context.getString(R.string.setting_030402)));
            }
        }
        String[] strs = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            strs[i] = list.get(i).getName();
        }
        setStringArray(strs);

        int bodyFeeling = AppConfig.getInstance().getBodyInduction();
        switch (bodyFeeling) {
            case 0:
                setSelection(0);
                break;
            case 1:
                setSelection(1);
                break;
            case 2:
                setSelection(list.size()-1);
                break;
            case 3:
                setSelection(list.size()-1);
                break;
        }
    }

    @Override
    protected int getStringArrayId() {
        return R.array.setting_body_detection;
    }

    @Override
    protected void onItemClick(int position) {
        SettingFunc settingFunc = list.get(position);
        String code = settingFunc.getCode();
        switch (code) {
            //触发开屏
            case body_touch:
                AppConfig.getInstance().setBodyInduction(0);
                break;
            //人脸识别
            case body_face:
                AppConfig.getInstance().setBodyInduction(1);
                break;
            //扫码开门
            case body_scan:
                AppConfig.getInstance().setBodyInduction(2);
                break;
            //蓝牙开门器
            case body_blutooth:
                AppConfig.getInstance().setBodyInduction(3);
                break;
        }

        if (itemClickListener != null) {
            itemClickListener.OnItemListener(position);
        }
    }

}
