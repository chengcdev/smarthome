package com.mili.smarthome.tkj.setting.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.facefunc.FacePresenterProxy;
import com.mili.smarthome.tkj.dao.FingerDao;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;
import com.mili.smarthome.tkj.utils.ExternalMemoryUtils;
import com.mili.smarthome.tkj.view.SetOperateView;

public class SetClearDatasFragment extends BaseFragment implements View.OnClickListener, SetOperateView.IOperateListener {

    private TextView tvHint;
    private String mFuncCode;
    private SetOperateView mOperateView;
    private TextView mTvtitle1;
    private TextView mTvtitle2;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_clear_face;
    }

    @Override
    protected void bindView() {
        tvHint = findView(R.id.tv_hint);
        mOperateView = findView(R.id.rootview);
        mTvtitle1 = findView(R.id.tv_title1);
        mTvtitle2 = findView(R.id.tv_title2);
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
                    case SettingFunc.SET_FACE_CLEAR:
                        mTvtitle1.setText(getString(R.string.setting_0303));
                        mTvtitle2.setText(getString(R.string.setting_030301));
                        tvHint.setText(R.string.clear_face_record);
                        break;
                    case SettingFunc.SET_FINGER_PRINT_CLEAR:
                        mTvtitle1.setText(getString(R.string.setting_0305));
                        mTvtitle2.setText(getString(R.string.setting_030503));
                        tvHint.setText(R.string.clear_face_finger_print);
                        break;
                    case SettingFunc.SET_MEMORY_FORMAT:
                        mTvtitle1.setText(getString(R.string.setting_0407));
                        mTvtitle2.setText(getString(R.string.setting_040702));
                        tvHint.setText(R.string.memory_format_confirm);
                        break;
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                exitFragment(this);
                break;
            case R.id.btn_confirm:
                switch (mFuncCode) {
                    case SettingFunc.SET_FACE_CLEAR:
                        //清空人脸
                        boolean result = FacePresenterProxy.clearFaceInfo();
                        mOperateView.operateBackState(getString(R.string.set_success));
                        break;
                    case SettingFunc.SET_FINGER_PRINT_CLEAR:
                        SinglechipClientProxy.getInstance().clearFinger();
                        //清空数据库指纹
                        FingerDao fingerDao = new FingerDao();
                        fingerDao.clear();
                        //清空指纹
                        mOperateView.operateBackState(getString(R.string.set_success));
                        break;
                    case SettingFunc.SET_MEMORY_FORMAT:
                        //格式化存储
                        mOperateView.showProcessing(getString(R.string.set_please_wating));
                        final boolean isSuccess = ExternalMemoryUtils.externalMemoryFormat();
                        mMainHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (isSuccess) {
                                    mOperateView.operateBackState(getString(R.string.set_success));
                                }else {
                                    mOperateView.operateBackState(getString(R.string.set_fail));
                                }
                            }
                        }, 2000);
                        break;
                }

                break;
        }
    }


    @Override
    public void success() {
        exitFragment(SetClearDatasFragment.this);
    }

    @Override
    public void fail() {
        exitFragment(SetClearDatasFragment.this);
    }
}
