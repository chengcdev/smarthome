package com.mili.smarthome.tkj.main.entity;

import java.io.Serializable;

public class ResidentListEntity implements Serializable {

    private String roomNo;

    private String roomName;

    public ResidentListEntity(String roomNo, String roomName) {
        this.roomNo = roomNo;
        this.roomName = roomName;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
