package com.mili.smarthome.tkj.utils;

import android.util.Log;

/**
 * 2017-12-01: Created by zenghm.
 */

public class LogUtils {

    private static final String TAG = "smarthome_tkj";

    public static void v(String msg) {
        Log.v(TAG, msg);
    }

    public static void v(String format, Object... args) {
        v(String.format(format, args));
    }

    public static void d(String msg) {
        Log.d(TAG, msg);
    }

    public static void d(String format, Object... args) {
        d(String.format(format, args));
    }

    public static void i(String msg) {
        Log.i(TAG, msg);
    }

    public static void i(String format, Object... args) {
        i(String.format(format, args));
    }

    public static void w(String msg) {
        Log.w(TAG, msg);
    }

    public static void w(String format, Object... args) {
        w(String.format(format, args));
    }

    public static void e(String msg) {
        Log.e(TAG, msg);
    }

    public static void e(String format, Object... args) {
        e(String.format(format, args));
    }

    public static void e(Throwable ex) {
        printThrowable(ex);
    }

    public static void printThrowable(Throwable ex) {
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
        w(sb.toString());
    }

    public static void printStackTrace() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stack) {
            if (element.getClassName().startsWith("com.mili.smarthome.tkj")) {
                d("--->>> " + element.toString());
            }
        }
    }
}
