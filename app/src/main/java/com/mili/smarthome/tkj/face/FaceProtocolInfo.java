package com.mili.smarthome.tkj.face;

public class FaceProtocolInfo {
    /**
     * 人脸注册名称
     */
    private String faceFirstName;
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

    public String getFaceFirstName() {
        return faceFirstName;
    }

    public void setFaceFirstName(String faceFirstName) {
        this.faceFirstName = faceFirstName;
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
