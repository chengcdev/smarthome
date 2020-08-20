package com.mili.smarthome.tkj.dao.param;

import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.appfunc.facefunc.FacePresenterProxy;
import com.mili.smarthome.tkj.entities.param.NetworkParam;
import com.mili.smarthome.tkj.entities.param.ParamModel;
import com.mili.smarthome.tkj.utils.EthernetUtils;

import java.util.List;

/**
 * 网络参数访问类
 */
public class NetworkParamDao {

    public static final String NETWORK_PARAM = "network_param";

    public static final String KEY_NETWORK_TYPE = "network_type";
    public static final String KEY_NETWORK_MAC = "network_mac";
    public static final String KEY_LOCAL_IP = "local_ip";
    public static final String KEY_SUBNET_IP = "subnet_ip";
    public static final String KEY_GATEWAY_IP = "gateway_ip";
    public static final String KEY_CENTER_IP = "center_ip";
    public static final String KEY_ADMIN_IP = "admin_ip";
    public static final String KEY_MEDIA_IP = "media_ip";
    public static final String KEY_FACE_IP = "face_ip";
    public static final String KEY_ELEVATOR_IP = "elevator_ip";
    public static final String KEY_DNS1 = "dns1";
    public static final String KEY_DNS2 = "dns2";

    /**
     * 网络类型
     */
    public static int getNetWorkType() {
        return ParamDao.queryParamValue(KEY_NETWORK_TYPE, EthernetUtils.getNetWorkType());
    }

    /**
     * 网络类型
     */
    public static void setNetWorkType(int value) {
        ParamDao.saveParam(NETWORK_PARAM, KEY_NETWORK_TYPE, value);
    }

    /**
     * MAC
     */
    public static String getMac() {
        return ParamDao.queryParamValue(KEY_NETWORK_MAC, EthernetUtils.getMacAddress());
    }

    /**
     * MAC
     */
    public static void setMac(String value) {
        ParamDao.saveParam(NETWORK_PARAM, KEY_NETWORK_MAC, value);
    }

    /**
     * 本地IP
     */
    public static String getLocalIp() {
        return ParamDao.queryParamValue(KEY_LOCAL_IP, Const.SetNetWorkId.NET_IP);
    }

    /**
     * 本地IP
     */
    public static void setLocalIp(String value) {
        ParamDao.saveParam(NETWORK_PARAM, KEY_LOCAL_IP, value);
    }

    /**
     * 子网掩码
     */
    public static String getSubNet() {
        return ParamDao.queryParamValue(KEY_SUBNET_IP, Const.SetNetWorkId.NET_MASK);
    }

    /**
     * 子网掩码
     */
    public static void setSubNet(String value) {
        ParamDao.saveParam(NETWORK_PARAM, KEY_SUBNET_IP, value);
    }

    /**
     * 网关
     */
    public static String getGateway() {
        return ParamDao.queryParamValue(KEY_GATEWAY_IP, Const.SetNetWorkId.NET_GATEWAY);
    }

    /**
     * 网关
     */
    public static void setGateway(String value) {
        ParamDao.saveParam(NETWORK_PARAM, KEY_GATEWAY_IP, value);
    }

    /**
     * 中心服务器
     */
    public static String getCenterIp() {
        return ParamDao.queryParamValue(KEY_CENTER_IP, Const.SetNetWorkId.NET_CENTER);
    }

    /**
     * 中心服务器
     */
    public static void setCenterIp(String value) {
        ParamDao.saveParam(NETWORK_PARAM, KEY_CENTER_IP, value);
    }

    /**
     * 管理员机
     */
    public static String getAdminIp() {
        return ParamDao.queryParamValue(KEY_ADMIN_IP, Const.SetNetWorkId.NET_ADMIN);
    }

    /**
     * 管理员机
     */
    public static void setAdminIp(String value) {
        ParamDao.saveParam(NETWORK_PARAM, KEY_ADMIN_IP, value);
    }

    /**
     * 流媒体服务器
     */
    public static String getMediaIp() {
        return ParamDao.queryParamValue(KEY_MEDIA_IP, Const.SetNetWorkId.NET_MEDIA);
    }

    /**
     * 流媒体服务器
     */
    public static void setMediaIp(String value) {
        ParamDao.saveParam(NETWORK_PARAM, KEY_MEDIA_IP, value);
    }

    /**
     * 人脸识别服务器
     */
    public static String getFaceIp() {
        return ParamDao.queryParamValue(KEY_FACE_IP, Const.SetNetWorkId.NET_FACE);
    }

    /**
     * 人脸识别服务器
     */
    public static void setFaceIp(String value) {
        ParamDao.saveParam(NETWORK_PARAM, KEY_FACE_IP, value);
        onFaceIpChanged(value);
    }

