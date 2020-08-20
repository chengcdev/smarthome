package com.mili.smarthome.tkj.setting.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.dao.param.VolumeParamDao;
import com.mili.smarthome.tkj.utils.SystemSetUtils;
import com.mili.smarthome.tkj.widget.NumInputView;

/**
 * 通话音量
 */
public class SetCallVolumeFragment extends BaseSetFragment {

    private NumInputView tvVolume;
    private int maxVolume;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_call_volume;
    }

    @Override
    protected void bindView() {
        tvVolume = findView(R.id.tv_volume);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int callVolume = VolumeParamDao.getCallVolume();
        maxVolume = SystemSetUtils.getCallMaxVolume();
        tvVolume.setText(String.valueOf(callVolume));
        tvVolume.requestFocus();
    }

    @Override
    public boolean onKeyEvent(int keyCode, int keyState) {
        if (keyState == KEYSTATE_DOWN)
            return false;
        switch (keyCode) {
            case KEYCODE_1:
            case KEYCODE_2:
            case KEYCODE_3:
            case KEYCODE_4:
            case KEYCODE_5:
            case KEYCODE_6:
            case KEYCODE_7:
            case KEYCODE_8:
            case KEYCODE_9:
                if (keyCode <= maxVolume) {
                    tvVolume.setText(String.valueOf(keyCode));
                }
                break;
            case KEYCODE_CALL:
                save();
                break;
            case KEYCODE_BACK:
                requestBack();
                break;
        }
        return true;
    }

    private void save() {
        VolumeParamDao.setCallVolume(Integer.valueOf(tvVolume.getText().toString()));
        showResultAndBack(R.string.setting_suc);
    }
}
