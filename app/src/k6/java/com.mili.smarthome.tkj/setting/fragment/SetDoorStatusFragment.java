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
 * 设置门状态
 */
public class SetDoorStatusFragment extends BaseFragment implements View.OnClickListener, SetOperateView.IOperateListener {

    private static final int DOOR_DETECTION = 0x030201;
    private static final int DOOR_ALARM = 0x030202;
    private static final int DOOR_UPLOAD = 0x030203;

    private View vwAlarm;
    private View vwUpload;
    private RecyclerView mRecyclerView;

    private int mSetFunc = DOOR_DETECTION;
    private ItemSelectorAdapter mAdapter;
    private int currentDoorState = 0;
    private int currenAlarmState = 0;
    private int currentCenterState = 0;
    private SetOperateView mOperateView;
    private ImageView mImaBack;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_door_status;
    }

    @Override
    protected void bindView() {
        vwAlarm = findView(R.id.ll_alarm);
        vwUpload = findView(R.id.ll_upload);
        mRecyclerView = findView(R.id.recyclerview);
        mOperateView = findView(R.id.rootview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        setBackVisibility(View.GONE);
        mImaBack = findView(R.id.iv_back);
        mImaBack.setOnClickListener(this);
        mOperateView.setSuccessListener(this);
    }

    @Override
    protected void bindData() {
        mImaBack.setVisibility(View.VISIBLE);
        currentDoorState = EntranceGuardDao.getDoorStateCheck();
        currenAlarmState = EntranceGuardDao.getAlarmOut();
        currentCenterState = EntranceGuardDao.getUpdateCenter();

        mAdapter = new SetAdapter(mContext);
        mAdapter.setSelection(currentDoorState);
        mRecyclerView.setAdapter(mAdapter);
        mSetFunc = DOOR_DETECTION;
        vwAlarm.setVisibility(View.GONE);
        vwUpload.setVisibility(View.GONE);
        mAdapter.setSelection(currentDoorState);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                if (mSetFunc == DOOR_DETECTION) {
                    setBackVisibility(View.VISIBLE);
                    requestBack();
                } else {
                    mSetFunc--;
                    if (mSetFunc == DOOR_DETECTION) {
                        vwAlarm.setVisibility(View.GONE);
                        vwUpload.setVisibility(View.GONE);
                        mAdapter.setSelection(currentDoorState);
                    } else if (mSetFunc == DOOR_ALARM) {
                        vwAlarm.setVisibility(View.VISIBLE);
                        vwUpload.setVisibility(View.GONE);
                        mAdapter.setSelection(currenAlarmState);
                    }
                }
                break;
        }
    }


    private class SetAdapter extends ItemSelectorAdapter {

        private SetAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getStringArrayId() {
            return R.array.setting_yesno;
        }

        @Override
        protected void onItemClick(int position) {
            switch (mSetFunc) {
                case DOOR_DETECTION:
                    currentDoorState = position;
                    vwAlarm.setVisibility(View.VISIBLE);
                    vwUpload.setVisibility(View.GONE);
                    mAdapter.setSelection(currenAlarmState);
                    mSetFunc++;
                    break;
                case DOOR_ALARM:
                    currenAlarmState = position;
                    vwAlarm.setVisibility(View.VISIBLE);
                    vwUpload.setVisibility(View.VISIBLE);
                    mAdapter.setSelection(currentCenterState);
                    mSetFunc++;
                    break;
                case DOOR_UPLOAD:
                    currentCenterState = position;
                    //设置成功
                    saveDatas();
                    break;
            }

        }

    }

    private void saveDatas() {
        EntranceGuardDao.setDoorStateCheck(currentDoorState);
        EntranceGuardDao.setAlarmOut(currenAlarmState);
        EntranceGuardDao.setUpdateCenter(currentCenterState);
        mOperateView.operateBackState(getString(R.string.set_success));
        mImaBack.setVisibility(View.GONE);
        setBackVisibility(View.VISIBLE);

        AppUtils.getInstance().sendReceiver(CommSysDef.BROADCAST_DOOR_STATE);
    }


    @Override
    public void success() {
        requestBack();
    }

    @Override
    public void fail() {

    }
}
