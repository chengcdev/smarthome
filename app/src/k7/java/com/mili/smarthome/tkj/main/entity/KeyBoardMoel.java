package com.mili.smarthome.tkj.main.entity;

import java.io.Serializable;

public class KeyBoardMoel implements Serializable {

    private String kId;

    private String name;

    private int type; // 0 主界面 1 密码界面

    private int drawbleId;

    public KeyBoardMoel(String kId, String name, int drawbleId) {
        this.kId = kId;
        this.name = name;
        this.drawbleId = drawbleId;
    }

    public KeyBoardMoel(String kId, String name, int type, int drawbleId) {
        this.kId = kId;
        this.name = name;
        this.type = type;
        this.drawbleId = drawbleId;
    }

    public int getDrawbleId() {
        return drawbleId;
    }

    public void setDrawbleId(int drawbleId) {
        this.drawbleId = drawbleId;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
