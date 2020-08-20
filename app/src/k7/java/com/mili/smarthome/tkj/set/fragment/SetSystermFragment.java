package com.mili.smarthome.tkj.set.fragment;


import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;

import com.android.CommTypeDef;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.main.adapter.SettingAdapter;
import com.mili.smarthome.tkj.main.entity.SettingModel;
import com.mili.smarthome.tkj.main.fragment.BaseKeyBoardFragment;
import com.mili.smarthome.tkj.main.widget.KeyBoardRecyclerView;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.utils.AppManage;

import java.util.List;

/**
 * 系统设置
 */

public class SetSystermFragment extends BaseKeyBoardFragment {


    private TextView tvTitle;
    private KeyBoardRecyclerView rv;
    private int count;
    private List<SettingModel> datas;
    private SettingAdapter adapter;
    private LinearLayoutManager manager;
    private FullDeviceNo fullDeviceNo;

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
        tvTitle.setText(getString(R.string.setting_systerm));

        fullDeviceNo = new FullDeviceNo(getContext());

        datas = Constant.getSetSysList();
        //列表显示，梯口号设置改为区口号设置
        if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_AREA) {
            for (SettingModel settingModel : datas) {
                if (settingModel.getkId().equals(Constant.SettinSystermId.SYSTERM_TKH)) {
                    settingModel.setName(getString(R.string.setting_qkh_set));
                }
            }
        }
        rv.initAdapter(datas);
    }


    @Override
    public void initListener() {

    }


    @Override
    public void setKeyBoard(int viewId, String keyId) {
        switch (keyId) {
            case Constant.KEY_CANCLE:
                //退出界面
                exitFragment(this);
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
        switch (sId) {
            case Constant.SettinSystermId.SYSTERM_NET:
                SetNetFragment setNetFragment = new SetNetFragment();
                AppManage.getInstance().replaceFragment(getActivity(),setNetFragment);
                break;
            case Constant.SettinSystermId.SYSTERM_TKH:
                if (fullDeviceNo == null) {
                    fullDeviceNo = new FullDeviceNo(getContext());
                }
                //梯口机
                if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
                    SetLadderNoFragment setDeviceNoFragment = new SetLadderNoFragment();
                    AppManage.getInstance().replaceFragment(getActivity(),setDeviceNoFragment);
                }else  {
                    //区口机
                    SetDistrictNoFragment districtNoFragment = new SetDistrictNoFragment();
                    AppManage.getInstance().replaceFragment(getActivity(),districtNoFragment);
                }
                break;
            case Constant.SettinSystermId.SYSTERM_DEVICES_RULE:
                SetDeviceRuleFragment setDeviceRuleFragment = new SetDeviceRuleFragment();
                AppManage.getInstance().replaceFragment(getActivity(),setDeviceRuleFragment);
                break;
            case Constant.SettinSystermId.SYSTERM_SENIOR:
                SetSeniorFragment setSeniorFragment = new SetSeniorFragment();
                AppManage.getInstance().replaceFragment(getActivity(),setSeniorFragment);
                break;
        }
    }
}
