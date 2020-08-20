package com.example.authrolibrary.entity;

public class DeviceParamEntity {

    /**
     * 设备标识
     */
    private String id;
    /**
     * 设备sn
     */
    private String sn;
    /**
     * 设备MAC
     */
    private String mac;
    /**
     * 设备版本
     */
    private String version;
    /**
     * 设备型号
     */
    private String type;
    /**
     * 签名参数
     */
    private String sign;
    /**
     * 协议版本号
     */
    private String ver;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
