package com.mili.smarthome.tkj.setting.fragment;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.android.CommSysDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.dao.param.EntranceGuardDao;
import com.mili.smarthome.tkj.setting.adapter.ItemSelectorAdapter;
import com.mili.smarthome.tkj.utils.AppUtils;
import com.mili.smarthome.tkj.view.SetOperateView;

/**
 * 锁属性设置
 */
public class SetLockAttrFragment extends BaseFragment implements View.OnClickListener, SetOperateView.IOperateListener {

    private static final int LOCK_TYPE = 0x030101;
    private static final int LOCK_TIME = 0x030102;

    private View vwTime;
    private RecyclerView mRecyclerView;

    private int mSetFunc = LOCK_TYPE;
    private ItemSelectorAdapter mTypeAdapter;
    private ItemSelectorAdapter mTimeAdapter;
    private int selecItem = 0;
    private int currentLockType = 0;
    private int currentLockTime = 0;
    private SetOperateView mOperateView;
    private ImageView mImaBack;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_lock_attr;
    }

    @Override
    protected void bindView() {
        vwTime = findView(R.id.ll_time);
        mOperateView = findView(R.id.rootview);
        mRecyclerView = findView(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        setBackVisibility(View.GONE);

        mImaBack = findView(R.id.iv_back);
        mImaBack.setOnClickListener(this);
        mOperateView.setSuccessListener(this);
    }

    @Override
    protected void bindData() {
        mImaBack.setVisibility(View.VISIBLE);
        //获取开锁类型和开锁时间
        //开锁类型
        int openLockType = EntranceGuardDao.getOpenLockType();
        //开锁时间
        int openLockTime = EntranceGuardDao.getOpenLockTime();

        switch (openLockTime) {
            case 0:
                selecItem = 0;
                break;
            case 3:
                selecItem = 1;
                break;
            case 6:
                selecItem = 2;
                break;
            case 9:
                selecItem = 3;
                break;
        }

        mTypeAdapter = new SetTypeAdapter(mContext);
        mTypeAdapter.setSelection(openLockType);
        mTimeAdapter = new SetTimeAdapter(mContext);
        mTimeAdapter.setSelection(selecItem);
        setLockType();
    }

    @Override
    protected void unbindView() {
        setBackVisibility(View.VISIBLE);
    }

    private void setLockType() {
        vwTime.setVisibility(View.GONE);
        mSetFunc = LOCK_TYPE;
        mRecyclerView.setAdapter(mTypeAdapter);
    }

    private void setLockTime() {
        vwTime.setVisibility(View.VISIBLE);
        mSetFunc = LOCK_TIME;
        mRecyclerView.setAdapter(mTimeAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                if (mSetFunc == LOCK_TIME) {
                    setLockType();
                } else {
                    requestBack();
                }
                break;
        }
    }

    private class SetTypeAdapter extends ItemSelectorAdapter {

        public SetTypeAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getStringArrayId() {
            return R.array.setting_lock_type;
        }

        @Override
        protected void onItemClick(int position) {
            switch (position) {
                case 0:
                    currentLockType = 0;
                    break;
                case 1:
                    currentLockType = 1;
                    break;
            }
            setLockTime();
        }

    }

    private class SetTimeAdapter extends ItemSelectorAdapter {

        public SetTimeAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getStringArrayId() {
            return R.array.setting_lock_time;
        }

        @Override
        protected void onItemClick(int position) {
            switch (position) {
                case 0:
                    currentLockTime = 0;
                    break;
                case 1:
                    currentLockTime = 3;
                    break;
                case 2:
                    currentLockTime = 6;
                    break;
                case 3:
                    currentLockTime = 9;
                    break;
            }
            saveDatas();
        }

    }

    private void saveDatas() {
        EntranceGuardDao.setOpenLockType(currentLockType);
        EntranceGuardDao.setOpenLockTime(currentLockTime);
        mOperateView.operateBackState(getString(R.string.set_success));
        mImaBack.setVisibility(View.GONE);
        AppUtils.getInstance().sendReceiver(CommSysDef.BROADCAST_LOCK_STATE);
    }

    @Override
    public void success() {
        requestBack();
    }

    @Override
    public void fail() {

    }
}
