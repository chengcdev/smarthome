package com.mili.smarthome.tkj.main.entity;

public class NetWorkSettingEntity {

    private String id;

    private int nameId;

    private String value;

    public NetWorkSettingEntity(String id, int nameId, String value) {
        this.id = id;
        this.nameId = nameId;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getName() {
        return nameId;
    }

    public void setName(int name) {
        this.nameId = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
