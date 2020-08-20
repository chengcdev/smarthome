package com.android.main;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.CommStorePathDef;
import com.android.Common;
import com.android.IntentDef;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.app.AppPreferences;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.utils.ExternalMemoryUtils;
import com.mili.smarthome.tkj.utils.FileUtils;
import com.mili.smarthome.tkj.utils.FtpUtils;

import java.io.File;

import static com.android.CommStorePathDef.EX_MULTIMEDIA_DIR_PATH;


public class MultimediaLogic extends ServiceLogic implements IntentDef.OnNetCommDataReportListener {

    private static final String TAG = "MultimediaLogic";

    private static final int MEDIA_DEFAULT  = 0;		//缺省模式
    private static final int MEDIA_VIDEO    = 1;		//视频
    private static final int MEDIA_PHOTO    = 2;		//图片
    private static final int MEDIA_AREALOGO	= 3;		//小区logo

    private Context mContext = null;
    private MainJni mMainJni = null;
    private boolean downloadFlag = false;

    public MultimediaLogic(String action) {
        super(action);
        MainJni.setmMultimediaListener(this);
    }

    public void MultimediaLogicStart(Context context, MainJni mainJni) {
        mContext = context;
        mMainJni = mainJni;
    }

    @Override
    public void OnDataReport(String action, int type, byte[] data) {

        if (!action.equals(IntentDef.MODULE_MULTIMEDIA)) {
            return;
        }

//        LogUtils.e(TAG + " OnDataReport type : " + type);

        switch (type) {
            case IntentDef.PubIntentTypeE.Multimedia_Set_Media_Type:    // 设置媒体视频广告播放


                int state = Common.bytes2int(data, 0);   /*1、启用播放 0、禁止播放*/
//                LogUtils.e(TAG + " state " + state);
                //保存状态
                AppPreferences.setMediaPlay(state);
                App.getInstance().sendBroadcast(new Intent(Const.ActionId.ACTION_MULTI_MEDIA));

                break;

            case IntentDef.PubIntentTypeE.Multimedia_DownLoad:  // 多媒体ftp下载
                MultimediaDownLoad(data);
                break;

            case IntentDef.PubIntentTypeE.Multimedia_Del:  // 多媒体删除
                MultimediaDel(data);
                break;

            default:
                break;
        }
    }

    private boolean MultimediaDel(byte[] data) {
        int filetype = Common.bytes2int(data, 0);   //文件类型
        int mediaID = Common.bytes2int(data, 4);   //媒体ID

        byte[] mediaFtpFileNameByte = new byte[64];
        System.arraycopy(data, 8, mediaFtpFileNameByte, 0, 64);
        String mediaFtpFileName = Common.byteToString(mediaFtpFileNameByte);    // Ftp服务器上传下载时文件名称(带后缀名)

        byte[] mediaAliasNameByte = new byte[100];
        System.arraycopy(data, 8+64, mediaAliasNameByte, 0, 100);
        String mediaAliasName = Common.byteToString(mediaAliasNameByte);    // 本地显示文件名称，别名(不带后缀名)

        //解析后缀名//文件格式
        String strFileType = null;
        if (mediaFtpFileName.contains(".")) {
            String [] strFileTypeSplit = mediaFtpFileName.split("\\.");
            if (strFileTypeSplit.length > 1) {
                strFileType = strFileTypeSplit[1];
            }
        }

        if (strFileType == null) {
            return false;
        }

        String localFileName;

        if ( filetype == 0 ) {  // 视频
            if (Common.hasExternalSdCard()) {
                localFileName = CommStorePathDef.EX_MULTIMEDIA_VIDEO_DIR_PATH + "/" + mediaID + "_" + mediaAliasName + "." + strFileType;
                boolean isExists = FileUtils.existsFile(localFileName);
                if (!isExists) {
                    localFileName = CommStorePathDef.MULTIMEDIA_VIDEO_DIR_PATH + "/" + mediaID + "_" + mediaAliasName + "." + strFileType;
                }
            } else {
                localFileName = CommStorePathDef.MULTIMEDIA_VIDEO_DIR_PATH + "/" + mediaID + "_" + mediaAliasName + "." + strFileType;
            }
        } else {
            localFileName = CommStorePathDef.MULTIMEDIA_PHOTO_DIR_PATH + "/" + mediaID + "_" + mediaAliasName + "." + strFileType;
        }

        Log.e(TAG, "del localFileName = " + localFileName);

        boolean ret = FileUtils.deleteFile(localFileName);
        if (ret) {
            mMainJni.multimediaDelResult(1);
        } else {
            mMainJni.multimediaDelResult(0);
        }

        return true;
    }

