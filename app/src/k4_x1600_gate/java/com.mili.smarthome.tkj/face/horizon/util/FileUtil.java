package com.mili.smarthome.tkj.face.horizon.util;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;

public class FileUtil {
    private final static String TAG = FileUtil.class.getName();

    public static byte[] getFileContent(String filePath) {
        File file = new File(filePath);
        byte[] fileContent = null;
        Log.d(TAG, "getFileContent file.exists() = " + file.exists());
        if (file.exists()) {
            long filelength = file.length();
            if (filelength != 0) {
                fileContent = new byte[(int) filelength];
                try {
                    FileInputStream in = new FileInputStream(file);
                    in.read(fileContent);
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return fileContent;
    }

    public static void bytesToImageFile(byte[] bytes, String filePath) {
        try {
            //File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/bbb.jpg");
            File file = new File(filePath);
            if (file.exists()) {
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes, 0, bytes.length);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void bytesToImageFile(byte[] bytes, String filePath, int wtrieLength) {
        try {
            //File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/bbb.jpg");
            File file = new File(filePath);
            if (file.exists()) {
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes, 0, wtrieLength);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveBitmapToFile(String path, Bitmap bitmap) {
        try {
            File file = new File(path);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteFile(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    public static boolean copyFile(String oldPath, String newPath) {
        try {
            File oldFile = new File(oldPath);
            if (!oldFile.exists() || !oldFile.isFile() || !oldFile.canRead()) {
                return false;
            }
            FileInputStream fileInputStream = new FileInputStream(oldPath);
            FileOutputStream fileOutputStream = new FileOutputStream(newPath);
            byte[] buffer = new byte[1024];
            int byteRead;
            while (-1 != (byteRead = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.getFD().sync();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int writeFile(String content, String path) {
        FileOutputStream fop = null;
        File file;
        try {
            file = new File(path);
            Log.d(TAG, "writeFile path = " + path);
            //if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            Log.d(TAG, "writeFile file = " + file);
            fop = new FileOutputStream(file);
            Log.d(TAG, "writeFile fop = " + fop);
            byte[] contentInBytes = content.getBytes();
            //true = append file
            fop.write(contentInBytes);
            fop.flush();
            fop.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static int writeFileByLine(String line, String path, boolean append) {
        BufferedWriter bw = null;
        FileWriter fileWriter = null;
        try {
            File file = new File(path);
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            fileWriter = new FileWriter(file.getAbsoluteFile(), append);
            bw = new BufferedWriter(fileWriter);
            bw.write(line);
            if (append)
                bw.newLine();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bw.flush();
                bw.close();
                fileWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static int createDir(String dirPath) {
        int ret = 0;
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdir();
        }
        return ret;
    }
}
