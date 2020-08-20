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
import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.dao.param.EntranceGuardDao;
import com.mili.smarthome.tkj.setting.adapter.ItemSelectorAdapter;

public class SetLockparamFragment extends K4BaseFragment implements View.OnClickListener {

    private static final int LOCK_TYPE = 0x030101;
    private static final int LOCK_TIME = 0x030102;

    private RecyclerView mRecyclerView;
    private TextView mTvHead, mTvHint;
    private View mLine;
    private ImageButton mIbUp, mIbDown;

    private int mSetFunc = LOCK_TYPE;
    private ItemSelectorAdapter mTypeAdapter;
    private ItemSelectorAdapter mTimeAdapter;

    private int mOpenLockType;
    private int mOpenLockTime;

    @Override
    public boolean onKeyCancel() {
        super.onKeyCancel();
        if (mSetFunc == LOCK_TIME) {
            setLockType();
        } else {
            exitFragment();
        }
        return true;
    }

    @Override
    public boolean onKeyConfirm() {
        super.onKeyConfirm();
        if (mSetFunc == LOCK_TYPE) {
            mOpenLockType = mTypeAdapter.getSelection();
            setLockTime();
        } else {
            mOpenLockTime = mTimeAdapter.getSelection() * 3;
            saveParam();
        }
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_lockparam;
    }

    @Override
    protected void bindView() {
        super.bindView();
        mTvHead = findView(R.id.tv_head);
        mTvHint = findView(R.id.tv_hint);
        mLine = findView(R.id.v_line);

        mRecyclerView = findView(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        mIbUp = findView(R.id.ib_up);
        mIbDown = findView(R.id.ib_down);
        assert mIbUp != null;
        mIbUp.setOnClickListener(this);
        assert mIbDown != null;
        mIbDown.setOnClickListener(this);

        showView(true);
    }

    @Override
    protected void bindData() {
        super.bindData();

        mOpenLockType = EntranceGuardDao.getOpenLockType();
        mOpenLockTime = EntranceGuardDao.getOpenLockTime();

        //locktype
        mTypeAdapter = new ItemSelectorAdapter(mContext);
        mTypeAdapter.setStringArray(mContext.getResources().getStringArray(R.array.setting_lock_type));
        mTypeAdapter.setOnItemClickListener(new ItemSelectorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mTypeAdapter.setSelection(position);
                mOpenLockType = position;
                setLockTime();
            }
        });

        //locktime
        mTimeAdapter = new ItemSelectorAdapter(mContext);
        mTimeAdapter.setStringArray(mContext.getResources().getStringArray(R.array.setting_lock_time));
        mTimeAdapter.setOnItemClickListener(new ItemSelectorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mTimeAdapter.setSelection(position);
                mOpenLockTime = position * 3;
                saveParam();
            }
        });

        setLockType();
    }

    private void setLockType() {
        mSetFunc = LOCK_TYPE;
        mOpenLockType = EntranceGuardDao.getOpenLockType();
        mTypeAdapter.setSelection(mOpenLockType);
        mRecyclerView.setAdapter(mTypeAdapter);
        setHead(R.string.set_locktype);
    }

    private void setLockTime() {
        mSetFunc = LOCK_TIME;
        mOpenLockTime = EntranceGuardDao.getOpenLockTime();
        mTimeAdapter.setSelection(mOpenLockTime/3);
        mRecyclerView.setAdapter(mTimeAdapter);
        setHead(R.string.set_locktime);
        mIbUp.setVisibility(View.VISIBLE);
        mIbDown.setVisibility(View.VISIBLE);
    }

    protected void setHead(int resId) {
        if (mTvHead != null) {
            mTvHead.setText(resId);
        }
    }

    private void showView(boolean show) {
        if (show) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mTvHint.setVisibility(View.INVISIBLE);
            mLine.setVisibility(View.INVISIBLE);
        } else {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mTvHint.setVisibility(View.VISIBLE);
            mLine.setVisibility(View.VISIBLE);
        }
        mIbUp.setVisibility(View.INVISIBLE);
        mIbDown.setVisibility(View.INVISIBLE);
    }

    private void saveParam() {
        EntranceGuardDao.setOpenLockAttr(mOpenLockType, mOpenLockTime);
        mTvHint.setText(R.string.set_success);
        showView(false);
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                exitFragment();
            }
        }, Constant.SET_HINT_TIMEOUT);

        Intent intent = new Intent(CommSysDef.BROADCAST_LOCK_STATE);
        App.getInstance().sendBroadcast(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_up:
                if (mSetFunc == LOCK_TIME) {
                    mTimeAdapter.prePage();
                }
                break;

            case R.id.ib_down:
                if (mSetFunc == LOCK_TIME) {
                    mTimeAdapter.nextPage();
                }
                break;
        }
    }
}
