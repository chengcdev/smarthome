package com.mili.smarthome.tkj.entities.userInfo;

import io.realm.RealmObject;

/**
 * 用户密码
 */

public class UserPwdModels extends RealmObject {

    /**
     * 房号
     */
    private String roomNo;
    /**
     * 开门密码
     */
    private String openDoorPwd;
    /**
     * 密码类型：0x00住户密码（默认），0x01快递，0x02外卖，0x03其他
     */
    private int pwdType = 0x00;

    /**
     * 房号状态 0：有房号 1：空房号
     */
    private int roomNoState;
    /**
     * 用户ID
     */
    private String keyID;

    /**
     * 密码属性
     */
    private int attri;
    /**
     * 生效时间: 默认值0
     */
    private int startTime;
    /**
     * 失效时间: 默认值0
     */
    private int endTime;
    /**
     * 有效次数: 默认值-2
     */
    private int lifecycle;

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getOpenDoorPwd() {
        return openDoorPwd;
    }

    public void setOpenDoorPwd(String openDoorPwd) {
        this.openDoorPwd = openDoorPwd;
    }

    public int getPwdType() {
        return pwdType;
    }

    public void setPwdType(int pwdType) {
        this.pwdType = pwdType;
    }

    public int getRoomNoState() {
        return roomNoState;
    }

    public void setRoomNoState(int roomNoState) {
        this.roomNoState = roomNoState;
    }

    public String getKeyID() {
        return keyID;
    }

    public void setKeyID(String keyID) {
        this.keyID = keyID;
    }

    public int getAttri() {
        return attri;
    }

    public void setAttri(int attri) {
        this.attri = attri;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(int lifecycle) {
        this.lifecycle = lifecycle;
    }
}
