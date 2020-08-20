package com.mili.smarthome.tkj.set.fragment;


import android.widget.TextView;

import com.android.CommSysDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.dao.param.EntranceGuardDao;
import com.mili.smarthome.tkj.main.entity.SettingModel;
import com.mili.smarthome.tkj.main.fragment.BaseKeyBoardFragment;
import com.mili.smarthome.tkj.main.interf.ISetCallBackListener;
import com.mili.smarthome.tkj.main.widget.KeyBoardRecyclerView;
import com.mili.smarthome.tkj.main.widget.SetSuccessView;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.utils.AppManage;

import java.util.ArrayList;
import java.util.List;

/**
 * 门禁设置
 */

public class SetEntranceGuardFragment extends BaseKeyBoardFragment {
    private TextView tvTitle;
    private KeyBoardRecyclerView rv;
    private List<SettingModel> mList;
    private SetSuccessView successView;
    private int currentOpenType;
    private int currentOpenTime;
    private int currentDoorStateCheck;
    private int currentAlarmOut;
    private int currentUpdateCenter;

    @Override
    public int getLayout() {
        return R.layout.fragment_setting;
    }


    @Override
    public void initView() {
        rv = (KeyBoardRecyclerView) getContentView().findViewById(R.id.rv);
        tvTitle = (TextView) getContentView().findViewById(R.id.tv_title);
        successView = (SetSuccessView) getContentView().findViewById(R.id.root);
    }

    @Override
    public void initAdapter() {
        tvTitle.setText(getString(R.string.setting_entrance_guard));
        mList = Constant.getEntranceGuardList();
        rv.initAdapter(mList);
    }


