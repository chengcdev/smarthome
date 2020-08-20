package com.mili.smarthome.tkj.setting.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.cardfunc.CardPresenter;
import com.mili.smarthome.tkj.appfunc.cardfunc.CardPresenterImpl;
import com.mili.smarthome.tkj.appfunc.facefunc.FacePresenterProxy;
import com.mili.smarthome.tkj.dao.FingerDao;
import com.mili.smarthome.tkj.dao.ResetFactoryDao;
import com.mili.smarthome.tkj.dao.UserInfoDao;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.setting.adapter.SetFuncAdapter;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;
import com.mili.smarthome.tkj.utils.ExternalMemoryUtils;

public class SetConfirmFragment extends BaseSetFragment implements View.OnClickListener {

    private RecyclerView rvFunc;
    private TextView tvHint;
    private String mFuncCode;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_confirm;
    }

    @Override
    protected void bindView() {
        rvFunc = findView(R.id.rv_func);
        rvFunc.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        tvHint = findView(R.id.tv_hint);
        findView(R.id.btn_cancel).setOnClickListener(this);
        findView(R.id.btn_confirm).setOnClickListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setBackVisibility(View.GONE);
        Bundle args = getArguments();
        if (args != null) {
            mFuncCode = args.getString(FragmentFactory.ARGS_FUNCCODE);
            rvFunc.setAdapter(new SetFuncAdapter(mContext, mFuncCode));
            switch (mFuncCode) {
                case SettingFunc.CARD_CLEAR:
                    tvHint.setText(R.string.clear_card_confirm);
                    break;
                case SettingFunc.PASSWORD_CLEAR:
                    tvHint.setText(R.string.clear_pwd_confirm);
                    break;
                case SettingFunc.SET_FACE_CLEAR:
                    tvHint.setText(R.string.face_manage_clear_confirm);
                    break;
                case SettingFunc.SET_FINGER_CLEAR:
                    tvHint.setText(R.string.finger_clear_confirm);
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

    @Override
    public void onDestroyView() {
        setBackVisibility(View.VISIBLE);
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                requestBack();
                break;
            case R.id.btn_confirm:
                boolean result = false;
                switch (mFuncCode) {
                    case SettingFunc.CARD_CLEAR:
                        result = clearCard();
                        break;
                    case SettingFunc.PASSWORD_CLEAR:
                        result = clearPassword();
                        break;
                    case SettingFunc.SET_FACE_CLEAR:
                        result = clearFace();
                        break;
                    case SettingFunc.SET_FINGER_CLEAR:
                        result = clearFinger();
                        break;
                    case SettingFunc.SET_MEMORY_FORMAT:
                        formatSD();
                        return;
                    case SettingFunc.SET_FACTORY:
                        resetAppData();
                        return;
                }
                if (result) {
                    showResultAndBack(R.string.setting_suc);
                } else {
                    showResultAndBack(R.string.setting_fail);
                }
                break;
        }
    }

    private boolean clearCard() {
        CardPresenter cardPresenter = new CardPresenterImpl();
        return cardPresenter.clearCards();
    }

    private boolean clearPassword() {
        UserInfoDao userInfoDao = new UserInfoDao();
        userInfoDao.clearAllOpenPwd();
        return true;
    }

    private boolean clearFace() {
        return FacePresenterProxy.clearFaceInfo();
    }

    private boolean clearFinger() {
        SinglechipClientProxy.getInstance().clearFinger();
        FingerDao fingerDao = new FingerDao();
        fingerDao.clear();
        return true;
    }

    private boolean formatSD() {
        showHint(R.string.setting_wait);
        ExternalMemoryUtils.externalMemoryFormat();
        showResultAndBack(R.string.setting_suc);
        return true;
    }

    private boolean resetAppData() {
        ResetFactoryDao factoryDao = new ResetFactoryDao();
        factoryDao.resetDatas();
        showHint(R.string.setting_ing);
        return true;
    }

}
