package com.mili.smarthome.tkj.setting.fragment;

import android.support.annotation.ArrayRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.setting.adapter.ResetSelectorAdapter;

public abstract class ResetSelectorFragment extends ResetBaseFragment implements View.OnClickListener {

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
        findView(R.id.btn_cancel).setOnClickListener(this);
        findView(R.id.btn_confirm).setOnClickListener(this);
    }

    @Override
    protected void bindData() {
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                onCancel();
                break;
            case R.id.btn_confirm:
                onConfirm();
                break;
        }
    }

    @Override
    public boolean onKeyEvent(int keyCode, int keyState) {
        if (keyState == KEYSTATE_DOWN)
            return false;
        switch (keyCode) {
            case KEYCODE_BACK:
                onCancel();
                break;
            case KEYCODE_CALL:
                onConfirm();
                break;
            case KEYCODE_UP:
                selectPrevious();
                break;
            case KEYCODE_DOWN:
                selectNext();
                break;
        }
        return true;
    }

    private void selectPrevious() {
        int selection = mAdapter.getSelection();
        if (selection <= 0)
            return;
        mAdapter.setSelection(selection - 1);
    }

    private void selectNext() {
        int selection = mAdapter.getSelection();
        if (selection >= mAdapter.getItemCount() - 1)
            return;
        mAdapter.setSelection(selection + 1);
    }

    @StringRes
    protected abstract int getTitleId();

    @ArrayRes
    protected abstract int getStringArrayId();

    protected abstract void onItemClick(int position);

    protected abstract void onCancel();

    protected abstract void onConfirm();
}
