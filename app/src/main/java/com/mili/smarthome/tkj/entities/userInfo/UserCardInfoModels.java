package com.mili.smarthome.tkj.entities.userInfo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * 卡信息
 */

public class UserCardInfoModels extends RealmObject {
    /**
     * 房号
     */
    private String roomNo;
    /**
     * 卡号
     */
    @PrimaryKey
    private String cardNo;
    /**
     * 卡类型
     */
    private int cardType;
    /**
     * 房号状态 0：有房号 1：空房号
     */
    private int roomNoState;
    /**
     * 用户ID
     */
    private String keyID;
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

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
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

    /**
     * 是否是管理员卡
     * @return true是，else否
     */
    public boolean isAdmin() {
        return  (cardType & 0b10) > 0;
    }
}
