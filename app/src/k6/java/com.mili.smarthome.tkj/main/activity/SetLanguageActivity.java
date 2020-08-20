package com.mili.smarthome.tkj.main.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.base.BaseActivity;
import com.mili.smarthome.tkj.setting.adapter.ItemSelectorAdapter;
import com.mili.smarthome.tkj.utils.AppUtils;
import com.mili.smarthome.tkj.utils.SystemSetUtils;
import com.mili.smarthome.tkj.view.SetOperateView;

/**
 * 设置语言
 */
@SuppressLint("Registered")
public class SetLanguageActivity extends BaseActivity implements SetOperateView.IOperateListener, View.OnClickListener {

    private RecyclerView mRecyclerView;
    private ItemSelectorAdapter mAdapter;
    private SetOperateView mOperateView;
    private TextView mTvTitle;
    private TextView mTvCancel;
    private TextView mTvConfirm;
    private int mCurrentLanPos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_language);

        initView();

        initData();


    }



    private void initView() {
        mOperateView = findView(R.id.rootview);
        mRecyclerView = findView(R.id.recyclerview);
        mTvTitle = findView(R.id.tv_title);
        mTvCancel = findView(R.id.tv_cancle);
        mTvConfirm = findView(R.id.tv_confirm);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mOperateView.setSuccessListener(this);
        mTvConfirm.setOnClickListener(this);
        mTvCancel.setOnClickListener(this);
    }

    private void initData() {
        mAdapter = new SetLanguageAdapter(this);
        mTvTitle.setText(getString(R.string.setting_language));
        mAdapter.setSelection(0);
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void success() {

    }

    @Override
    public void fail() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //取消
            case R.id.tv_cancle:
                break;
            //确定
            case R.id.tv_confirm:
                //设置语言
                SystemSetUtils.setSystemLanguage(mCurrentLanPos);
                AppUtils.getInstance().toActFinish(this,ResetActivity.class);
                break;
            default:
                break;
        }
    }

    private class SetLanguageAdapter extends ItemSelectorAdapter {

        public SetLanguageAdapter(Context context) {
            super(context, true);
        }

        @Override
        protected int getStringArrayId() {
            return R.array.setting_language;
        }

        @Override
        protected void onItemClick(int position) {
            mCurrentLanPos = position;
        }

    }
}
