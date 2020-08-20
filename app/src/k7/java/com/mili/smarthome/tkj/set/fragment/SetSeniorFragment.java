package com.mili.smarthome.tkj.set.fragment;


import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;

import com.android.CommSysDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.dao.param.AlarmParamDao;
import com.mili.smarthome.tkj.dao.param.ParamDao;
import com.mili.smarthome.tkj.main.adapter.SettingAdapter;
import com.mili.smarthome.tkj.main.entity.SettingModel;
import com.mili.smarthome.tkj.main.fragment.BaseKeyBoardFragment;
import com.mili.smarthome.tkj.main.interf.ISetCallBackListener;
import com.mili.smarthome.tkj.main.widget.KeyBoardRecyclerView;
import com.mili.smarthome.tkj.main.widget.SetSuccessView;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.set.resident.ResidentListManage;
import com.mili.smarthome.tkj.utils.AppManage;

import java.util.List;

/**
 * 高级设置
 */

public class SetSeniorFragment extends BaseKeyBoardFragment implements ISetCallBackListener {


    private TextView tvTitle;
    private KeyBoardRecyclerView rv;
    private List<SettingModel> mList;
    private SettingAdapter adapter;
    private LinearLayoutManager manager;
    //当前标题id，区分各个页面
    private String currentTitleId;
    private SetSuccessView successView;

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
        initListAdapter(getString(R.string.setting_senior), Constant.getSetSeniorList(),0);
    }


    @Override
    public void initListener() {
        successView.setSuccessListener(this);
    }


    @Override
    public void setKeyBoard(int viewId, String keyId) {
        switch (keyId) {
            case Constant.KEY_CANCLE:
                String currentTitle = tvTitle.getText().toString();
                if (getString(R.string.setting_senior).equals(currentTitle)) {
                    //退出界面
                    exitFragment(this);
                } else if (getString(R.string.setting_enable_center).equals(currentTitle)) {
                    initListAdapter(getString(R.string.setting_choice_call_type), Constant.getCallTypeList(), AppConfig.getInstance().getCallType());
                    currentTitleId = Constant.SettinSeniorId.SENIOR_CALLTYPE_SETTING;
                } else {
                    initListAdapter(getString(R.string.setting_senior), Constant.getSetSeniorList(),0);
                }
                break;
            case Constant.KEY_CONFIRM:
                String sId = mList.get(rv.getItemPosition()).getkId();
                refreshView(sId);
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


    private void refreshView(String sId) {
        switch (sId) {
            //密码进门模式
            case Constant.SettinSeniorId.SENIOR_PWD_DOOR_MODEL:
                int pwdDoorModel = AppConfig.getInstance().getOpenPwdMode();
                initListAdapter(getString(R.string.setting_senior_pwd_model), Constant.getPwdModelList(),pwdDoorModel);
                currentTitleId = sId;
                break;
            //报警参数
            case Constant.SettinSeniorId.SENIOR_ALARM_PARAM:
                int alarmParam = AlarmParamDao.getForceOpen();
                initListAdapter(getString(R.string.setting_senior_alarm_param), Constant.getAlarmParamList(),alarmParam);
                currentTitleId = sId;
                break;
            //拍照参数
            case Constant.SettinSeniorId.SENIOR_CAMERA_PARAM:
                SetPhotoParamFragment setPhotoParamFragment = new SetPhotoParamFragment();
                AppManage.getInstance().replaceFragment(getActivity(),setPhotoParamFragment);
                break;
            //省电模式
            case Constant.SettinSeniorId.SENIOR_POWER_SAVE:
                int powerSave = AppConfig.getInstance().getPowerSaving();
                initListAdapter(getString(R.string.setting_senior_power_save), Constant.getPowerSaveList(),powerSave);
                currentTitleId = sId;
                break;
            //动态密码
            case Constant.SettinSeniorId.SENIOR_PWD_DYNAMIC:
                int dynamic = AppConfig.getInstance().getPwdDynamic();
                initListAdapter(getString(R.string.setting_senior_pwd_dynamic), Constant.getPwdDynamicList(),dynamic);
                currentTitleId = sId;
                break;
            //屏保设置
            case Constant.SettinSeniorId.SENIOR_SCREEN_SETING:
                int screenPro = AppConfig.getInstance().getScreenSaver();
                initListAdapter(getString(R.string.setting_senior_screen_pro), Constant.getScreenProList(),screenPro);
                currentTitleId = sId;
                break;
            //灵敏度设置
            case Constant.SettinSeniorId.SENIOR_SENSITIVITY_SETTING:
                int sensitivitySet = ParamDao.getTouchSensitivity();
                if (sensitivitySet == 0) {
                    sensitivitySet = 2;
                }else if (sensitivitySet == 2) {
                    sensitivitySet = 0;
                }
                initListAdapter(getString(R.string.setting_senior_sensitivity), Constant.getSensitivityList(),sensitivitySet);
                currentTitleId = sId;
                break;
            //呼叫方式
            case Constant.SettinSeniorId.SENIOR_CALLTYPE_SETTING:
                int callType = AppConfig.getInstance().getCallType();
                initListAdapter(getString(R.string.setting_choice_call_type), Constant.getCallTypeList(),callType);
                currentTitleId = sId;
                break;
            //启用
            case Constant.SettinSeniorId.SENIOR_ENABLE:
                saveDatas(1);
                break;
            //不启用
            case Constant.SettinSeniorId.SENIOR_ENABLE_NOT:
                saveDatas(0);
                break;
            //关闭
            case Constant.SettinSeniorId.SENIOR_CLOSE:
                saveDatas(0);
                break;
            //高
            case Constant.SettinSeniorId.SENIOR_HIGE:
                saveDatas(2);
                break;
            //中
            case Constant.SettinSeniorId.SENIOR_MID:
                saveDatas(1);
                break;
            //低
            case Constant.SettinSeniorId.SENIOR_LOW:
                saveDatas(0);
                break;
            //简易模式
            case Constant.SettinSeniorId.SENIOR_MODEL_EASY:
                saveDatas(0);
                break;
            //高级模式
            case Constant.SettinSeniorId.SENIOR_MODEL_SENIOR:
                saveDatas(1);
                break;
            //编码式
            case Constant.SettinSeniorId.SENIOR_CALL_TYPE_BIANMA:
                saveDatas(0);
                break;
            //直按式
            case Constant.SettinSeniorId.SENIOR_CALL_TYPE_ZHIAN:
                saveDatas(1);
                int enableCenter = ParamDao.getEnableCenter();
                if (enableCenter == 1) {
                    //启用中心机
                    enableCenter = 0;
                }else {
                    //不启用中心机
                    enableCenter = 1;
                }
                initListAdapter(getString(R.string.setting_enable_center), Constant.getEnableCenterList(),enableCenter);
                currentTitleId = sId;
                break;
            //是
            case Constant.SettinSeniorId.SENIOR_ENABLE_CENTER_YES:
                //启用中心机
                if (getString(R.string.setting_enable_center).equals(tvTitle.getText().toString())) {
                    currentTitleId = Constant.SettinSeniorId.SENIOR_ENABLE_CENTER;
                }
                saveDatas(1);
                break;
            //否
            case Constant.SettinSeniorId.SENIOR_ENABLE_CENTER_NO:
                //启用中心机
                if (getString(R.string.setting_enable_center).equals(tvTitle.getText().toString())) {
                    currentTitleId = Constant.SettinSeniorId.SENIOR_ENABLE_CENTER;
                }
                saveDatas(0);
                break;
        }
    }

    private void initListAdapter(String title, List<SettingModel> lists,int selecPosition) {
        mList = lists;
        tvTitle.setText(title);
        rv.initAdapter(lists,selecPosition);
    }

    @Override
    public void success() {
        initAdapter();
    }

    @Override
    public void fail() {

    }

    private void saveDatas(int state) {
        switch (currentTitleId) {
            //密码进门模式
            case Constant.SettinSeniorId.SENIOR_PWD_DOOR_MODEL:
                AppConfig.getInstance().setOpenPwdMode(state);
                break;
            //报警参数
            case Constant.SettinSeniorId.SENIOR_ALARM_PARAM:
                AlarmParamDao.setForceOpen(state);
                AppManage.getInstance().sendReceiver(CommSysDef.BROADCAST_FORCEDOPENDOOR);
                break;
            //省电模式
            case Constant.SettinSeniorId.SENIOR_POWER_SAVE:
                AppConfig.getInstance().setPowerSaving(state);
                break;
            //动态密码
            case Constant.SettinSeniorId.SENIOR_PWD_DYNAMIC:
                AppConfig.getInstance().setPwdDynamic(state);
                break;
            //屏保设置
            case Constant.SettinSeniorId.SENIOR_SCREEN_SETING:
                AppConfig.getInstance().setScreenSaver(state);
                break;
            //灵敏度设置
            case Constant.SettinSeniorId.SENIOR_SENSITIVITY_SETTING:
                ParamDao.setTouchSensitivity(state);
                //设置灵敏度
                AppManage.getInstance().sendReceiver(CommSysDef.BROADCAST_TOUCHSENS);
                break;
            //呼叫方式
            case Constant.SettinSeniorId.SENIOR_CALLTYPE_SETTING:
                AppConfig.getInstance().setCallType(state);
                if (state == 1) {
                    return;
                }
                break;
            //启用中心机
            case Constant.SettinSeniorId.SENIOR_ENABLE_CENTER:
                ParamDao.setEnableCenter(state);
                //初始化住户列表
                ResidentListManage.getInstance().addResidentList();
                //刷新主界面列表
                AppManage.getInstance().sendReceiver(Constant.ActionId.ACTION_INIT_MAIN_DIRECT);
                break;

        }
        successView.showSuccessView(getString(R.string.setting_success));
    }
}
