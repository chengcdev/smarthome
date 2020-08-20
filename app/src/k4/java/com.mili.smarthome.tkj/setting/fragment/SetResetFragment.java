package com.mili.smarthome.tkj.setting.fragment;

import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.AppPreferences;
import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.dao.ResetFactoryDao;

public class SetResetFragment extends K4BaseFragment {

    private TextView mTvHint, mTvHead;

    @Override
    public boolean onKeyCancel() {
        super.onKeyCancel();
        exitFragment();
        return true;
    }

    @Override
    public boolean onKeyConfirm() {
        super.onKeyConfirm();
//        AppPreferences.setReset(true);
        ResetFactoryDao factoryDao = new ResetFactoryDao();
        factoryDao.resetDatas();
        mTvHint.setText(R.string.restore_hint);
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_reset;
    }

    @Override
    protected void bindView() {
        super.bindView();
        mTvHint = findView(R.id.tv_hint);
        mTvHead = findView(R.id.tv_head);
        mTvHead.setText(R.string.setting_0408);
    }
}
