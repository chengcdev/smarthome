package com.mili.smarthome.tkj.utils;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 软键盘工具类
 *
 * 2017-12-04: Created by zenghm.
 */
public final class KeyboardUtils {

    public static void show(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && view.requestFocus()) {
            imm.showSoftInput(view, 0);
        }
    }

    public static void show(Activity activity) {
        View currentFocus = activity.getCurrentFocus();
        if (currentFocus != null)
            show(currentFocus);
    }

    public static void hide(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            View currentFocus = activity.getCurrentFocus();
            IBinder token = (currentFocus == null) ? null : currentFocus.getWindowToken();
            imm.hideSoftInputFromWindow(token, 0);
        }
    }

    public static void toggle(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            View currentFocus = activity.getCurrentFocus();
            IBinder token = (currentFocus == null) ? null : currentFocus.getWindowToken();
            imm.toggleSoftInputFromWindow(token, 0, 0);
        }
    }
}
