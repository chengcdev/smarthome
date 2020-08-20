package com.example.authrolibrary.entity;

public class AutuParamEntity {

    private String appKey;

    private String appSecret;

    private String rsa;

    //设备类型
    private String deviceType;

    //协议版本号
    private String agreementVer;

    //设备版本号
    private String deviceVersion;

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getRsa() {
        return rsa;
    }

    public void setRsa(String rsa) {
        this.rsa = rsa;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getAgreementVer() {
        return agreementVer;
    }

    public void setAgreementVer(String agreementVer) {
        this.agreementVer = agreementVer;
    }

    public String getDeviceVersion() {
        return deviceVersion;
    }

    public void setDeviceVersion(String deviceVersion) {
        this.deviceVersion = deviceVersion;
    }
}
