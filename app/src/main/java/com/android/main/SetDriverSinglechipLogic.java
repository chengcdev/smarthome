package com.android.main;

import android.content.Context;
import android.util.Log;

import com.android.IntentDef;


/**
 * Created by zhengxc on 2019/1/2 0002.
 */

public class SetDriverSinglechipLogic extends ServiceLogic implements IntentDef.OnNetCommDataReportListener {

    private static final String TAG = "SetDriverSinglechipLogic";

    private Context mContext = null;


    public SetDriverSinglechipLogic(String action) {
        super(action);
        MainJni.setmSinglechipListener(this);
    }

    public void SetDriverSinglechipStart(SysArbitration nSysArbitration, Context context, MainJni Jni) {
        mContext = context;
    }

    @Override
    public void OnDataReport(String action, int type, byte[] data) {

        if (!action.equals(IntentDef.MODULE_SINGLECHIP)) {
            return;
        }

        switch (type) {
            case IntentDef.PubIntentTypeE.Singlecgip_AddCard:

                break;

            default:
                break;
        }
    }

}
