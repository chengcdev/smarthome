
package com.android.main;

import android.content.Context;
import android.util.Log;

import com.android.IntentDef;
import com.android.IntentDef.OnNetCommDataReportListener;
import com.android.IntentDef.PubIntentTypeE;
import com.android.InterCommTypeDef;
import com.android.interf.ICallLockStateListener;

import java.util.Date;


public class InterCommLogic extends ServiceLogic implements
		OnNetCommDataReportListener {
    private static final String tag = "InterCommLogic";
    private InterCallOutLogicListener mInterCallOutLogicListener = null;
    private InterMonitorLogicListener mInterMonitorLogicListener = null;
    private InterCommDistribute mInterCommDistribute = null;
    private SysArbitration mSysArbitration = null;
    private MainJni mMainJni = null;
    private Context mContext = null;
    private Date mRecordDate = null;
    private int mSD = 0;
    private String localfilePath;
    private String SdfilePath;

    public InterCommLogic() {

    }

    /**
     * @param action
     */
    public InterCommLogic(String action) {
        super(action);
        // TODO Auto-generated constructor stub
        MainJni.setmInterCommListener(this);
    }

    public void InterCommLogicStart(SysArbitration nSysArbitration, MainJni Jni, Context context) {
        mInterCallOutLogicListener = new InterCallOutLogicListener(this);
        mInterMonitorLogicListener = new InterMonitorLogicListener(this);
        mInterCommDistribute = new InterCommDistribute();
        mInterCommDistribute.setInterCallOutListener(mInterCallOutLogicListener);
        mSysArbitration = nSysArbitration;
        mMainJni = Jni;
        mContext = context;
    }


    @Override
    public void OnDataReport(String action, int type, byte[] data) {

        if (false == action.equals(IntentDef.MODULE_INTERCOMM)) {
            return;
        }

        switch (type) {
            case IntentDef.PubIntentTypeE.CallOutStatusNofity:
                if (mInterCommDistribute != null) {
                    mInterCommDistribute.InterCommDistributeCallOut(null, data);
                }
                break;

            case IntentDef.PubIntentTypeE.MonitorStatusNotify:
                if (mInterCommDistribute != null) {
                    mInterCommDistribute.InterCommDistributeMonitor(null, data);
                }
                break;

            case PubIntentTypeE.IntercomLock:
                if (mInterCommDistribute != null) {
                    mInterCommDistribute.InterCommDistributeLock(null, data);
                }
                break;

            case PubIntentTypeE.INTENT_CLEAR_ACCESS_PASS:
                setDefaultDoorPwd();
                break;

            default:
                break;
        }
    }

    public void setDefaultDoorPwd() {
    }

        class InterCallOutLogicListener implements
                InterCommTypeDef.InterCallOutListener {
            private InterCommLogic mInterCommLogic;

            public InterCallOutLogicListener(InterCommLogic Context) {
                mInterCommLogic = Context;
            }

            @Override
            public void InterCallOutNone(int param) {
                // TODO Auto-generated method stub

            }

            @Override
            public void InterCallOutCalling(int param) {
                // TODO Auto-generated method stub
                mInterCommLogic.mSysArbitration.OnInterCallStateChange(
                        InterCommTypeDef.CallActive.CALLACTIVE_OUT,
                        InterCommTypeDef.CallState.CALL_STATE_CALLING, 0, null,
                        param);
            }

            @Override
            public void InterCallOutTalking(int param) {
                // TODO Auto-generated method stub

            }

            @Override
            public void InterCallOutEnd(int param) {
                // TODO Auto-generated method stub

            }

            @Override
            public void InterCallOutRecording(int param) {
                // TODO Auto-generated method stub

            }

            @Override
            public void InterCallOutRecordHit(int param) {
                // TODO Auto-generated method stub

            }

            @Override
            public void InterCallOutTimer(int maxtime, int exittime) {
                // TODO Auto-generated method stub

            }

            @Override
            public void InterCallOutMoveing(int param) {
                // TODO Auto-generated method stub

            }

            @Override
            public void InterCallOutHitState(int wordhit, int voicehit) {

            }
        }

        class InterMonitorLogicListener implements
                InterCommTypeDef.InterMonitorListener {

            private InterCommLogic mInterCommLogic;

            public InterMonitorLogicListener(InterCommLogic Context) {
                mInterCommLogic = Context;
            }

            @Override
            public void InterMonitorTalking(int param) {
                // TODO Auto-generated method stub

            }

            @Override
            public void InterMonitorEnd(int param) {

            }

            @Override
            public void InterMonitorTalkEnd(int param) {

            }
        }

    }
