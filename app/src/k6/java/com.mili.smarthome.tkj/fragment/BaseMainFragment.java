package com.mili.smarthome.tkj.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.main.activity.MainActivity;
import com.mili.smarthome.tkj.setting.fragment.BaseFragment;
import com.mili.smarthome.tkj.utils.AppUtils;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.widget.InputView;

import java.util.Objects;

public abstract class BaseMainFragment extends BaseFragment {

    private View contentView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return initFragment(inflater, container);
    }

    public View initFragment(LayoutInflater inflater, @Nullable ViewGroup container) {
        if (contentView == null) {
            contentView = inflater.inflate(getLayout(), container, false);
        }
        initView(contentView);
        return contentView;
    }

    //初始化view
    public abstract void initView(View view);
    //获取布局
    public abstract int getLayout();

    public void backFragment(){
        Objects.requireNonNull(getActivity()).onBackPressed();
    }
    public void backReplaceFragment(){
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
    }

    /**
     * 初始化主界面底部的按钮点击状态
     */
    public void backMainActivity(){
        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        activity.initBottomBtn();
        LogUtils.w("  BaseMainFragment backMainActivity");
    }

    /**
     * 主界面底部的按钮点击状态
     * @param isEnable 状态
     */
    public void setMainBtnEnable(boolean isEnable){
        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        if (activity.getSupportFragmentManager() != null) {
            activity.setBottomBtnEnable(isEnable);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!Constant.IS_MSGDIALOG_EXIT) {
            //开启屏幕服务
            AppUtils.getInstance().startScreenService();
        }
        Constant.IS_MSGDIALOG_EXIT = false;
    }

    /**
     * 输入
     */
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

    /**
     * 后退
     */
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
        backReplaceFragment();
    }

    public String getStr(int strId){
        if (isAdded()) {
          return getString(strId);
        }
        return "";
    }

    public String getStr(int strId,Object... formatArgs ){
        if (isAdded()) {
            return getString(strId,formatArgs);
        }
        return "";
    }
}
