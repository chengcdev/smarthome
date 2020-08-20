package com.mili.smarthome.tkj.entities;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * 人脸信息
 */
public class FaceWffrModel extends RealmObject {

    private String firstName;
    private String lastName;
    private String cardNo;
    private int index;

    @Ignore
    private float confidence;//相似度
    @Ignore
    private String snapPath;//抓拍路径

    /**
     * 房号状态 0：有房号 1：空房号
     */
    private int roomNoState;
    /**
     * 用户ID
     */
    private String keyID;
    /**
     * 扩展URL
     */
    private String exturl;
    /**
     * 人脸属性
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


    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCardNo() {
        return cardNo;
    }

    public int getIndex() {
        return index;
    }

    public float getConfidence() {
        return confidence;
    }

    public String getSnapPath() {
        return snapPath;
    }

    public FaceWffrModel setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public FaceWffrModel setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public FaceWffrModel setCardNo(String cardNo) {
        this.cardNo = cardNo;
        return this;
    }

    public FaceWffrModel setIndex(int index) {
        this.index = index;
        return this;
    }

    public FaceWffrModel setConfidence(float confidence) {
        this.confidence = confidence;
        return this;
    }

    public FaceWffrModel setSnapPath(String snapPath) {
        this.snapPath = snapPath;
        return this;
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

    public String getExturl() {
        return exturl;
    }

    public void setExturl(String exturl) {
        this.exturl = exturl;
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
