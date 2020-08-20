package com.mili.smarthome.tkj.setting.fragment;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.setting.activity.ResetActivity;
import com.mili.smarthome.tkj.widget.InputView;

public abstract class ResetBaseFragment extends K4BaseFragment {

    protected void gotoNextFragment() {
         FragmentActivity activity = getActivity();
        if (activity instanceof ResetActivity) {
            ((ResetActivity) activity).gotoNextFragment();
        }
    }

    protected void gotoPreviousFragment() {
        Log.d("resetbase", "=---");
        FragmentActivity activity = getActivity();
        if (activity instanceof ResetActivity) {
            ((ResetActivity) activity).gotoPreviousFragment();
        }
    }

    @Override
    public boolean requestBack() {
        gotoPreviousFragment();
        return true;
    }

    /**
     * 编辑框退格并退出
     */
    protected void backspacePrevious() {
        View root = getView();
        if (root != null) {
            View focus = root.findFocus();
            if (focus instanceof InputView) {
                InputView iptView = (InputView) focus;
                if (iptView.backspace())
                    return ;
            }
        }
        gotoPreviousFragment();
    }
}
