package com.android.provider;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.Common;
import com.mili.smarthome.tkj.dao.param.NetworkParamDao;

public class NetworkHelp implements Parcelable {
    /**
     * IP
     */
    public int Ip;
    /**
     * 子网掩码
     */
    public int SubNet;
    /**
     * 默认网关
     */
    public int DefaultGateway;
    /**
     * 中心服务器IP
     */
    public int CenterIP;
    /**
     * 管理员机IP
     */
    public int ManagerIP;
    /**
     * 流媒体服务器
     */
    private int MediaServer;
    /**
     * 本机mac
     */
    private String LocalMac;
    /**
     * 人脸服务器Ip
     */
    private int FaceIp;
    /**
     * 电梯控制器IP
     */
    private int ElevatorIp;
    /**
     * DNS1服务器IP
     */
    public int DNS1;
    /**
     * DNS2服务器IP 预留上网用
     */
    public int DNS2;


    /**
     * 网络类型
     * 0 以太网络
     * 1 WIFI网络
     * 2 其他
     */
    public int NetType;

    public static int NET_TYPE_ETHER = 0;

    public static int NET_TYPE_WIFI = 1;

    public static int NET_TYPE_OTHER = 2;


    public NetworkHelp() {
        super();
        //设置mac
//        setLocalMac(EthernetUtils.getMacAddress());
        Getvalues();
    }

    /**
     * 取所有的值
     */
    public void Getvalues() {
        Ip = getIp();
        SubNet = getSubNet();
        DefaultGateway = getDefaultGateway();
        CenterIP = getCenterIP();
        LocalMac = getLocalMac();
        NetType = getNetType();
        ManagerIP = getManagerIP();
        MediaServer = getMediaServer();
        FaceIp =
        ElevatorIp = getElevatorIp();
        DNS1 = getDNS1();
        DNS2 = getDNS2();
    }

    public NetworkHelp(Parcel source) {
        if (source != null) {
            Ip = source.readInt();
            SubNet = source.readInt();
            DefaultGateway = source.readInt();
            CenterIP = source.readInt();
            MediaServer = source.readInt();
            LocalMac = source.readString();
            ElevatorIp = source.readInt();
            NetType = source.readInt();
            DNS1 = source.readInt();
            DNS2 = source.readInt();
        }
    }

    /**
     * 取IP
     */
    public int getIp() {
        Ip = Common.ipToint(NetworkParamDao.getLocalIp());
        return Ip;
    }

    /**
     * 保存IP
     */
    public void setIp(final int iP) {
        Ip = iP;
        NetworkParamDao.setLocalIp(Common.intToIP(iP));
    }

    /**
     * 取子网掩码
     */
    public int getSubNet() {
        SubNet = Common.ipToint(NetworkParamDao.getSubNet());
        return SubNet;
    }

    /**
     * 保存子网掩码
     */
    public void setSubNet(final int subNet) {
        SubNet = subNet;
        NetworkParamDao.setSubNet(Common.intToIP(subNet));
    }

    /**
     * 取默认网关
     */
    public int getDefaultGateway() {
        DefaultGateway = Common.ipToint(NetworkParamDao.getGateway());
        return DefaultGateway;
    }

    /**
     * 保存默认网关
     */
    public void setDefaultGateway(final int defaultGateway) {
        DefaultGateway = defaultGateway;
        NetworkParamDao.setGateway(Common.intToIP(defaultGateway));
    }


    /**
     * 取中心服务器IP
     */
    public int getCenterIP() {
        CenterIP = Common.ipToint(NetworkParamDao.getCenterIp());
        return CenterIP;
    }

    /**
     * 保存中心服务器IP
     */
    public void setCenterIP(final int centerIP) {
        CenterIP = centerIP;
        NetworkParamDao.setCenterIp(Common.intToIP(centerIP));
    }

    /**
     * 取管理员机IP
     */
    public int getManagerIP() {
        ManagerIP = Common.ipToint(NetworkParamDao.getAdminIp());
        return ManagerIP;
    }

    /**
     * 保存管理员机IP
     */
    public void setManagerIP(final int managerIP) {
        ManagerIP = managerIP;
        NetworkParamDao.setAdminIp(Common.intToIP(managerIP));
    }

    /**
     * 取流媒体服务器IP
     */
    public int getMediaServer() {
        MediaServer = Common.ipToint(NetworkParamDao.getMediaIp());
        return MediaServer;
    }

    /**
     * 保存流媒体服务器IP
     */
    public void setMediaServer(final int mediaServer) {
        MediaServer = mediaServer;
        NetworkParamDao.setMediaIp(Common.intToIP(mediaServer));
    }


