package com.mili.smarthome.tkj.entities;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * 住户设置
 */

public class ResidentSettingModel extends RealmObject {
    @PrimaryKey
    private String Id;
    /**
     * 起始房号
     */
    private String roomNoStart;
    /**
     * 楼层数
     */
    private String floorCount;
    /**
     * 每层户数
     */
    private String floorHouseNum;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        this.Id = id;
    }

    public String getRoomNoStart() {
        return roomNoStart;
    }

    public void setRoomNoStart(String roomNoStart) {
        this.roomNoStart = roomNoStart;
    }

    public String getFloorCount() {
        return floorCount;
    }

    public void setFloorCount(String floorCount) {
        this.floorCount = floorCount;
    }

    public String getFloorHouseNum() {
        return floorHouseNum;
    }

    public void setFloorHouseNum(String floorHouseNum) {
        this.floorHouseNum = floorHouseNum;
    }
}
