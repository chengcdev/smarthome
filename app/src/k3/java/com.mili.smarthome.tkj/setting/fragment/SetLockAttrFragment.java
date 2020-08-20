package com.mili.smarthome.tkj.setting.fragment;

import com.android.CommSysDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.dao.param.EntranceGuardDao;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;

import java.util.Arrays;
import java.util.List;

/**
 * 锁属性设置
 */
public class SetLockAttrFragment extends SetSelectorFragment {

    private List<String> mFuncList;
    private String[] mTypeOption;
    private String[] mTimeOption;

    private String mSetFunc;

    private int mOpenLockType;
    private int mOpenLockTime;

    @Override
    protected void bindView() {
        super.bindView();
        mFuncList = Arrays.asList(
                SettingFunc.SET_ENTRANCE_GUARD,
                SettingFunc.SET_LOCK_TYPE,
                SettingFunc.SET_LOCK_TIME
        );
        mTypeOption = new String[]{
                mContext.getString(R.string.setting_close_often),
                mContext.getString(R.string.setting_open_often)
        };
        mTimeOption = new String[]{
                "0S", "3S", "6S", "9S"
        };
    }

    @Override
    protected void bindData() {
        super.bindData();
        mOpenLockType = EntranceGuardDao.getOpenLockType();
        mOpenLockTime = EntranceGuardDao.getOpenLockTime();
        setLockType();
    }

    private void setLockType() {
        mSetFunc = SettingFunc.SET_LOCK_TYPE;
        setFuncList(mFuncList.subList(0, 2));
        setOptions(mTypeOption);
        setSelection(mOpenLockType);
    }

    private void setLockTime() {
        mSetFunc = SettingFunc.SET_LOCK_TIME;
        setFuncList(mFuncList);
        setOptions(mTimeOption);
        setSelection(mOpenLockTime/3);
    }

    @Override
    public void onBackPressed() {
        if (SettingFunc.SET_LOCK_TIME.equals(mSetFunc)) {
            setLockType();
        } else {
            requestBack();
        }
    }

    @Override
    public void onItemClick(int position) {
        if (SettingFunc.SET_LOCK_TYPE.equals(mSetFunc)) {
            mOpenLockType = position;
            setLockTime();
        } else if (SettingFunc.SET_LOCK_TIME.equals(mSetFunc)) {
            mOpenLockTime = position * 3;
            save();
        }
    }

    private void save() {
        EntranceGuardDao.setOpenLockAttr(mOpenLockType, mOpenLockTime);

        //发送广播
        ContextProxy.sendBroadcast(CommSysDef.BROADCAST_LOCK_STATE);

        showResultAndBack(R.string.setting_suc);
    }
}
