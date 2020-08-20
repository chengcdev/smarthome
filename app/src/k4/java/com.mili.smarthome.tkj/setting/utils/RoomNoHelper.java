package com.mili.smarthome.tkj.setting.utils;

import com.mili.smarthome.tkj.dao.ResidentSettingDao;
import com.mili.smarthome.tkj.entities.ResidentSettingModel;

import java.util.Locale;

public final class RoomNoHelper {

    private int mRoomNoStart;   // 起始房号
    private int mFloorCount;    // 楼层数
    private int mFloorHouseNum; // 每层户数

    private int mCurFloorNo;
    private int mCurRoomNo;

    public RoomNoHelper() {
        ResidentSettingDao rsDao = new ResidentSettingDao();
        ResidentSettingModel rsModel = rsDao.queryModel();
        mRoomNoStart = Integer.valueOf(rsModel.getRoomNoStart());
        mFloorCount = Integer.valueOf(rsModel.getFloorCount());
        mFloorHouseNum = Integer.valueOf(rsModel.getFloorHouseNum());
    }

    public void reset() {
        mCurFloorNo = 0;
        mCurRoomNo = 0;
    }

    public String getCurrentRoomNo() {
        return String.format(Locale.getDefault(), "%02d%02d", mCurFloorNo, mCurRoomNo);
    }

    public String getNextRoomNo() {
        if (mCurFloorNo == 0 || mCurRoomNo == 0) {
            mCurRoomNo = mRoomNoStart;
            mCurFloorNo = 1;
        } else {
            mCurRoomNo++;
            if (mCurRoomNo >= (mRoomNoStart + mFloorHouseNum)) {
                mCurRoomNo = mRoomNoStart;
                mCurFloorNo++;
            }
            if (mCurFloorNo > mFloorCount) {
                mCurFloorNo = 0;
                mCurRoomNo = 0;
            }
        }
        return getCurrentRoomNo();
    }

    public String getPreviousRoomNo() {
        if (mCurFloorNo == 0 || mCurRoomNo == 0) {
            mCurRoomNo = mRoomNoStart + mFloorHouseNum - 1;
            mCurFloorNo = mFloorCount;
        } else {
            mCurRoomNo--;
            if (mCurRoomNo < mRoomNoStart) {
                mCurRoomNo = mRoomNoStart + mFloorHouseNum - 1;
                mCurFloorNo--;
            }
            if (mCurFloorNo < 1) {
                mCurRoomNo = 0;
            }
        }
        return getCurrentRoomNo();
    }
}
