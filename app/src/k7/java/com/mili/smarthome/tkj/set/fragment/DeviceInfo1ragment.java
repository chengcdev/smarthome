package com.mili.smarthome.tkj.set.fragment;


import android.widget.LinearLayout;

import com.android.CommTypeDef;
import com.android.client.CardClient;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.appfunc.facefunc.FacePresenterProxy;
import com.mili.smarthome.tkj.dao.ResidentSettingDao;
import com.mili.smarthome.tkj.dao.param.EntranceGuardDao;
import com.mili.smarthome.tkj.main.fragment.BaseKeyBoardFragment;
import com.mili.smarthome.tkj.set.widget.DeviceInfoTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备信息1
 */

public class DeviceInfo1ragment extends BaseKeyBoardFragment {


    private LinearLayout mLinRoot;
    private List<String> dataList = new ArrayList<>();

    public int getLayout() {
        return R.layout.fragment_decice_info1;
    }

    @Override
    public void setKeyBoard(int viewId, String keyId) {

    }

    @Override
    public void initView() {
        mLinRoot = (LinearLayout) getContentView().findViewById(R.id.root);

    }

    @Override
    public void initAdapter() {
        dataList.clear();
        mLinRoot.removeAllViews();

        FullDeviceNo fullDeviceNo = new FullDeviceNo(getContext());
        //设备类型
        int type = fullDeviceNo.getDeviceType();
        String devType;
        String stairNo;
        if (type == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            devType = getString(R.string.intercall_dev_stair);
            //梯口号
            stairNo = getString(R.string.setting_tkh) + Integer.valueOf(fullDeviceNo.getStairNo());
        } else {
            devType = getString(R.string.intercall_dev_area);
            //区口号
            stairNo = getString(R.string.setting_qkh) + Integer.valueOf(fullDeviceNo.getStairNo());
        }
        //设备号
        String deviceNo = fullDeviceNo.getCurrentDeviceNo();

        //单元号
        String cellNo;
        if (fullDeviceNo.getUseCellNo() == 0) {
            cellNo = getString(R.string.setting_no);
        } else {
            cellNo = getString(R.string.setting_yes);
        }

        ResidentSettingDao residentSettingDao = new ResidentSettingDao();
        //起始房号
        int roomNumStart = Integer.valueOf(residentSettingDao.getRoomStart());
        //楼层数
        int floorNum = Integer.valueOf(residentSettingDao.getFloorCount());
        //每层户数
        int floorHouseNum = Integer.valueOf(residentSettingDao.getFloorHouseNum());

        //开锁类型
        String openLockType;
        //锁类型
        if (EntranceGuardDao.getOpenLockType() == 0) {
            openLockType = getString(R.string.device_info_lock_type) + getString(R.string.setting_close_often);
        } else {
            openLockType = getString(R.string.device_info_lock_type) + getString(R.string.setting_open_often);
        }
        //开锁时间
        String openLockTime = getString(R.string.device_info_lock_time) + EntranceGuardDao.getOpenLockTime() + "s";

        //门状态检测
        String doorStateCheckStr;
        //门检测状态
        if (EntranceGuardDao.getDoorStateCheck() == 0) {
            doorStateCheckStr = getString(R.string.device_info_lock_door_check) + getString(R.string.setting_no);
        } else {
            doorStateCheckStr = getString(R.string.device_info_lock_door_check) + getString(R.string.setting_yes);
        }
        //报警输出
        String alarmOutStr;
        //报警输出
        if (EntranceGuardDao.getAlarmOut() == 0) {
            alarmOutStr = getString(R.string.device_info_alarm_out) + getString(R.string.setting_no);
        } else {
            alarmOutStr = getString(R.string.device_info_alarm_out) + getString(R.string.setting_yes);
        }
        //上报中心
        String updateCenterStr;
        //上报中心
        if (EntranceGuardDao.getUpdateCenter() == 0) {
            updateCenterStr = getString(R.string.device_info_update_center) + getString(R.string.setting_no);
        } else {
            updateCenterStr = getString(R.string.device_info_update_center) + getString(R.string.setting_yes);
        }

        //呼叫方式
        String callType;
        if (AppConfig.getInstance().getCallType() == 0) {
            callType = getString(R.string.device_info_calltype_bm);
        } else {
            callType = getString(R.string.device_info_calltype_za);
        }
        //剩余卡数
        int surplusCards = CardClient.getInstance().GetCardFreeCount();

        //剩余人脸数
        long surplus = FacePresenterProxy.getSurplus();

        //获取数据
        dataList.add(getString(R.string.device_info_nature) + devType);
        dataList.add(stairNo);
        if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            dataList.add(getString(R.string.setting_deviceno) + deviceNo);
            if (fullDeviceNo.getRoomNoLen() == 4) {
                dataList.add(getString(R.string.device_info_room_start) + roomNumStart);
                dataList.add(getString(R.string.device_info_floor_num) + floorNum);
                dataList.add(getString(R.string.device_info_floor_num_user) + floorHouseNum);
            }
        }
        dataList.add(openLockType);
        dataList.add(openLockTime);
        dataList.add(doorStateCheckStr);
        dataList.add(alarmOutStr);
        dataList.add(updateCenterStr);
        dataList.add(getString(R.string.device_info_call_type) + callType);
        dataList.add(getString(R.string.device_info_unit_num) + cellNo);
        dataList.add(getString(R.string.device_info_card_num) + surplusCards);
        //剩余用户数
        if (AppConfig.getInstance().getFaceModule() == 1 && AppConfig.getInstance().getFaceRecognition() == 1) {
            dataList.add(getString(R.string.device_info_face_num) + surplus);
        }

        for (int i = 0; i < dataList.size(); i++) {
            DeviceInfoTextView textView = new DeviceInfoTextView(getContext());
            textView.setText(dataList.get(i));
            mLinRoot.addView(textView);
        }
    }


    @Override
    public void initListener() {

    }

}
