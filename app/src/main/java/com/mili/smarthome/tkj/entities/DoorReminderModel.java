package com.mili.smarthome.tkj.entities;

import io.realm.RealmObject;

public class DoorReminderModel extends RealmObject {
    /**
     * 身份识别类型: 0：房号 1：卡号 2：人员ID
     */
    private int flagType;
    /**
     * 身份识别ID: 如房号
     */
    private String flagID;
    /**
     * TTS语音播放内容
     */
    private String voiceText;
    /**
     * 生效时间: 默认值0
     */
    private int startTime;
    /**
     * 失效时间: 默认值0
     */
    private int endTime;

    public int getFlagType() {
        return flagType;
    }

    public void setFlagType(int flagType) {
        this.flagType = flagType;
    }

    public String getFlagID() {
        return flagID;
    }

    public void setFlagID(String flagID) {
        this.flagID = flagID;
    }

    public String getVoiceText() {
        return voiceText;
    }

    public void setVoiceText(String voiceText) {
        this.voiceText = voiceText;
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
}
