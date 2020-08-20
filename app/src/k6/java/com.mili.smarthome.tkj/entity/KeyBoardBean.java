package com.mili.smarthome.tkj.entity;

public class KeyBoardBean {

    //按键Id
    private String kId;
    //按键名称
    private String name;
    //按键图片Id
    private int resId;



    public KeyBoardBean(String kId, String name) {
        this.kId = kId;
        this.name = name;
    }

    public KeyBoardBean(String kId, String name, int resId) {
        this.kId = kId;
        this.name = name;
        this.resId = resId;
    }

    public String getkId() {
        return kId;
    }

    public void setkId(String kId) {
        this.kId = kId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
