package com.mili.smarthome.tkj.setting.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.android.interf.IKeyEventListener;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.base.K3BaseFragment;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.setting.view.ResultView;

public abstract class BaseSetFragment extends K3BaseFragment {

    private View ivBack;
    private View vwContent;
    private ResultView vwResult;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initBackBtn();
        vwContent = findView(R.id.setting_content);
        vwResult = findView(R.id.setting_result);
    }

    @Override
    public boolean onKeyEvent(int keyCode, int keyState) {
        if (keyState == KEYSTATE_UP && keyCode == KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return false;
    }

    private void initBackBtn() {
        FragmentActivity activity = getActivity();
        if (activity == null)
            return;
        ivBack = activity.findViewById(R.id.iv_back);
        if (ivBack == null)
            return;
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void onBackPressed() {
        requestBack();
    }

    protected void setBackVisibility(int visibility) {
        if (ivBack != null) {
            ivBack.setVisibility(visibility);
        }
    }

    protected void showHint(@StringRes int textId) {
        if (vwContent != null && vwResult != null) {
            // 增加按键事件屏蔽
            SinglechipClientProxy.getInstance().addKeyEventListener(KEY_EVENT_SHIELD);
            setBackVisibility(View.GONE);
            vwContent.setVisibility(View.GONE);
            vwResult.setVisibility(View.VISIBLE);
            vwResult.showResult(textId);
        }
    }

    protected void showResultAndBack(@StringRes int textId) {
        showResult(textId, 1500, false, new Runnable() {
            @Override
            public void run() {
                requestBack();
            }
        });
    }

    protected void showResult(@StringRes int textId, final Runnable task) {
        showResult(textId, 1500, false, task);
    }

    protected void showResult(@StringRes int textId, long delay, boolean cancelable, final Runnable task) {
        if (vwContent != null && vwResult != null) {
            // 增加按键事件屏蔽
            SinglechipClientProxy.getInstance().addKeyEventListener(KEY_EVENT_SHIELD);
            setBackVisibility(View.GONE);
            vwContent.setVisibility(View.GONE);
            vwResult.setVisibility(View.VISIBLE);
            vwResult.showResult(textId);
            //
            final Runnable action = new Runnable() {
                @Override
                public void run() {
                    // 移除按键事件屏蔽
                    SinglechipClientProxy.getInstance().removeKeyEventListener(KEY_EVENT_SHIELD);
                    vwResult.setVisibility(View.GONE);
                    vwContent.setVisibility(View.VISIBLE);
                    setBackVisibility(View.VISIBLE);
                    if (task != null)
                        task.run();
                }
            };
            //
            vwResult.postDelayed(action, delay);
            if (cancelable) {
                vwResult.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        vwResult.removeCallbacks(action);
                        vwResult.post(action);
                    }
                });
            }
        }
    }

    private static final IKeyEventListener KEY_EVENT_SHIELD = new IKeyEventListener() {
        @Override
        public boolean onKeyEvent(int keyCode, int keyState) {
            return true;
        }
    };
}
