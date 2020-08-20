package com.mili.smarthome.tkj.setting.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;
import com.mili.smarthome.tkj.utils.ExternalMemoryUtils;

/**
 * 容量
 */
public class SetCapacityFragment extends BaseFragment implements View.OnClickListener {


    private TextView mTvTotal;
    private TextView mTvUsed;
    private TextView mTvResdual;
    private ImageView mImaBack;
    private String sdTotalSize;
    private String usedSize;
    private String sdAvailableSize;
    private TextView mTvTitle;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_capacity;
    }

    @Override
    protected void bindView() {
        mTvTotal = findView(R.id.tv_total_capacity);
        mTvUsed = findView(R.id.tv_used_capacity);
        mTvResdual = findView(R.id.tv_residual_capacity);
        mImaBack = findView(R.id.iv_back);
        mTvTitle = findView(R.id.tv_title);
        mImaBack.setOnClickListener(this);
        setBackVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void bindData() {
        Bundle args = getArguments();
        if (args != null) {
            String mFuncCode = args.getString(FragmentFactory.ARGS_FUNCCODE);
            if (!TextUtils.isEmpty(mFuncCode)) {
                switch (mFuncCode) {
                    case SettingFunc.SET_MEMORY_CAPACITY:
                        mTvTitle.setText(R.string.setting_local_memory);
                        sdTotalSize = ExternalMemoryUtils.getSDTotalSize();
                        usedSize = ExternalMemoryUtils.getSDusedSize();
                        sdAvailableSize = ExternalMemoryUtils.getSDAvailableSize();
                        break;
                    case SettingFunc.SET_EXTERNAL_MEMORY:
                        mTvTitle.setText(R.string.setting_external_memory);
                        sdTotalSize = ExternalMemoryUtils.getExternalSDTotalSize();
                        usedSize = ExternalMemoryUtils.getExternalSDusedSize();
                        sdAvailableSize = ExternalMemoryUtils.getExternalSDAvailableSize();
                        break;
                }
            }
        }
        mTvTotal.setText(getResString(R.string.setting_total_capacity)+sdTotalSize);
        mTvUsed.setText(getResString(R.string.setting_used_capacity)+usedSize);
        mTvResdual.setText(getResString(R.string.setting_residual_capacity)+sdAvailableSize);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                setBackVisibility(View.VISIBLE);
                exitFragment(this);
                break;

        }
    }
}
