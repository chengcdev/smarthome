package com.mili.smarthome.tkj.face;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.base.K3BaseFragment;
import com.mili.smarthome.tkj.main.widget.GotoMainDefaultTask;

public class FacePromptFragment extends K3BaseFragment {

    private GotoMainDefaultTask mGotoMainDefaultTask;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_face_prompt;
    }

    @Override
    protected void bindView() {
        mGotoMainDefaultTask = GotoMainDefaultTask.getInstance();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMainHandler.postDelayed(mGotoMainDefaultTask, 10000);
    }

    @Override
    public void onDestroyView() {
        mMainHandler.removeCallbacks(mGotoMainDefaultTask);
        super.onDestroyView();
    }

    @Override
    public boolean onKeyEvent(int keyCode, int keyState) {
        if (keyState == KEYSTATE_DOWN)
            return false;
        switch (keyCode) {
            case KEYCODE_BACK:
            case KEYCODE_CALL:
                mGotoMainDefaultTask.run();
                break;
        }
        return true;
    }
}
