package com.mili.smarthome.tkj.setting.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.setting.adapter.ItemSelectorAdapter;


public abstract class ItemSelectorFragment extends K4BaseFragment implements ItemSelectorAdapter.OnItemClickListener {

    private RecyclerView mRecyclerView;
    private ItemSelectorAdapter mAdapter;
    private TextView mTvHead, mTvHint;
    private View mLine;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_item_selector;
    }

    @Override
    protected void bindView() {
        super.bindView();
        mTvHead = findView(R.id.tv_head);
        mTvHint = findView(R.id.tv_hint);
        mLine = findView(R.id.v_line);

        mRecyclerView = findView(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void bindData() {
        super.bindData();
        mAdapter = new ItemSelectorAdapter(mContext);
        mAdapter.setStringArray(getStringArray());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMainHandler.removeCallbacksAndMessages(0);
    }

    protected abstract String[] getStringArray();

    protected void setSelection(int selection) {
        if (mAdapter != null) {
            mAdapter.setSelection(selection);
        }
    }

    protected int getSelection() {
        if (mAdapter != null) {
            return mAdapter.getSelection();
        }
        return 0;
    }

    protected void setHead(String text) {
        if (mTvHead != null) {
            mTvHead.setText(text);
        }
    }

    protected void showSetHint(int resId) {
        if (mTvHint != null) {
            mTvHint.setText(resId);
            mTvHint.setVisibility(View.VISIBLE);
            mLine.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);

            mMainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    exitFragment();
                }
            }, Constant.SET_HINT_TIMEOUT);
        }
    }
}
