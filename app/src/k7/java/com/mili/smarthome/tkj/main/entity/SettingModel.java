package com.mili.smarthome.tkj.main.entity;

public class SettingModel {

    private String kId;

    private String name;

    public SettingModel(String kId, String name) {
        this.kId = kId;
        this.name = name;
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
}

