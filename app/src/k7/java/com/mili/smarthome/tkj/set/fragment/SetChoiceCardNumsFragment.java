package com.mili.smarthome.tkj.set.fragment;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;

import com.android.CommSysDef;
import com.android.CommTypeDef;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.dao.param.ParamDao;
import com.mili.smarthome.tkj.main.adapter.SettingAdapter;
import com.mili.smarthome.tkj.main.entity.SettingModel;
import com.mili.smarthome.tkj.main.fragment.BaseKeyBoardFragment;
import com.mili.smarthome.tkj.main.widget.KeyBoardRecyclerView;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.utils.AppManage;

import java.util.List;

/**
 * 选择卡号
 */

public class SetChoiceCardNumsFragment extends BaseKeyBoardFragment {


    private TextView tvTitle;
    private KeyBoardRecyclerView rv;
    private List<SettingModel> datas;
    private SettingAdapter adapter;
    private LinearLayoutManager manager;
    private FullDeviceNo fullDeviceNo;
    private String TAG = "SetChoiceLanguageFragment";
    private SetLadderNoFragment setDeviceNoFragment;
    private SetDistrictNoFragment setDistrictNoFragment;
    private String currentTitle;

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
        if (currentTitle != null && currentTitle.equals(getString(R.string.setting_choice_device_nature))) {
            datas = Constant.getDevicesNature();
            initListAdapter(getString(R.string.setting_choice_device_nature),datas,0);
        }else {
            datas = Constant.getCardNumsList();
            initListAdapter(getString(R.string.setting_choice_card_num),datas,1);
        }
    }


    @Override
    public void initListener() {

    }

    @Override
    public void setKeyBoard(int viewId, String keyId) {
        switch (keyId) {
            case Constant.KEY_CANCLE:
                String title = tvTitle.getText().toString();
                if (getString(R.string.setting_choice_card_num).equals(title)) {
                    exitFragment(this);
                } else if (getString(R.string.setting_choice_device_nature).equals(title)) {
                    initListAdapter(getString(R.string.setting_choice_card_num),
                            Constant.getCardNumsList(), getCardCount());
                }
                break;
            case Constant.KEY_CONFIRM:
                String sId = datas.get(rv.getItemPosition()).getkId();
                toFragment(sId);
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
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constant.SETTING_FIRST, true);
        switch (sId) {
            //6位卡号
            case Constant.SetCardManageId.CARD_NUMS_6:
                initListAdapter(getString(R.string.setting_choice_device_nature), Constant.getDevicesNature(), 0);
                //保存卡号位数
                saveCardNo(6);
                break;
            //8位卡号
            case Constant.SetCardManageId.CARD_NUMS_8:
                initListAdapter(getString(R.string.setting_choice_device_nature), Constant.getDevicesNature(), 0);
                saveCardNo(8);
                break;
            //梯口号
            case Constant.SettingMainId.SETTING_DEVICES_TK:
                if (setDeviceNoFragment == null) {
                    setDeviceNoFragment = new SetLadderNoFragment();
                }
                AppManage.getInstance().replaceFragment(getActivity(), setDeviceNoFragment, bundle);
                //保存设备类型
                saveDeviceType((byte) CommTypeDef.DeviceType.DEVICE_TYPE_STAIR);
                break;
            //区口号
            case Constant.SettingMainId.SETTING_DEVICES_QK:
                if (setDistrictNoFragment == null) {
                    setDistrictNoFragment = new SetDistrictNoFragment();
                }
                AppManage.getInstance().replaceFragment(getActivity(), setDistrictNoFragment, bundle);
                //保存设备类 型
                saveDeviceType((byte) CommTypeDef.DeviceType.DEVICE_TYPE_AREA);
                break;
        }
    }

    private void saveCardNo(int no) {
        ParamDao.setCardNoLen(no);
        //设置卡位数
        AppManage.getInstance().sendReceiver(CommSysDef.BROADCAST_CARDNUMS);
    }

    private void saveDeviceType(byte deviceType) {
        if (fullDeviceNo == null) {
            fullDeviceNo = new FullDeviceNo(getContext());
        }
        fullDeviceNo.setDeviceType(deviceType);
    }

    private void initListAdapter(String title, List<SettingModel> lists, int state) {
        tvTitle.setText(title);
        currentTitle = title;
        datas = lists;
        rv.initAdapter(lists,state);
    }

    private int getCardCount() {
        if (ParamDao.getCardNoLen() == 8) {
            return 1;
        }
        return 0;
    }

    @Override
    public void onStop() {
        super.onStop();
        AppManage.isChangeLang = false;
    }
}
