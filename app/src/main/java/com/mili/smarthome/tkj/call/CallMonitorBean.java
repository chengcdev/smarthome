package com.mili.smarthome.tkj.call;

import java.io.Serializable;

public class CallMonitorBean implements Serializable {
    //呼叫方
    private String callFrom;
    //是否开门
    private boolean isOpenDoor;
    //是否通话
    private boolean isCallTalk;
    //是否通话结束
    private boolean isCallEnd;
    //没有在呼叫中心和呼叫用户界面，开门
    private boolean onlyOpenDoor;

    public boolean isOnlyOpenDoor() {
        return onlyOpenDoor;
    }

    public void setOnlyOpenDoor(boolean onlyOpenDoor) {
        this.onlyOpenDoor = onlyOpenDoor;
    }

    public String getCallFrom() {
        return callFrom;
    }

    public void setCallFrom(String callFrom) {
        this.callFrom = callFrom;
    }

    public boolean isOpenDoor() {
        return isOpenDoor;
    }

    public void setOpenDoor(boolean openDoor) {
        isOpenDoor = openDoor;
    }

    public boolean isCallTalk() {
        return isCallTalk;
    }

    public void setCallTalk(boolean callTalk) {
        isCallTalk = callTalk;
    }

    public boolean isCallEnd() {
        return isCallEnd;
    }

    public void setCallEnd(boolean callEnd) {
        isCallEnd = callEnd;
    }
}
