package com.mili.smarthome.tkj.face;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.fragment.BaseMainFragment;
import com.mili.smarthome.tkj.utils.ViewUtils;

public class FacePromptFragment extends BaseMainFragment {

    private BackTask mBackTask = new BackTask();

    @Override
    public int getLayout() {
        return R.layout.fragment_face_prompt;
    }

    @Override
    public void initView(View view) {
        ViewUtils.findView(view, R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backMainActivity();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMainHandler.postDelayed(mBackTask, 10000);
    }

    @Override
    public void onDestroyView() {
        mMainHandler.removeCallbacks(mBackTask);
        super.onDestroyView();
    }

    private class BackTask implements Runnable {
        @Override
        public void run() {
            backMainActivity();
        }
    }
}
