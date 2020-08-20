package com.mili.smarthome.tkj.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.android.CommStorePathDef;
import com.android.interf.IKeyEventListener;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.base.K3BaseFragment;
import com.mili.smarthome.tkj.base.K3Const;
import com.mili.smarthome.tkj.dao.param.ParamDao;
import com.mili.smarthome.tkj.main.adapter.NumBitmapAdapter;
import com.mili.smarthome.tkj.main.widget.GotoMainDefaultTask;
import com.mili.smarthome.tkj.setting.activity.SettingActivity;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;
import com.mili.smarthome.tkj.widget.MultiImageView;

public class AdminFragment extends K3BaseFragment {

    private TextView tvHint;
    private NumBitmapAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_admin_password;
    }

    @Override
    protected void bindView() {
        tvHint = findView(R.id.tv_hint);

        MultiImageView ivNums = findView(R.id.iv_nums);
        ivNums.setAdapter(mAdapter = new NumBitmapAdapter(mContext) {
            @Override
            public boolean isMask(int position) {
                return true;
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvHint.setVisibility(View.VISIBLE);
        mAdapter.clear();
        PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1305_PATH);
    }

    @Override
    public boolean onKeyEvent(int keyCode, int keyState) {
        if (keyState == IKeyEventListener.KEYSTATE_DOWN)
            return false;
        switch (keyCode) {
            case IKeyEventListener.KEYCODE_0:
                inputNum(0);
                break;
            case IKeyEventListener.KEYCODE_1:
            case IKeyEventListener.KEYCODE_2:
            case IKeyEventListener.KEYCODE_3:
            case IKeyEventListener.KEYCODE_4:
            case IKeyEventListener.KEYCODE_5:
            case IKeyEventListener.KEYCODE_6:
            case IKeyEventListener.KEYCODE_7:
            case IKeyEventListener.KEYCODE_8:
            case IKeyEventListener.KEYCODE_9:
                inputNum(keyCode);
                break;
            case IKeyEventListener.KEYCODE_BACK:
                backspace();
                break;
        }
        return true;
    }

    @Override
    protected void inputNum(int num) {
        if (mAdapter.getCount() >= 8) {
            return;
        }
        mAdapter.input(num);
        tvHint.setVisibility(View.INVISIBLE);
        if (mAdapter.getCount() == 8) {
            String adminPwd = ParamDao.getAdminPwd();
            FragmentActivity activity = getActivity();
            if (adminPwd.equals(mAdapter.getText()) && activity != null) {
                Intent intent = new Intent(mContext, SettingActivity.class);
                activity.startActivityForResult(intent, K3Const.REQUEST_SETTING);
                GotoMainDefaultTask.getInstance().run();
            } else {
                Bundle args = new Bundle();
                args.putInt(MainFragment.TEXT_ID, R.string.comm_text_f1);
                args.putInt(MainFragment.COLOR_ID, R.color.txt_red);
                ContextProxy.sendBroadcast(Const.Action.MAIN_DEFAULT, args);
            }
        }
    }

    @Override
    protected void backspace() {
        if (mAdapter.getCount() == 0) {
            GotoMainDefaultTask.getInstance().run();
            return;
        }
        mAdapter.backspace();
        if (mAdapter.getCount() == 0) {
            tvHint.setVisibility(View.VISIBLE);
        }
    }

}