    @Override
    public void initListener() {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void setKeyBoard(int viewId, String keyId) {
        switch (keyId) {
            case Constant.KEY_CANCLE:
                toCancle();
                break;
            case Constant.KEY_CONFIRM:
                String sId = mList.get(rv.getItemPosition()).getkId();
                String t = tvTitle.getText().toString();
                toFragment(sId, t);
                break;
            case Constant.KEY_UP:
                rv.preScroll();
                break;
            case Constant.KEY_DELETE:

                break;
            case Constant.KEY_NEXT:
                rv.nextScroll();
                break;
        }
    }

    private void toCancle() {
        String title = tvTitle.getText().toString();
        if (getString(R.string.setting_open_lock_time).equals(title)) {
            int openLockType = EntranceGuardDao.getOpenLockType();
            rv.setItemPosition(0);
            initListAdapter(getString(R.string.setting_lock_type), Constant.getLockList(), openLockType);
        } else if (getString(R.string.setting_lock_type).equals(title)) {
            rv.setItemPosition(0);
            initAdapter();
        } else if (getString(R.string.setting_door_testing).equals(title)) {
            rv.setItemPosition(0);
            initAdapter();
        } else if (getString(R.string.setting_alarm_out).equals(title)) {
            rv.setItemPosition(0);
            int doorStateCheck = EntranceGuardDao.getDoorStateCheck();
            initListAdapter(getString(R.string.setting_door_testing), Constant.getDoorTestList(), doorStateCheck);
        } else if (getString(R.string.setting_center_up).equals(title)) {
            rv.setItemPosition(0);
            int alarmOut = EntranceGuardDao.getAlarmOut();
            initListAdapter(getString(R.string.setting_alarm_out), Constant.getDoorTestList(), alarmOut);
        } else if (getString(R.string.setting_saoma).equals(title)) {
            rv.setItemPosition(0);
            initAdapter();
        } else if (getString(R.string.setting_body_feel).equals(title)) {
            rv.setItemPosition(0);
            initAdapter();
        } else {
            //退出界面
            exitFragment(this);
        }
    }

    private void toFragment(String sId, String title) {
        switch (sId) {
            //门状态设置
            case Constant.SetEntranceGuardId.GUARD_DOOR_NATURE:
                int doorStateCheck = EntranceGuardDao.getDoorStateCheck();
                initListAdapter(getString(R.string.setting_door_testing), Constant.getDoorTestList(), doorStateCheck);
                break;
            //锁属性设置
            case Constant.SetEntranceGuardId.GUARD_LOCK_NATURE:
                int openLockType = EntranceGuardDao.getOpenLockType();
                initListAdapter(getString(R.string.setting_lock_type), Constant.getLockList(), openLockType);
                break;
            //人脸识别
            case Constant.SetEntranceGuardId.GUARD_FACE:
                if (getString(R.string.setting_body_feel).equals(title)) {
                    AppConfig.getInstance().setBodyInduction(1);
                    setSuccess();
                } else {
                    //人脸识别界面设置
                    SetFaceFragment setFaceFragment = new SetFaceFragment();
                    AppManage.getInstance().replaceFragment(getActivity(), setFaceFragment);
                }
                break;
            //扫码开门
            case Constant.SetEntranceGuardId.GUARD_SAOMA:
                if (getString(R.string.setting_body_feel).equals(title)) {
                    AppConfig.getInstance().setBodyInduction(2);
                    setSuccess();
                } else {
                    int sweepCodeOpen = AppConfig.getInstance().getQrScanEnabled();
                    initListAdapter(getString(R.string.setting_saoma), Constant.getSaomaList(), sweepCodeOpen);
                }
                break;
            //蓝牙开门器
            case Constant.SetEntranceGuardId.GUARD_BLUETOOTH_OPEN:
                AppConfig.getInstance().setBodyInduction(3);
                setSuccess();
                break;
            //人体感应
            case Constant.SetEntranceGuardId.GUARD_BODY_FEEL:
                int bodyFeeling = AppConfig.getInstance().getBodyInduction();
                String bodyId = "";
                switch (bodyFeeling) {
                    case 0:
                        bodyId = Constant.SetEntranceGuardId.GUARD_TRIGGER_SCREEN;
                        break;
                    case 1:
                        bodyId = Constant.SetEntranceGuardId.GUARD_FACE;
                        break;
                    case 2:
                        bodyId = Constant.SetEntranceGuardId.GUARD_SAOMA;
                        break;
                    case 3:
                        bodyId = Constant.SetEntranceGuardId.GUARD_BLUETOOTH_OPEN;
                        break;
                }
                List<SettingModel> bodyFeelList = Constant.getBodyFeelList();
                for (int i = 0; i < bodyFeelList.size(); i++) {
                    if (bodyId.equals(bodyFeelList.get(i).getkId())) {
                        initListAdapter(getString(R.string.setting_body_feel), bodyFeelList, i);
                        return;
                    }
                }
                initListAdapter(getString(R.string.setting_body_feel), bodyFeelList, 0);
                break;
            //触发开屏
            case Constant.SetEntranceGuardId.GUARD_TRIGGER_SCREEN:
                AppConfig.getInstance().setBodyInduction(0);
                setSuccess();
                break;
            //是
            case Constant.SetEntranceGuardId.GUARD_YES:
                //门状态检测
                if (getString(R.string.setting_door_testing).equals(title)) {
                    currentDoorStateCheck = 1;
                    int alarmOut = EntranceGuardDao.getAlarmOut();
                    initListAdapter(getString(R.string.setting_alarm_out), Constant.getDoorTestList(), alarmOut);
                } else if (getString(R.string.setting_alarm_out).equals(title)) {
                    currentAlarmOut = 1;
                    //报警输出
                    int updateCenter = EntranceGuardDao.getUpdateCenter();
                    initListAdapter(getString(R.string.setting_center_up), Constant.getDoorTestList(), updateCenter);
                } else if (getString(R.string.setting_center_up).equals(title)) {
                    //上报中心
                    //保存退出
                    currentUpdateCenter = 1;
                    saveDoorState(currentDoorStateCheck, currentAlarmOut, currentUpdateCenter);
                    setSuccess();
                }

                break;
            //否
            case Constant.SetEntranceGuardId.GUARD_NO:
                if (getString(R.string.setting_door_testing).equals(title)) {
                    currentDoorStateCheck = 0;
                    EntranceGuardDao.setDoorStateCheck(currentDoorStateCheck);
                    setSuccess();
                } else if (getString(R.string.setting_alarm_out).equals(title)) {
                    currentAlarmOut = 0;
                    int updateCenter = EntranceGuardDao.getUpdateCenter();
                    //报警输出
                    initListAdapter(getString(R.string.setting_center_up), Constant.getDoorTestList(), updateCenter);
                } else if (getString(R.string.setting_center_up).equals(title)) {
                    //上报中心
                    //保存退出
                    currentUpdateCenter = 0;
                    saveDoorState(currentDoorStateCheck, currentAlarmOut, currentUpdateCenter);
                    setSuccess();
                }
                break;
            //常开
            case Constant.SetEntranceGuardId.GUARD_OPEN:
                //开锁类型
                if (getString(R.string.setting_lock_type).equals(title)) {
                    int openLockTime = EntranceGuardDao.getOpenLockTime();
                    currentOpenType = 1;
                    initListAdapter(getString(R.string.setting_open_lock_time), getLockTime(), getLockTimeDirecCount(openLockTime));
                }
                break;
            //常闭
            case Constant.SetEntranceGuardId.GUARD_CLOSE:
                //开锁类型
                if (getString(R.string.setting_lock_type).equals(title)) {
                    int openLockTime = EntranceGuardDao.getOpenLockTime();
                    currentOpenType = 0;
                    initListAdapter(getString(R.string.setting_open_lock_time), getLockTime(), getLockTimeDirecCount(openLockTime));
                }
                break;
            //启用
            case Constant.SetEntranceGuardId.GUARD_ENABLE:
                if (getString(R.string.setting_saoma).equals(title)) {
                    AppConfig.getInstance().setQrScanEnabled(1);
                }
                setSuccess();
                break;
            //禁用
            case Constant.SetEntranceGuardId.GUARD_BAN:
                if (getString(R.string.setting_saoma).equals(title)) {
                    AppConfig.getInstance().setQrScanEnabled(0);
                }
                setSuccess();
                break;
            //0s
            case "0s":
                currentOpenTime = 0;
                //保存数据
                saveLockNature(currentOpenType, currentOpenTime);
                setSuccess();
                break;
            //3s
            case "3s":
                currentOpenTime = 3;
                saveLockNature(currentOpenType, currentOpenTime);
                setSuccess();
                break;
            //6s
            case "6s":
                currentOpenTime = 6;
                saveLockNature(currentOpenType, currentOpenTime);
                setSuccess();
                break;
            //9s
            case "9s":
                currentOpenTime = 9;
                saveLockNature(currentOpenType, currentOpenTime);
                setSuccess();
                break;
        }
    }

    private void setSuccess() {
        successView.showSuccessView(getString(R.string.setting_success), new ISetCallBackListener() {
            @Override
            public void success() {
                initListAdapter(getString(R.string.setting_entrance_guard), Constant.getEntranceGuardList(), 0);
            }

            @Override
            public void fail() {

            }
        });
    }

    private void initListAdapter(String title, List<SettingModel> lists, int selecPosition) {
        tvTitle.setText(title);
        mList = lists;
        rv.initAdapter(lists);
        rv.setItemPosition(selecPosition);
    }

    private List<SettingModel> getLockTime() {
        List<SettingModel> list = new ArrayList<>();
        list.clear();
        list.add(new SettingModel("0s", "0s"));
        list.add(new SettingModel("3s", "3s"));
        list.add(new SettingModel("6s", "6s"));
        list.add(new SettingModel("9s", "9s"));
        return list;
    }

    /**
     * @return 获取开锁时间的指向
     */
    private int getLockTimeDirecCount(int time) {
        switch (time) {
            case 0:
                return 0;
            case 3:
                return 1;
            case 6:
                return 2;
            case 9:
                return 3;
        }
        return 0;
    }

    /**
     * 保存锁属性设置
     *
     * @param openLockType 开锁类型
     * @param openLockTime 开锁时间
     */
    private void saveLockNature(int openLockType, int openLockTime) {
        EntranceGuardDao.setOpenLockType(openLockType);
        EntranceGuardDao.setOpenLockTime(openLockTime);
        //设置状态
        AppManage.getInstance().sendReceiver(CommSysDef.BROADCAST_LOCK_STATE);
    }


    /**
     * 保存门状态设置
     *
     * @param doorStateCheck 门检测状态
     * @param alarmOut       报警上报
     * @param updateCenter   上报中心
     */
    private void saveDoorState(int doorStateCheck, int alarmOut, int updateCenter) {
        EntranceGuardDao.setDoorStateCheck(doorStateCheck);
        EntranceGuardDao.setAlarmOut(alarmOut);
        EntranceGuardDao.setUpdateCenter(updateCenter);
        //设置状态
        AppManage.getInstance().sendReceiver(CommSysDef.BROADCAST_DOOR_STATE);
    }
}
