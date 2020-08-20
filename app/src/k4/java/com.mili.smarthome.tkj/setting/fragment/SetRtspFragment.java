package com.mili.smarthome.tkj.setting.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.base.FragmentProxy;
import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.utils.KeyboardUtils;

public class SetRtspFragment extends K4BaseFragment {

    private View llContent, mFooter;
    private TextView tvHead, mTvHint;
    private EditText etRtspUrl;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_rtsp;
    }

    @Override
    protected void bindView() {
        super.bindView();
        tvHead = findView(R.id.tv_head);
        mFooter = findView(R.id.listview_footer);
        llContent = findView(R.id.ll_content);
        etRtspUrl = findView(R.id.et_rtsp_url);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvHead.setText(R.string.setting_030304);
        mTvHint = findView(R.id.tv_hint);
        etRtspUrl.setText(AppConfig.getInstance().getRtspUrl());
        etRtspUrl.requestFocus();
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
        save();
        return true;
    }

    @Override
    public void onDestroyView() {
        Activity activity = getActivity();
        if (activity != null) {
            KeyboardUtils.hide(activity);
        }
        super.onDestroyView();
    }

    private void save() {
        AppConfig.getInstance().setRtspUrl(etRtspUrl.getText().toString());

        mTvHint.setText(R.string.set_success);
        llContent.setVisibility(View.INVISIBLE);
        mFooter.setVisibility(View.INVISIBLE);
        mTvHint.setVisibility(View.VISIBLE);

        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentProxy.getInstance().exitFragment();
            }
        }, Constant.SET_HINT_TIMEOUT);
    }

}
