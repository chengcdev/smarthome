package com.mili.smarthome.tkj.utils;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class FragmentUtils {

    public static void clear(FragmentActivity activity) {
        clear(activity.getSupportFragmentManager());
    }

    public static void clear(FragmentManager fm) {
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public static void add(FragmentActivity activity, @IdRes int containerId, Fragment fragment) {
        add(activity.getSupportFragmentManager(), containerId, fragment, false);
    }

    public static void add(FragmentActivity activity, @IdRes int containerId, Fragment fragment, boolean addToBackStack) {
        add(activity.getSupportFragmentManager(), containerId, fragment, addToBackStack);
    }

    public static void add(FragmentManager fm, @IdRes int containerId, Fragment fragment, boolean addToBackStack) {
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(containerId, fragment);
        if (addToBackStack) {
            ft.addToBackStack(fragment.getClass().getName());
        }
        ft.commitAllowingStateLoss();
    }

    public static void replace(FragmentActivity activity, @IdRes int containerId, Fragment fragment) {
        replace(activity.getSupportFragmentManager(), containerId, fragment, false);
    }

    public static void replace(FragmentActivity activity, @IdRes int containerId, Fragment fragment, boolean addToBackStack) {
        replace(activity.getSupportFragmentManager(), containerId, fragment, addToBackStack);
    }

    public static void replace(FragmentManager fm, @IdRes int containerId, Fragment fragment, boolean addToBackStack) {
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(containerId, fragment);
        if (addToBackStack) {
            ft.addToBackStack(fragment.getClass().getName());
        }
        ft.commitAllowingStateLoss();
    }
}
