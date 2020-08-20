package com.mili.smarthome.tkj.setting.fragment;

import android.support.annotation.ArrayRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.setting.adapter.ResetSelectorAdapter;

public abstract class ResetSelectorFragment extends ResetBaseFragment {

    private TextView tvTitle;
    private RecyclerView mRecyclerView;
    private ResetSelectorAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_reset_selector;
    }

    @Override
    protected void bindView() {
        tvTitle = findView(R.id.tv_title);
        mRecyclerView = findView(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void bindData() {
        super.bindData();
        tvTitle.setText(getTitleId());
        mAdapter = new ResetSelectorAdapter(mContext);
        mAdapter.setStringArray(mContext.getResources().getStringArray(getStringArrayId()));
        mAdapter.setOnItemClickListener(new ResetSelectorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ResetSelectorFragment.this.onItemClick(position);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    protected void setSelection(int selection) {
        mAdapter.setSelection(selection);
    }

    protected int getSelection() {
        return mAdapter.getSelection();
    }

    @StringRes
    protected abstract int getTitleId();

    @ArrayRes
    protected abstract int getStringArrayId();

    protected abstract void onItemClick(int position);
}
