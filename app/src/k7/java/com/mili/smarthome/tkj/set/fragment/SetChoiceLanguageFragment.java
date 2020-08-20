package com.mili.smarthome.tkj.set.fragment;


import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.main.adapter.SettingAdapter;
import com.mili.smarthome.tkj.main.entity.SettingModel;
import com.mili.smarthome.tkj.main.fragment.BaseKeyBoardFragment;
import com.mili.smarthome.tkj.main.widget.KeyBoardRecyclerView;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.utils.AppManage;

import java.util.List;

/**
 * 选择语言
 */

public class SetChoiceLanguageFragment extends BaseKeyBoardFragment {


    private TextView tvTitle;
    private KeyBoardRecyclerView rv;
    private List<SettingModel> datas;
    private SettingAdapter adapter;
    private LinearLayoutManager manager;
    private String TAG = "SetChoiceLanguageFragment";
    private SetChoiceCardNumsFragment cardNumsFragment;
//    private SetChoiceCardNumsFragment cardNumsFragment;

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
        tvTitle.setText(getString(R.string.setting_choice_language));
        datas = Constant.getLanguageList();
        rv.initAdapter(datas);
    }


    @Override
    public void initListener() {
        Constant.IS_SET_FRAGMENT = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (AppManage.isChangeLang) {
            if (cardNumsFragment == null) {
                cardNumsFragment = new SetChoiceCardNumsFragment();
            }
            AppManage.getInstance().replaceFragment(getActivity(),cardNumsFragment);
        }
    }


    @Override
    public void setKeyBoard(int viewId, String keyId) {
        switch (keyId) {
            case Constant.KEY_CANCLE:
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
            //中文简体
            case Constant.SetLanguageId.LANGUAGE_ZH_SIMPLE:
                AppManage.isChangeLang = AppManage.getInstance().changeSystemLanguage(0);
                break;
            //中文繁体
            case Constant.SetLanguageId.LANGUAGE_ZH_FANTI:
                AppManage.isChangeLang= AppManage.getInstance().changeSystemLanguage(1);
                break;
            //英文
            case Constant.SetLanguageId.LANGUAGE_EG:
                AppManage.isChangeLang = AppManage.getInstance().changeSystemLanguage(2);
                break;
        }

        if (!AppManage.isChangeLang) {
            if (cardNumsFragment == null) {
                cardNumsFragment = new SetChoiceCardNumsFragment();
            }
            AppManage.getInstance().replaceFragment(getActivity(),cardNumsFragment);
        }
    }

}
