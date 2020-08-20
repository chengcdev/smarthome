package com.mili.smarthome.tkj.base;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.mili.smarthome.tkj.R;

public class FragmentProxy {

    public interface FragmentListener {
        void onExitFragment();
        void setClickable(boolean clickable);
    }

    private static FragmentProxy instance;
    private FragmentListener mListener;
    private FragmentManager mFragmentManager;

    public static FragmentProxy getInstance() {
        if (instance == null) {
            instance = new FragmentProxy();
        }
        return instance;
    }

    public void setFragmentListener(FragmentListener listener) {
        mListener = listener;
    }

    public FragmentListener getFragmentListener() {
        return mListener;
    }

    public void setFragmentManager(FragmentManager fm) {
        mFragmentManager = fm;
    }

    public void showFragment(K4BaseFragment fragment) {
        if (mFragmentManager != null && fragment != null) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.replace(R.id.fl_content, fragment);
            ft.addToBackStack(fragment.getClass().getName());
            ft.commitAllowingStateLoss();
        }
    }

    public void exitFragment() {
        if (mFragmentManager != null) {
            mFragmentManager.popBackStackImmediate();
        }
    }

    public void exitFragmentAll() {
        if (mFragmentManager != null) {
            mFragmentManager.popBackStackImmediate(0, 1);
        }
    }
}
