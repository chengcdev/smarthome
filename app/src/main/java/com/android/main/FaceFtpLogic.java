package com.android.main;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.CommStorePathDef;
import com.android.Common;
import com.android.FtpSystemParam;
import com.android.client.MainClient;
import com.android.interf.IFaceListener;
import com.mili.smarthome.tkj.appfunc.service.RestartService;
import com.mili.smarthome.tkj.utils.FileUtils;
import com.mili.smarthome.tkj.utils.FtpUtils;

import java.io.File;


public class FaceFtpLogic {

    private static final String TAG = "FaceFtpLogic";
    private static int mRegCode;                                // 人脸注册码
    private static int mPicCount;                               // 待下载图片数量
    private static String mFaceToken;                           // 人脸ID
    private static String mFtpUserName;                         // FTP用户名
    private static String mFtpPassWord;                         // FTP密码
    private static String mFtpIP;                               // FTPIP地址
    private static int mFtpPort;                                // FTP端口号

    /** 是否正在执行人脸注册 */
    public static boolean mEnrolling = false;

    private IFaceListener mFaceListener;

    public FaceFtpLogic(@NonNull IFaceListener listener) {
        mFaceListener = listener;
    }

    public boolean FaceFtpDownLoad(byte[] data) {
        int dataIndex = 0;

        mRegCode = Common.bytes2int(data, dataIndex);                // 照片下发注册码
        dataIndex += 4;
        mPicCount = Common.bytes2int(data, dataIndex);               // 照片总数
        Log.e(TAG,"mRegCode: "+mRegCode+ "  mPicCount: "+mPicCount);
        if (mPicCount <= 0){
            return false;
        }
        dataIndex += 4;
        byte[] faceToken = new byte[32];
        System.arraycopy(data, dataIndex, faceToken, 0, 32);
        mFaceToken = Common.byteToString(faceToken);

        FtpDownloadThread ftpDownloadThread = new FtpDownloadThread();
        new Thread(ftpDownloadThread).start();
        return true;
    }

    public boolean FaceFtpUpLoad(){
        FtpUploadThread ftpUpLoadThread = new FtpUploadThread();
        new Thread(ftpUpLoadThread).start();
        return true;
    }

    /**
     * ftp下载线程
     */
    private class FtpDownloadThread implements Runnable {

        public void run() {
            // 进行照片注册，反馈结果
            String faceId = mFaceToken;
            String faceDir = CommStorePathDef.FACE_DIR_PATH + "/" + mRegCode;
            int result = faceEnroll(faceId, faceDir);
            MainClient.getInstance().Main_FacePicRegResult(mRegCode, result, mFaceToken);
        }
    }

    /**
     * 人脸注册
     * @param faceId 人脸ID
     * @param faceDir 人脸图片文件夹（可能包含多张图片）
     * @return 注册成功返回1，失败返回0
     */
    private synchronized int faceEnroll(String faceId, String faceDir) {
        mEnrolling = true;
        int result = 0;
        File dir = new File(faceDir);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (RestartService.mRestartFlag) {
                    // 设备将要重启，停止人脸注册
                    break;
                }
                if (mFaceListener.onFaceEnroll(file.getPath(), faceId)) {
                    // 有一张人脸注册成功就算成功
                    result = 1;
                }
            }
        }
        FileUtils.deleteDirWihtFile(dir);
        mEnrolling = false;
        return result;
    }

    /**
     * ftp上传线程
     */
    private class FtpUploadThread implements Runnable {
        public void run() {

            boolean ret;
            mFtpUserName = FtpSystemParam.getmFtpUserName();
            mFtpPassWord = FtpSystemParam.getmFtpPassWord();
            mFtpIP = FtpSystemParam.getmFtpIP();
            try{
                mFtpPort = Integer.parseInt(FtpSystemParam.getmFtpPort());
            }catch (Exception e){
                mFtpPort = 0;
                e.printStackTrace();
            }

            if (mFtpIP == null || mFtpIP.length() == 0 || mFtpPort == 0)
            {
                return;
            }
            ret = FtpUtils.getInstance().initFTPSetting(mFtpIP, mFtpPort, mFtpUserName, mFtpPassWord);
            if (ret) {
                ret = FtpUtils.getInstance().uploadFile("/mnt/sdcard/DCIM/1.jpg", "1.jpg", "/frface/010100007");
                if (ret) {
                    Log.e(TAG, "mSuccessUpLoadFile!!!");
                }
            }
        }
    }
}

