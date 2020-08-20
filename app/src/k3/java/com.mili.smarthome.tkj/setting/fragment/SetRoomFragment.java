package com.mili.smarthome.tkj.setting.fragment;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.dao.ResidentSettingDao;
import com.mili.smarthome.tkj.entities.ResidentSettingModel;
import com.mili.smarthome.tkj.widget.NumInputView;

/**
 * 住户设置
 */
public class SetRoomFragment extends BaseSetFragment {

    private NumInputView tvRoomStart;
    private NumInputView tvFloorCount;
    private NumInputView tvRoomCount;

    private ResidentSettingDao mResidentSettingDao;
    private ResidentSettingModel mResidentSetting;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_room;
    }

    @Override
    protected void bindView() {
        tvRoomStart = findView(R.id.tv_room_start);
        tvFloorCount = findView(R.id.tv_floor_count);
        tvRoomCount = findView(R.id.tv_room_count);
    }

    @Override
    protected void bindData() {
        if (mResidentSettingDao == null) {
            mResidentSettingDao = new ResidentSettingDao();
        }
        mResidentSetting = mResidentSettingDao.queryModel();
        tvRoomStart.setText(mResidentSetting.getRoomNoStart());
        tvFloorCount.setText(mResidentSetting.getFloorCount());
        tvRoomCount.setText(mResidentSetting.getFloorHouseNum());
        tvRoomStart.requestFocus();
    }

    @Override
    public boolean onKeyEvent(int keyCode, int keyState) {
        if (keyState == KEYSTATE_DOWN)
            return false;
        switch (keyCode) {
            case KEYCODE_0:
                inputNum(0);
                break;
            case KEYCODE_1:
            case KEYCODE_2:
            case KEYCODE_3:
            case KEYCODE_4:
            case KEYCODE_5:
            case KEYCODE_6:
            case KEYCODE_7:
            case KEYCODE_8:
            case KEYCODE_9:
                inputNum(keyCode);
                break;
            case KEYCODE_CALL:
                save();
                break;
            case KEYCODE_BACK:
                backspace();
                break;
        }
        return true;
    }

    private void save() {
        mResidentSetting.setRoomNoStart(tvRoomStart.getText().toString());
        mResidentSetting.setFloorCount(tvFloorCount.getText().toString());
        mResidentSetting.setFloorHouseNum(tvRoomCount.getText().toString());
        mResidentSettingDao.insertOrUpdate(mResidentSetting);
        showResultAndBack(R.string.setting_suc);
    }

}
