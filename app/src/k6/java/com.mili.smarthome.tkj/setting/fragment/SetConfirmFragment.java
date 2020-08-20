package com.mili.smarthome.tkj.setting.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.cardfunc.CardPresenter;
import com.mili.smarthome.tkj.appfunc.cardfunc.CardPresenterImpl;
import com.mili.smarthome.tkj.dao.ResetFactoryDao;
import com.mili.smarthome.tkj.dao.UserInfoDao;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;
import com.mili.smarthome.tkj.view.SetOperateView;

public class SetConfirmFragment extends BaseFragment implements View.OnClickListener, SetOperateView.IOperateListener {

    private TextView tvHint;
    private String mFuncCode;
    private SetOperateView mOperateView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_confirm;
    }

    @Override
    protected void bindView() {
        tvHint = findView(R.id.tv_hint);
        mOperateView = findView(R.id.rootview);
        findView(R.id.btn_cancel).setOnClickListener(this);
        findView(R.id.btn_confirm).setOnClickListener(this);

        setBackVisibility(View.GONE);
    }

    @Override
    protected void bindData() {

        mOperateView.setSuccessListener(this);

        Bundle args = getArguments();
        if (args != null) {
            mFuncCode = args.getString(FragmentFactory.ARGS_FUNCCODE);
            if (!TextUtils.isEmpty(mFuncCode)) {
                switch (mFuncCode) {
                    case SettingFunc.CARD_CLEAR:
                        tvHint.setText(R.string.clear_card_confirm);
                        break;
                    case SettingFunc.PASSWORD_CLEAR:
                        tvHint.setText(R.string.clear_pwd_confirm);
                        break;
                    case SettingFunc.SET_FACTORY:
                        tvHint.setText(R.string.restore_factory_confirm);
                        break;
                    case SettingFunc.SET_MEMORY_FORMAT:
                        tvHint.setText(R.string.memory_format_confirm);
                        break;
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        setBackVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                requestBack();
                break;
            case R.id.btn_confirm:
                switch (mFuncCode) {
                    case SettingFunc.CARD_CLEAR:
                        //清空卡号
                        boolean result = clearCard();
                        if (result) {
                            //成功
                            mOperateView.operateBackState(getString(R.string.set_success));
                        }else {
                            //失败
                            mOperateView.operateBackState(getString(R.string.set_fail));
                        }
                        break;
                    case SettingFunc.PASSWORD_CLEAR:
                        //清空密码
                        clearPwd();
                        break;
                    case SettingFunc.SET_FACTORY:
                        //恢复出厂
                        resetAppData();
                        break;
                    case SettingFunc.SET_MEMORY_FORMAT:
                        //格式化存储卡

                        break;
                }
                setBackVisibility(View.GONE);
                break;
        }
    }

    private void resetAppData() {
        ResetFactoryDao factoryDao = new ResetFactoryDao();
        //恢复出厂
        factoryDao.resetDatas();
        //处理中
        mOperateView.showProcessing(getString(R.string.set_processing));
        //隐藏返回键
        setBackVisibility(View.GONE);
    }

    private void clearPwd() {
        //清空数据库密码
        UserInfoDao userPwd = new UserInfoDao();
        userPwd.clearAllOpenPwd();
        //成功
        mOperateView.operateBackState(getString(R.string.set_success));
    }


    @Override
    public void success() {
        requestBack();
    }

    @Override
    public void fail() {
        requestBack();
    }

    private boolean clearCard() {
        CardPresenter cardPresenter = new CardPresenterImpl();
        return cardPresenter.clearCards();
    }
}
