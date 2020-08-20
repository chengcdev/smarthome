package com.mili.smarthome.tkj.main.face.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.facefunc.FacePresenterProxy;
import com.mili.smarthome.tkj.main.activity.BaseK7Activity;
import com.mili.smarthome.tkj.main.widget.KeyBoardItemView;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.utils.AppManage;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FaceDeleteActivity extends BaseK7Activity implements KeyBoardItemView.IOnKeyClickListener {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ll_hint)
    LinearLayout llHint;
    @BindView(R.id.key_ok)
    KeyBoardItemView keyOK;
    @BindView(R.id.key_cancle)
    KeyBoardItemView keyCancle;

    private String mCardNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_del);
        ButterKnife.bind(this);
        KeyBoardItemView.setOnkeyClickListener(this);

        Intent intent = getIntent();
        mCardNo = intent.getStringExtra(FaceManageActivity.EXTRA_CARDNO);
    }

    @Override
    public void OnViewDownClick(int code, View view) {
        int position = AppManage.getInstance().getPosition(code);
        switch (position) {
            case Constant.KeyNumId.KEY_NUM_9:
                AppManage.getInstance().keyBoardDown(keyOK);
                break;
            case Constant.KeyNumId.KEY_NUM_11:
                AppManage.getInstance().keyBoardDown(keyCancle);
                break;
        }
    }

    @Override
    public void OnViewUpClick(int code, View view) {
        int position = AppManage.getInstance().getPosition(code);
        switch (position) {
            case Constant.KeyNumId.KEY_NUM_9:
                AppManage.getInstance().keyBoardUp(keyOK);
                delFaceInfo(mCardNo);
                break;
            case Constant.KeyNumId.KEY_NUM_11:
                AppManage.getInstance().keyBoardUp(keyCancle);
                finish();
                break;
        }
    }

    private void delFaceInfo(String cardNo) {
        FacePresenterProxy.delFaceInfo(cardNo);
        llHint.setVisibility(View.GONE);
        tvTitle.setText(R.string.face_manage_del_suc);
        tvTitle.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }

}
