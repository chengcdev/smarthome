package com.mili.smarthome.tkj.call;


import android.annotation.SuppressLint;
import android.content.Context;

import com.android.CommTypeDef;
import com.android.InterCommTypeDef;
import com.android.client.InterCommClient;
import com.android.client.SetDriverSinglechipClient;
import com.android.main.MainCommDefind;
import com.mili.smarthome.tkj.BuildConfig;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.SystemSetUtils;

public class CallManage implements InterCommTypeDef.InterMonitorListener, InterCommTypeDef.InterLockListener {

    @SuppressLint("StaticFieldLeak")
//    private static volatile CallManage callManage;
    private static final CallManage callManage = new CallManage();
    @SuppressLint("StaticFieldLeak")
    private ICallMonitorListener callMonitorListener;
    private CallMonitorBean callMonitorBean;
    private String TAG = "CallManage";
    private InterCommClient interCommClient;

    private CallManage() {
        interCommClient = new InterCommClient(App.getInstance());
        LogUtils.w(" =========== CallManage ============");
    }

    public static CallManage getInstance() {
//        if (callManage == null) {
//            synchronized (CallManage.class) {
//                if (callManage == null) {
//                    callManage = new CallManage();
//                }
//            }
//        }
        return callManage;
    }

    //初始化对讲操作
    public void initCallClient(Context context) {
        if (interCommClient == null) {
            LogUtils.e(TAG + " initCallClient");
            interCommClient = new InterCommClient(context);
        }
        interCommClient.setInterMonitorListener(this);
        interCommClient.setInterLockListener(this);
        LogUtils.w(" =========== initCallClient ============");
    }

    public InterCommClient getInterCommClient() {
        return interCommClient;
    }

    //监视对讲
    public void setCallMonitorListener(ICallMonitorListener listener) {
        this.callMonitorListener = listener;
    }

    //停止监视对讲
    public void stopCallMonitor() {
        if (interCommClient != null) {
            interCommClient.InterMontorStop();
            interCommClient.setInterMonitorListener(null);
        }
    }

    //停止对讲操作
    public void stopCallCommClient() {
        if (interCommClient != null) {
            interCommClient.StopInterCommClient();
            interCommClient = null;
        }
    }

    //正在播放语音时，设置不能点击通话
    public void setInterSetAudioState(int state) {
        if (interCommClient != null) {
            interCommClient.InterSetAudioState(state);
        }
    }

    @Override
    public void InterMonitorTalking(int param) {

        if (!SystemSetUtils.isScreenOn()) {
            SystemSetUtils.screenOn();
        }

        if (BuildConfig.DevType == MainCommDefind.DEVICE_TYPE_K4_X1600_REL) {
            if (!SetDriverSinglechipClient.getInstance().getSystemSleep()) {
                SetDriverSinglechipClient.getInstance().setSystemSleep(1);
            }
        }

        LogUtils.w(TAG + "InterMonitorTalking=====param: " + param);

        if (param == CommTypeDef.DeviceType.DEVICE_TYPE_ROOM || param == CommTypeDef.DeviceType.DEVICE_TYPE_ROOMFJ) {
            //住户监视对讲
            callMonitorBean = new CallMonitorBean();
            callMonitorBean.setCallFrom(Const.CallAction.CALL_FROM_RESIDENT);
            callMonitorBean.setCallTalk(true);
            callMonitorBean.setOpenDoor(false);
            if (callMonitorListener != null) {
                callMonitorListener.onCallMonitor(callMonitorBean);
            }
        } else if (param == CommTypeDef.DeviceType.DEVICE_TYPE_MANAGER) {
            //中心监视对讲
            callMonitorBean = new CallMonitorBean();
            callMonitorBean.setCallFrom(Const.CallAction.CALL_FROM_CENTER);
            callMonitorBean.setCallTalk(true);
            callMonitorBean.setOpenDoor(false);
            if (callMonitorListener != null) {
                callMonitorListener.onCallMonitor(callMonitorBean);
            }
        }
    }

    @Override
    public void InterMonitorEnd(int param) {
        LogUtils.w(TAG + "InterMonitorEnd=====param: " + param);
    }

    @Override
    public void InterMonitorTalkEnd(int param) {

        LogUtils.w(TAG + "InterMonitorTalkEnd=====param: " + param);

        if (param == CommTypeDef.DeviceType.DEVICE_TYPE_ROOM || param == CommTypeDef.DeviceType.DEVICE_TYPE_ROOMFJ) {
            //住户监视对讲结束
            callMonitorBean = new CallMonitorBean();
            callMonitorBean.setCallFrom(Const.CallAction.CALL_FROM_RESIDENT);
            callMonitorBean.setCallTalk(false);
            callMonitorBean.setOpenDoor(false);
            callMonitorBean.setCallEnd(true);
            if (callMonitorListener != null) {
                callMonitorListener.onCallMonitor(callMonitorBean);
            }
        } else if (param == CommTypeDef.DeviceType.DEVICE_TYPE_MANAGER) {
            //中心监视对讲结束
            callMonitorBean = new CallMonitorBean();
            callMonitorBean.setCallFrom(Const.CallAction.CALL_FROM_CENTER);
            callMonitorBean.setCallTalk(false);
            callMonitorBean.setOpenDoor(false);
            callMonitorBean.setCallEnd(true);
            if (callMonitorListener != null) {
                callMonitorListener.onCallMonitor(callMonitorBean);
            }
        }
    }

    @Override
    public void InterLock(int param) {

        LogUtils.w(TAG + "InterLock=====param: " + param);

        if (!SystemSetUtils.isScreenOn()) {
            SystemSetUtils.screenOn();
        }

        if (BuildConfig.DevType == MainCommDefind.DEVICE_TYPE_K4_X1600_REL) {
            if (!SetDriverSinglechipClient.getInstance().getSystemSleep()) {
                SetDriverSinglechipClient.getInstance().setSystemSleep(1);
            }
        }

        if (param == CommTypeDef.DeviceType.DEVICE_TYPE_ROOM || param == CommTypeDef.DeviceType.DEVICE_TYPE_ROOMFJ) {
            //住户监视门开了
            callMonitorBean = new CallMonitorBean();
            callMonitorBean.setCallFrom(Const.CallAction.CALL_FROM_RESIDENT);
            callMonitorBean.setOpenDoor(true);
            callMonitorBean.setCallTalk(false);
            if (callMonitorListener != null) {
                callMonitorListener.onCallMonitor(callMonitorBean);
            }
        } else if (param == CommTypeDef.DeviceType.DEVICE_TYPE_MANAGER) {
            //中心监视门开了
            callMonitorBean = new CallMonitorBean();
            callMonitorBean.setCallFrom(Const.CallAction.CALL_FROM_CENTER);
            callMonitorBean.setOpenDoor(true);
            callMonitorBean.setCallTalk(false);
            if (callMonitorListener != null) {
                callMonitorListener.onCallMonitor(callMonitorBean);
            }
        }
    }
}
