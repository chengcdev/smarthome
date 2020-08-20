package com.example.authrolibrary.utils;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by chenrh on 2019/5/18.
 */

public class AuthUtils {

    /**
     * 得到设备标识( MD5(mac+sn+androidid) )
     * @return
     */
    public static String getDeviceId(){
        String device_id = "";
        device_id = getMacAddr() + "|" + getSerialNumber() + "|" + getCpuInfo();
        Log.d("AuthUtils", "device_id=" + MD5Utils.toMD5String(device_id));
        return MD5Utils.toMD5String(device_id);
    }

    /**
     * 得到设备CPU架构信息
     * @return
     */
    public static String getCpuInfo(){
        String cpu_info = "";
        try{
            cpu_info = android.os.Build.CPU_ABI;
        }catch(Exception ex)
        {
        }finally {
            Log.d("AuthUtils", "cpu_info=" + cpu_info);
            return cpu_info;
        }
    }

    /**
     * 得到安卓系统唯一标识
     * @param mContext
     * @return
     */
    private String getAndroidId(Context mContext){
        String android_id = "";
        try {
            android_id = Settings.System.getString(mContext.getContentResolver(), Settings.System.ANDROID_ID);
        }catch(Exception ex)
        {
        }finally {
            Log.d("AuthUtils", "android_id=" + android_id);
            return android_id;
        }
    }

    /**
     * 得到设备序列号
     * @return
     */
    public static String getSerialNumber(){
        String serial_number = "";
        try {
            serial_number = android.os.Build.SERIAL;
        }catch(Exception ex)
        {
        }finally {
            Log.d("AuthUtils", "serial_number=" + serial_number);
            return serial_number;
        }
    }

    /**
     * 得到设备MAC码
     * @return
     */
    public static String getMacAddr(){
        String mac_addr = "";
        try {
            //获得IP地址
            InetAddress ip = null;
            //列举
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface.getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {
                //是否还有元素
                NetworkInterface ni = (NetworkInterface) en_netInterface.nextElement();
                //得到下一个元素
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();
                // 得到一个ip地址的列举
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1)
                        break;
                    else
                        ip = null;
                }

                if (ip != null) {
                    break;
                }
            }
            byte[] b = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            mac_addr = buffer.toString().toUpperCase();
        } catch (Exception e) {

        }finally {
            Log.d("AuthUtils", "mac_addr=" + mac_addr);
            return mac_addr;
        }
    }

    /**
     * 判断设备是否授权合法
     * @param authCode      云端认证后返回的授权许可码
     * @param publicKey    私钥
     * @param appKey        云端分配的appKey
     * @param appSecret     云端分配的appSecret
     * @return
     */
    public static boolean getIsAuth(String authCode, String publicKey, String appKey, String appSecret ){
        boolean isAuth = false;
        try {

//            LogUtils.e("AuthUtils  getIsAuth auth_code=" + authCode );
//            LogUtils.e("AuthUtils  getIsAuth publicKey=" + publicKey );
//            LogUtils.e("AuthUtils  getIsAuth appKey=" + appKey );
//            LogUtils.e("AuthUtils  getIsAuth appSecret=" + appSecret );


            String str1 = getAuthCodeRSA(authCode, publicKey);

//            LogUtils.e("AuthUtils  getAuthCodeRSA str1 : " + str1 );

            String str2 = getAuthCodeMD5(appKey, getDeviceId(), appSecret);

//            LogUtils.e("AuthUtils  getAuthCodeMD5 str2 : " + str2 );

            if(str1.toLowerCase().equals(str2.toLowerCase())){
                isAuth = true;
            }else{
                isAuth = false;
            }
        }catch(Exception ex){

        }finally {
            return isAuth;
        }
    }

    /**
     * 得到校验串(MD5(appKey:deviceId:appSecret))
     * @param appKey    云端分配的appKey
     * @param deviceId      设备标识
     * @param appSecret     云端分配的appSecret
     * @return
     */
    private static String getAuthCodeMD5(String appKey, String deviceId, String appSecret){
        String auth_code = "";
        try {
            String str = appKey + ":" + deviceId + ":" + appSecret;
            auth_code = MD5Utils.toMD5String(str);
        }catch(Exception e){

        }finally {
            Log.d("AuthUtils", "auth_code=" + auth_code);
            return auth_code;
        }
    }

    /**
     * 得到授权许可码RSA解密串
     * @param authCode      云端认证后返回的授权许可码
     * @param publicKey     公钥
     * @return
     */
    private static String getAuthCodeRSA(String authCode, String publicKey){
        String rsa_code = "";
        try{
            rsa_code = RsaUtils.decrypt(authCode, publicKey);
        }catch(Exception e){
            Log.d("AuthUtils", "Exception=" + e.toString());
        }finally {
            return rsa_code;
        }
    }



}
