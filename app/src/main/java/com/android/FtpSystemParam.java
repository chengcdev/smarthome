package com.android;

import android.text.TextUtils;

import com.mili.smarthome.tkj.dao.param.NetworkParamDao;

public class FtpSystemParam {
    private static String mFtpUserName;                         // FTP用户名
    private static String mFtpPassWord;                         // FTP密码
    private static String mFtpIP;                               // FTPIP地址
    private static String mFtpPort;                             // FTP端口号

    public static void setmFtpUserName(String mFtpUserName) {
        FtpSystemParam.mFtpUserName = mFtpUserName;
        //Log.e("getCenterSystemParam","FtpSystemParam mFtpUserName: "+ FtpSystemParam.mFtpUserName);
    }

    public static String getmFtpUserName() {
        if (mFtpUserName == null || mFtpUserName.length() == 0) {
            return "admin";
        }
        return mFtpUserName;
    }

    public static void setmFtpPassWord(String mFtpPassWord) {
        FtpSystemParam.mFtpPassWord = mFtpPassWord;
        //Log.e("getCenterSystemParam","FtpSystemParam mFtpPassWord: "+ FtpSystemParam.mFtpPassWord);
    }

    public static String getmFtpPassWord() {
        if (mFtpPassWord == null || mFtpPassWord.length() == 0) {
            return "admin";
        }
        return mFtpPassWord;
    }

    public static void setmFtpIP(String mFtpIP) {
        FtpSystemParam.mFtpIP = mFtpIP;
        //Log.e("getCenterSystemParam","FtpSystemParam  FtpIP: "+ FtpSystemParam.mFtpIP);
    }

    public static String getmFtpIP() {
        if (mFtpIP == null || mFtpIP.length() == 0) {
            return NetworkParamDao.getCenterIp();
        }
        return mFtpIP;
    }

    public static void setmFtpPort(String mFtpPort) {
        FtpSystemParam.mFtpPort = mFtpPort;
        //Log.e("getCenterSystemParam","FtpSystemParam mFtpPort: "+ FtpSystemParam.mFtpPort);
    }

    public static String getmFtpPort() {
        if (mFtpPort == null || mFtpPort.length() == 0) {
            return "21";
        }
        return mFtpPort;
    }

    public static int getFtpPort() {
        if (mFtpPort != null && TextUtils.isDigitsOnly(mFtpPort)) {
            return Integer.parseInt(mFtpPort);
        }
        return 21;
    }
}
