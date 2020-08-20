package com.mili.smarthome.tkj.entities.param;

import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.utils.EthernetUtils;

public class NetworkParam {

    /**
     * 本地IP
     */
    private String localIp = Const.SetNetWorkId.NET_IP;
    /**
     * 子网掩码
     */
    private String subNet = Const.SetNetWorkId.NET_MASK;
    /**
     * 网关
     */
    private String gateway = Const.SetNetWorkId.NET_GATEWAY;
    /**
     * 中心服务器
     */
    private String centerIp = Const.SetNetWorkId.NET_CENTER;
    /**
     * 管理员器
     */
    private String adminIp = Const.SetNetWorkId.NET_ADMIN;
    /**
     * 流媒体服务器
     */
    private String mediaIp = Const.SetNetWorkId.NET_MEDIA;
    /**
     * 人脸识别服务器
     */
    private String faceIp = Const.SetNetWorkId.NET_FACE;
    /**
     * 电梯控制器
     */
    private String elevatorIp = Const.SetNetWorkId.NET_ELEVATOR;
    /**
     * DNS1服务器IP
     */
    private String DNS1 = Const.SetNetWorkId.NET_DNS_1;
    /**
     * DNS2服务器IP 预留上网用
     */
    private String DNS2 = Const.SetNetWorkId.NET_DNS_2;
    /**
     * Mac
     */
    private String mac = EthernetUtils.getMacAddress();
    /**
     * 网络类型
     */
    private int netType = EthernetUtils.getNetWorkType();


    public int getNetType() {
        return netType;
    }

    public void setNetType(int netType) {
        this.netType = netType;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    public String getSubNet() {
        return subNet;
    }

    public void setSubNet(String subNet) {
        this.subNet = subNet;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getCenterIp() {
        return centerIp;
    }

    public void setCenterIp(String centerIp) {
        this.centerIp = centerIp;
    }

    public String getAdminIp() {
        return adminIp;
    }

    public void setAdminIp(String adminIp) {
        this.adminIp = adminIp;
    }

    public String getMediaIp() {
        return mediaIp;
    }

    public void setMediaIp(String mediaIp) {
        this.mediaIp = mediaIp;
    }

    public String getFaceIp() {
        return faceIp;
    }

    public void setFaceIp(String faceIp) {
        this.faceIp = faceIp;
    }

    public String getElevatorIp() {
        return elevatorIp;
    }

    public void setElevatorIp(String elevatorIp) {
        this.elevatorIp = elevatorIp;
    }

    public String getDNS1() {
        return DNS1;
    }

    public void setDNS1(String DNS1) {
        this.DNS1 = DNS1;
    }

    public String getDNS2() {
        return DNS2;
    }

    public void setDNS2(String DNS2) {
        this.DNS2 = DNS2;
    }
}
