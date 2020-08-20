package com.mili.smarthome.tkj.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.widget.InputView;

import java.util.Objects;

public abstract class BaseFragment extends Fragment {

    protected Context mContext;
    protected Handler mMainHandler;
    private View mContentView;

    protected void handleMessage(Message msg) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d("LIFECYCLE: %s--->>>onCreate", getClass().getName());
        mMainHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                BaseFragment.this.handleMessage(msg);
                return true;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d("LIFECYCLE: %s--->>>onDestroy", getClass().getName());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LogUtils.d("LIFECYCLE: %s--->>>onAttach", getClass().getName());
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtils.d("LIFECYCLE: %s--->>>onDetach", getClass().getName());
        mMainHandler.removeCallbacksAndMessages(null);
        mContext = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.d("LIFECYCLE: %s--->>>onCreateView", getClass().getName());
        if (mContentView == null) {
            mContentView = inflater.inflate(getLayoutId(), container, false);
            bindView();
        }
        bindData();
        return mContentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtils.d("LIFECYCLE: %s--->>>onViewCreated", getClass().getName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.d("LIFECYCLE: %s--->>>onDestroyView", getClass().getName());
        unbindView();
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d("LIFECYCLE: %s--->>>onResume", getClass().getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.d("LIFECYCLE: %s--->>>onPause", getClass().getName());
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.d("LIFECYCLE: %s--->>>onStart", getClass().getName());
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.d("LIFECYCLE: %s--->>>onStop", getClass().getName());
    }

    @LayoutRes
    protected int getLayoutId() {
        return 0;
    }

    protected void bindView() {

    }

    protected void bindData() {

    }

    protected void unbindView() {

    }

    @SuppressWarnings("unchecked")
    protected final <T extends View> T findView(@IdRes int id) {
        if (mContentView == null)
            return null;
        return (T) mContentView.findViewById(id);
    }

    protected void runOnUiThread(Runnable runnable) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(runnable);
        } else {
            mMainHandler.post(runnable);
        }
    }

    public boolean requestBack() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.onBackPressed();
            return true;
        }
        return false;
    }

    protected void inputNum(int num) {
        View root = getView();
        if (root == null)
            return;
        View focus = root.findFocus();
        if (focus instanceof InputView) {
            InputView iptView = (InputView) focus;
            iptView.input(num);
        }
    }

    protected void backspace() {
        View root = getView();
        if (root != null) {
            View focus = root.findFocus();
            if (focus instanceof InputView) {
                InputView iptView = (InputView) focus;
                if (iptView.backspace())
                    return;
            }
        }
        requestBack();
    }

    public void exitFragment(Fragment fragment) {
        if (fragment.isAdded()) {
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
        }
    }
}
