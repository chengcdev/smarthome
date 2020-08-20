package com.mili.smarthome.tkj.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 保存4G网络状态
 * Created by zhengxc on 2019/12/31.
 */

public class MobileNetworkStateFileUtils {
    private static final String TAG = "MobileNetworkStateFile";

    private static final String NET_STATE_FILE = "netState.dat";
    private static final String NET_STATE_DIR_PATH = "/DCIM/param/";
    private static final String NET_STATE_FILE_PATH = "/DCIM/param/netState.dat";

    private static final String NET_STATE_CONNECTED = "1";
    private static final String NET_STATE_DISCONNECTED = "0";

    public void saveNetState(boolean state) {
        String data;
        if (state) {
            data = NET_STATE_CONNECTED;
        } else {
            data = NET_STATE_DISCONNECTED;
        }
        Log.w(TAG, "saveNetState data = " + data);
        writeNetStateFile(data);
    }

    public int readNetState() {
        String state = readNetStateFile();
        Log.w(TAG, "saveNetState state = " + state);
        if (NET_STATE_CONNECTED.equals(state)) {
            return 1;
        } else if (NET_STATE_DISCONNECTED.equals(state)) {
            return 0;
        } else {
            return -1;
        }
    }

    public void delNetState() {
        delNetStateFile();
    }

    private void writeNetStateFile(String data) {
        String dirPath = Environment.getExternalStorageDirectory().getPath() + NET_STATE_DIR_PATH;

        File dirPathFile = new File(dirPath);
        if (!dirPathFile.exists()) {
            dirPathFile.mkdir();
        }

        Log.w(TAG, "writeNetStateFile path = " + dirPath + "netState.dat");
        try {
            File file = new File(dirPath + NET_STATE_FILE);
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(data.getBytes());
            outputStream.flush();
            outputStream.getFD().sync();
            outputStream.close();
        } catch (Exception e) {
            Log.e(TAG, "FileNotFoundException : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String readNetStateFile() {
        String path = Environment.getExternalStorageDirectory().getPath() + NET_STATE_FILE_PATH;
        FileInputStream fileInputStream = null;
        String data = "";
        Log.w(TAG, "readNetStateFile path = " + path);

        File file = new File(path);
        if (file != null && file.exists()) {
            try {
                fileInputStream = new FileInputStream(file);
                int length = fileInputStream.available();
                byte[] buffer = new byte[length];
                fileInputStream.read(buffer);
                data = new String(buffer);
            } catch (Exception e) {
                Log.e("  Exception", e.getMessage());
            } finally {
                try {
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    private void delNetStateFile() {
        String path = Environment.getExternalStorageDirectory().getPath() + NET_STATE_FILE_PATH;
        Log.w(TAG, "delNetStateFile path = " + path);

        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

}
