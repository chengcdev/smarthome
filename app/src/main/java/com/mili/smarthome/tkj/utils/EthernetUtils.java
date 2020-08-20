package com.mili.smarthome.tkj.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.EthernetManager;
import android.net.IpConfiguration;
import android.net.LinkAddress;
import android.net.NetworkInfo;
import android.net.StaticIpConfiguration;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;

import com.mili.smarthome.tkj.app.App;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * 以太网接口
 * Created by zhengxc on 2019/2/26 0026.
 */

public class EthernetUtils {

    private static final String TAG = "EthernetUtils";
    private final static String nullIpInfo = "0.0.0.0";

    /*** 没有网络*/
    public static final int NETWORK_TYPE_INVALID = 0;
    /*** 以太网络*/
    public static final int NETWORK_TYPE_ETHERNET = 1;
    /*** WIFI网络*/
    public static final int NETWORK_TYPE_WIFI = 2;
    /*** 2G网络*/
    public static final int NETWORK_TYPE_2G = 3;
    /*** 3G网络*/
    public static final int NETWORK_TYPE_3G = 4;
    /*** Wap网络*/
    public static final int NETWORK_TYPE_WAP = 5;

    public static class NetParam {
        private String ipAddr;
        private String gateway;
        private String netMask;
        private String dns1;
        private String dns2;

        public void setIpAddr(String ipAddrStr) {
            ipAddr = ipAddrStr;
        }

        public String getIpAddr() {
            return ipAddr;
        }

        public void setGateway(String gatewayStr) {
            gateway = gatewayStr;
        }

        public String getGateway() {
            return gateway;
        }

        public void setNetMask(String netMaskStr) {
            netMask = netMaskStr;
        }

        public String getNetMask() {
            return netMask;
        }

        public void setDns1(String dns1Str) {
            dns1 = dns1Str;
        }

        public String getDns1() {
            return dns1;
        }

        public void setDns2(String dns2Str) {
            dns2 = dns2Str;
        }

        public String getDns2() {
            return dns2;
        }
    }

    /**
     * 将子网掩码转换成ip子网掩码形式，比如输入32输出为255.255.255.255
     * @param prefixLength
     * @return
     */
    public static String interMask2String(int prefixLength) {
        String netMask = null;
        int inetMask = prefixLength;

        int part = inetMask / 8;
        int remainder = inetMask % 8;
        int sum = 0;

        for (int i = 8; i > 8 - remainder; i--) {
            sum = sum + (int) Math.pow(2, i - 1);
        }

        if (part == 0) {
            netMask = sum + ".0.0.0";
        } else if (part == 1) {
            netMask = "255." + sum + ".0.0";
        } else if (part == 2) {
            netMask = "255.255." + sum + ".0";
        } else if (part == 3) {
            netMask = "255.255.255." + sum;
        } else if (part == 4) {
            netMask = "255.255.255.255";
        }

        return netMask;
    }

    /**
     * 将ip子网掩码形式转换成子网掩码，比如输入255.255.255.255输出为32
     * @param maskStr
     * @return
     */
    public static int maskStr2InetMask(String maskStr) {
        StringBuffer sb ;
        String str;
        int inetmask = 0;
        int count = 0;
    	/*
    	 * check the subMask format
    	 */
        Pattern pattern = Pattern.compile("(^((\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.){3}(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])$)|^(\\d|[1-2]\\d|3[0-2])$");
        if (pattern.matcher(maskStr).matches() == false) {
            Log.e(TAG,"subMask is error");
            return 0;
        }

        String[] ipSegment = maskStr.split("\\.");
        for(int n =0; n<ipSegment.length;n++) {
            sb = new StringBuffer(Integer.toBinaryString(Integer.parseInt(ipSegment[n])));
            str = sb.reverse().toString();
            count=0;
            for(int i=0; i<str.length();i++) {
                i=str.indexOf("1",i);
                if(i==-1)
                    break;
                count++;
            }
            inetmask+=count;
        }
        return inetmask;
    }

    /**
     * 判断是否是IP
     * @param value
     * @return
     */
    public static boolean isIpAddress(String value) {
        int start = 0;
        int end = value.indexOf('.');
        int numBlocks = 0;

        while (start < value.length()) {
            if (end == -1) {
                end = value.length();
            }

            try {
                int block = Integer.parseInt(value.substring(start, end));
                if ((block > 255) || (block < 0)) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }

            numBlocks++;

            start = end + 1;
            end = value.indexOf('.', start);
        }
        return numBlocks == 4;
    }

