package com.mili.smarthome.tkj.main.entity;


public class HintBean extends CommonBean {
    //显示类型
    private int type;
    //房号
    private String roomNo;
    //呼叫返回状态
    // 0 正常 非0 空号
    private int code;
    //房号名称
    private String roomName;
    //呼叫方
    private String callFrome;
    //状态变化文字状态
    private int callStatus;
    //状态变化文字颜色
    private int callStatusColor;
    //状态变化图片
    private int callStatusImgId;
    //是否主动呼叫
    private boolean isActiveCall;
    //是否管理员密码
    private boolean isAdminPwd;

    public HintBean() {

    }

    public HintBean(int type) {
        this.type = type;
    }

    public HintBean(int type, String deviceNo, String roomName) {
        this.type = type;
        this.roomNo = deviceNo;
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getCallFrome() {
        return callFrome;
    }

    public void setCallFrome(String callFrome) {
        this.callFrome = callFrome;
    }

    public int getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(int callStatus) {
        this.callStatus = callStatus;
    }

    public int getCallStatusColor() {
        return callStatusColor;
    }

    public void setCallStatusColor(int callStatusColor) {
        this.callStatusColor = callStatusColor;
    }

    public int getCallStatusImgId() {
        return callStatusImgId;
    }

    public void setCallStatusImgId(int callStatusImgId) {
        this.callStatusImgId = callStatusImgId;
    }

    public boolean isActiveCall() {
        return isActiveCall;
    }

    public void setActiveCall(boolean activeCall) {
        isActiveCall = activeCall;
    }

    public boolean isAdminPwd() {
        return isAdminPwd;
    }

    public void setAdminPwd(boolean adminPwd) {
        isAdminPwd = adminPwd;
    }
}
