package com.mili.smarthome.tkj.entities;

import io.realm.RealmObject;

/**
 * 信息
 */
public class MessageModel extends RealmObject {

    private String title;    // 信息标题
    private String datetime;  // 时间
    private String textPath; //文本路径
    private int saveDay; //保留天数


    public String getTextPath() {
        return textPath;
    }

    public void setTextPath(String textPath) {
        this.textPath = textPath;
    }


    public String getTitle() {
        return title;
    }


    public String getDateTime() {
        return datetime;
    }


    public void setTitle(String val) {
        title = val;
    }


    public void setDateTime(String val) {
        datetime = val;
    }

    public int getSaveDay() {
        return saveDay;
    }

    public void setSaveDay(int saveDay) {
        this.saveDay = saveDay;
    }
}
