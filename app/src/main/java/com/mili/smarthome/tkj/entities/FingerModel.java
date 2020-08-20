package com.mili.smarthome.tkj.entities;

import io.realm.RealmObject;

/**
 * 指纹数据库
 */
public class FingerModel extends RealmObject {

    private int fingerId;   // 指纹ID，本地添加该值为0，上位机下发该值不为0，本地不能删除上位机下发的指纹
    private String roomNo; // 房号
    private int valid;  // 指纹有效标志
    private byte[] fingerInfo = new byte[384];//指纹特征值


    public int getFingerId() {
        return fingerId;
    }

    public void setFingerId(int fingerId) {
        this.fingerId = fingerId;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public int getValid() {
        return valid;
    }

    public void setValid(int valid) {
        this.valid = valid;
    }

    public byte[] getFingerInfo() {
        return fingerInfo;
    }

    public void setFingerInfo(byte[] fingerInfo) {
        this.fingerInfo = fingerInfo;
    }
}
