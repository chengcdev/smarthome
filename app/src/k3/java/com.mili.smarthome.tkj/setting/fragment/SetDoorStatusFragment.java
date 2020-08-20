package com.mili.smarthome.tkj.setting.fragment;

import com.android.CommSysDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.dao.param.EntranceGuardDao;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;

import java.util.Arrays;
import java.util.List;

/**
 * 设置门状态
 */
public class SetDoorStatusFragment extends SetSelectorFragment {

    private List<String> mFuncList;

    private String mSetFunc;

    private int mDetection;
    private int mAlarm;
    private int mUpload;

    @Override
    protected void bindView() {
        super.bindView();
        mFuncList = Arrays.asList(
                SettingFunc.SET_ENTRANCE_GUARD,
                SettingFunc.SET_DOOR_DETECTION,
                SettingFunc.SET_DOOR_ALARM,
                SettingFunc.SET_DOOR_UPLOAD
        );
    }

    @Override
    protected void bindData() {
        super.bindData();
        mDetection = EntranceGuardDao.getDoorStateCheck();
        mAlarm = EntranceGuardDao.getAlarmOut();
        mUpload = EntranceGuardDao.getUpdateCenter();

        setOptions(new String[] {
                mContext.getString(R.string.pub_no),
                mContext.getString(R.string.pub_yes)
        });
        showSetDetection();
    }

    @Override
    public void onItemClick(int position) {
        if (SettingFunc.SET_DOOR_DETECTION.equals(mSetFunc)) {
            mDetection = position;
            if (position == 0) {
                mAlarm = 0;
                mUpload = 0;
                save();
            } else {
                showSetAlarm();
            }
        } else if (SettingFunc.SET_DOOR_ALARM.equals(mSetFunc)) {
            mAlarm = position;
            showSetUpload();
        } else if (SettingFunc.SET_DOOR_UPLOAD.equals(mSetFunc)) {
            mUpload = position;
            save();
        }
    }

    @Override
    public void onBackPressed() {
        if (SettingFunc.SET_DOOR_DETECTION.equals(mSetFunc)) {
            requestBack();
        } else if (SettingFunc.SET_DOOR_ALARM.equals(mSetFunc)) {
            showSetDetection();
        } else if (SettingFunc.SET_DOOR_UPLOAD.equals(mSetFunc)) {
            showSetAlarm();
        }
    }

    private void showSetDetection() {
        mSetFunc = SettingFunc.SET_DOOR_DETECTION;
        setFuncList(mFuncList.subList(0, 2));
        setSelection(mDetection);
    }

    private void showSetAlarm() {
        mSetFunc = SettingFunc.SET_DOOR_ALARM;
        setFuncList(mFuncList.subList(0, 3));
        setSelection(mAlarm);
    }

    private void showSetUpload() {
        mSetFunc = SettingFunc.SET_DOOR_UPLOAD;
        setFuncList(mFuncList);
        setSelection(mUpload);
    }

    private void save() {
        EntranceGuardDao.setDoorState(mDetection, mAlarm, mUpload);

        //发送广播
        ContextProxy.sendBroadcast(CommSysDef.BROADCAST_DOOR_STATE);

        showResultAndBack(R.string.setting_suc);
    }
}
