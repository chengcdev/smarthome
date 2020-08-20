package com.mili.smarthome.tkj.app;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 未捕捉异常处理类
 *
 * 2017-12-01: Created by zenghm.
 */
public class CrashHandler implements UncaughtExceptionHandler {

    // 用于格式化日期,作为日志文件名的一部分
    private static final String DATE_PATTERN = "yyyyMMddHHmmss";

    // CrashHandler实例
    private static CrashHandler INSTANCE = new CrashHandler();

    // 系统默认的UncaughtException处理类
    private UncaughtExceptionHandler mDefaultHandler;

    // 程序的Context对象
    private Context mContext;

    // 异常日志保存路径
    private String mLogPath;

    /** 保证只有一个CrashHandler实例 */
    private CrashHandler() {}

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     */
    public void init(Context context, String logPath) {
        mContext = context.getApplicationContext();
        mLogPath = logPath;
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        StringBuilder sb = new StringBuilder();
        sb.append(collectDeviceInfo(mContext));
        sb.append("\n");
        sb.append(getThrowableInfo(ex));
        debug(sb);
//        if (BuildConfig.DEBUG) {
//            // DEBUG版本才保存文件
//            saveCrashInfo2File(sb);
//        }
        saveCrashInfo2File(sb);
        if (mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /** 收集设备参数信息 */
    private CharSequence collectDeviceInfo(Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n**************************************************");
        sb.append("\n Device Manufacturer: ").append(Build.MANUFACTURER);
        sb.append("\n Device WindowModel       : ").append(Build.MODEL);
        sb.append("\n Android Version    : ").append(Build.VERSION.RELEASE);
        sb.append("\n Android SDK        : ").append(Build.VERSION.SDK_INT);
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            if (pi != null) {
                String verName = pi.versionName == null ? "null" : pi.versionName;
                int verCode = pi.versionCode;
                sb.append("\n APP VersionName    : ").append(verName);
                sb.append("\n APP VersionCode    : ").append(verCode);
            }
        } catch (NameNotFoundException e) {
            debug("an error occured when collect package info", e);
        }
        sb.append("\n**************************************************");
        return sb;
    }

    /** 读取异常信息 */
    private CharSequence getThrowableInfo(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        while (ex != null) {
            sb.append(ex.getClass().getName());
            sb.append(": ");
            sb.append(ex.getMessage());
            StackTraceElement[] stacks = ex.getStackTrace();
            for (StackTraceElement stack : stacks) {
                sb.append("\nat ").append(stack.toString());
            }
            sb.append("\n");
            ex = ex.getCause();
        }
        return sb;
    }

    /**
     * 保存错误信息到文件中
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCrashInfo2File(CharSequence text) {
        try {
            long timestamp = System.currentTimeMillis();
            DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
            String timeNow = dateFormat.format(new Date(timestamp));
            String fileName = "crash-" + timeNow + "-" + timestamp + ".log";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File file = new File(mLogPath, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(text.toString().getBytes());
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            debug("an error occured while writing file...", e);
        }
        return null;
    }

    private void debug(String msg, Throwable ex) {
        StringBuilder sb = new StringBuilder(msg);
        sb.append("\n");
        sb.append(getThrowableInfo(ex));
        debug(sb);
    }

    private void debug(CharSequence msg) {
        Log.e("===== crash =====", msg.toString());
    }
}
