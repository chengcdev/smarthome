package com.mili.smarthome.tkj.set.fragment;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.AppPreferences;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.dao.param.ParamDao;
import com.mili.smarthome.tkj.main.activity.direct.DirectPressMainActivity;
import com.mili.smarthome.tkj.main.adapter.SettingAdapter;
import com.mili.smarthome.tkj.main.entity.SettingModel;
import com.mili.smarthome.tkj.main.fragment.BaseKeyBoardFragment;
import com.mili.smarthome.tkj.main.fragment.MainFragment;
import com.mili.smarthome.tkj.main.widget.KeyBoardRecyclerView;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.utils.AppManage;

import java.util.List;

/**
 * 呼叫方式
 */

public class SetCallTypeFragment extends BaseKeyBoardFragment {


    private TextView tvTitle;
    private KeyBoardRecyclerView rv;
    private int count;
    private List<SettingModel> datas;
    private SettingAdapter adapter;
    private LinearLayoutManager manager;
    private MainFragment mainFragment;

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
        tvTitle.setText(getString(R.string.setting_choice_call_type));

        datas = Constant.getCallTypeList();
        rv.initAdapter(datas);
    }


    @Override
    public void initListener() {

    }

    @Override
    public void setKeyBoard(int viewId, String keyId) {
        switch (keyId) {
            case Constant.KEY_CANCLE:
                //如果是启用中心机
                if (tvTitle.getText().toString().equals(getString(R.string.setting_enable_center))) {
                    initListAdapter(getString(R.string.setting_choice_call_type),Constant.getCallTypeList(),0);
                }else {
                    //退出界面
                    exitFragment(this);
                }
                break;
            case Constant.KEY_CONFIRM:
                String sId = datas.get(rv.getItemPosition()).getkId();
                notifyList(sId);
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

    private void updateList() {
        adapter.refreshList(count);
        adapter.notifyDataSetChanged();
        rv.scrollToPosition(count);
    }

    private void notifyList(String sId) {
        switch (sId) {
            //编码式
            case Constant.SettinSeniorId.SENIOR_CALL_TYPE_BIANMA:
                if (mainFragment == null) {
                    mainFragment = new MainFragment();
                }
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constant.SETTING_FIRST,true);
                AppManage.getInstance().replaceFragment(getActivity(),mainFragment,bundle);
                AppConfig.getInstance().setCallType(0);
                //是否第一次安装软件
                AppPreferences.setReset(false);
                break;
             //直按式
            case Constant.SettinSeniorId.SENIOR_CALL_TYPE_ZHIAN:
                initListAdapter(getString(R.string.setting_enable_center),Constant.getEnableCenterList(),0);
                AppConfig.getInstance().setCallType(1);
                break;
            //启用中心机
            case Constant.SettinSeniorId.SENIOR_ENABLE_CENTER_YES:
                ParamDao.setEnableCenter(1);
                //更新直按式主界面
                toDirecPressAct();
                //是否第一次安装软件
                AppPreferences.setReset(false);
                break;
            //不启用中心机
            case Constant.SettinSeniorId.SENIOR_ENABLE_CENTER_NO:
                ParamDao.setEnableCenter(0);
                //更新直按式主界面
                toDirecPressAct();
                //是否第一次安装软件
                AppPreferences.setReset(false);
                break;
        }
    }

    private void initListAdapter(String title, List<SettingModel> lists, int state) {
        tvTitle.setText(title);
        datas = lists;
        rv.setItemPosition(state);
        rv.initAdapter(datas);
    }

    private void toDirecPressAct(){
        //初始化住户列表
        AppManage.getInstance().toActFinish(getActivity(), DirectPressMainActivity.class);
    }


}
