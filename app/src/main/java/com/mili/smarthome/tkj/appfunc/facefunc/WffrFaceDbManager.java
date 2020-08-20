package com.mili.smarthome.tkj.appfunc.facefunc;

import android.content.Context;
import android.os.SystemClock;

import com.android.FtpSystemParam;
import com.android.main.FaceFtpLogic;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.app.AppPreferences;
import com.mili.smarthome.tkj.face.wffr.WffrUtils;
import com.mili.smarthome.tkj.utils.FtpUtils;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.ThreadUtils;

import java.io.File;
import java.util.concurrent.Executors;

/**
 * EI人脸数据库文件备份
 */
public class WffrFaceDbManager {

    /**
     * 上传人脸数据库文件
     */
    public static boolean upload(Context context) {
        long startTime = SystemClock.uptimeMillis();
        // 等待人脸注册结束
        while (FaceFtpLogic.mEnrolling) {
            ThreadUtils.sleep(100);
        }
        String dbDirPath = WffrUtils.getDbDirPath(context);
        String dbFileName = WffrUtils.DB_NAME;
        File dbFile = new File(dbDirPath, dbFileName);
        // 判断人脸数据库文件是否存在
        if (!dbFile.exists()) {
            return true;
        }
        long newSize = dbFile.length();
        long backupSize = AppPreferences.getWffrDbSize();
        // 比较上一次备份到FTP的大小
        if (newSize == backupSize) {
            return true;
        }
        // FTP上传
        LogUtils.d("wffr db--->>>upload...");

        String ftpUserName = FtpSystemParam.getmFtpUserName();
        String ftpPassword = FtpSystemParam.getmFtpPassWord();
        String ftpIP = FtpSystemParam.getmFtpIP();
        int ftpPort = FtpSystemParam.getFtpPort();

        if (ftpIP == null || ftpIP.length() == 0 || ftpPort == 0) {
            return false;
        }

        LogUtils.d("wffr db--->>>upload param: ip=%s, port=%d, username=%s, password=%s", ftpIP, ftpPort, ftpUserName, ftpPassword);

        boolean result;
        result = FtpUtils.getInstance().initFTPSetting(ftpIP, ftpPort, ftpUserName, ftpPassword);
        if (result) {
            FullDeviceNo fullDeviceNo = new FullDeviceNo(context);
            String ftpFilePath = "/frface/" + fullDeviceNo.getDeviceNo();
            String dbFilePath = dbFile.getAbsolutePath();

            LogUtils.d("wffr db--->>>upload param: dbFilePath=%s, dbFileName=%s, targetFilePath=%s", dbFilePath, dbFileName, ftpFilePath);

            result = FtpUtils.getInstance().uploadFile(dbFilePath, dbFileName, ftpFilePath);
            if (result) {
                //上传成功，保存人脸数据库文件的大小
                AppPreferences.setWffrDbSize(newSize);
            }
        }
        long spendTime = SystemClock.uptimeMillis() - startTime;
        LogUtils.d("wffr db--->>>upload size=%d, result=%b(%dms)", newSize, result, spendTime);
        return result;
    }

    /**
     * 异步上传人脸数据库文件
     */
    public static void uploadAsync(final Context context) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                upload(context);
            }
        });
    }

    /**
     * 下载并覆盖人脸数据库文件
     */
    public static boolean restore(Context context) {
        long startTime = SystemClock.uptimeMillis();
        String dbDirPath = WffrUtils.getDbDirPath(context);
        String dbFileName = WffrUtils.DB_NAME;
        File dbFile = new File(dbDirPath, dbFileName);
        if (dbFile.exists() && !dbFile.delete()) {
            // 无法删除本地人脸数据库文件
            return false;
        }
        LogUtils.d("wffr db--->>>download...");

        String ftpUserName = FtpSystemParam.getmFtpUserName();
        String ftpPassword = FtpSystemParam.getmFtpPassWord();
        String ftpIP = FtpSystemParam.getmFtpIP();
        int ftpPort = FtpSystemParam.getFtpPort();

        if (ftpIP == null || ftpIP.length() == 0 || ftpPort == 0) {
            return false;
        }

        LogUtils.d("wffr db--->>>download param: ip=%s, port=%d, username=%s, password=%s", ftpIP, ftpPort, ftpUserName, ftpPassword);

        long downloadSize = 0;
        boolean result;
        result = FtpUtils.getInstance().initFTPSetting(ftpIP, ftpPort, ftpUserName, ftpPassword);
        if (result) {
            FullDeviceNo fullDeviceNo = new FullDeviceNo(context);
            String ftpFilePath = "/frface/" + fullDeviceNo.getDeviceNo();
            String dbFilePath = dbFile.getAbsolutePath();

            LogUtils.d("wffr db--->>>download param: dbFilePath=%s, dbFileName=%s, ftpFilePath=%s", dbFilePath, dbFileName, ftpFilePath);

            result = FtpUtils.getInstance().downLoadFile(dbFilePath, dbFileName, ftpFilePath);
            if (result) {
                //下载成功，保存人脸数据库文件的大小
                dbFile = new File(dbDirPath, dbFileName);
                downloadSize = dbFile.length();
                AppPreferences.setWffrDbSize(dbFile.length());
            }
        }
        long spendTime = SystemClock.uptimeMillis() - startTime;
        LogUtils.d("wffr db--->>>download size=%d, result=%b(%dms)", downloadSize, result, spendTime);
        return result;
    }
}
