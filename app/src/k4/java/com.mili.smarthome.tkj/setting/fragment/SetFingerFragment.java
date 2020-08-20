package com.mili.smarthome.tkj.setting.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.CommSysDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.base.FragmentProxy;
import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.setting.adapter.PagingAdapter;


/**
 * 指纹识别
 */
public class SetFingerFragment extends K4BaseFragment implements PagingAdapter.OnItemClickListener, View.OnClickListener {

    private RecyclerView mRecyclerView;
    private PagingAdapter mAdapter;
    private TextView mTvHead, mTvHint;
    private View mLine, mFooter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_finger;
    }

    @Override
    protected void bindView() {
        super.bindView();
        mTvHead = findView(R.id.tv_head);
        mTvHint = findView(R.id.tv_hint);
        mLine = findView(R.id.v_line);
        mFooter = findView(R.id.listview_footer);

        ImageButton ibUp = findView(R.id.ib_up);
        ImageButton ibDown = findView(R.id.ib_down);
        assert ibUp != null;
        ibUp.setOnClickListener(this);
        assert ibDown != null;
        ibDown.setOnClickListener(this);

        mRecyclerView = findView(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void bindData() {
        super.bindData();
        mTvHead.setText(R.string.setting_0305);
        mAdapter = new PagingAdapter(mContext);
        mAdapter.setOnItemClickListener(this);
        if (!AppConfig.getInstance().isFingerEnabled()) {
            mAdapter.setStringArray(new String[] {
                    mContext.getString(R.string.pub_disable),
                    mContext.getString(R.string.pub_enable)
            });
            mAdapter.setSelection(0);
            mFooter.setVisibility(View.GONE);
        } else {
            mAdapter.setStringArray(new String[] {
                    mContext.getString(R.string.pub_disable),
                    mContext.getString(R.string.pub_enable),
                    mContext.getString(R.string.setting_030501),
                    mContext.getString(R.string.setting_030502),
                    mContext.getString(R.string.setting_030503)
            });
            mAdapter.setSelection(1);
            mFooter.setVisibility(View.VISIBLE);
        }
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_up:
                mAdapter.prePage();
                break;
            case R.id.ib_down:
                mAdapter.nextPage();
                break;
        }
    }

    @Override
    public void onItemClick(int position) {
        switch (position) {
            case 0: // 禁用
            case 1: // 启用
                AppConfig.getInstance().setFingerprint(position);
                showSetHint();
                mMainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exitFragment();
                    }
                }, 2000);
                // 发送广播
                Intent intent = new Intent(CommSysDef.BROADCAST_ENABLE_FINGER);
                App.getInstance().sendBroadcast(intent);
                break;

            case 2: // 添加指纹
                showFragment(new SetFingerAddFragment());
                break;
            case 3: // 删除指纹
                showFragment(new SetFingerDelFragment());
                break;
            case 4: // 清空指纹
                showFragment(new SetFingerClearFragment());
                break;
        }
    }

    private void showFragment(K4BaseFragment fragment) {
        FragmentProxy.getInstance().showFragment(fragment);
    }

    private void showSetHint() {
        mTvHint.setText(R.string.set_ok);
        mTvHint.setVisibility(View.VISIBLE);
        mLine.setVisibility(View.VISIBLE);
        mFooter.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onKeyCancel() {
        super.onKeyCancel();
        exitFragment();
        return true;
    }

    @Override
    public boolean onKeyConfirm() {
        super.onKeyConfirm();
        int position = mAdapter.getSelection();
        if (position == 0 || position == 1) {
            onItemClick(position);
        }
        return true;
    }
}
