package com.mili.smarthome.tkj.inteface;

import com.mili.smarthome.tkj.call.CallMonitorBean;

public interface IActCallBackListener {
    void callBackValue(String param, String roomNo);

    void callBack(CallMonitorBean callMonitorBean);
}
