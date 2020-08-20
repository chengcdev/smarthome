package com.mili.smarthome.tkj.setting.fragment;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.adapter.KeyBoardAdapter;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.entity.KeyBoardBean;
import com.mili.smarthome.tkj.utils.KeyboardUtils;
import com.mili.smarthome.tkj.view.KeyBoardView;
import com.mili.smarthome.tkj.view.SetOperateView;

public class SetRtspFragment extends BaseFragment implements KeyBoardAdapter.IKeyBoardListener, SetOperateView.IOperateListener, View.OnClickListener {

    private KeyBoardView keyBoardView;
    private SetOperateView mOperateView;
    private ImageView mImaBack;
    private EditText etRtspUrl;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_rtsp;
    }

    @Override
    protected void bindView() {
        keyBoardView = findView(R.id.keyboardview);
        mOperateView = findView(R.id.rootview);
        mImaBack = findView(R.id.iv_back);
        etRtspUrl = findView(R.id.et_rtsp_url);
    }

    @Override
    protected void bindData() {
        keyBoardView.init(KeyBoardView.KEY_BOARD_SET);
        keyBoardView.setKeyBoardListener(this);
        mOperateView.setSuccessListener(this);
        setBackVisibility(View.GONE);
        mImaBack.setVisibility(View.VISIBLE);
        mImaBack.setOnClickListener(this);
        etRtspUrl.setText(AppConfig.getInstance().getRtspUrl());
    }

    @Override
    public void onKeyBoardListener(View view, int potion, KeyBoardBean keyBoardBean) {
        switch (keyBoardBean.getkId()) {
            case Const.KeyBoardId.KEY_CANCEL:
                exitFragment(this);
                break;
            case Const.KeyBoardId.KEY_CONFIRM:
                String rtspUrl = etRtspUrl.getText().toString();
                //保存数据
                AppConfig.getInstance().setRtspUrl(rtspUrl);
                mImaBack.setVisibility(View.GONE);
                mOperateView.operateBackState(getString(R.string.set_success));
                mOperateView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exitFragment(SetRtspFragment.this);
                    }
                }, 1000);
                break;
            default:
                String id = keyBoardBean.getkId();
                inputNum(Integer.valueOf(id));
                break;
        }

    }

    @Override
    public void success() {

    }

    @Override
    public void fail() {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_back) {
            exitFragment(this);
        }
    }

    @Override
    public void onDestroyView() {
        Activity activity = getActivity();
        if (activity != null) {
            KeyboardUtils.hide(activity);
        }
        super.onDestroyView();
    }

}
