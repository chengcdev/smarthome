package com.mili.smarthome.tkj.setting.fragment;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;

import java.util.Arrays;

/**
 * 人体感应
 * {@link com.mili.smarthome.tkj.setting.entities.SettingFunc.SET_BODY_DETECTION}: 人体感应
 */
public class SetBodyDetectionFragment extends ItemSelectorFragment {

    /** 触发开屏 */
    private static final int SCREEN_ON = 0;
    /** 人脸识别 */
    private static final int FACE = 1;
    /** 扫码开门 */
    private static final int QRCODE = 2;
    /** 蓝牙开门器 */
    private static final int BLUETOOTH = 3;

    private int[] mCodes;

    @Override
    protected String[] getStringArray() {
        mCodes = new int[4];
        String[] names = new String[4];
        int len = 0;
        mCodes[len] = SCREEN_ON;
        names[len] = getString(R.string.body_detection_0);
        len++;
        if (AppConfig.getInstance().isFaceEnabled()) {
            mCodes[len] = FACE;
            names[len] = getString(R.string.setting_0303);
            len++;
        }
        if (AppConfig.getInstance().getQrOpenType() == 0) {
            if (AppConfig.getInstance().getQrScanEnabled() == 1) {
                mCodes[len] = QRCODE;
                names[len] = getString(R.string.setting_030401);
                len++;
            }
        } else if (AppConfig.getInstance().getQrOpenType() == 1) {
            String bleDevId = AppConfig.getInstance().getBluetoothDevId();
            if (bleDevId != null && bleDevId.length() > 0) {
                mCodes[len] = BLUETOOTH;
                names[len] = getString(R.string.setting_030402);
                len++;
            }
        }
        mCodes = Arrays.copyOf(mCodes, len);
        names = Arrays.copyOf(names, len);
        return names;
    }

    @Override
    protected void bindData() {
        super.bindData();
        for (int i = 0; i < mCodes.length; i++) {
            if (mCodes[i] == AppConfig.getInstance().getBodyInduction()) {
                setSelection(i);
                break;
            }
        }
    }

    @Override
    public void onItemClick(int position) {
        AppConfig.getInstance().setBodyInduction(mCodes[position]);
        showResultAndBack(R.string.setting_suc);
    }
}
