package com.mili.smarthome.tkj.main.face.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.main.activity.BaseK7Activity;
import com.mili.smarthome.tkj.main.widget.KeyBoardItemView;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.utils.AppManage;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FaceManageActivity extends BaseK7Activity implements KeyBoardItemView.IOnKeyClickListener {

    public static final String EXTRA_CARDNO = "extra_cardno";

    @BindView(R.id.key_cancle)
    KeyBoardItemView keyCancle;
    @BindView(R.id.key_add)
    KeyBoardItemView keyAdd;
    @BindView(R.id.key_del)
    KeyBoardItemView keyDel;

    private String mCardNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_manage);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mCardNo = intent.getStringExtra(EXTRA_CARDNO);


        TextView tvAdd = (TextView) keyAdd.findViewById(R.id.tv_title);
        tvAdd.setText(R.string.pub_add);
        TextView tvDel = (TextView) keyDel.findViewById(R.id.tv_title);
        tvDel.setText(R.string.pub_del);
    }

    @Override
    protected void onResume() {
        super.onResume();
        KeyBoardItemView.setOnkeyClickListener(this);
    }

    @Override
    public void OnViewDownClick(int code, View view) {
        int position = AppManage.getInstance().getPosition(code);
        switch (position) {
            case Constant.KeyNumId.KEY_NUM_2:
                AppManage.getInstance().imgDown(keyAdd);
                break;
            case Constant.KeyNumId.KEY_NUM_5:
                AppManage.getInstance().imgDown(keyDel);
                break;
            case Constant.KeyNumId.KEY_NUM_12:
            case Constant.KeyNumId.KEY_NUM_13:
            case Constant.KeyNumId.KEY_NUM_14:
                AppManage.getInstance().keyBoardDown(keyCancle);
                break;
        }
    }

    @Override
    public void OnViewUpClick(int code, View view) {
        int position = AppManage.getInstance().getPosition(code);
        switch (position) {
            case Constant.KeyNumId.KEY_NUM_2:
                AppManage.getInstance().imgUp(keyAdd);
                gotoEnroll();
                break;
            case Constant.KeyNumId.KEY_NUM_5:
                AppManage.getInstance().imgUp(keyDel);
                gotoDelete();
                break;
            case Constant.KeyNumId.KEY_NUM_12:
            case Constant.KeyNumId.KEY_NUM_13:
            case Constant.KeyNumId.KEY_NUM_14:
                AppManage.getInstance().keyBoardUp(keyCancle);
                AppManage.getInstance().restartLauncherAct();
                break;
        }
    }

    private void gotoEnroll() {
        Class<? extends Activity> clazz;
        switch (AppConfig.getInstance().getFaceManufacturer()) {
            case 0:
                clazz = WffrFaceEnrollActivity.class;
                break;
            case 1:
                clazz = MegviiFaceEnrollActivity.class;
                break;
            default:
                return;
        }
        Intent intent = new Intent(this, clazz);
        intent.putExtra(EXTRA_CARDNO, mCardNo);
        startActivity(intent);
    }

    private void gotoDelete() {
        Intent intent = new Intent(this, FaceDeleteActivity.class);
        intent.putExtra(EXTRA_CARDNO, mCardNo);
        startActivity(intent);
    }
}
