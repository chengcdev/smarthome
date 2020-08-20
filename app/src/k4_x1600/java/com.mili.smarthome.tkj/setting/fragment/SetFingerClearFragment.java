package com.mili.smarthome.tkj.setting.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.base.FragmentProxy;
import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.dao.FingerDao;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;

public class SetFingerClearFragment extends K4BaseFragment {

    private TextView tvHead, tvHint;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_confirm;
    }

    @Override
    protected void bindView() {
        super.bindView();
        tvHead = findView(R.id.tv_head);
        tvHint = findView(R.id.tv_hint);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvHead.setText(R.string.setting_030503);
        tvHint.setText(R.string.finger_clear_confirm);
    }

    @Override
    public boolean onKeyCancel() {
        super.onKeyCancel();
        FragmentProxy.getInstance().exitFragment();
        return true;
    }

    @Override
    public boolean onKeyConfirm() {
        super.onKeyConfirm();
        SinglechipClientProxy.getInstance().clearFinger();
        FingerDao fingerDao = new FingerDao();
        fingerDao.clear();
        tvHint.setText(R.string.set_ok);
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentProxy.getInstance().exitFragment();
            }
        }, 1000);
        return true;
    }
}
