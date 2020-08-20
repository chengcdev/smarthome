package com.example.authrolibrary.utils;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 文件操作类，包括获取文件目录，创建和删除文件
 * <p>
 * 2017-12-01: Created by zenghm.
 */
public final class FileUtils {

    private FileUtils() {
    }

    /**
     * 判断SD卡是否存在
     */
    public static boolean hasSdCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡路径
     */
    public static String getSdPath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return Environment.getExternalStorageDirectory().getPath();
        else
            return null;
    }

    /**
     * 判断文件是否存在
     */
    public static boolean existsFile(String path) {
        File file = new File(path);
        return file.exists();
    }

    /**
     * 判断文件是否存在
     */
    public static boolean existsFile(String dirPath, String fileName) {
        File file = new File(dirPath, fileName);
        return file.exists();
    }

    /**
     * 创建目录
     */
    public static boolean createDirectory(String dirPath) {
        File file = new File(dirPath);
        return file.exists() || file.mkdirs();
    }

    /**
     * 创建文件
     */
    public static boolean createFile(String dirPath, String fileName) {
        boolean result = true;
        try {
            File file = new File(dirPath, fileName);
            if (!file.exists()) {
                result = file.createNewFile();
            }
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    /**
     * 删除文件
     */
    public static boolean deleteFile(String path) {
        File file = new File(path);
        return !file.exists() || file.delete();
    }

    /**
     * 删除文件
     */
    public static boolean deleteFile(String dirPath, String fileName) {
        File file = new File(dirPath, fileName);
        return !file.exists() || file.delete();
    }

    /**
     * 获取目录下文件列表
     */
    public static File[] getFileList(String path) {
        File file = new File(path);
        return file.listFiles();
    }

    /**
     * 获取目录下文件列表
     */
    public static File[] getFileList(String path, FilenameFilter filter) {
        File file = new File(path);
        return file.listFiles(filter);
    }

    /**
     * 获取目录下文件列表
     */
    public static File[] getFileList(String path, final String[] suffixList) {
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                String fname = filename.toLowerCase();
                for (String suffix : suffixList) {
                    if (fname.endsWith(suffix.toLowerCase())) {
                        return true;
                    }
                }
                return false;
            }
        };
        return getFileList(path, filter);
    }

    /**
     * 读取目录具体文件
     */
    public static String readSDFile(String fileName) throws IOException {

        File file = new File(fileName);

        if (file == null || !file.exists()) {
            return "";
        }

        FileInputStream fis = new FileInputStream(file);

        int length = fis.available();

        byte[] buffer = new byte[length];
        fis.read(buffer);

        String res = new String(buffer, "GB2312");

        fis.close();

        return res;
    }


    /**
     * 清除缓存
     *
     * @param context
     */
    public static void clearAllCache(Context context) {
        deleteDir(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteDir(context.getExternalCacheDir());
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }


    public static String getFromAssets(Context context, String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 写认证文件到sd卡AurineAuth/auth.txt
     *
     * @param content 写的内容
     */
    public static void writeAuthFile(String content) {
        String path = Environment.getExternalStorageDirectory().getPath() + "/appAuth/";
        createDirectory(path);
        try {
            File fs = new File(path+"auth.dat");
            FileOutputStream outputStream = new FileOutputStream(fs);
            outputStream.write(content.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
//            Log.e("  writeAuthFile", " writeAuthFile FileNotFoundException : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 读文件从sd卡AurineAuth/auth.txt
     */
    public static String readAuthFile() {
        String path = Environment.getExternalStorageDirectory().getPath()+"/appAuth/auth.dat";
        String content;
        FileInputStream fis = null;
        File file = new File(path);
        if (file == null || !file.exists()) {
            return "";
        }
        try {
            fis = new FileInputStream(file);
            int length = fis.available();
            byte[] buffer = new byte[length];
            fis.read(buffer);
            content = new String(buffer);
        } catch (Exception e) {
//            Log.e("  Exception", e.getMessage());
            return "";
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }


    /**
     * 写文件
     */
    public static void writeFile(String content,String path,String fileName) {
        createDirectory(path);
        try {
            File fs = new File(path+fileName);
            FileOutputStream outputStream = new FileOutputStream(fs);
            outputStream.write(content.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
//            Log.e("  writeAuthFile", " writeAuthFile FileNotFoundException : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 读文件
     */
    public static String readFile(String path) {
        String content;
        FileInputStream fis = null;
        File file = new File(path);
        if (file == null || !file.exists()) {
            return "";
        }
        try {
            fis = new FileInputStream(file);
            int length = fis.available();
            byte[] buffer = new byte[length];
            fis.read(buffer);
            content = new String(buffer);
        } catch (Exception e) {
//            Log.e("  Exception", e.getMessage());
            return "";
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }

}