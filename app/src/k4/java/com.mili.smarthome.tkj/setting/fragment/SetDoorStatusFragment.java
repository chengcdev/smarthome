package com.mili.smarthome.tkj.setting.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.CommSysDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.dao.param.EntranceGuardDao;
import com.mili.smarthome.tkj.setting.adapter.ItemSelectorAdapter;

public class SetDoorStatusFragment extends K4BaseFragment {

    private static final int TYPE_DOOR_STATUS = 0x030201;
    private static final int TYPE_ALARM_OUT = 0x030202;
    private static final int TYPE_REPORT_CENTER = 0x030203;

    private RecyclerView mRecyclerView;
    private TextView mTvHead, mTvHint;
    private View mLine;

    private int mSetFunc = TYPE_DOOR_STATUS;
    private ItemSelectorAdapter mDoorStatusAdapter;
    private ItemSelectorAdapter mAlarmOutAdapter;
    private ItemSelectorAdapter mReportCenterAdapter;

    private int mDoorStatus;
    private int mAlarmOut;
    private int mReportCenter;

    @Override
    public boolean onKeyCancel() {
        super.onKeyCancel();
        if (mSetFunc == TYPE_ALARM_OUT){
            setDoorStatus();
        } else if (mSetFunc == TYPE_REPORT_CENTER) {
            setAlarmOut();
        } else {
            exitFragment();
        }
        return true;
    }

    @Override
    public boolean onKeyConfirm() {
        super.onKeyConfirm();
        if (mSetFunc == TYPE_DOOR_STATUS) {
            mDoorStatus = mDoorStatusAdapter.getSelection();
            if (mDoorStatus == 1) {
                setAlarmOut();
            } else {
                saveParam();
            }
        } else if (mSetFunc == TYPE_ALARM_OUT){
            mAlarmOut = mAlarmOutAdapter.getSelection();
            setReportCenter();
        } else {
            mReportCenter = mReportCenterAdapter.getSelection();
            saveParam();
        }
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_doorstatus;
    }

    @Override
    protected void bindView() {
        super.bindView();
        mTvHead = findView(R.id.tv_head);
        mTvHint = findView(R.id.tv_hint);
        mLine = findView(R.id.v_line);

        mRecyclerView = findView(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        showView(true);
    }

    @Override
    protected void bindData() {
        super.bindData();

        mDoorStatus = EntranceGuardDao.getDoorStateCheck();
        mAlarmOut = EntranceGuardDao.getAlarmOut();
        mReportCenter = EntranceGuardDao.getUpdateCenter();

        //door status
        mDoorStatusAdapter = new ItemSelectorAdapter(mContext);
        mDoorStatusAdapter.setStringArray(mContext.getResources().getStringArray(R.array.setting_yesno));
        mDoorStatusAdapter.setOnItemClickListener(new ItemSelectorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mDoorStatusAdapter.setSelection(position);
                mDoorStatus = position;
                if (mDoorStatus == 0) {
                    saveParam();
                } else {
                    setAlarmOut();
                }
            }
        });

        //alarm out
        mAlarmOutAdapter = new ItemSelectorAdapter(mContext);
        mAlarmOutAdapter.setStringArray(mContext.getResources().getStringArray(R.array.setting_yesno));
        mAlarmOutAdapter.setOnItemClickListener(new ItemSelectorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mAlarmOutAdapter.setSelection(position);
                mAlarmOut = position;
                setReportCenter();
            }
        });

        //repoet center
        mReportCenterAdapter = new ItemSelectorAdapter(mContext);
        mReportCenterAdapter.setStringArray(mContext.getResources().getStringArray(R.array.setting_yesno));
        mReportCenterAdapter.setOnItemClickListener(new ItemSelectorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mReportCenterAdapter.setSelection(position);
                mReportCenter = position;
                saveParam();
            }
        });

        setDoorStatus();
    }

    private void setDoorStatus() {
        mSetFunc = TYPE_DOOR_STATUS;
        mDoorStatus = EntranceGuardDao.getDoorStateCheck();
        mDoorStatusAdapter.setSelection(mDoorStatus);
        mRecyclerView.setAdapter(mDoorStatusAdapter);
        setHead(R.string.setting_030201);
    }

    private void setAlarmOut() {
        mSetFunc = TYPE_ALARM_OUT;
        mAlarmOut = EntranceGuardDao.getAlarmOut();
        mAlarmOutAdapter.setSelection(mAlarmOut);
        mRecyclerView.setAdapter(mAlarmOutAdapter);
        setHead(R.string.setting_030202);
    }

    private void setReportCenter() {
        mSetFunc = TYPE_REPORT_CENTER;
        mReportCenter = EntranceGuardDao.getUpdateCenter();
        mReportCenterAdapter.setSelection(mReportCenter);
        mRecyclerView.setAdapter(mReportCenterAdapter);
        setHead(R.string.setting_030203);
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
    }

    private void saveParam() {
        if (mDoorStatus == 1) {
            EntranceGuardDao.setDoorState(mDoorStatus, mAlarmOut, mReportCenter);
        } else {
            EntranceGuardDao.setDoorStateCheck(mDoorStatus);
        }

        //发送广播
        Intent intent = new Intent(CommSysDef.BROADCAST_DOOR_STATE);
        App.getInstance().sendBroadcast(intent);

        mTvHint.setText(R.string.set_success);
        showView(false);
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                exitFragment();
            }
        }, Constant.SET_HINT_TIMEOUT);
    }
}
