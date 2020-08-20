package com.mili.smarthome.tkj.main.manage;

import android.app.Activity;
import android.content.Context;

import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.main.activity.MainActivity;
import com.mili.smarthome.tkj.main.activity.direct.DirectPressMainActivity;
import com.mili.smarthome.tkj.main.entity.HintBean;
import com.mili.smarthome.tkj.main.interf.IHintEventListener;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.set.activity.HintActivity;
import com.mili.smarthome.tkj.utils.AppManage;

public class HintEventManage {

    public static HintEventManage hintEventManage;
    private IHintEventListener mHintEventListener;


    public static HintEventManage getInstance() {
        if (hintEventManage == null) {
            hintEventManage = new HintEventManage();
        }
        return hintEventManage;
    }

    public void setHintEventListener(IHintEventListener hintEventListener) {
        mHintEventListener = hintEventListener;
    }

    public void setHintEvent(HintBean hintBean) {
        if (mHintEventListener != null) {
            mHintEventListener.onHintEvent(hintBean);
        }
    }

    public void toHintAct(Context context, HintBean hintBean) {
        Activity currentActivity = App.getInstance().getCurrentActivity();
        if (currentActivity instanceof HintActivity) {
            setHintEvent(hintBean);
        } else {
            //通话结束不做跳转
            if (hintBean.getType() != Constant.SetHintId.HINT_CALL_END) {
                if (currentActivity instanceof MainActivity) {
                    //直按式直接退出
                    if (AppConfig.getInstance().getCallType() == 1) {
                        currentActivity.finish();
                    }
                }else {
                    if (currentActivity instanceof DirectPressMainActivity) {

                    }else {
                        currentActivity.finish();
                    }
                }
                AppManage.getInstance().toActExtra(context, HintActivity.class, Constant.KEY_HINT, hintBean);
            }
        }
    }
}
