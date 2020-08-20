package com.mili.smarthome.tkj.setting.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.CommStorePathDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;
import com.mili.smarthome.tkj.utils.ExternalMemoryUtils;
import com.mili.smarthome.tkj.utils.LogUtils;

public class SetMemoryFragment extends K4BaseFragment {

    private TextView mTvHead;
    private TextView mTvHint;
    private TextView mTvTotal, mTvUsed, mTvLeft;
    private LinearLayout mLlContent;

    private String mFuncCode;

    @Override
    public boolean onKeyCancel() {
        exitFragment();
        return true;
    }

    @Override
    public boolean onKeyConfirm() {
        if (mFuncCode.equals(SettingFunc.SET_MEMORY_FORMAT)) {
            ExternalMemoryUtils.externalMemoryFormat();
            mTvHint.setText(R.string.set_please_wating);
            mMainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mTvHint.setText(R.string.set_success);
                    mMainHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            exitFragment();
                        }
                    }, Constant.SET_HINT_TIMEOUT);
                }
            }, 1000);
        }
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_memory;
    }

    @Override
    protected void bindView() {
        super.bindView();
        mTvHead = findView(R.id.tv_head);
        mTvHint = findView(R.id.tv_hint);
        mTvTotal = findView(R.id.tv_total);
        mTvUsed = findView(R.id.tv_used);
        mTvLeft = findView(R.id.tv_surplus);
        mLlContent = findView(R.id.Ll_content);
    }

    @Override
    protected void bindData() {
        super.bindData();
        Bundle arg = getArguments();
        if (arg != null) {
            mFuncCode = arg.getString(FragmentFactory.ARGS_FUNCCODE, SettingFunc.SET_MEMORY_CAPACITY);
            String headStr = SettingFunc.getNameByCode(mFuncCode);
            mTvHead.setText(headStr);
        }
        LogUtils.d(" mFuncCode is " + mFuncCode);

        showView(mFuncCode);
        switch (mFuncCode) {
            case SettingFunc.SET_MEMORY_CAPACITY:
                showMemory(false);
                break;
            case SettingFunc.SET_MEMORY_CAPACITY_SD:
                showMemory(true);
                break;
            case SettingFunc.SET_MEMORY_FORMAT:
                mTvHint.setText(R.string.set_memory_fomat);
                break;
        }

    }

    private void showView(String mFuncCode) {
        if (mFuncCode.equals(SettingFunc.SET_MEMORY_FORMAT)) {
            mLlContent.setVisibility(View.GONE);
            mTvHint.setVisibility(View.VISIBLE);
        } else {
            mLlContent.setVisibility(View.VISIBLE);
            mTvHint.setVisibility(View.GONE);
        }
    }

    private void showMemory(boolean sdCard) {
        String path;
        if (sdCard) {
            path = CommStorePathDef.EXTERNAL_SD_PATH;
        } else {
            path = Environment.getExternalStorageDirectory().getPath();
        }
        StatFs stat = new StatFs(path);
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        long availableBlocks = stat.getAvailableBlocksLong();

        long total = blockSize * totalBlocks;
        long available = blockSize * availableBlocks;
        long used = total - available;

        mTvTotal.setText(Formatter.formatFileSize(App.getInstance(), total));
        mTvUsed.setText(Formatter.formatFileSize(App.getInstance(), used));
        mTvLeft.setText(Formatter.formatFileSize(App.getInstance(), available));
    }
}
