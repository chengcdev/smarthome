package com.mili.smarthome.tkj.setting.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.widget.TextView;

import com.android.CommStorePathDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;
import com.mili.smarthome.tkj.utils.LogUtils;

public class SetMemoryCapacityFragment extends BaseSetFragment {

    private TextView tvTitle;
    private TextView tvTotal;
    private TextView tvUsed;
    private TextView tvSurplus;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_memory_capacity;
    }

    @Override
    protected void bindView() {
        super.bindView();
        tvTitle = findView(R.id.tv_title);
        tvTotal = findView(R.id.tv_total);
        tvUsed = findView(R.id.tv_used);
        tvSurplus = findView(R.id.tv_surplus);
    }

    @Override
    protected void bindData() {
        super.bindData();
        Bundle args = getArguments();
        String funcCode = SettingFunc.SET_MEMORY_CAPACITY;
        if (args != null) {
            funcCode = args.getString(FragmentFactory.ARGS_FUNCCODE);
        }
        tvTitle.setText(SettingFunc.getName(funcCode));

        switch (funcCode) {
            case SettingFunc.SET_MEMORY_CAPACITY:
                showSDSize(Environment.getExternalStorageDirectory().getPath());
                break;
            case SettingFunc.SET_MEMORY_EXT_CAPACITY:
                showSDSize(CommStorePathDef.EXTERNAL_SD_PATH);
                break;
        }
    }

    private void showSDSize(String sdpath) {
        LogUtils.d("SD Path: " + sdpath);

        StatFs stat = new StatFs(sdpath);
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        long usedBlocks = totalBlocks - availableBlocks;

        final Context context = ContextProxy.getContext();
        String sdTotalSize = Formatter.formatFileSize(context, blockSize * totalBlocks);
        String sdAvailableSize = Formatter.formatFileSize(context, blockSize * availableBlocks);
        String sdUsedSize = Formatter.formatFileSize(context, blockSize * usedBlocks);
        tvTotal.setText(sdTotalSize);
        tvUsed.setText(sdUsedSize);
        tvSurplus.setText(sdAvailableSize);
    }
}
