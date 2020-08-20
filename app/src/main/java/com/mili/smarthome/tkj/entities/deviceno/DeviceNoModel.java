package com.mili.smarthome.tkj.entities.deviceno;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DeviceNoModel extends RealmObject {
    @PrimaryKey
    private String Id;
    /**
     * 梯口号长度
     */
    private int stairNoLen;
    /**
     * 房号长度
     */
    private int roomNoLen;
    /**
     * 单元号长度
     */
    private int cellNoLen;
    /**
     * 梯口号
     */
    private String stairNo;
    /**
     * 设备编号
     */
    private String deviceNo;
    /**
     * 启用单元号
     */
    private int useCellNo;
    /**
     * 分段参数
     */
    private int subSection;
    /**
     * 设备类型
     */
    private int deviceType;
    /**
     * 当前界面显示设备号
     */
    private String currentDeviceNo;
    /**
     * 区口号
     */
    private String areaNo;

    public String getAreaNo() {
        return areaNo;
    }

    public void setAreaNo(String areaNo) {
        this.areaNo = areaNo;
    }

    public String getCurrentDeviceNo() {
        return currentDeviceNo;
    }

    public void setCurrentDeviceNo(String currentDeviceNo) {
        this.currentDeviceNo = currentDeviceNo;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        this.Id = id;
    }

    public int getStairNoLen() {
        return stairNoLen;
    }

    public void setStairNoLen(int stairNoLen) {
        this.stairNoLen = stairNoLen;
    }

    public int getRoomNoLen() {
        return roomNoLen;
    }

    public void setRoomNoLen(int roomNoLen) {
        this.roomNoLen = roomNoLen;
    }

    public int getCellNoLen() {
        return cellNoLen;
    }

    public void setCellNoLen(int cellNoLen) {
        this.cellNoLen = cellNoLen;
    }

    public String getStairNo() {
        return stairNo;
    }

    public void setStairNo(String stairNo) {
        this.stairNo = stairNo;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public int getUseCellNo() {
        return useCellNo;
    }

    public void setUseCellNo(int useCellNo) {
        this.useCellNo = useCellNo;
    }

    public int getSubSection() {
        return subSection;
    }

    public void setSubSection(int subSection) {
        this.subSection = subSection;
    }
}