    private boolean MultimediaDownLoad(byte[] data) {
        int mediaID = Common.bytes2int(data, 0);   //媒体ID
        int mediaFileSize = Common.bytes2int(data, 4);  //文件大小，单位kB

        byte[] mediaFtpFileNameByte = new byte[64];
        System.arraycopy(data, 8, mediaFtpFileNameByte, 0, 64);
        String mediaFtpFileName = Common.byteToString(mediaFtpFileNameByte);    // Ftp服务器上传下载时文件名称(带后缀名)

        byte[] mediaAliasNameByte = new byte[100];
        System.arraycopy(data, 8+64, mediaAliasNameByte, 0, 100);
        String mediaAliasName = Common.byteToString(mediaAliasNameByte);    // 本地显示文件名称，别名(不带后缀名)

        byte[] mediaFtpUrlByte = new byte[128];
        System.arraycopy(data, 8+64+100, mediaFtpUrlByte, 0, 128);
        String mediaFtpUrl = Common.byteToString(mediaFtpUrlByte);    // FTP目录URL，例如：[ftp://user:password@129.145.22.34:21/upload/]

        byte[] reserverByte = new byte[64];
        System.arraycopy(data, 8+64+100+128, reserverByte, 0, 64);
        String reserver = Common.byteToString(reserverByte);    // 保留(用于文件内容校验，暂不使用)

        int mediaType = Common.bytes2int(data, 8+64+100+128+64);  //文件大小，单位kB


        //解析后缀名//文件格式
        String strFileType = null;
        if (mediaFtpFileName.contains(".")) {
            String [] strFileTypeSplit = mediaFtpFileName.split("\\.");
            if (strFileTypeSplit.length > 1) {
                strFileType = strFileTypeSplit[1];
        }
        }

        if (strFileType == null) {
            mMainJni.multimediaDownloadResult(0);
            return false;
        }

        FileUtils.createDirectory(CommStorePathDef.MULTIMEDIA_DIR_PATH);
        long memorySize = 0;
        String localTempFile = null;
        String localFileName = null;
        String tmpPath = null;

        switch(mediaType) {
            case MEDIA_VIDEO:
                /*sd卡剩余内存空间*/
                if (Common.hasExternalSdCard()) {
                    memorySize = mMainJni.getSystemFreeMemory(1);
                }

                // sd卡空间不够下载到本地，本地空间剩余的要大于512M给系统其他使用
                if ( memorySize <= 0 || memorySize < mediaFileSize/1024 + 1 ) {
                    memorySize = mMainJni.getSystemFreeMemory(0);
                    if ( memorySize > mediaFileSize/1024 + 512 ) {
                        localFileName = CommStorePathDef.MULTIMEDIA_VIDEO_DIR_PATH + "/" + mediaID + "_" + mediaAliasName + "." + strFileType;
                        tmpPath = CommStorePathDef.MULTIMEDIA_DIR_TMP_PATH;
                        FileUtils.createDirectory(CommStorePathDef.MULTIMEDIA_VIDEO_DIR_PATH);
                    } else {
                        mMainJni.multimediaDownloadResult(0);
                        Log.e(TAG, "memory is full");
                        return false;
                    }

                } else {
                    localFileName = CommStorePathDef.EX_MULTIMEDIA_VIDEO_DIR_PATH + "/" + mediaID + "_" + mediaAliasName + "." + strFileType;
                    tmpPath = CommStorePathDef.EX_MULTIMEDIA_DIR_TMP_PATH;
                    FileUtils.createDirectory(CommStorePathDef.EX_MULTIMEDIA_DIR_PATH);
                    FileUtils.createDirectory(CommStorePathDef.EX_MULTIMEDIA_VIDEO_DIR_PATH);
                }
                break;

            case MEDIA_PHOTO:
                localFileName = CommStorePathDef.MULTIMEDIA_PHOTO_DIR_PATH + "/" + mediaID + "_" + mediaAliasName + "." + strFileType;
                tmpPath = CommStorePathDef.MULTIMEDIA_DIR_TMP_PATH;
                FileUtils.createDirectory(CommStorePathDef.MULTIMEDIA_PHOTO_DIR_PATH);
                break;

            case MEDIA_AREALOGO:
                localFileName = CommStorePathDef.LOGO_DIR_PATH + "/" + mediaAliasName + "." + strFileType;
                tmpPath = CommStorePathDef.MULTIMEDIA_DIR_TMP_PATH;
                break;

            default:
                mMainJni.multimediaDownloadResult(0);
                return false;
        }

        //创建临时目录
        FileUtils.createDirectory(tmpPath);
        localTempFile = tmpPath + "/" + mediaFtpFileName;

        Log.e(TAG, "mediaFtpUrl = " + mediaFtpUrl);
        Log.e(TAG, "mediaFtpFileName = " + mediaFtpFileName);
        Log.e(TAG, "localTempFile = " + localTempFile);
        Log.e(TAG, "localFileName = " + localFileName);

        String dataStr = mediaFtpUrl.substring(6, mediaFtpUrl.length());
        String useNmae = dataStr.substring(0, dataStr.indexOf(":"));

        dataStr = dataStr.substring(useNmae.length() + 1, dataStr.length());
        String passwoed = dataStr.substring(0, dataStr.indexOf("@"));

        dataStr = dataStr.substring(passwoed.length() + 1, dataStr.length());
        String ip = dataStr.substring(0, dataStr.indexOf(":"));

        dataStr = dataStr.substring(ip.length() + 1, dataStr.length());
        String portStr = dataStr.substring(0, dataStr.indexOf("/"));
        int port = Integer.parseInt(portStr);

        dataStr = dataStr.substring(portStr.length(), dataStr.length());
        String filePath = dataStr.substring(0, dataStr.length());

        Log.e(TAG, "useNmae = " + useNmae);
        Log.e(TAG, "passwoed = " + passwoed);
        Log.e(TAG, "ip = " + ip);
        Log.e(TAG, "portStr = " + portStr);
        Log.e(TAG, "port = " + port);
        Log.e(TAG, "filePath = " + filePath);

        if (null == localFileName || null == localTempFile) {
            mMainJni.multimediaDownloadResult(0);
            return false;
        }

        FtpDownloadParam ftpDownloadParam = new FtpDownloadParam();
        ftpDownloadParam.ftpUrl = ip;
        ftpDownloadParam.ftpPort = port;
        ftpDownloadParam.userName = useNmae;
        ftpDownloadParam.userPassword = passwoed;
        ftpDownloadParam.downloadLocalFilePath = localTempFile;
        ftpDownloadParam.downloadFtpFileName = mediaFtpFileName;
        ftpDownloadParam.ftpFilePath = filePath;
        ftpDownloadParam.localFileName = localFileName;
        ftpDownloadParam.mediaType = mediaType;

        if (!downloadFlag) {
            downloadFlag = true;
            FtpDownloadThread ftpDownloadThread = new FtpDownloadThread(ftpDownloadParam);
            new Thread(ftpDownloadThread).start();
        } else {
            Log.e(TAG, "ftp is busy!!!");
            mMainJni.multimediaDownloadResult(0);
        }
        return true;
    }

