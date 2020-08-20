package com.android.client;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.android.CommSysDef;
import com.android.IntentDef;
import com.android.IntentDef.PubIntentTypeE;
import com.android.InterCommTypeDef.InterCallOutListener;
import com.android.InterCommTypeDef.InterDefVideoDataListener;
import com.android.InterCommTypeDef.InterLockListener;
import com.android.InterCommTypeDef.InterMonitorListener;
import com.android.InterCommTypeDef.InterSnapListener;
import com.android.main.InterCommDistribute;
import com.android.main.MainJni;


public class InterCommClient extends BaseClient implements
        IntentDef.OnNetCommDataReportListener, IntentDef.OnVideoDataReportListener {
    private static final String tag = "InterCommClient";
    private Context mContext;
    private InterCommDistribute mInterCommDistribute = null;

    /**
     * @param context
     */
    public InterCommClient(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        String[] list = new String[]{IntentDef.MODULE_INTERCOMM};

        startReceiver(context, list);
        StartIPC_Main(context);

        setmDataReportListener(this);
        mContext = context;
        mInterCommDistribute = new InterCommDistribute();
    }

    public void StopInterCommClient() {
        if (mContext == null) {
            Log.d(tag, "mContext is null");
        }
        stopReceiver(mContext, IntentDef.MODULE_INTERCOMM);
        StopIPC(mContext, CommSysDef.SERVICE_NAME_MAIN, IntentDef.MODULE_INTERCOMM);
    }

    public void setInterCallOutListener(InterCallOutListener Listener) {
        mInterCommDistribute.setInterCallOutListener(Listener);
    }

    public void setInterMonitorListener(InterMonitorListener Listener) {
        mInterCommDistribute.setInterMonitorListener(Listener);
    }

    public void setInterLockListener(InterLockListener Listener) {
        if (mInterCommDistribute != null) {
            mInterCommDistribute.setInterLockListener(Listener);
        }
    }

    public void setInterSnapListener(InterSnapListener Listener) {
        if (mInterCommDistribute != null) {
            mInterCommDistribute.setSnapDataListener(Listener);
        }
    }

    public void setInterVideoDataCallBKListener(InterDefVideoDataListener Listener) {
        if (mInterCommDistribute != null) {
            mInterCommDistribute.setInterVideoDataListener(Listener);
        }

        if (Listener != null) {
            MainJni.setmInterVideoDataCallBKListener(this);
        } else {
            MainJni.setmInterVideoDataCallBKListener(null);
        }
    }

    @Override
    public void OnDataReport(String action, int type, byte[] data) {
        if (false == action.equals(IntentDef.MODULE_INTERCOMM)) {
            return;
        }

        switch (type) {
            case PubIntentTypeE.MonitorStatusNotify:
                if (mInterCommDistribute != null) {
                    mInterCommDistribute.InterCommDistributeMonitor(mContext, data);
                }
                break;

            case PubIntentTypeE.CallOutStatusNofity:
                if (mInterCommDistribute != null) {
                    mInterCommDistribute.InterCommDistributeCallOut(mContext, data);
                }
                break;

            case PubIntentTypeE.IntercomLock:
                if (mInterCommDistribute != null) {
                    mInterCommDistribute.InterCommDistributeLock(mContext, data);
                }
                break;

            case PubIntentTypeE.InterComSnap:
                if (mInterCommDistribute != null) {
                    mInterCommDistribute.InterCommDistributeSnap(mContext, data);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void OnVideoDataReport(byte[] data, int datalen, int width, int height, int type) {

        if (data.length <= 0 || datalen <= 0 || width <= 0 || height <= 0) {
            Log.e(tag, "InterCommClient the data is error!!!");
            return;
        }
        //Log.e("InterCommClient", "DataLen:  "+datalen+" width:  "+width+" height: "+height);
        mInterCommDistribute.InterCommDistributeVideoDataCallBK(mContext, data, datalen, width, height, type);
    }

    public int InterCallRoom(String InputDev) {
        if (mMainService == null)
            return -1;
        try {
            return mMainService.Main_intercomCallRoom(InputDev);
        } catch (RemoteException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int InterCallCenter(int centerdevno, int exno) {
        if (mMainService == null)
            return -1;
        try {
            return mMainService.Main_intercomCallCenter(centerdevno, exno);
        } catch (RemoteException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void InterHandDown() {
        if (mMainService == null)
            return;
        try {
            mMainService.Main_intercomHandDown();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void InterMontorStop() {
        if (mMainService == null)
            return;
        try {
            mMainService.Main_intercomMonitorStop();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public int InterPreviewStart(int state) {
        if (mMainService == null)
            return -1;
        try {
            return mMainService.Main_intercomPreviewStart(state);
        } catch (RemoteException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void InterPreviewStop(int state) {
        if (mMainService == null)
            return;
        try {
            mMainService.Main_intercomPreviewStop(state);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void InterSetAudioState(int state) {
        if (mMainService == null)
            return;
        try {
            mMainService.Main_intercomSetAudioState(state);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public int InterRtspStart(String url) {
        if (mMainService == null)
            return -1;
        try {
            return mMainService.Main_intercomRtspStart(url);
        } catch (RemoteException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void InterRtspStop() {
        if (mMainService == null)
            return;
        try {
            mMainService.Main_intercomRtspStop();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置云电话呼叫状态
     * @param state 0 空闲 1 呼叫中 2 通话中 3 呼叫结束 4 通话结束
     */
    public void IntercomCloudPhoneState(int state) {
        if (mMainService == null)
            return;
        try {
            mMainService.Main_intercomCloudPhoneState(state);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param faceName   人脸ID
     * @param faceLen    人脸ID长度
     * @param confidence 相似度
     * @param snaptype   0本地抓拍；1本地数据；2流媒体抓拍；3流媒体数据
     * @param snapdata   抓拍数据
     * @param datalen    抓拍数据长度
     * return            0: 忽略 1：开锁成功 -1：无效人脸
     */
    public int InterFaceRecognizeOk(String faceName, int faceLen, String keyID, float confidence, int snaptype, byte[] snapdata, int datalen){
        if (mMainService == null)
            return 0;
        try {
            return mMainService.Main_dealFaceEvent(faceName, faceLen, keyID, confidence, snaptype, snapdata, datalen);
        } catch (RemoteException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void InterSnapFacePhoto(String snapFile){
        if (mMainService == null)
            return;

        try {
            mMainService.Main_snapFacePhoto(snapFile);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param faceName   人脸ID
     * @param snapPath   抓拍路径
     * @param confidence 相似度
     * return            0: 忽略 1：开锁成功 -1：无效人脸
     */
    public int InterFaceSnapAndReport(String faceName, String snapPath, float confidence){
        if (mMainService == null)
            return 0;
        try {
            return mMainService.Main_appSnapFaceAndReport(faceName, snapPath, confidence);
        } catch (RemoteException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int Main_intercomUnLock() {
        if (mMainService == null)
            return -1;
        try {
            return mMainService.Main_intercomUnLock();
        } catch (RemoteException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
