package com.mili.smarthome.tkj.set.fragment;


import android.os.Bundle;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.facefunc.FacePresenterProxy;
import com.mili.smarthome.tkj.main.fragment.BaseKeyBoardFragment;
import com.mili.smarthome.tkj.main.interf.ISetCallBackListener;
import com.mili.smarthome.tkj.main.widget.SetSuccessView;
import com.mili.smarthome.tkj.set.Constant;


public class SetClearFragment extends BaseKeyBoardFragment implements ISetCallBackListener {

    private TextView tvTitle;
    private String code;
    private SetSuccessView setSuccessView;

    @Override
    public int getLayout() {
        return R.layout.set_clear_view;
    }

    @Override
    public void initView() {
        tvTitle = (TextView) getContentView().findViewById(R.id.tv_title);
        setSuccessView = (SetSuccessView) getContentView().findViewById(R.id.root);
        setSuccessView.setSuccessListener(this);
    }

    @Override
    public void initAdapter() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            code = bundle.getString(Constant.KEY_PARAM);
            if (code != null) {
                switch (code) {
                    //清理人脸
                    case Constant.ClearId.CLEAR_FACE:
                        tvTitle.setText(getString(R.string.setting_face_clear));
                        break;
                    default:
                        break;
                }
            }
        }
    }


    @Override
    public void initListener() {

    }

    @Override
    public void setKeyBoard(int viewId, String keyId) {
        switch (keyId) {
            case Constant.KEY_CANCLE:
                exitFragment(this);
                break;
            case Constant.KEY_CONFIRM:
                if (code != null) {
                    switch (code) {
                        //清理人脸
                        case Constant.ClearId.CLEAR_FACE:
                            boolean result = FacePresenterProxy.clearFaceInfo();
                            if (result) {
                                setSuccessView.showSuccessView(getString(R.string.setting_success),2000,this);
                            }else {
                                setSuccessView.showSuccessView(getString(R.string.set_fail),2000,this);
                            }
                            break;
                        default:
                            break;
                    }
                }
                break;
        }
    }


    @Override
    public void success() {
        exitFragment(this);
    }

    @Override
    public void fail() {
        exitFragment(this);
    }
}