    private class FtpDownloadParam {
        private String ftpUrl;
        private int ftpPort;
        private String userName;
        private String userPassword;

        private String downloadLocalFilePath;
        private String downloadFtpFileName;
        private String ftpFilePath;

        private String localFileName;
        private int mediaType;
    }

    /**
     * ftp下载线程
     */
    private class FtpDownloadThread implements Runnable {
        private FtpDownloadParam param;

        public FtpDownloadThread(FtpDownloadParam ftpDownloadParam) {
            param = ftpDownloadParam;
        }

        public void run() {

            boolean ret;
            Log.e(TAG, "ftp downLoadFile start!!!");
            ret = FtpUtils.getInstance().initFTPSetting(param.ftpUrl, param.ftpPort, param.userName, param.userPassword);
            if (ret) {
                ret = FtpUtils.getInstance().downLoadFile(param.downloadLocalFilePath, param.downloadFtpFileName, param.ftpFilePath);
                if (ret) {
                    if (param.mediaType == MEDIA_AREALOGO) {
                        // 删除之前的logo
                        FileUtils.deleteDirWihtFile(new File(CommStorePathDef.USERDATA_PATH));
                    }

                    Log.e(TAG, "param.downloadLocalFilePath = " + param.downloadLocalFilePath);
                    Log.e(TAG, "param.localFileName = " + param.localFileName);
                    boolean flag = Common.moveFileToFile(param.downloadLocalFilePath, param.localFileName);
                    if (flag) {
                        Log.e(TAG, "downLoadFile ok!!!");
                        mMainJni.multimediaDownloadResult(1);
                        downloadFlag = false;
                        return;
                    }
                }
            }

            Log.e(TAG, "initFTPSetting error!!!");
            //删除下载到一半的数据
            FileUtils.deleteFile(param.downloadLocalFilePath);
            mMainJni.multimediaDownloadResult(0);
            downloadFlag = false;
        }
    }

}

