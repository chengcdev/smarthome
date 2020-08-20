package com.mili.smarthome.tkj.utils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * FTP操作工具类(含上传，下载功能)
 * 2018-10-22: Created by chenrh.
 */
public class FtpUtils {

    private static final int CONNECT_TIMEOUT = 3000;
    private static FtpUtils mInstance = null;
    private FTPClient ftpClient;
    private String ftpUrl;
    private int ftpPort;
    private String userName;
    private String userPassword;

    private FtpUtils() {
        ftpClient = new FTPClient();
    }

    /*
     * 得到类对象实例（因为只能有一个这样的类对象，所以用单例模式）
     */
    public static FtpUtils getInstance() {
        if (mInstance == null) {
            synchronized (FtpUtils.class) {
                if (mInstance == null) {
                    mInstance = new FtpUtils();
                }
            }
        }
        return mInstance;
    }

    /**
     * FTP服务器连接状态
     */
    public boolean isFTPConnected() {
        try {
            return ftpClient.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 设置FTP服务器
     *
     * @param FTPUrl       FTP服务器ip地址
     * @param FTPPort      FTP服务器端口号
     * @param UserName     登陆FTP服务器的账号
     * @param UserPassword 登陆FTP服务器的密码
     * @return true为成功，false为失败
     */
    public boolean initFTPSetting(String FTPUrl, int FTPPort, String UserName, String UserPassword) {
        ftpUrl = FTPUrl;
        ftpPort = FTPPort;
        userName = UserName;
        userPassword = UserPassword;
        int reply;
        try {
            // 定连接超时时间
            ftpClient.setConnectTimeout(CONNECT_TIMEOUT);
            // 要连接的FTP服务器Url,Port
            ftpClient.connect(FTPUrl, FTPPort);
            //内网用被动模式 ，外网连接时用主动模式
            ftpClient.enterLocalPassiveMode();//被动模式
            // 登陆FTP服务器
            ftpClient.login(UserName, UserPassword);
            // 看返回的值是不是230，如果是，表示登陆成功
            reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                // 断开
                ftpClient.disconnect();
                return false;
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 断开FTP服务器
     */
    public boolean disconnectFTP() {
        try {
            ftpClient.disconnect();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 上传文件
     *
     * @param FilePath       要上传文件所在SDCard的路径
     * @param FileName       要上传的文件的文件名(如：Sim唯一标识码)
     * @param TargetFilePath 存放的ftp服务器路径
     * @return true为成功，false为失败
     */
    public boolean uploadFile(String FilePath, String FileName, String TargetFilePath) {
        if (!ftpClient.isConnected()) {
            if (!initFTPSetting(ftpUrl, ftpPort, userName, userPassword)) {
                return false;
            }
        }
        try {
            // 设置存储路径
            ftpClient.makeDirectory(TargetFilePath);
            ftpClient.changeWorkingDirectory(TargetFilePath);
            // 设置上传文件需要的一些基本信息
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            // 文件上传
            FileInputStream fileInputStream = new FileInputStream(FilePath);
            ftpClient.storeFile(FileName, fileInputStream);
            // 关闭文件流
            fileInputStream.close();
//            // 退出登陆FTP，关闭ftpCLient的连接
//            ftpClient.logout();
//            ftpClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 下载文件
     *
     * @param LocalFilePath 要存放的文件的路径
     * @param FileName      远程FTP服务器上的那个文件的名字
     * @return true为成功，false为失败
     */
    public boolean downLoadFile(String LocalFilePath, String FileName, String FtpFilePath) {
        if (!ftpClient.isConnected()) {
            if (!initFTPSetting(ftpUrl, ftpPort, userName, userPassword)) {
                return false;
            }
        }
        try {
            // 转到指定下载目录
            ftpClient.changeWorkingDirectory(FtpFilePath);
            // 列出该目录下所有文件
            FTPFile[] files = ftpClient.listFiles();
            // 遍历所有文件，找到指定的文件
            for (FTPFile file : files) {
                if (file.getName().equals(FileName)) {
                    // 根据绝对路径初始化文件
                    File localFile = new File(LocalFilePath);
                    // 输出流
                    OutputStream outputStream = new FileOutputStream(localFile);

                    // 设置上传文件需要的一些基本信息 不设置下载视频播放不了
                    ftpClient.setBufferSize(1024);
                    ftpClient.setControlEncoding("UTF-8");
                    ftpClient.enterLocalPassiveMode();
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

                    // 下载文件
                    ftpClient.retrieveFile(file.getName(), outputStream);
                    // 清除缓存
                    outputStream.flush();
                    // 关闭流
                    outputStream.close();
                }
            }
//            // 退出登陆FTP，关闭ftpCLient的连接
//            ftpClient.logout();
//            ftpClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
