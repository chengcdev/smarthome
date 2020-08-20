package com.mili.smarthome.tkj.set.fragment;


import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.dao.ResetFactoryDao;
import com.mili.smarthome.tkj.main.fragment.BaseKeyBoardFragment;
import com.mili.smarthome.tkj.main.interf.ISetCallBackListener;
import com.mili.smarthome.tkj.main.widget.SetSuccessView;
import com.mili.smarthome.tkj.set.Constant;

/**
 * 恢复出厂设置
 */

public class ResetFactoryFragment extends BaseKeyBoardFragment implements ISetCallBackListener {


    private TextView tvTitle;
    private SetSuccessView successView;


    @Override
    public int getLayout() {
        return R.layout.fragment_restart_factory;
    }


    @Override
    public void initView() {
        tvTitle = (TextView) getContentView().findViewById(R.id.tv_title);
        successView = (SetSuccessView) getContentView().findViewById(R.id.root);
    }

    @Override
    public void initAdapter() {
       tvTitle.setText(getString(R.string.setting_reset_factory));
    }


    @Override
    public void initListener() {

    }

    @Override
    public void onResume() {
        super.onResume();
        successView.setSuccessListener(this);

    }


    @Override
    public void setKeyBoard(int viewId, String keyId) {
        switch (keyId) {
            case Constant.KEY_CANCLE:
                //退出界面
                getActivity().getSupportFragmentManager().popBackStack();
                break;
            case Constant.KEY_CONFIRM:
                successView.showProcessing(getString(R.string.setting_reset_factory_processing), new ISetCallBackListener() {
                    @Override
                    public void success() {
                        ResetFactoryDao factoryDao = new ResetFactoryDao();
                        //恢复出厂
                        factoryDao.resetDatas();
                    }

                    @Override
                    public void fail() {

                    }
                });
                break;
            case Constant.KEY_UP:
                break;
            case Constant.KEY_DELETE:
                break;
            case Constant.KEY_NEXT:
                break;
            default:
                break;
        }
    }


    @Override
    public void success() {

    }

    @Override
    public void fail() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

}