    public static boolean setStaticIpConfiguration(Context context, NetParam netParam) {

        StaticIpConfiguration staticIpConfiguration = new StaticIpConfiguration();
        EthernetManager ethernetManager = (EthernetManager) context.getSystemService(Context.ETHERNET_SERVICE);
        IpConfiguration ipConfiguration;

        if ((staticIpConfiguration == null) || (ethernetManager == null)) {
            return false;
        }

//        ContentResolver contentResolver = context.getContentResolver();
//        Settings.System.putString(contentResolver, Settings.System.ETHERNET_STATIC_IP, netParam.getIpAddr());
//        Settings.System.putString(contentResolver, Settings.System.ETHERNET_STATIC_GATEWAY, netParam.gateway);
//        Settings.System.putString(contentResolver, Settings.System.ETHERNET_STATIC_NETMASK, netParam.getNetMask());
//        Settings.System.putString(contentResolver, Settings.System.ETHERNET_STATIC_DNS1, netParam.getDns1());
//        Settings.System.putString(contentResolver, Settings.System.ETHERNET_STATIC_DNS2, netParam.getDns2());

        try {
            InetAddress inetAddr = InetAddress.getByName(netParam.getIpAddr());
            int prefixLength = maskStr2InetMask(netParam.getNetMask());
            InetAddress gatewayAddr = InetAddress.getByName(netParam.getGateway());
            InetAddress dnsAddr = InetAddress.getByName(netParam.getDns1());

            if (inetAddr.toString().isEmpty() || prefixLength == 0 || gatewayAddr.toString().isEmpty()
                    || dnsAddr.toString().isEmpty()) {
                Log.e(TAG, "ip,mask or dnsAddr is wrong");
                return false;
            }

            String dnsStr2 = netParam.getDns2();
            staticIpConfiguration.ipAddress = new LinkAddress(inetAddr, prefixLength);
            staticIpConfiguration.gateway = gatewayAddr;
            staticIpConfiguration.dnsServers.add(dnsAddr);
//            staticIpConfiguration.domains = netParam.getNetMask();

            if (isIpAddress(netParam.getDns1())) {
                staticIpConfiguration.dnsServers.add(InetAddress.getByName(netParam.getDns1()));
            }
            if (isIpAddress(netParam.getDns2())) {
                staticIpConfiguration.dnsServers.add(InetAddress.getByName(netParam.getDns2()));
            }

            ipConfiguration = new IpConfiguration(IpConfiguration.IpAssignment.STATIC, IpConfiguration.ProxySettings.NONE, staticIpConfiguration, null);
            if (ipConfiguration == null) {
                return false;
            }
            ethernetManager.setConfiguration(ipConfiguration);
        } catch (UnknownHostException e) {
            return false;
        }
        return true;
    }

    public static boolean setDhcpIpConfiguration(Context context) {
        EthernetManager ethernetManager = (EthernetManager) context.getSystemService(Context.ETHERNET_SERVICE);
        if (ethernetManager == null) {
            return false;
        }
        ethernetManager.setConfiguration(new IpConfiguration(IpConfiguration.IpAssignment.DHCP, IpConfiguration.ProxySettings.NONE, null, null));
        return true;
    }

    public static NetParam getEthInfoFromDhcp(){
        NetParam netParam = new NetParam();
        String tempIpInfo;
        String iface = "eth0";

        tempIpInfo = SystemProperties.get("dhcp."+ iface +".ipaddress");

        if ((tempIpInfo != null) && (!tempIpInfo.equals("")) ){
            netParam.setIpAddr(tempIpInfo);
        } else {
            netParam.setIpAddr(nullIpInfo);
        }

        tempIpInfo = SystemProperties.get("dhcp."+ iface +".mask");
        if ((tempIpInfo != null) && (!tempIpInfo.equals("")) ){
            netParam.setNetMask(tempIpInfo);
        } else {
            netParam.setNetMask(nullIpInfo);
        }

        tempIpInfo = SystemProperties.get("dhcp."+ iface +".gateway");
        if ((tempIpInfo != null) && (!tempIpInfo.equals(""))){
            netParam.setGateway(tempIpInfo);
        } else {
            netParam.setGateway(nullIpInfo);
        }

        tempIpInfo = SystemProperties.get("dhcp."+ iface +".dns1");
        if ((tempIpInfo != null) && (!tempIpInfo.equals(""))){
            netParam.setDns1(tempIpInfo);
        } else {
            netParam.setDns1(nullIpInfo);
        }

        tempIpInfo = SystemProperties.get("dhcp."+ iface +".dns2");
        if ((tempIpInfo != null) && (!tempIpInfo.equals(""))){
            netParam.setDns2(tempIpInfo);
        } else {
            netParam.setDns2(nullIpInfo);
        }

        return netParam;
    }

