package com.mili.smarthome.tkj.setting.fragment;

import android.view.View;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.adapter.KeyBoardAdapter;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.dao.param.VolumeParamDao;
import com.mili.smarthome.tkj.entity.KeyBoardBean;
import com.mili.smarthome.tkj.utils.SystemSetUtils;
import com.mili.smarthome.tkj.view.KeyBoardView;
import com.mili.smarthome.tkj.view.SetOperateView;
import com.mili.smarthome.tkj.widget.NumInputView;

/**
 * 通话音量
 */
public class SetCallVolumeFragment extends BaseFragment implements KeyBoardAdapter.IKeyBoardListener,SetOperateView.IOperateListener {


    private NumInputView mTvVolume;
    private KeyBoardView keyBoardView;
    private String callVolume = "";
    private SetOperateView setOperateView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_call_volume;
    }

    @Override
    protected void bindView() {
        mTvVolume = findView(R.id.tv_volume);
        keyBoardView = findView(R.id.keyboardview);
        setOperateView = findView(R.id.rootview);
    }

    @Override
    protected void bindData() {
        keyBoardView.init(KeyBoardView.KEY_BOARD_SET);
        keyBoardView.setKeyBoardListener(this);
        setOperateView.setSuccessListener(this);

        int callVolume = VolumeParamDao.getCallVolume();
        mTvVolume.setMaxLength(String.valueOf(callVolume).length());
        mTvVolume.setText(String.valueOf(callVolume));
        mTvVolume.requestFocus();
    }

    @Override
    public void onKeyBoardListener(View view, int potion, KeyBoardBean keyBoardBean) {
        switch (keyBoardBean.getkId()) {
            case Const.KeyBoardId.KEY_CANCEL:
                requestBack();
                break;
            case Const.KeyBoardId.KEY_CONFIRM:
                callVolume= mTvVolume.getText().toString();
                if (!callVolume.equals("")) {
                    VolumeParamDao.setCallVolume(Integer.parseInt(callVolume));
                    setOperateView.operateBackState(getString(R.string.set_success));
                    setBackVisibility(View.GONE);
                }
                break;
            default:
                int callMaxVolume = SystemSetUtils.getCallMaxVolume();
                int valoume = Integer.valueOf(keyBoardBean.getName());
                if (valoume > 0 && valoume <= callMaxVolume) {
                    mTvVolume.setText(keyBoardBean.getName());
                }
                break;
        }
    }


    @Override
    public void success() {
        setBackVisibility(View.VISIBLE);
        requestBack();
    }

    @Override
    public void fail() {
        setBackVisibility(View.VISIBLE);
    }
}