    /**
     * 获取人脸服务器IP
     */
    public int getFaceIp() {
        FaceIp = Common.ipToint(NetworkParamDao.getFaceIp());
        return FaceIp;
    }

    /**
     * 设置人脸服务器IP
     */
    public void setFaceIp(final int faceIp) {
        FaceIp = faceIp;
        NetworkParamDao.setFaceIp(Common.intToIP(faceIp));
    }

    /**
     * 获取电梯控制器
     */
    public int getElevatorIp() {
        ElevatorIp = Common.ipToint(NetworkParamDao.getElevatorIp());
        return ElevatorIp;
    }

    /**
     * 保存电梯控制器
     */
    public void setElevatorIp(final int elevIp) {
        ElevatorIp = elevIp;
        NetworkParamDao.setElevatorIp(Common.intToIP(ElevatorIp));
    }

    /**
     * 获取本机mac
     */
    public String getLocalMac() {
        String mac = NetworkParamDao.getMac();
        if (mac != null) {
            LocalMac = mac.replace(":", "");
        }
        return LocalMac;
    }

    /**
     * 保存本机mac
     */
    public void setLocalMac(final String localMac) {
        LocalMac = localMac;
        NetworkParamDao.setMac(localMac);
    }

    /**
     * 网络类型
     */
    public int getNetType() {
        NetType = NetworkParamDao.getNetWorkType();
        return NetType;
    }

    /**
     * 保存网络类型
     */
    public void setNetType(final int netType) {
        NetType = netType;
        NetworkParamDao.setNetWorkType(netType);
    }

    /**
     * 取DNS1
     */
    public int getDNS1() {
        String dns1 = NetworkParamDao.getDNS1();
        DNS1 = Common.ipToint(dns1);
        return DNS1;
    }

    /**
     * 保存DNS1
     */
    public boolean setDNS1(int dNS1) {
		DNS1 = dNS1;
        NetworkParamDao.setDNS1(Common.intToIP(dNS1));
        return true;
    }

    /**
     * 取DNS2
     */
    public int getDNS2() {
        String dns2 = NetworkParamDao.getDNS2();
        DNS2 = Common.ipToint(dns2);
        return DNS2;
    }

    /**
     * 保存DNS2
     */
    public boolean setDNS2(int dNS2) {
		DNS2 = dNS2;
        NetworkParamDao.setDNS2(Common.intToIP(dNS2));
        return true;
    }


    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断IP字符串格式是否正确
     */
    public static int ipTointright(String strIp) {
        int[] ip = new int[4];
        // 先找到IP地址字符串中.的位置
        int position1 = strIp.indexOf(".");
        if (position1 == -1) {
            return 1;
        } else {
            int position2 = strIp.indexOf(".", position1 + 1);
            if (position2 == -1) {
                return 1;
            } else {
                int position3 = strIp.indexOf(".", position2 + 1);
                if (position3 == -1) {
                    return 1;
                } else {
                    if (!isInteger(strIp.substring(0, position1)))
                        return 1;
                    ip[0] = Integer.parseInt(strIp.substring(0, position1));
                    if (!isInteger(strIp.substring(position1 + 1, position2)))
                        return 1;
                    ip[1] = Integer.parseInt(strIp.substring(position1 + 1, position2));
                    if (!isInteger(strIp.substring(position2 + 1, position3)))
                        return 1;
                    ip[2] = Integer.parseInt(strIp.substring(position2 + 1, position3));
                    if (!isInteger(strIp.substring(position3 + 1)))
                        return 1;
                    ip[3] = Integer.parseInt(strIp.substring(position3 + 1));
                    if (ip[0] > 255)
                        return 1;
                    if (ip[1] > 255)
                        return 1;
                    if (ip[2] > 255)
                        return 1;
                    if (ip[3] > 255)
                        return 1;
                }
            }
        }
        return 0;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel arg0, int arg1) {
        arg0.writeInt(Ip);
        arg0.writeInt(SubNet);
        arg0.writeInt(DefaultGateway);
        arg0.writeInt(CenterIP);
        arg0.writeInt(MediaServer);
        arg0.writeString(LocalMac);
        arg0.writeInt(ElevatorIp);
        arg0.writeInt(NetType);
        arg0.writeInt(DNS1);
        arg0.writeInt(DNS2);
    }

    public static final Creator<NetworkHelp> CREATOR = new Creator<NetworkHelp>() {

        public NetworkHelp createFromParcel(Parcel source) {
            return new NetworkHelp(source);
        }

        public NetworkHelp[] newArray(int size) {
            return new NetworkHelp[size];
        }

    };
}
