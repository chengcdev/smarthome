package com.mili.smarthome.tkj.face;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.appfunc.facefunc.FacePresenter;
import com.mili.smarthome.tkj.appfunc.facefunc.MegviiFacePresenterImpl;
import com.mili.smarthome.tkj.appfunc.facefunc.WffrFacePresenterImpl;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.fragment.BaseMainFragment;
import com.mili.smarthome.tkj.utils.ViewUtils;

public class FaceDelFragment extends BaseMainFragment implements View.OnClickListener {

    public static final String ARGS_CARD_NO = "args_card_no";

    private static final int MSG_DEL_ING = 1;
    private static final int MSG_DEL_SUC = 2;
    private static final int MSG_BACK_MAIN = 3;

    private View vwConfirm;
    private TextView tvHint;

    private String mCardNo;

    @Override
    public int getLayout() {
        return R.layout.fragment_face_del;
    }

    @Override
    public void initView(View view) {
        vwConfirm = ViewUtils.findView(view, R.id.fl_confirm);
        tvHint = ViewUtils.findView(view, R.id.tv_hint);
        vwConfirm.setVisibility(View.VISIBLE);
        tvHint.setVisibility(View.GONE);
        ViewUtils.findView(view, R.id.btn_cancel).setOnClickListener(this);
        ViewUtils.findView(view, R.id.btn_confirm).setOnClickListener(this);

        Bundle args = getArguments();
        if (args != null) {
            mCardNo = args.getString(ARGS_CARD_NO);
        } else {
            backMainActivity();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                mContext.sendBroadcast(new Intent(Constant.Action.BODY_FACE_RECOGNITION_ACTION));
                break;
            case R.id.btn_confirm:
                delFaceInfo(mCardNo);
                break;
        }
    }


    private void delFaceInfo(String cardNo) {
        mMainHandler.sendEmptyMessage(MSG_DEL_ING);
        int faceManufacturer = AppConfig.getInstance().getFaceManufacturer();
        FacePresenter facePresenter;
        if (faceManufacturer == 1) {
            facePresenter = new MegviiFacePresenterImpl();
        } else {
            facePresenter = new WffrFacePresenterImpl();
        }
        facePresenter.delFaceInfo(cardNo);
        mMainHandler.sendEmptyMessage(MSG_DEL_SUC);
    }

    @Override
    protected void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_DEL_ING:
                vwConfirm.setVisibility(View.GONE);
                tvHint.setVisibility(View.VISIBLE);
                tvHint.setText(R.string.face_manage_del_ing);
                break;
            case MSG_DEL_SUC:
                vwConfirm.setVisibility(View.GONE);
                tvHint.setVisibility(View.VISIBLE);
                tvHint.setText(R.string.face_manage_del_suc);
                mMainHandler.sendEmptyMessageDelayed(MSG_BACK_MAIN, 2000);
                break;
            case MSG_BACK_MAIN:
                backMainActivity();
                break;
        }
    }
}