    /**
     * 电梯控制器
     */
    public static String getElevatorIp() {
        return ParamDao.queryParamValue(KEY_ELEVATOR_IP, Const.SetNetWorkId.NET_ELEVATOR);
    }

    /**
     * 电梯控制器
     */
    public static void setElevatorIp(String value) {
        ParamDao.saveParam(NETWORK_PARAM, KEY_ELEVATOR_IP, value);
    }

    /**
     * DNS1服务器IP
     */
    public static String getDNS1() {
        return ParamDao.queryParamValue(KEY_DNS1, Const.SetNetWorkId.NET_DNS_1);
    }

    /**
     * DNS1服务器IP
     */
    public static void setDNS1(String value) {
        ParamDao.saveParam(NETWORK_PARAM, KEY_DNS1, value);
    }

    /**
     * DNS2服务器IP
     */
    public static String getDNS2() {
        return ParamDao.queryParamValue(KEY_DNS2, Const.SetNetWorkId.NET_DNS_2);
    }

    /**
     * DNS2服务器IP
     */
    public static void setDNS2(String value) {
        ParamDao.saveParam(NETWORK_PARAM, KEY_DNS2, value);
    }

    public static NetworkParam getNetWorkParam() {
        NetworkParam networkParam = new NetworkParam();
        List<ParamModel> paramList = ParamDao.queryParamListByType(NETWORK_PARAM);
        if (paramList != null) {
            for (ParamModel paramModel : paramList) {
                switch (paramModel.getKey()) {
                    case KEY_LOCAL_IP:
                        networkParam.setLocalIp(paramModel.getValue());
                        break;
                    case KEY_SUBNET_IP:
                        networkParam.setSubNet(paramModel.getValue());
                        break;
                    case KEY_GATEWAY_IP:
                        networkParam.setGateway(paramModel.getValue());
                        break;
                    case KEY_CENTER_IP:
                        networkParam.setCenterIp(paramModel.getValue());
                        break;
                    case KEY_ADMIN_IP:
                        networkParam.setAdminIp(paramModel.getValue());
                        break;
                    case KEY_MEDIA_IP:
                        networkParam.setMediaIp(paramModel.getValue());
                        break;
                    case KEY_FACE_IP:
                        networkParam.setFaceIp(paramModel.getValue());
                        break;
                    case KEY_ELEVATOR_IP:
                        networkParam.setElevatorIp(paramModel.getValue());
                        break;
                    case KEY_DNS1:
                        networkParam.setDNS1(paramModel.getValue());
                        break;
                    case KEY_DNS2:
                        networkParam.setDNS2(paramModel.getValue());
                        break;
                    case KEY_NETWORK_TYPE:
                        networkParam.setNetType(paramModel.getIntValue());
                        break;
                    case KEY_NETWORK_MAC:
                        networkParam.setMac(paramModel.getValue());
                        break;
                }
            }
        }
        return networkParam;
    }

    public static void setNetworkParam(NetworkParam networkParam) {
        ParamDao.saveParamArray(
                new ParamModel().setType(NETWORK_PARAM).setKey(KEY_LOCAL_IP).setValue(networkParam.getLocalIp()),
                new ParamModel().setType(NETWORK_PARAM).setKey(KEY_SUBNET_IP).setValue(networkParam.getSubNet()),
                new ParamModel().setType(NETWORK_PARAM).setKey(KEY_GATEWAY_IP).setValue(networkParam.getGateway()),
                new ParamModel().setType(NETWORK_PARAM).setKey(KEY_CENTER_IP).setValue(networkParam.getCenterIp()),
                new ParamModel().setType(NETWORK_PARAM).setKey(KEY_ADMIN_IP).setValue(networkParam.getAdminIp()),
                new ParamModel().setType(NETWORK_PARAM).setKey(KEY_MEDIA_IP).setValue(networkParam.getMediaIp()),
                new ParamModel().setType(NETWORK_PARAM).setKey(KEY_FACE_IP).setValue(networkParam.getFaceIp()),
                new ParamModel().setType(NETWORK_PARAM).setKey(KEY_ELEVATOR_IP).setValue(networkParam.getElevatorIp()),
                new ParamModel().setType(NETWORK_PARAM).setKey(KEY_DNS1).setValue(networkParam.getDNS1()),
                new ParamModel().setType(NETWORK_PARAM).setKey(KEY_DNS2).setValue(networkParam.getDNS2()),
                new ParamModel().setType(NETWORK_PARAM).setKey(KEY_NETWORK_TYPE).setValue(networkParam.getNetType()),
                new ParamModel().setType(NETWORK_PARAM).setKey(KEY_NETWORK_MAC).setValue(networkParam.getMac())
        );
        onFaceIpChanged(networkParam.getFaceIp());
    }

    private static void onFaceIpChanged(String faceIp) {
        FacePresenterProxy.registerFaceType();
    }
}
