package com.mili.smarthome.tkj.main.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mili.smarthome.tkj.base.BaseFragment;
import com.mili.smarthome.tkj.main.interf.IKeyBoardListener;
import com.mili.smarthome.tkj.main.manage.KeyBoardEventManage;

import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseKeyBoardFragment extends BaseFragment implements IKeyBoardListener {

    private static final String TAG = "BaseKeyBoardFragment";
    private View contentView;
    public Unbinder bindView;
    private int layout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initFragment(inflater, container);
    }

    public View initFragment(LayoutInflater inflater, @Nullable ViewGroup container) {
        if (contentView == null) {
            contentView = inflater.inflate(getLayout(), container, false);
            bindView = ButterKnife.bind(this, contentView);
        }
        initView();
        initListener();
        initAdapter();
        return contentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        KeyBoardEventManage.getInstance().setKeyBoardListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bindView.unbind();
    }

    //初始化view
    public abstract void initView();

    //初始化适配器
    public abstract void initAdapter();

    //初始化监听
    public abstract void initListener();

    //布局
    public void setLayout(int layout) {
        this.layout = layout;
    }

    public int getLayout() {
        return layout;
    }

    public View getContentView() {
        return contentView;
    }

    public abstract void setKeyBoard(int viewId,String keyId);

    @Override
    public void onKeyBoard(int viewId, String kid) {
        setKeyBoard(viewId,kid);
    }


    public void exitFragment(Fragment fragment) {
        if (fragment.isAdded()) {
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
        }
    }

}
