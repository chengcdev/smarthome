package com.mili.smarthome.tkj.utils;

import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import com.android.CommStorePathDef;
import com.android.Common;
import com.android.main.MainCommDefind;
import com.mili.smarthome.tkj.app.App;

import java.io.File;

public class ExternalMemoryUtils {

    /**
     * 获得内置sd卡总大小
     */
    public static String getSDTotalSize() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return Formatter.formatFileSize(App.getInstance(), blockSize * totalBlocks);
    }

    /**
     * 获得内置sd卡剩余容量，即可用大小
     */
    public static String getSDAvailableSize() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return Formatter.formatFileSize(App.getInstance(), blockSize * availableBlocks);
    }


    /**
     * @return 内置sd卡使用了的内存
     */
    public static String getSDusedSize() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        long total = blockSize * totalBlocks;

        long availableBlocks = stat.getAvailableBlocksLong();
        long available = blockSize * availableBlocks;
        long used = total - available;
        return Formatter.formatFileSize(App.getInstance(), used);
    }


    /**
     * 获得外置sd卡总大小
     */
    public static String getExternalSDTotalSize() {
        StatFs stat = new StatFs(CommStorePathDef.EXTERNAL_SD_PATH);
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return Formatter.formatFileSize(App.getInstance(), blockSize * totalBlocks);
    }

    /**
     * 获得外置sd卡剩余容量，即可用大小
     */
    public static String getExternalSDAvailableSize() {
        StatFs stat = new StatFs(CommStorePathDef.EXTERNAL_SD_PATH);
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return Formatter.formatFileSize(App.getInstance(), blockSize * availableBlocks);
    }


    /**
     * @return 外置sd卡使用了的内存
     */
    public static String getExternalSDusedSize() {
        StatFs stat = new StatFs(CommStorePathDef.EXTERNAL_SD_PATH);
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        long total = blockSize * totalBlocks;

        long availableBlocks = stat.getAvailableBlocksLong();
        long available = blockSize * availableBlocks;
        long used = total - available;
        return Formatter.formatFileSize(App.getInstance(), used);
    }

    /**
     * 外置SdCard是否在
     */
    public static boolean externalMemoryAvailable() {
        return Common.hasExternalSdCard();
    }

    /**
     * 格式化存储卡
     */
    public static boolean externalMemoryFormat() {
        FileUtils.deleteDirWihtFile(new File(CommStorePathDef.EX_MULTIMEDIA_DIR_PATH));
        FileUtils.deleteDirWihtFile(new File(CommStorePathDef.MULTIMEDIA_DIR_PATH));
        if (externalMemoryAvailable()) {
            if (MainCommDefind.mainClient != null) {
                return MainCommDefind.mainClient.Main_FormatExternalSdCard() == 0;
            }
        }
        return true;
    }

    /**
     * 获取sd卡路径
     */
    private static String getSdPath() {
        if (externalMemoryAvailable()){
            return CommStorePathDef.EXTERNAL_SD_PATH;
        }
        return Environment.getExternalStorageDirectory().getPath();
    }


    /**
     * 获取视频路径
     */
    public static String getVideoPath(){
        if (externalMemoryAvailable()) {
            return CommStorePathDef.EX_MULTIMEDIA_VIDEO_DIR_PATH;
        }
        return CommStorePathDef.MULTIMEDIA_VIDEO_DIR_PATH;
    }

    /**
     * 获取多媒体图片列表
     */
    public static File[] queryPhotoList() {
        final String[] suffixList = new String[] { ".JPG", ".PNG", ".BMP" };
        return FileUtils.getFileList(CommStorePathDef.MULTIMEDIA_PHOTO_DIR_PATH, suffixList);
    }

    /**
     * 获取多媒体视频列表
     */
    public static File[] queryVedioList() {
        final String[] suffixList = new String[] { ".MP4" };
        File[] vedioFiles1 = FileUtils.getFileList(CommStorePathDef.MULTIMEDIA_VIDEO_DIR_PATH, suffixList);
        File[] vedioFiles2 = null;
        if (externalMemoryAvailable()) {
            vedioFiles2 = FileUtils.getFileList(CommStorePathDef.EX_MULTIMEDIA_VIDEO_DIR_PATH, suffixList);
        }
        if (vedioFiles1 == null || vedioFiles1.length == 0) {
            return vedioFiles2;
        } else if (vedioFiles2 == null || vedioFiles2.length == 0) {
            return vedioFiles1;
        } else {
            int count = vedioFiles1.length + vedioFiles2.length;
            File[] newFiles = new File[count];
            int destPos = 0;
            System.arraycopy(vedioFiles1, 0, newFiles, destPos, vedioFiles1.length);
            destPos += vedioFiles1.length;
            System.arraycopy(vedioFiles2, 0, newFiles, destPos, vedioFiles2.length);
            return newFiles;
        }
    }

}
