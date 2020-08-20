package com.mili.smarthome.tkj.entities;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * 蓝牙开门器
 */

public class BlueToothOpenModel extends RealmObject {

    @PrimaryKey
    private String Id;
    /**
     * 设备注册ID
     */
    private String registerId;
    /**
     * 设备注册IDs加密的二维码字符串
     */
    private String qrCodeString;

    public String getQrCodeString() {
        return qrCodeString;
    }

    public void setQrCodeString(String qrCodeString) {
        this.qrCodeString = qrCodeString;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        this.Id = id;
    }

    public String getRegisterId() {
        return registerId;
    }

    public void setRegisterId(String registerId) {
        this.registerId = registerId;
    }
}
