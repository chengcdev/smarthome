package com.example.authrolibrary.entity;

public class RequestEntity {

    /**
     * 产品钥匙
     */
    private String appkey;
    /**
     * 设备对象
     */
    private DeviceParamEntity device;
    /**
     * 协议版本号
     */
    private String ver;
    /**
     * 签名参数
     */
    private String sign;

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public DeviceParamEntity getDevice() {
        return device;
    }

    public void setDevice(DeviceParamEntity device) {
        this.device = device;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
