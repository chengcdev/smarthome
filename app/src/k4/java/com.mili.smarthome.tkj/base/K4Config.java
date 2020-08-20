package com.mili.smarthome.tkj.base;

import com.mili.smarthome.tkj.utils.LogUtils;

public class K4Config {

    private static K4Config mInstance = new K4Config();
    public static K4Config getInstance() {
        return mInstance;
    }

    /** 是否正在呼叫住户或中心 */
    private boolean mCallState = false;
    /** 是否正在监视通话 */
    private boolean mMonitorTalk = false;

    private String mDeviceDesc;


    private K4Config() {
        init();
    }

    private void init() {

    }

    /**
     * 设置呼叫对讲状态
     * @param state 是否处于呼叫对讲状态
     */
    public void setCallState(boolean state) {
        mCallState = state;
    }

    /**
     * 获取呼叫对讲状态
     * @return  是否处于呼叫对讲状态
     */
    public boolean getCallState() {
        return mCallState;
    }

    /**
     * 设置监视通话状态
     * @param state 是否处于监视通话状态
     */
    public void setMonitorTalk(boolean state) {
        mMonitorTalk = state;
    }

    /**
     * 获取监视通话状态
     * @return  是否处于监视通话状态
     */
    public boolean getMonitorTalk() {
        return mMonitorTalk;
    }

    /**
     * 设置设备编号描述
     * @param desc  设备编号描述
     */
    public void setDeviceDesc(String desc) {
        mDeviceDesc = desc;
    }

    /**
     * 获取设备编号描述
     * @param cut   是否进行裁剪
     * @return      描述
     */
    public String getDeviceDesc(boolean cut) {
        if (cut) {
            if (mDeviceDesc != null && mDeviceDesc.length() > 12) {
                mDeviceDesc = mDeviceDesc.substring(0, 12);
            }
        }
        LogUtils.d(" mDeviceDesc is " + mDeviceDesc);
        return mDeviceDesc;
    }

    /** 是否与阿里边缘网关设备对接二维码人脸识别开门功能 */
    private boolean mAliyunEdge = false;
    public boolean getAliyunEdge() {
        return mAliyunEdge;
    }
}
