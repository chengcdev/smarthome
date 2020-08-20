package com.mili.smarthome.tkj.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * 2018-03-26: Created by zenghm.
 */

public final class IOUtils {

    private IOUtils() {}

    public static void write(File file, String data) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write(data);
            bw.flush();
            bw.close();
            fos.close();
        } catch (IOException ex) {
            LogUtils.printThrowable(ex);
        }
    }

    public static void write(String dirPath, String fileName, String data) {
        File file = new File(dirPath, fileName);
        write(file, data);
    }

    public static String read(File file) {
        try {
            InputStream is = new FileInputStream(file);
            return read(is);
        } catch (IOException ex) {
            LogUtils.printThrowable(ex);
        }
        return "";
    }

    public static String readFromAssets(Context context, String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);
            return read(is);
        } catch (IOException e) {
            LogUtils.printThrowable(e);
        }
        return "";
    }

    private static String read(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder data = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            data.append(line.trim());
        }
        br.close();
        is.close();
        return data.toString();
    }

}
