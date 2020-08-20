package com.mili.smarthome.tkj.call;

public interface IActCallBackListener {
    void callBackValue(String param, String roomNo);

    void callBack(CallMonitorBean callMonitorBean);
}
