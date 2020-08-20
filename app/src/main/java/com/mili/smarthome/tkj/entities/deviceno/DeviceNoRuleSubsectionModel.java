package com.mili.smarthome.tkj.entities.deviceno;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * 上位机下发设备编号规则描述符
 */

public class DeviceNoRuleSubsectionModel extends RealmObject {

    private int subCount;
    private String subsection;
    @PrimaryKey
    private int Id;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getSubCount() {
        return subCount;
    }

    public void setSubCount(int SubCount) {
        subCount = SubCount;
    }

    public String getSubsection() {
        return subsection;
    }

    public void setSubsection(String Subsection) {
        subsection = Subsection;
    }

}
