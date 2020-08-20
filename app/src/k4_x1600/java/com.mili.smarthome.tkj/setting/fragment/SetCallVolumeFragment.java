package com.mili.smarthome.tkj.setting.fragment;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.dao.param.VolumeParamDao;
import com.mili.smarthome.tkj.utils.SystemSetUtils;
import com.mili.smarthome.tkj.widget.NumInputView;

public class SetCallVolumeFragment extends K4BaseFragment {

    private static final String Tag = "SetCallVolumeFragment";
    private NumInputView mIvCallVolume;
    private TextView mTvHint, mTvHead;
    private LinearLayout mLlcontent;
    private RelativeLayout mLlbutton;

    private int mMaxVolume = 0;

    @Override
    public boolean onKeyCancel() {
        super.onKeyCancel();
        exitFragment();
        return true;
    }

    @Override
    public boolean onKeyConfirm() {
        super.onKeyConfirm();
        int volume = Integer.parseInt(mIvCallVolume.getText().toString());
        VolumeParamDao.setCallVolume(volume);

        mTvHint.setText(R.string.set_success);
        showView(false);
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                exitFragment();
            }
        }, Constant.SET_HINT_TIMEOUT);
        return true;
    }

    @Override
    public boolean onKey(int code) {
        super.onKey(code);
        if (code <= mMaxVolume) {
            mIvCallVolume.input(code);
            mIvCallVolume.setCursorIndex(0);
        }
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_callvolume;
    }

    @Override
    protected void bindView() {
        super.bindView();
        mLlcontent = findView(R.id.ll_content);
        mLlbutton = findView(R.id.ll_button);
        mTvHint = findView(R.id.tv_hint);
        mTvHead = findView(R.id.tv_head);
        mIvCallVolume = findView(R.id.iv_volume);
    }

    @Override
    protected void bindData() {
        super.bindData();
        if (mTvHead != null) {
            mTvHead.setText(R.string.setting_040601);
        }
        showView(true);
        mIvCallVolume.requestFocus();

        int callVolume = VolumeParamDao.getCallVolume();
        mMaxVolume = SystemSetUtils.getCallMaxVolume();
        mIvCallVolume.setText(String.valueOf(callVolume));
        Log.d(Tag, "maxvolume = " + mMaxVolume);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMainHandler.removeCallbacksAndMessages(0);
    }

    private void showView(boolean show) {
        if (show) {
            mLlcontent.setVisibility(View.VISIBLE);
            mLlbutton.setVisibility(View.VISIBLE);
            mTvHint.setVisibility(View.INVISIBLE);
        } else {
            mLlcontent.setVisibility(View.INVISIBLE);
            mLlbutton.setVisibility(View.INVISIBLE);
            mTvHint.setVisibility(View.VISIBLE);
        }
    }
}
