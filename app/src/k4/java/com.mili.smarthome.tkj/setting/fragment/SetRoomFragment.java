package com.mili.smarthome.tkj.setting.fragment;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.dao.ResidentSettingDao;
import com.mili.smarthome.tkj.entities.ResidentSettingModel;
import com.mili.smarthome.tkj.widget.NumInputView;

public class SetRoomFragment extends K4BaseFragment {

    private LinearLayout mLlcontent;
    private RelativeLayout mLlButton;
    private NumInputView mIvRoomStart;
    private NumInputView mIvFloornum;
    private NumInputView mIvRoomnum;
    private TextView mTvHint;

    private ResidentSettingDao mResidentSettingDao;
    private ResidentSettingModel mResidentSetting;


    @Override
    public boolean onKeyCancel() {
        super.onKeyCancel();
        backspaceExit();
        return true;
    }

    @Override
    public boolean onKeyConfirm() {
        super.onKeyConfirm();

        if (mIvRoomStart != null) {
            saveData();
            mTvHint.setText(R.string.set_ok);
            showView(false);
            mMainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    exitFragment();
                }
            }, Constant.SET_HINT_TIMEOUT);
        }
        return true;
    }

    @Override
    public boolean onKey(int code) {
        super.onKey(code);
        inputNum(code);
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_room;
    }

    @Override
    protected void bindView() {
        super.bindView();
        TextView head = findView(R.id.tv_head);
        if (head != null) {
            head.setText(R.string.setting_0404);
        }

        mIvRoomStart = findView(R.id.iv_startroom);
        mIvFloornum = findView(R.id.iv_floornum);
        mIvRoomnum = findView(R.id.iv_unitnum);

        mTvHint = findView(R.id.tv_hint);
        if (mTvHint != null) {
            mTvHint.setVisibility(View.INVISIBLE);
        }

        mLlcontent = findView(R.id.ll_content);
        mLlButton = findView(R.id.ll_button);
    }

    @Override
    protected void bindData() {
        super.bindData();
        showView(true);

        if (mResidentSettingDao == null) {
            mResidentSettingDao = new ResidentSettingDao();
        }
        mResidentSetting = mResidentSettingDao.queryModel();
        mIvRoomStart.setText(mResidentSetting.getRoomNoStart());
        mIvFloornum.setText(mResidentSetting.getFloorCount());
        mIvRoomnum.setText(mResidentSetting.getFloorHouseNum());

        mIvRoomStart.requestFocus();
        mIvRoomStart.setCursorIndex(0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMainHandler.removeCallbacksAndMessages(0);
    }

    private void showView(boolean show) {
        if (show) {
            mLlcontent.setVisibility(View.VISIBLE);
            mLlButton.setVisibility(View.VISIBLE);
            mTvHint.setVisibility(View.INVISIBLE);
        } else {
            mLlcontent.setVisibility(View.INVISIBLE);
            mLlButton.setVisibility(View.INVISIBLE);
            mTvHint.setVisibility(View.VISIBLE);
        }
    }

    private void saveData() {
        mResidentSetting.setRoomNoStart(mIvRoomStart.getText().toString());
        mResidentSetting.setFloorCount(mIvFloornum.getText().toString());
        mResidentSetting.setFloorHouseNum(mIvRoomnum.getText().toString());
        mResidentSettingDao.insertOrUpdate(mResidentSetting);
    }
}
