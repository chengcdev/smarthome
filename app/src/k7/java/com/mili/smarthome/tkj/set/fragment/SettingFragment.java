package com.mili.smarthome.tkj.set.fragment;


import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.main.activity.MainActivity;
import com.mili.smarthome.tkj.main.activity.direct.DirectPressMainActivity;
import com.mili.smarthome.tkj.main.entity.SettingModel;
import com.mili.smarthome.tkj.main.fragment.BaseKeyBoardFragment;
import com.mili.smarthome.tkj.main.fragment.MainFragment;
import com.mili.smarthome.tkj.main.widget.KeyBoardRecyclerView;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.set.activity.DeviceInfoActivity;
import com.mili.smarthome.tkj.utils.AppManage;

import java.util.List;
import java.util.Objects;

/**
 * 设置主界面
 */

public class SettingFragment extends BaseKeyBoardFragment {


    private TextView tvTitle;
    private KeyBoardRecyclerView rv;
    private List<SettingModel> datas;
    private Fragment currentFrag;
    private String selecId;
    private MainActivity mAct;

    @Override
    public int getLayout() {
        return R.layout.fragment_setting;
    }


    @Override
    public void initView() {
        rv = (KeyBoardRecyclerView) getContentView().findViewById(R.id.rv);
        tvTitle = (TextView) getContentView().findViewById(R.id.tv_title);
    }

    @Override
    public void initAdapter() {
        tvTitle.setText(getString(R.string.setting_title));
        initDataLists();
    }

    @Override
    public void onAttach(Context context) {
        mAct = (MainActivity) context;
        super.onAttach(context);
    }

    void initDataLists() {
        datas = Constant.getSettingList();
        rv.initAdapter(datas,selecId);
    }


    @Override
    public void initListener() {

    }

    @Override
    public void setKeyBoard(int viewId, String keyId) {
        switch (keyId) {
            case Constant.KEY_CANCLE:
                if (AppConfig.getInstance().getCallType() == 1) {
                    //直按式，退出当前activity
                    AppManage.getInstance().toActFinish(mContext, DirectPressMainActivity.class);
                } else {
                    //回退到主界面fragment
                    Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStackImmediate(MainFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    MainFragment mainFragment = new MainFragment();
                    AppManage.getInstance().replaceFragment(getActivity(), mainFragment);
                    //关闭直按式主界面
                    AppManage.getInstance().sendReceiver(Constant.ActionId.ACTION_ACTIVITY_CLOSE);
                }
                break;
            case Constant.KEY_CONFIRM:
                selecId = datas.get(rv.getItemPosition()).getkId();
                toFragment(selecId);
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


    private void toFragment(String sId) {
        switch (sId) {
            case Constant.SettingMainId.SETTING_SYSTERM:
                currentFrag = new SetSystermFragment();
                break;
            case Constant.SettingMainId.SETTING_CARD_MANAGE:
                currentFrag = new SetCardManageFragment();
                break;
            case Constant.SettingMainId.SETTING_PWD_MANAGE:
                if (AppConfig.getInstance().getPwdDoorMode() == 1) {
                    //高级密码模式
                    PwdontrolFragment pwdontrolFragment = new PwdontrolFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.KEY_PARAM, Constant.SetPwdManageId.PWD_UPDATE);
                    AppManage.getInstance().replaceFragment(mAct, pwdontrolFragment, bundle);
                    return;
                } else {
                    //简易密码模式
                    currentFrag = new SetPwdManageFragment();
                }
                break;
            case Constant.SettingMainId.SETTING_DOOR_BAN:
                currentFrag = new SetEntranceGuardFragment();
                break;
            case Constant.SettingMainId.SETTING_ZHUHU:
                currentFrag = new SetHouseholdFragment();
                break;
            case Constant.SettingMainId.SETTING_SOUND:
                currentFrag = new SetVolumeFragment();
                break;
            case Constant.SettingMainId.SETTING_DEVICES:
                startActivity(new Intent(getActivity(), DeviceInfoActivity.class));
                return;
            case Constant.SettingMainId.SETTING_DOOR_BLUE:
                currentFrag = new BluetoothOpenFragment();
                break;
            case Constant.SettingMainId.SETTING_RESET:
                currentFrag = new ResetFactoryFragment();
                break;
        }
        AppManage.getInstance().replaceFragment(mAct, currentFrag);
    }

}
