package com.mili.smarthome.tkj.set.fragment;


import android.os.Bundle;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.main.entity.SettingModel;
import com.mili.smarthome.tkj.main.fragment.BaseKeyBoardFragment;
import com.mili.smarthome.tkj.main.widget.KeyBoardRecyclerView;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.utils.AppManage;

import java.util.List;


/**
 * 卡管理
 */

public class SetCardManageFragment extends BaseKeyBoardFragment {


    private TextView tvTitle;
    private KeyBoardRecyclerView rv;
    private List<SettingModel> datas;
    private CardControlFragment cardControlFragment;

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
        tvTitle.setText(getString(R.string.setting_card_manage));

        datas = Constant.getCardManageList();
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
        Bundle bundle = new Bundle();
        switch (sId) {
            case Constant.SetCardManageId.CARD_ADD:
                bundle.putString(Constant.KEY_PARAM,Constant.SetCardManageId.CARD_ADD);
                break;
            case Constant.SetCardManageId.CARD_DELETE:
                bundle.putString(Constant.KEY_PARAM,Constant.SetCardManageId.CARD_DELETE);
                break;
            case Constant.SetCardManageId.CARD_CLEAR:
                bundle.putString(Constant.KEY_PARAM,Constant.SetCardManageId.CARD_CLEAR);
                break;
        }
            cardControlFragment = new CardControlFragment();
            AppManage.getInstance().replaceFragment(getActivity(),cardControlFragment,bundle);
    }
}