    public static int getEthUseDhcpOrStaticIp(Context context) {
        EthernetManager ethernetManager = (EthernetManager) context.getSystemService(Context.ETHERNET_SERVICE);
        if (ethernetManager == null) {
            return 0;
        }

        IpConfiguration.IpAssignment ipAssignment = ethernetManager.getConfiguration().ipAssignment;
        if (ipAssignment == IpConfiguration.IpAssignment.STATIC) {
            return 1;
        } else if (ipAssignment == IpConfiguration.IpAssignment.DHCP) {
            return 2;
        }
        return 0;
    }

    public static NetParam getEthInfoFromStaticIp(Context context) {
        EthernetManager ethernetManager = (EthernetManager) context.getSystemService(Context.ETHERNET_SERVICE);
        if (ethernetManager == null) {
            return null;
        }

        StaticIpConfiguration staticIpConfiguration = ethernetManager.getConfiguration().getStaticIpConfiguration();
        if(staticIpConfiguration == null) {
            return null;
        }

        NetParam netParam = new NetParam();

        LinkAddress ipAddress = staticIpConfiguration.ipAddress;
        InetAddress gateway   = staticIpConfiguration.gateway;
        ArrayList<InetAddress> dnsServers = staticIpConfiguration.dnsServers;

        if( ipAddress != null) {
            netParam.setIpAddr(ipAddress.getAddress().getHostAddress());
            netParam.setNetMask(interMask2String(ipAddress.getPrefixLength()));
        }
        if(gateway != null) {
            netParam.setGateway(gateway.getHostAddress());
        }
        netParam.setDns1(dnsServers.get(0).getHostAddress());

        if(dnsServers.size() > 1) { /* 只保留两个*/
            netParam.setDns2(dnsServers.get(1).getHostAddress());
        }

        return netParam;
    }

    /**
     * 获取Ethernet的MAC地址
     * @return
     */
    public static String getMacAddress() {
        try {
            return loadFileAsString("/sys/class/net/eth0/address").toUpperCase(Locale.ENGLISH).substring(0, 17);
        } catch (IOException e) {
            return null;
        }
    }

    private static String loadFileAsString(String filePath) throws java.io.IOException{
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024]; int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        String mac = fileData.toString();
//        ProviderUtil.setValue(mContext, ProviderUtil.Name.ETHERNET_MAC, mac);
        if (mac == null) {
            mac = "00:00:00:00:00:00";
        }
        return mac;
    }

    /**
     * 获取网络连接状态（网络是否连接）
     * @return
     */
    public static int getNetWorkType() {
        int mNetWorkType = NETWORK_TYPE_INVALID;
        ConnectivityManager manager = (ConnectivityManager) App.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
            String type = networkInfo.getTypeName();
            if (type.equalsIgnoreCase("ETHERNET")) {
                mNetWorkType = NETWORK_TYPE_ETHERNET;
            } else if (type.equalsIgnoreCase("WIFI")) {
                mNetWorkType = NETWORK_TYPE_WIFI;
            } else if (type.equalsIgnoreCase("MOBILE")) {
//                String proxyHost = android.net.Proxy.getDefaultHost();
//                mNetWorkType = TextUtils.isEmpty(proxyHost)
//                        ? (isFastMobileNetwork() ? NETWORK_TYPE_3G : NETWORK_TYPE_2G)
//                        : NETWORK_TYPE_WAP;
            }
        }
        return mNetWorkType;
    }

    /**
     * 系统飞行模式接口
     * @param enabling true:打开飞行模式  false:关闭飞行模式
     */
    public static void setAirplaneModeOn(boolean enabling) {
        // Change the system setting
        Settings.Global.putInt(App.getInstance().getContentResolver(), Settings.Global.AIRPLANE_MODE_ON,
                enabling ? 1 : 0);

        // Post the intent
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", enabling);
        App.getInstance().sendBroadcastAsUser(intent, UserHandle.ALL);
    }

}
