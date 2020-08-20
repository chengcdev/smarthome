package com.mili.smarthome.tkj.main.entity;

public class InputBean extends CommonBean{

    private int inputType;

    private String inputKey;

    public int getInputType() {
        return inputType;
    }

    public void setInputType(int inputType) {
        this.inputType = inputType;
    }

    public String getInputKey() {
        return inputKey;
    }

    public void setInputKey(String inputKey) {
        this.inputKey = inputKey;
    }

    public InputBean(int inputType) {
        this.inputType = inputType;
    }
}
