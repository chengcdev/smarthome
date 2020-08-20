package com.mili.smarthome.tkj.setting.fragment;

import android.app.Activity;
import android.widget.EditText;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.utils.KeyboardUtils;

/**
 * IPC地址
 */
public class SetRtspFragment extends BaseSetFragment {

    private EditText etRtspUrl;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_rtsp;
    }

    @Override
    protected void bindView() {
        etRtspUrl = findView(R.id.et_rtsp_url);
    }

    @Override
    protected void bindData() {
        etRtspUrl.setText(AppConfig.getInstance().getRtspUrl());
    }

    @Override
    public boolean onKeyEvent(int keyCode, int keyState) {
        if (keyState == KEYSTATE_DOWN)
            return false;
        switch (keyCode) {
            case KEYCODE_CALL:
                save();
                break;
            default:
                super.onKeyEvent(keyCode, keyState);
                break;
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        Activity activity = getActivity();
        if (activity != null) {
            KeyboardUtils.hide(activity);
        }
        super.onDestroyView();
    }

    private void save() {
        Activity activity = getActivity();
        if (activity != null) {
            KeyboardUtils.hide(activity);
        }
        AppConfig.getInstance().setRtspUrl(etRtspUrl.getText().toString());
        showResultAndBack(R.string.setting_suc);
    }
}
