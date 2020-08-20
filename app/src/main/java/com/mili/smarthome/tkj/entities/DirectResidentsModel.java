package com.mili.smarthome.tkj.entities;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * 直按式住户列表
 */

public class DirectResidentsModel extends RealmObject {

    @PrimaryKey
    private String roomNo; //房号

    private String roomName; //房名

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
