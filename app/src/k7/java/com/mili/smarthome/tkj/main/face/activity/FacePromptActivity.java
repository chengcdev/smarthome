package com.mili.smarthome.tkj.main.face.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.base.BaseActivity;
import com.mili.smarthome.tkj.main.activity.BaseK7Activity;
import com.mili.smarthome.tkj.main.widget.KeyBoardItemView;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.utils.AppManage;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FacePromptActivity extends BaseK7Activity implements KeyBoardItemView.IOnKeyClickListener {

    @BindView(R.id.key_ok)
    KeyBoardItemView keyOK;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_prompt);
        ButterKnife.bind(this);
        KeyBoardItemView.setOnkeyClickListener(this);
    }

    @Override
    public void OnViewDownClick(int code, View view) {
        int position = AppManage.getInstance().getPosition(code);
        switch (position) {
            case Constant.KeyNumId.KEY_NUM_12:
            case Constant.KeyNumId.KEY_NUM_13:
            case Constant.KeyNumId.KEY_NUM_14:
                AppManage.getInstance().keyBoardDown(keyOK);
                break;
        }
    }

    @Override
    public void OnViewUpClick(int code, View view) {
        int position = AppManage.getInstance().getPosition(code);
        switch (position) {
            case Constant.KeyNumId.KEY_NUM_12:
            case Constant.KeyNumId.KEY_NUM_13:
            case Constant.KeyNumId.KEY_NUM_14:
                AppManage.getInstance().keyBoardUp(keyOK);
                AppManage.getInstance().restartLauncherAct();
                break;
        }
    }

}
