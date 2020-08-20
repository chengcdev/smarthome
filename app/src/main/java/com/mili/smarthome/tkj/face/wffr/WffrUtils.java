package com.mili.smarthome.tkj.face.wffr;

import android.content.Context;
import android.content.res.AssetManager;

import com.mili.smarthome.tkj.utils.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class WffrUtils {

    private static final String BASE_DIR = "wffr";
    private static final String DB_DIR = "wffrdb";
    public static final String DB_NAME = "db.dat";

    public static String getBasePath(Context context) {
        File baseDir = context.getDir(BASE_DIR, Context.MODE_PRIVATE);
        File databseDir = new File(baseDir, DB_DIR);
        if (!databseDir.exists() || !databseDir.isDirectory())
            databseDir.mkdir();
        return baseDir.getAbsolutePath() + "/";
    }

    public static String getDbDirPath(Context context) {
        return getBasePath(context) + DB_DIR + "/";
    }

    public static boolean copyAssets(Context context) {
        final String ASSET_DIR = "wffrdata";
        try {
            AssetManager assetManager = context.getAssets();
            String[] assetFiles = assetManager.list(ASSET_DIR);
            for (String assetFile : assetFiles) {
                File outFile;
                if (assetFile.equals(DB_NAME)) {
                    File databseDir = new File(getBasePath(context), DB_DIR);
                    outFile = new File(databseDir, assetFile);
                } else {
                    outFile = new File(getBasePath(context), assetFile);
                }
                if (outFile.exists() && outFile.length() > 0) {
                    continue;
                } else {
                    outFile.createNewFile();
                }
                InputStream inputStream = assetManager.open(ASSET_DIR + "/" + assetFile);
                OutputStream outputStream = new FileOutputStream(outFile);

                copyFile(inputStream, outputStream);

                outputStream.flush();
                outputStream.close();
                inputStream.close();
            }
            return true;
        } catch (Exception e) {
            LogUtils.printThrowable(e);
            return false;
        }
    }

    private static void copyFile(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[10 * 1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
    }

}
